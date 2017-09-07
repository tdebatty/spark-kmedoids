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
 *
 * @author Thibault Debatty
 */
public class XY implements Comparable<XY> {
    private final double x;
    private final double y;

    /**
     *
     * @param x
     * @param y
     */
    public XY(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return
     */
    public final double getX() {
        return x;
    }

    /**
     *
     * @return
     */
    public final double getY() {
        return y;
    }



    @Override
    public final int compareTo(final XY other) {
        if (this.x == other.x) {
            return 0;
        }

        if (this.x > other.x) {
            return 1;
        }

        return -1;
    }

    @Override
    public final String toString() {
        return "[" + x + ";\t" + y + "]";
    }
}
