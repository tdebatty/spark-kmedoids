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

import info.debatty.java.datasets.gaussian.Dataset;
import junit.framework.TestCase;
import info.debatty.spark.kmedoids.budget.TrialsBudget;
import info.debatty.spark.kmedoids.neighborgenerator.ClaransNeighborGenerator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author Thibault Debatty
 */
public class ClustererTest extends TestCase {

    /**
     * Test of cluster method, of class Clusterer.
     */
    public final void testCluster() {

        System.out.println("Test cluster");
        System.out.println("============");

        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);

        SparkConf conf = new SparkConf();
        conf.setAppName("Spark k-medoids clusterer");
        conf.setIfMissing("spark.master", "local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        Dataset dataset = new Dataset.Builder(3, 10)
                .setOverlap(Dataset.Builder.Overlap.MEDIUM)
                .setSize(100000)
                .build();

        JavaRDD<double[]> data = sc.parallelize(dataset.getAll());

        Clusterer<double[]> clusterer = new Clusterer<>();
        clusterer.setK(10);
        clusterer.setSimilarity(new L2Similarity());
        clusterer.setNeighborGenerator(new ClaransNeighborGenerator<>());
        clusterer.setBudget(new TrialsBudget(20));
        System.out.println(clusterer.cluster(data));
        sc.close();
    }

    public final void testImbalance() {

        System.out.println("Test imbalance");
        System.out.println("==============");

        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);

        SparkConf conf = new SparkConf();
        conf.setAppName("Spark balanced k-medoids clusterer");
        conf.setIfMissing("spark.master", "local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        Dataset dataset = new Dataset.Builder(3, 10)
                .setOverlap(Dataset.Builder.Overlap.MEDIUM)
                .setSize(100000)
                .build();

        JavaRDD<double[]> data = sc.parallelize(dataset.getAll());

        Clusterer<double[]> clusterer = new Clusterer<>();
        clusterer.setK(10);
        clusterer.setSimilarity(new L2Similarity());
        clusterer.setNeighborGenerator(new ClaransNeighborGenerator<>());
        clusterer.setBudget(new TrialsBudget(20));
        clusterer.setImbalance(1.0); // Perfectly balanced!
        System.out.println(clusterer.cluster(data));
        sc.close();
    }

}
