/*
 * The MIT License
 *
 * Copyright 2017 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package info.debatty.spark.kmedoids.Voronoi;

import info.debatty.spark.kmedoids.AbstractClustering;
import info.debatty.spark.kmedoids.Config;
import info.debatty.spark.kmedoids.SimilarityInterface;
import info.debatty.spark.kmedoids.Solution;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.Partitioner;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;

/**
 *
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered.
 */
public class Voronoi<T> extends AbstractClustering<T> {

    private final Config conf;
    private final JavaSparkContext sc;

    private Solution solution;

    /**
     * Uses Voronoi iteration to compute k-medoids clustering.
     * @param conf
     * @param sc
     */
    public Voronoi(final Config conf, final JavaSparkContext sc) {
        super(conf, sc);
        this.sc = sc;
        this.conf = conf;
    }

    @Override
    public final void cluster(final JavaRDD<T> data, final Solution solution) {
        long n = data.count();

        this.solution = solution;

        // Select initial solution
        solution.setMedoids(sample(data, conf.getK()));
        solution.setSimilarity(totalSimilarity(solution.getMedoids(), data));
        solution.incComputedSimilarities(n * conf.getK());

        // While similarity increases
        while (true) {
            solution.incIterations();

            // In each cluster, find the most central point
            // (maximizes similarity to other points)
            List<T> candidate_medoids = findNewMedoids(
                    solution.getMedoids(), data);

            assert (candidate_medoids.size() == conf.getK());

            // Assign each point to new medoid
            // and compute new total similarity
            double candidate_similarity = totalSimilarity(
                    candidate_medoids, data);

            if (candidate_similarity <= solution.getSimilarity()) {
                break;
            }

            solution.setSimilarity(candidate_similarity);
            solution.setMedoids(candidate_medoids);
        }

    }


    private List<T> findNewMedoids(
            final List<T> medoids, final JavaRDD<T> data) {

        LongAccumulator accumulator = sc.sc().longAccumulator();

        // Partition data according to most similar medoid
        JavaPairRDD<Integer, T> pairs = data.mapToPair(
                new AssignToMedoid(medoids, conf.getSimilarity(), accumulator));

        JavaPairRDD<Integer, T> partitionned_pairs =
                pairs.partitionBy(new SimplePartitioner(medoids.size()));

        // Find most central medoid in each partition
        JavaRDD<T> new_medoids_districuted = partitionned_pairs.mapPartitions(
                new FindMedoid<T>(conf.getSimilarity(), accumulator),
                true);

        List<T> new_medoids = new_medoids_districuted.collect();
        solution.incComputedSimilarities(accumulator.sum());

        return new_medoids;
    }
}

/**
 * Assign each element to most similar medoid and returns a pair
 * (medoid_id, element).
 * @author Thibault Debatty
 * @param <T>
 */
class AssignToMedoid<T> implements PairFunction<T, Integer, T> {
    private final ArrayList<T> medoids;
    private final SimilarityInterface similarity;
    private final LongAccumulator accumulator;

    AssignToMedoid(
            final List<T> medoids,
            final SimilarityInterface similarity,
            final LongAccumulator accumulator) {
        this.medoids = new ArrayList<>(medoids);
        this.similarity = similarity;
        this.accumulator = accumulator;
    }

    @Override
    public Tuple2<Integer, T> call(final T element) throws Exception {
        int most_similar = -1;
        double highest_similarity = -1.0;

        for (int i = 0; i < medoids.size(); i++) {
            double current_similarity =
                    similarity.similarity(element, medoids.get(i));
            if (current_similarity > highest_similarity) {
                highest_similarity = current_similarity;
                most_similar = i;
            }
        }

        accumulator.add(medoids.size());

        return new Tuple2<>(most_similar, element);
    }
}

/**
 * Partitions data according to the key, which must be an Integer.
 * @author Thibault Debatty
 */
class SimplePartitioner extends Partitioner {
    private final int partitions;

    SimplePartitioner(final int partitions) {
        this.partitions = partitions;
    }

    @Override
    public int numPartitions() {
        return partitions;
    }

    @Override
    public int getPartition(final Object key) {
        return ((Integer) key).intValue();
    }
}

/**
 * Find the most central element in each partition.
 * @author Thibault Debatty
 * @param <T>
 */
class FindMedoid<T>
        implements FlatMapFunction<Iterator<Tuple2<Integer, T>>, T> {
    private final SimilarityInterface<T> similarity;
    private LongAccumulator accumulator;

    FindMedoid(
            final SimilarityInterface<T> similarity,
            final LongAccumulator accumulator) {
        this.similarity = similarity;
        this.accumulator = accumulator;
    }

    @Override
    public Iterator<T> call(final Iterator<Tuple2<Integer, T>> tuples)
            throws Exception {
        // Collect all elements
        LinkedList<T> elements = new LinkedList<>();
        while (tuples.hasNext()) {
            elements.add(tuples.next()._2);
        }

        // Find most central element...
        T most_central = null;
        double most_central_similarity = 0;
        long computed_similarities = 0;

        for (T current : elements) {
            double current_similarity = 0;
            for (T other : elements) {
                current_similarity += similarity.similarity(
                        current, other);
                computed_similarities++;
            }

            if (current_similarity > most_central_similarity) {
                most_central_similarity = current_similarity;
                most_central = current;
            }
        }

        accumulator.add(computed_similarities);

        LinkedList<T> result = new LinkedList<>();
        result.add(most_central);
        return result.iterator();
    }
}
