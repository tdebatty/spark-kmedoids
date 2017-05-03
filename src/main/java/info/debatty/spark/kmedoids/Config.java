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
 * Basic clustering configuration: similarity + K.
 * @author Thibault Debatty
 * @param <T> Type of data that will be clustered
 */
public class Config<T> {

    private final int k;
    private final SimilarityInterface<T> similarity;

    /**
     *
     * @param similarity
     * @param k
     */
    public Config(
            final SimilarityInterface<T> similarity,
            final int k) {
        this.similarity = similarity;
        this.k = k;
    }

    /**
     * Get k (number of medoids).
     * @return
     */
    public final int getK() {
        return k;
    }

    /**
     *
     * @return
     */
    public final SimilarityInterface<T> getSimilarity() {
        return similarity;
    }

    /**
     * Compute and return the similarity between two objects.
     * @param object1
     * @param object2
     * @return
     */
    public final double similarity(final T object1, final T object2) {
        return similarity.similarity(object1, object2);
    }
}
