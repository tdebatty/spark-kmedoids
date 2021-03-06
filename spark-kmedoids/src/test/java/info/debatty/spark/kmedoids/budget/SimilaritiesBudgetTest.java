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
package info.debatty.spark.kmedoids.budget;

import info.debatty.java.datasets.gaussian.Dataset;
import junit.framework.TestCase;
import info.debatty.spark.kmedoids.Clusterer;
import info.debatty.spark.kmedoids.L2Similarity;
import info.debatty.spark.kmedoids.Solution;
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
public class SimilaritiesBudgetTest extends TestCase {

    /**
     * Test of isExhausted method, of class SimilaritiesBudget.
     */
    public final void testIsExhausted() {

        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);

        SparkConf conf = new SparkConf();
        conf.setAppName("Spark k-medoids clusterer");
        conf.setIfMissing("spark.master", "local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        Dataset dataset = new Dataset.Builder(3, 10)
                .setOverlap(Dataset.Builder.Overlap.MEDIUM)
                .setSize(10000)
                .build();

        JavaRDD<double[]> data = sc.parallelize(dataset.getAll());

        Clusterer<double[]> clusterer = new Clusterer<>();
        clusterer.setK(10);
        clusterer.setSimilarity(new L2Similarity());
        clusterer.setNeighborGenerator(new ClaransNeighborGenerator<>());
        clusterer.setBudget(new SimilaritiesBudget(2000000));
        Solution<double[]> solution = clusterer.cluster(data);
        System.out.println(solution);
        sc.close();

        assertEquals(19, solution.getTrials());
    }

}
