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

package info.debatty.spark.kmedoids;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.util.LongAccumulator;

/**
 *
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered
 */
public abstract class AbstractClustering<T> implements ClustererInterface<T> {

    private static final double OVERSAMPLING = 10.0;

    private final Random rand = new Random();
    private final Config<T> conf;
    private final JavaSparkContext sc;

    // Holds some of the state...
    private Solution<T> solution;

    /**
     * Abstract clustering implementation.
     * Provides some typical methods like compute total similarity of a solution
     * etc.
     * @param conf
     * @param sc
     */
    public AbstractClustering(final Config conf, final JavaSparkContext sc) {
        this.conf = conf;
        this.sc = sc;
    }

    final void setSolution(final Solution<T> solution) {
        this.solution = solution;
    }

    /**
     *
     * @param data
     * @return
     */
    @Override
    public final Solution cluster(final JavaRDD<T> data) {
        // Keep track of best solution
        solution = new Solution<>(conf, data);
        solution.tick();

        cluster(data, solution);

        solution.tock();
        return solution;
    }

    /**
     *
     * @param data
     * @param solution
     */
    public abstract void cluster(final JavaRDD<T> data, Solution solution);


    /**
     * Compute the total similarity of a solution.
     * Computation cost is k.n
     * @param medoids
     * @param data
     * @return
     */
    public final double totalSimilarity(
            final List<T> medoids, final JavaRDD<T> data) {

        LongAccumulator accumulator = sc.sc().longAccumulator();

        double total_similarity = data.aggregate(
                new Double(0),
                new SimilarityFunction<>(
                        medoids, conf.getSimilarity(), accumulator),
                new SumFunction());

        solution.incComputedSimilarities(accumulator.sum());
        return total_similarity;
    }

    /**
     * Get count random elements from the dataset.
     * @param data
     * @param count
     * @return
     */
    public final List<T> sample(final JavaRDD<T> data, final int count) {
        double fraction = OVERSAMPLING * count / data.count();
        List<T> sample = data.sample(false, fraction).collect();
        List<T> result = new LinkedList<>();

        while (result.size() < count) {
            T element = sample.get(rand.nextInt(sample.size()));
            if (!result.contains(element)) {
                result.add(element);
            }
        }

        return result;
    }
}

/**
 * Compute the total similarity of this solution (medoids).
 * @author Thibault Debatty
 * @param <T>
 */
class SimilarityFunction<T> implements Function2<Double, T, Double> {
    private final List<T> medoids;
    private final SimilarityInterface<T> similarity;
    private final LongAccumulator accumulator;

    /**
     * Compute the similarity of these medoids.
     * @param medoids
     * @param similarity
     */
    SimilarityFunction(
            final List<T> medoids,
            final SimilarityInterface<T> similarity,
            final LongAccumulator accumulator) {
        this.medoids = medoids;
        this.similarity = similarity;
        this.accumulator = accumulator;
    }

    @Override
    public Double call(final Double agg, final T point) throws Exception {

        double best_similarity = -1;
        for (T medoid : medoids) {
            double current_similarity = similarity.similarity(point, medoid);
            if (current_similarity > best_similarity) {
                best_similarity = current_similarity;
            }
        }
        accumulator.add(medoids.size());
        return new Double(agg + best_similarity);
    }
}

/**
 * Sum aggregate.
 * @author Thibault Debatty
 */
class SumFunction implements Function2<Double, Double, Double> {

    @Override
    public Double call(final Double value0, final Double value1)
            throws Exception {
        return value0 + value1;
    }
}
