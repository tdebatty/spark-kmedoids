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
import java.util.List;

/**
 *
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered
 */
public interface NeighborGenerotor<T> {

    /**
     * Create a neighbor candidate solution.
     *
     * @param helper
     * @param current_solution
     * @param similarity
     * @return
     * @throws info.debatty.spark.kmedoids.NoNeighborFoundException if we
     * could not find a neighbor solution
     */
    ArrayList<T> getNeighbor(
            NeighborGeneratorHelper<T> helper,
            Solution<T> current_solution,
            Similarity<T> similarity) throws NoNeighborFoundException;

    /**
     * Initialize the generator, before we start computing.
     * @param k
     */
    void init(final int k);

    /**
     * Inform the generator what the cost of the current solution is.
     * @param solution
     * @param cost
     */
    void notifyCandidateSolutionCost(
            final List<T> solution, final double cost);

    /**
     * Notify the generator that the new solution is better then the previous
     * one.
     * @param solution
     * @param cost
     */
    void notifyNewSolution(
            final List<T> solution, final double cost);
}
