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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic k-medoids clusterer.
 *
 * The concrete implementation of the neighbor generator will make the
 * difference between CLARANS, SA and Heuristical clustering.
 *
 * @author Thibault Debatty
 * @param <T> the type of data to cluster
 */
public class Clusterer<T> {
    // Algorithm configuration
    private int k;
    private double imbalance = Double.POSITIVE_INFINITY;
    private NeighborGenerotor<T> neighbor_generator;
    private Similarity<T> similarity;
    private Budget budget;

    private RandomPointsSupplier<T> points_supplier;

    private final Logger logger = LoggerFactory.getLogger(Clusterer.class);


    /**
     *
     * @param k
     */
    public final void setK(final int k) {
        this.k = k;
    }

    /**
     * Set imbalance : 1.0 equals perfectly balanced.
     * @param imbalance
     */
    public final void setImbalance(final double imbalance) {
        if (imbalance < 1.0) {
            throw new IllegalArgumentException("Imbalance must be >= 1.0");
        }

        this.imbalance = imbalance;
    }

    /**
     *
     * @param neighbor_generator
     */
    public final void setNeighborGenerator(
            final NeighborGenerotor<T> neighbor_generator) {
        logger.debug(
                "Using neighbor generator {}",
                neighbor_generator.getClass().getName());
        this.neighbor_generator = neighbor_generator;
    }

    /**
     *
     * @param similarity
     */
    public final void setSimilarity(final Similarity<T> similarity) {
        this.similarity = similarity;
    }

    /**
     *
     * @param budget
     */
    public final void setBudget(final Budget budget) {

        this.budget = budget;
    }

    /**
     * Cluster the data according to defined parameters.
     * @param input_data
     * @return
     */
    public final Solution<T> cluster(final JavaRDD<T> input_data) {
        if (k <= 0) {
            throw new IllegalStateException("k must be > 0!");
        }

        if (similarity == null) {
            throw new IllegalStateException("Similarity is not defined!");
        }

        if (budget == null) {
            throw new IllegalStateException("No budget is defined!");
        }

        JavaRDD<T> data = input_data.cache();

        // Keep track of best solution
        Solution<T> solution = new Solution<>(data.count());
        logger.debug("Dataset contains {} objects", solution.getDatasetSize());

        neighbor_generator.init(k);
        points_supplier = new RandomPointsSupplier<>(
                data, solution.getDatasetSize());

        // Select random initial solution and evaluate
        ArrayList<T> random_medoids = points_supplier.pick(k);
        solution.setNewMedoids(random_medoids, evaluate(data, random_medoids));
        solution.incComputedSimilarities(k * solution.getDatasetSize());

        while (true) {
            logger.trace("Trial {}", solution.getTrials());

            // Select neighbor solution
            CountingSimilarity<T> counting_sim =
                    new CountingSimilarity<>(similarity);

            ArrayList<T> candidate;
            try {
                candidate = neighbor_generator.getNeighbor(
                        new NeighborGeneratorHelper<>(this),
                        solution,
                        counting_sim);
            } catch (NoNeighborFoundException ex) {
                solution.incComputedSimilarities(counting_sim.getCount());
                break;
            }
            solution.incComputedSimilarities(counting_sim.getCount());

            // Evaluate
            double candidate_similarity = evaluate(data, candidate);
            solution.incComputedSimilarities(k * solution.getDatasetSize());
            neighbor_generator.notifyCandidateSolutionCost(
                    candidate, candidate_similarity);

            if (candidate_similarity > solution.getTotalSimilarity()) {
                solution.setNewMedoids(candidate, candidate_similarity);
                solution.incIterations();
                neighbor_generator.notifyNewSolution(
                        candidate, candidate_similarity);
            }

            solution.incTrials();

            if (budget.isExhausted(solution)) {
                break;
            }
        }

        solution.end();
        logger.debug("Found solution {}", solution);
        return solution;

    }

    private double evaluate(final JavaRDD<T> data, final ArrayList<T> medoids) {
        return data
                .mapPartitions(
                    new AssignToMedoid<>(medoids, similarity, imbalance))
                .reduce((v1, v2) -> (v1 + v2));

    }

