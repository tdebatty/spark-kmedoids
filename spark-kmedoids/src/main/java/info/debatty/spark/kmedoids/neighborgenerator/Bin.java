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

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Thibault Debatty
 */
public class Bin {
    private static final int SIZE = 3;
    private final SortedSet<XY> points = new TreeSet<>();

    /**
     * Add a point to this bin.
     * @param point
     */
    public final void add(final XY point) {
        this.points.add(point);
    }

    /**
     *
     * @return
     */
    public final double threshold() {
        if (points.size() < SIZE) {
            return Double.MAX_VALUE;
        }

        double min_average = Double.MAX_VALUE;
        double threshold = 0;

        Window w = new Window(SIZE);

        for (XY point : points) {
            w.add(point.getY());
            double average = w.average();
            if (average <= min_average) {
                min_average = average;
                threshold = point.getX();
            }
        }

        return threshold;
    }
}
