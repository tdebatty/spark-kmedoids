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

/**
 *
 * @author Thibault Debatty
 */
public class L2Similarity implements Similarity<double[]> {

    /**
     * Compute traditional Euclidean distance.
     * @param value1
     * @param value2
     * @return
     */
    public final double distance(final double[] value1, final double[] value2) {
        double agg = 0;
        for (int i = 0; i < value1.length; i++) {
            agg += (value1[i] - value2[i]) * (value1[i] - value2[i]);
        }
        return Math.sqrt(agg);
    }

    /**
     * Compute similarity as 1 / (1 + distance).
     *
     * @param value1
     * @param value2
     * @return
     */
    @Override
    public final double similarity(
            final double[] value1, final double[] value2) {

        return 1.0 / (1 + distance(value1, value2));
    }
}
