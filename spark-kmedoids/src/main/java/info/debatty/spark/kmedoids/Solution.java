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

/**
 * A clustering solution.
 *
 * Keeps the list of medoids, plus some other values: number of iterations,
 * number of computed similarities etc.
 *
 *
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered
 */
public class Solution<T> {
    private ArrayList<T> medoids = new ArrayList<>();
    private double total_similarity;
    private long computed_similarities;
    private final long start_time;
    private long end_time;
    private final long dataset_size;

    /**
     * Number of candidate solutions that we tried.
     */
    private int trials;

    /**
     * Number of candidate solutions that actually increased the total
     * similarity.
     */
    private int iterations;

    /**
     * Initialize a solution with dataset size and computation start time.
     * @param dataset_size
     */
    public Solution(final long dataset_size) {
        this.dataset_size = dataset_size;
        this.start_time = System.currentTimeMillis();
    }

    /**
     *
     * @return
     */
    public final ArrayList<T> getMedoids() {
        return medoids;
    }

    /**
     *
     * @return
     */
    public final double getTotalSimilarity() {
        return total_similarity;
    }

    /**
     *
     * @return
     */
    public final long getComputedSimilarities() {
        return computed_similarities;
    }

    /**
     *
     * @return
     */
    public final long getStartTime() {
        return start_time;
    }

    /**
     *
     * @return
     */
    public final long getEndTime() {
        return end_time;
    }

    /**
     *
     * @return
     */
    public final long getDatasetSize() {
        return dataset_size;
    }

    /**
     *
     * @return
     */
    public final int getTrials() {
        return trials;
    }

    /**
     *
     * @return
     */
    public final int getIterations() {
        return iterations;
    }

    /**
     * Set the new medoids and the total similarity of these medoids together.
     * @param medoids
     * @param total_similarity
     */
    public final void setNewMedoids(
            final ArrayList<T> medoids, final double total_similarity) {

        this.medoids = medoids;
        this.total_similarity = total_similarity;
    }


    /**
     *
     * @return
     */
    @Override
    public final String toString() {
        return "Total similarity: " + total_similarity + "\n"
                + "Trials: " + trials + "\n"
                + "Iterations: " + iterations + "\n"
                + "Computed similarities: " + computed_similarities + "\n"
                + "Run time: " + ((end_time - start_time) / 1000);
    }

    /**
     * Return a CSV line of the solution.
     * @return
     */
    public final String toCSV() {
        return total_similarity + ";\t" + trials + ";\t" + iterations + ";\t"
                + computed_similarities + ";\t"
                + ((end_time - start_time) / 1000);
    }

    /**
     * Increment the number of computed similarities.
     * @param similarities
     */
    public final void incComputedSimilarities(final long similarities) {
        this.computed_similarities += similarities;
    }

    /**
     *
     */
    public final void incIterations() {
        iterations++;
    }

    /**
     *
     */
    public final void incTrials() {
        trials++;
    }

    /**
     * Set end time.
     */
    public final void end() {
        this.end_time = System.currentTimeMillis();
    }
}
