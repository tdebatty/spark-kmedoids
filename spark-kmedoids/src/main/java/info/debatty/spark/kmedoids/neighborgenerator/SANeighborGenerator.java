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
package info.debatty.spark.kmedoids.neighborgenerator;

import info.debatty.spark.kmedoids.NoNeighborFoundException;
import java.util.ArrayList;
import java.util.Random;
import info.debatty.spark.kmedoids.NeighborGeneratorHelper;
import info.debatty.spark.kmedoids.Similarity;
import info.debatty.spark.kmedoids.Solution;

/**
 * Simulated Annealing is used to select replacement point to build the
 * candidate solution.
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered
 */
public class SANeighborGenerator<T> extends AbstractNeighborGenerator<T> {

    private final Random rand = new Random();
    private static final int MAX_TRIALS = 10000;

    private final double initial_temp;
    private final double gamma;

    /**
     * Initialize a Simulated Annealing based neighbor generator.
     * @param initial_temp
     * @param gamma
     */
    public SANeighborGenerator(final double initial_temp, final double gamma) {
        this.initial_temp = initial_temp;
        this.gamma = gamma;
    }

    @Override
    public final ArrayList<T> getNeighbor(
            final NeighborGeneratorHelper<T> helper,
            final Solution<T> current_solution,
            final Similarity<T> similarity) throws NoNeighborFoundException {

        ArrayList<T> new_solution = new ArrayList<>(
                current_solution.getMedoids());
        int k = current_solution.getMedoids().size();

        for (int i = 0; i < MAX_TRIALS; i++) {
            int medoid_position = rand.nextInt(k);
            T new_medoid = helper.getRandomPoint();

            double sim = similarity.similarity(
                    new_medoid,
                    current_solution.getMedoids().get(medoid_position));

            int failed_trials =
                    current_solution.getTrials()
                    - current_solution.getIterations();

            if (!accept(sim, failed_trials)) {
                continue;
            }

            new_solution.set(medoid_position, new_medoid);
            return new_solution;
        }

        throw new NoNeighborFoundException();
    }

    private boolean accept(final double similarity, final int failed_trials) {
        // Metropolis & Kirkpatrick:
        // Original probability is exp(-DELTA_E/T)
        double temperature = initial_temp * Math.pow(gamma, failed_trials);
        double distance = 1.0 / similarity - 1.0;
        double value = -distance / temperature;
        double probability = Math.exp(value);

        return rand.nextDouble() <= probability;
    }
}
