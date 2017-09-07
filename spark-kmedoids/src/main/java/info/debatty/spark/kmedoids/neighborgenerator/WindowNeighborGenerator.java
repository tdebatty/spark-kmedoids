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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import info.debatty.spark.kmedoids.NeighborGeneratorHelper;
import info.debatty.spark.kmedoids.NeighborGenerotor;
import info.debatty.spark.kmedoids.Similarity;
import info.debatty.spark.kmedoids.Solution;

/**
 *
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered
 */
public class WindowNeighborGenerator<T> implements NeighborGenerotor<T> {

    private static final double EXPANSION_COEFFICIENT = 1.001;

    private final Random rand = new Random();
    private int medoid_position;
    private double last_distance;
    private double current_solution_cost;
    private Bin[] bins;

    @Override
    public final ArrayList<T> getNeighbor(
            final NeighborGeneratorHelper<T> helper,
            final Solution<T> current_solution,
            final Similarity<T> similarity) {

        ArrayList<T> new_solution = new ArrayList<>(
                current_solution.getMedoids());
        medoid_position = rand.nextInt(current_solution.getMedoids().size());
        T old_medoid = current_solution.getMedoids().get(medoid_position);
        double threshold = bins[medoid_position].threshold();

        while (true) {

            T new_medoid = helper.getRandomPoint();
            if (old_medoid.equals(new_medoid)) {
                continue;
            }
            double sim = similarity.similarity(new_medoid, old_medoid);
            double distance = 1.0 / sim - 1.0;

            if (distance > threshold) {
                // try another medoid!
                threshold *= EXPANSION_COEFFICIENT;
                continue;
            }

            last_distance = distance;
            new_solution.set(medoid_position, new_medoid);
            return new_solution;
        }
    }

    /**
     *
     * @param solution
     * @param cost
     */
    @Override
    public final void notifyNewSolution(
            final List<T> solution, final double cost) {

        current_solution_cost = cost;
        bins[medoid_position] = new Bin();
    }

    /**
     *
     * @param solution
     * @param cost
     */
    @Override
    public final void notifyCandidateSolutionCost(
            final List<T> solution, final double cost) {
        if (current_solution_cost == 0) {
            return;
        }

        double delta_cost = cost - current_solution_cost;
        XY point = new XY(last_distance, delta_cost);
        //System.out.println(point);
        bins[medoid_position].add(point);

    }

    /**
     *
     * @param k
     */
    @Override
    public final void init(final int k) {
        bins = new Bin[k];
        for (int i = 0; i < k; i++) {
            bins[i] = new Bin();
        }
    }
}
