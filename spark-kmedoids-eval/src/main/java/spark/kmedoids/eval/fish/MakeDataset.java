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
package spark.kmedoids.eval.fish;

import info.debatty.java.datasets.fish.Dataset;
import info.debatty.java.datasets.fish.Image;
import info.debatty.java.datasets.fish.TimeSerie;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author Thibault Debatty
 */
public class MakeDataset {

    /**
     *
     * @param args
     */
    public static void main(final String[] args) {
        SparkConf conf = new SparkConf();
        conf.setAppName("Spark make fish dataset");
        conf.setIfMissing("spark.master", "local[*]");

        JavaSparkContext sc = new JavaSparkContext(conf);

        Dataset dataset =  new Dataset();
        LinkedList<Image> fishes = dataset.getAll();
        List<TimeSerie> time_series_0 = Dataset.extractTimeSeries(fishes, 0);
        List<TimeSerie> time_series_1 = Dataset.extractTimeSeries(fishes, 1);

        sc.parallelize(time_series_0)
                .saveAsObjectFile("datasets/fish-timeseries-0");
        sc.parallelize(time_series_1)
                .saveAsObjectFile("datasets/fish-timeseries-1");
    }
}
