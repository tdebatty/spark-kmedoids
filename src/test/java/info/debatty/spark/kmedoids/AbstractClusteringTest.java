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

import info.debatty.spark.kmedoids.CLARANS.Clarans;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author Thibault Debatty
 */
public class AbstractClusteringTest extends TestCase {
    private JavaSparkContext sc;

    @Override
    protected void tearDown() throws Exception {
        sc.close();
        super.tearDown();
    }



        /**
     * Test of totalSimilarity method, of class Implementation.
     */
    public final void testTotalSimilarity() {
        System.out.println("totalSimilarity");

        SparkConf conf = new SparkConf();
        conf.setAppName("CLARANS implementation test : totalSimilarity");
        conf.setIfMissing("spark.master", "local[*]");
        sc = new JavaSparkContext(conf);

        Random rand = new Random();

        List<Double> data = new LinkedList<>();
        for (int i = 0; i < 1234; i++) {
            data.add(rand.nextDouble());
        }

        // Parallelize the dataset in Spark
        JavaRDD<Double> parallel_data = sc.parallelize(data);

        int k = 7;
        SimilarityInterface<Double> similarity = new DummySimilarity();
        info.debatty.spark.kmedoids.CLARANS.Config<Double> config = new info.debatty.spark.kmedoids.CLARANS.Config<>(similarity, k, 10, 10);
        AbstractClustering<Double> clarans = new Clarans<>(config, sc);
        clarans.setSolution(new Solution<>(config, parallel_data));

        List<Double> initial_medoids = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            initial_medoids.add(data.get(i));
        }


        double total_similarity = clarans.totalSimilarity(
                initial_medoids, parallel_data);

        assertEquals(1234.0, total_similarity);

    }

}
