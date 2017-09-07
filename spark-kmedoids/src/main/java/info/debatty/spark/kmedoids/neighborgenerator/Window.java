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

/**
 * A gliding window for computing the average of a fixed number of values.
 *
 * The initial average is Double.MAX_VALUE.
 *
 * @author Thibault Debatty
 */
public class Window {

    private final double[] values;
    private int current_position = 0;

    /**
     * A gliding window for computing the average of a fixed number of values.
     * @param size
     */
    public Window(final int size) {
        values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = Double.MAX_VALUE;
        }
    }

    /**
     * Add a value to the window, maybe discarding a previous value.
     * @param value
     */
    public final void add(final double value) {
        values[current_position] = value;
        current_position = (current_position + 1) % values.length;
    }

    /**
     * Compute the average of values currently in the window.
     * @return
     */
    public final double average() {
        double agg = 0;
        for (double value : values) {
            agg += value;
        }

        return agg / values.length;
    }
}
