/*
 * The MIT License
 *
 * Copyright 2018 Thibault Debatty.
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
package spark.kmedoids.eval.sa;

import info.debatty.spark.kmedoids.L2Similarity;
import info.debatty.spark.kmedoids.Similarity;

/**
 *
 * @author Thibault Debatty
 */
public class SyntheticVaryGamma extends AbstractVaryGamma<double[]> {

    public SyntheticVaryGamma(
            final String[] args,
            final Similarity<double[]> similarity,
            final DatasetReader<double[]> reader) {
        super(args, similarity, reader);
    }

    public static void main(final String[] args) throws Exception {
        SyntheticVaryGamma test = new SyntheticVaryGamma(
                args,
                new L2Similarity(),
                ((sc, dataset_path) -> {
                    return sc.objectFile(dataset_path);
                }));
        test.run();
    }
}
