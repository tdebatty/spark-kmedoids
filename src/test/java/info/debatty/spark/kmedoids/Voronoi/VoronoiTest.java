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

package info.debatty.spark.kmedoids.Voronoi;

import info.debatty.spark.kmedoids.Config;
import info.debatty.spark.kmedoids.EuclideanSimilarity;
import info.debatty.spark.kmedoids.Solution;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author Thibault Debatty
 */
public class VoronoiTest extends TestCase {
    private JavaSparkContext sc;


    @Override
    protected void tearDown() throws Exception {
        sc.close();
        super.tearDown();
    }

    /**
     * Test of cluster method, of class Voronoi.
     */
    public void testCluster() {
        System.out.println("cluster");
        int k = 7;
        int dim = 3;

        SparkConf conf = new SparkConf();
        conf.setAppName("Voronoi test : cluster");
        conf.setIfMissing("spark.master", "local[*]");
        sc = new JavaSparkContext(conf);

        Random rand = new Random();

        List<double[]> data = new LinkedList<>();
        for (int i = 0; i < 1234; i++) {
            double[] vector = new double[dim];
            for (int j = 0; j < dim; j++) {
                vector[j] = rand.nextDouble();
            }
            data.add(vector);
        }

        // Parallelize the dataset in Spark
        JavaRDD<double[]> parallel_data = sc.parallelize(data);

        Config config = new Config(new EuclideanSimilarity(), k);

        Voronoi<double[]> voronoi = new Voronoi(config, sc);
        Solution solution = voronoi.cluster(parallel_data);
        System.out.println(solution);
    }

}
