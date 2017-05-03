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

package info.debatty.spark.kmedoids.CLARANS;

import info.debatty.spark.kmedoids.AbstractClustering;
import info.debatty.spark.kmedoids.Solution;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Parallel (Spark) implementation of CLARANS.
 * This is a data parallel implementation.
 * @author Thibault Debatty
 * @param <T> The type of data that will be clustered
 */
public abstract class AbstractClarans<T> extends AbstractClustering<T> {

    private final Config conf;

    /**
     *
     * @param conf
     * @param sc
     */
    public AbstractClarans(final Config<T> conf, final JavaSparkContext sc) {
        super(conf, sc);
        this.conf = conf;
    }

    @Override
    public final void cluster(final JavaRDD<T> data, final Solution solution) {

        int k = conf.getK();

        for (int i = 0; i < conf.getNumLocal(); i++) {

            // Select random solution and compute cost / total similarity
            List<T> current_medoids = sample(data, k);
            double current_similarity = totalSimilarity(current_medoids, data);

            int j = 0;
            while (j < conf.getMaxNeighbor()) {

                // Select neighbor solution
                List<T> candidate_solution = getNeighborSolution(
                        current_medoids, data);

                // Compute similarity of neighbor solution
                double candidate_similarity = totalSimilarity(
                        candidate_solution, data);

                solution.incIterations();

                if (candidate_similarity > current_similarity) {
                    current_similarity = candidate_similarity;
                    current_medoids = candidate_solution;
                    j = 0;

                } else {
                    j++;
                }
            }

            if (current_similarity > solution.getSimilarity()) {
                solution.setSimilarity(current_similarity);
                solution.setMedoids(current_medoids);
            }
        }
    }


    /**
     * Provide a neighbor solution.
     * @param medoids
     * @param data
     * @return
     */
    public abstract List<T> getNeighborSolution(
            final List<T> medoids, final JavaRDD<T> data);
}
