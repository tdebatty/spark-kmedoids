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
 * A wrapper around a Similarity implementation that will also count the
 * number of computed similarities.
 *
 * Used only by the clusterer to keep trace of computed similarities.
 *
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered
 */
class CountingSimilarity<T> implements Similarity<T> {

    private long count = 0;
    private final Similarity<T> concrete_similarity;

    /**
     * Wraps the concrete similarity implementation to count the number of
     * computed similarities.
     *
     * @param concrete_similarity
     */
    CountingSimilarity(final Similarity<T> concrete_similarity) {
        this.concrete_similarity = concrete_similarity;
    }

    @Override
    public final double similarity(final T obj1, final T obj2) {
        count++;
        return concrete_similarity.similarity(obj1, obj2);
    }

    /**
     * Get the number of computed similarities.
     * @return
     */
    public final long getCount() {
        return count;
    }
}
