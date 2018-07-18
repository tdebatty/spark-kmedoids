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

import info.debatty.java.datasets.tv.Sequence;
import info.debatty.spark.kmedoids.Similarity;
import spark.kmedoids.eval.tv.ParseSequenceFunction;
import spark.kmedoids.eval.tv.SequenceSimilarity;

/**
 *
 * @author Thibault Debatty
 */
public class TVVaryT0 extends AbstractVaryT0<Sequence> {

    public TVVaryT0(
            final String[] args,
            final Similarity<Sequence> similarity,
            final DatasetReader<Sequence> reader) {
        super(args, similarity, reader);
    }

    public static void main(final String[] args) throws Exception {
        TVVaryT0 test = new TVVaryT0(
                args,
                new SequenceSimilarity(),
                ((sc, dataset_path) -> {
                    return sc.textFile(dataset_path, 16)
                            .map(new ParseSequenceFunction());
                }));
        test.run();
    }
}
