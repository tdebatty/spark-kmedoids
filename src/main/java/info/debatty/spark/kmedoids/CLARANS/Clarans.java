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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered
 */
public class Clarans<T> extends AbstractClarans<T> {

    private final Random rand = new Random();

    /**
     * {@inherit}.
     *
     * @param conf
     * @param sc
     */
    public Clarans(final Config<T> conf, final JavaSparkContext sc) {
        super(conf, sc);
    }

    @Override
    public final List<T> getNeighborSolution(
            final List<T> medoids, final JavaRDD<T> data) {
        // Make a copy
        List<T> neighbor_solution = new ArrayList<>(medoids);

        // Select a medoid from the current solution
        int position = rand.nextInt(medoids.size());

        T sample;
        do {
            // Select a medoid from the dataset
            sample = sample(data, 1).get(0);
        } while (neighbor_solution.contains(sample));

        neighbor_solution.set(position, sample);
        return neighbor_solution;
    }
}