    /**
     *
     * @return
     */
    public final T getRandomPoint() {
        return points_supplier.pick();
    }
}

/**
 * Assign each point to the most similar medoid, and return the total (highest)
 * similarity.
 * @author Thibault Debatty
 * @param <T>
 */
class AssignToMedoid<T> implements FlatMapFunction<Iterator<T>, Double> {

    private final List<T> medoids;
    private final Similarity<T> similarity;
    private final double imbalance;

    AssignToMedoid(
            final List<T> medoids,
            final Similarity<T> similarity,
            final double imbalance) {

        this.medoids = medoids;
        this.similarity = similarity;
        this.imbalance = imbalance;
    }

    @Override
    public Iterator<Double> call(final Iterator<T> iterator) {
        int k = medoids.size();

        // Collect all points
        LinkedList<T> points = new LinkedList<>();
        while (iterator.hasNext()) {
            points.add(iterator.next());
        }

        int n_local = points.size();
        double capacity = imbalance * n_local / k;
        int[] cluster_sizes = new int[k];
        double total_similarity = 0;

        for (T point : points) {
            double[] sims = new double[k];
            double[] values = new double[k];

            for (int i = 0; i < k; i++) {
                sims[i] = similarity.similarity(point, medoids.get(i));
                values[i] =
                        sims[i] * (1.0 - (double) cluster_sizes[i] / capacity);
            }

            int cluster_index = argmax(values);
            cluster_sizes[cluster_index]++;
            total_similarity += sims[cluster_index];
        }

        LinkedList<Double> result = new LinkedList<>();
        result.add(total_similarity);
        return result.iterator();


        /*
        for (int i = 0; i < n; i++) {
                T item = data.get(i);

                double[] sims = new double[k];
                double[] values = new double[k];
                for (int j = 0; j < k; j++) {
                    similarities++;
                    sims[j] = similarity.similarity(item, medoids.get(j));
                    values[j] = sims[j];

                    // Capacity == 0.0 indicate we should compute classical
                    // (unconstrained) k-medoids
                    if (capacity == 0.0) {
                        continue;
                    }

                    values[j] *= (1.0 - (double) clusters[j].size() / capacity);
                }

                int cluster_index = argmax(values);
                clusters[cluster_index].add(item);
                total_similarity.add(sims[cluster_index]);
            }*/



        /*
        double highest_similarity = 0;
        for (T medoid : medoids) {
            double current_similarity = similarity.similarity(point, medoid);
            if (current_similarity > highest_similarity) {
                highest_similarity = current_similarity;
            }
        }

        return highest_similarity;*/
    }

    /**
     * Return the index of the highest value in the array.
     * @param values
     */
    private static int argmax(final double[] values) {
        double max = -Double.MAX_VALUE;
        int max_index = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
                max_index = i;
            }
        }

        return max_index;
    }
}

/**
 * Keeps a reserve of points in cache, to avoid executing rdd.sample()
 * operations.
 * @author Thibault Debatty
 * @param <T> type of points
 */
class RandomPointsSupplier<T> {

    private final Logger logger = LoggerFactory.getLogger(
            RandomPointsSupplier.class);

    private static final int STOCK_SIZE = 1000;

    private final JavaRDD<T> data;
    private final long n;
    private LinkedList<T> stock = new LinkedList<>();


    RandomPointsSupplier(final JavaRDD<T> data, final long n) {
        this.data = data;
        this.n = n;
    }

    T pick() {
        if (stock.size() <= 0) {
            fill();
        }

        return stock.pop();
    }

    ArrayList<T> pick(final int count) {
        if (stock.size() < count) {
            fill();
        }

        ArrayList<T> selection = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            selection.add(stock.pop());
        }

        return selection;
    }

    private void fill() {
        logger.info("Fetching new points...");
        double sampling = 1.0 * STOCK_SIZE / n;
        sampling = Math.min(sampling, 1.0);

        ArrayList<T> list = new ArrayList<>(
                    data
                    .sample(false, sampling)
                    .collect());
        Collections.shuffle(list);

        stock = new LinkedList<>(list);
    }
}
