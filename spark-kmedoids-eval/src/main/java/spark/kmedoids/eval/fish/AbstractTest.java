/*
 * The MIT License
 *
 * Copyright 2017 tibo.
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

import info.debatty.java.datasets.fish.TimeSerie;
import info.debatty.jinu.TestInterface;
import info.debatty.spark.kmedoids.Clusterer;
import info.debatty.spark.kmedoids.NeighborGenerotor;
import info.debatty.spark.kmedoids.Solution;
import info.debatty.spark.kmedoids.budget.SimilaritiesBudget;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author tibo
 */
public class AbstractTest implements TestInterface {

    public static String dataset_path;
    public static int parallelism;

    private final NeighborGenerotor<TimeSerie> neighbor_generator;

    /**
     *
     * @param neighbor_generator
     */
    public AbstractTest(final NeighborGenerotor<TimeSerie> neighbor_generator) {
        this.neighbor_generator = neighbor_generator;
    }

    @Override
    public final double[] run(final double budget) throws Exception {

        SparkConf conf = new SparkConf();
        conf.setAppName("Spark k-medoids for FISHES");
        conf.setIfMissing("spark.master", "local[*]");
        Solution<TimeSerie> solution;

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            JavaRDD<TimeSerie> data = sc.objectFile(dataset_path, parallelism);

            Clusterer<TimeSerie> clusterer = new Clusterer<>();
            clusterer.setK(10);
            clusterer.setSimilarity(new TimeSerieSimilarity());
            clusterer.setNeighborGenerator(neighbor_generator);
            clusterer.setBudget(new SimilaritiesBudget((long) budget));
            solution = clusterer.cluster(data);
        }

        return new double[]{solution.getTotalSimilarity()};

    }

}
