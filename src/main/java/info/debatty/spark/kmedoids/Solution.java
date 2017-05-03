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

import java.util.List;
import org.apache.spark.api.java.JavaRDD;

/**
 * The result of a clustering.
 * Will contain the clustering itself, plus additional side information.
 * @author Thibault Debatty
 * @param <T> type of data
 */
public class Solution<T> {
    private double similarity = -1;
    private List<T> medoids;
    private final Config<T> conf;
    private final JavaRDD<T> data;
    private int iterations = 0;
    private long computed_similarities = 0;
    private long start;
    private long end;


    /**
     * Solution of a CLARANS clustering process.
     * Keeps a reference to the configuration and dataset that were used.
     * @param conf
     * @param data
     */
    public Solution(final Config<T> conf, final JavaRDD<T> data) {
        this.conf = conf;
        this.data = data;
    }

    /**
     * Increment the number of computed similarities.
     * @param computed_similarities
     */
    public final void incComputedSimilarities(
            final long computed_similarities) {

        this.computed_similarities += computed_similarities;
    }

    /**
     * Increment the number of iterations.
     */
    public final void incIterations() {
        this.iterations++;
    }

    /**
     *
     * @return
     */
    public final double getSimilarity() {
        return similarity;
    }

    /**
     *
     * @param similarity
     */
    public final void setSimilarity(final double similarity) {
        this.similarity = similarity;
    }

    /**
     *
     * @return
     */
    public final List<T> getMedoids() {
        return medoids;
    }

    /**
     *
     * @param medoids
     */
    public final void setMedoids(final List<T> medoids) {
        this.medoids = medoids;
    }

    /**
     *
     * @return
     */
    public final int getIterations() {
        return iterations;
    }

    /**
     *
     * @param iterations
     */
    public final void setIterations(final int iterations) {
        this.iterations = iterations;
    }


    /**
     * Runtime in milliseconds.
     * @return
     */
    public final long runtime() {
        return end - start;
    }

    /**
     * Start the timer for computing runtime.
     */
    public final void tick() {
        start = System.currentTimeMillis();
    }

    /**
     * Stop timer for computing runtime.
     */
    public final void tock() {
        end = System.currentTimeMillis();
    }

    @Override
    public final String toString() {
        return    "Similarity:            " + similarity + "\n"
                + "Iterations:            " + iterations + "\n"
                + "Computed similarities: " + computed_similarities + "\n"
                + "Runtime:               " + runtime() + "ms";

    }
}
