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

package info.debatty.spark.kmedoids.CLARANS;

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
public class ClaransTest extends TestCase {

    private JavaSparkContext sc;

    @Override
    protected void tearDown() throws Exception {
        if (sc != null) {
            sc.close();
        }
        super.tearDown();
    }

    /**
     * Test of getNeighborSolution method, of class Implementation.
     */
    public void testGetNeighborSolution() {
        System.out.println("getNeighborSolution");

        SparkConf conf = new SparkConf();
        conf.setAppName("CLARANS implementation test : getNeighborSolution");
        conf.setIfMissing("spark.master", "local[*]");
        sc = new JavaSparkContext(conf);

        Random rand = new Random();
        List<Double> data = new LinkedList<>();
        for (int i = 0; i < 1234; i++) {
            data.add(rand.nextDouble());
        }
        JavaRDD<Double> parallel_data = sc.parallelize(data);

        List<Double> initial_medoids = new LinkedList<>();
        for (int i = 0; i < 7; i++) {
            initial_medoids.add(data.get(i));
        }


        Clarans clarans = new Clarans(null, sc);

        List<Double> neighbor_solution = clarans.getNeighborSolution(
                initial_medoids, parallel_data);

        neighbor_solution.removeAll(initial_medoids);
        assertEquals(1, neighbor_solution.size());
    }

    /**
     * Test of sample method, of class Implementation.
     */
    public void testSample() {
        System.out.println("sample");

        SparkConf conf = new SparkConf();
        conf.setAppName("CLARANS implementation test : sample");
        conf.setIfMissing("spark.master", "local[*]");
        sc = new JavaSparkContext(conf);

        Random rand = new Random();

        List<Double> data = new LinkedList<>();
        for (int i = 0; i < 1234; i++) {
            data.add(rand.nextDouble());
        }

        // Parallelize the dataset in Spark
        JavaRDD<Double> parallel_data = sc.parallelize(data);
        assertEquals(1234, parallel_data.count());

        Clarans clarans = new Clarans(null, sc);
        List sample = clarans.sample(parallel_data, 7);

        assertEquals(7, sample.size());
    }

    /**
     * Test of cluster method, of class Clarans.
     */
    public final void testCluster() {
        System.out.println("cluster");

        int k = 7;
        int dim = 3;

        SparkConf conf = new SparkConf();
        conf.setAppName("CLARANS test : cluster");
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

        Config config = new Config(new EuclideanSimilarity(), k, 5, 5);

        Clarans clarans = new Clarans(config, sc);
        Solution solution = clarans.cluster(parallel_data);
        System.out.println(solution);

    }

}
