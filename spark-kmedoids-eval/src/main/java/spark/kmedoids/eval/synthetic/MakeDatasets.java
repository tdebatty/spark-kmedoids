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
package spark.kmedoids.eval.synthetic;

import info.debatty.java.datasets.gaussian.Dataset;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author Thibault Debatty
 */
public class MakeDatasets {

    public static void main(final String[] args) {
        SparkConf conf = new SparkConf();
        conf.setAppName("Spark make datasets");
        conf.setIfMissing("spark.master", "local[*]");

        JavaSparkContext sc = new JavaSparkContext(conf);

        Dataset dataset =  new Dataset.Builder(3, 100)
                .setOverlap(Dataset.Builder.Overlap.HIGH)
                .setSize(10000)
                .build();
        sc.parallelize(dataset.getAll())
                .saveAsObjectFile("datasets/gaussian_3_100_10000");

        dataset =  new Dataset.Builder(10, 12)
                .setOverlap(Dataset.Builder.Overlap.HIGH)
                .setSize(10000)
                .build();
        sc.parallelize(dataset.getAll())
                .saveAsObjectFile("datasets/gaussian_10_12_10000");


        dataset =  new Dataset.Builder(3, 12)
                .setOverlap(Dataset.Builder.Overlap.HIGH)
                .setSize(100000)
                .build();
        sc.parallelize(dataset.getAll())
                .saveAsObjectFile("datasets/gaussian_3_12_100000");

        dataset =  new Dataset.Builder(10, 12)
                .setOverlap(Dataset.Builder.Overlap.HIGH)
                .setSize(100000)
                .build();
        sc.parallelize(dataset.getAll())
                .saveAsObjectFile("datasets/gaussian_10_12_100000");


        dataset =  new Dataset.Builder(3, 100)
                .setOverlap(Dataset.Builder.Overlap.HIGH)
                .setSize(100000)
                .build();
        sc.parallelize(dataset.getAll())
                .saveAsObjectFile("datasets/gaussian_3_100_100000");
    }
}
