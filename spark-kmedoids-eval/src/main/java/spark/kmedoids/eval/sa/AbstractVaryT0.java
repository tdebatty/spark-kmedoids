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
package spark.kmedoids.eval.sa;

import info.debatty.jinu.Case;
import info.debatty.jinu.TestInterface;
import java.util.Arrays;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import info.debatty.spark.kmedoids.Clusterer;
import info.debatty.spark.kmedoids.Similarity;
import info.debatty.spark.kmedoids.Solution;
import info.debatty.spark.kmedoids.budget.SimilaritiesBudget;
import info.debatty.spark.kmedoids.neighborgenerator.SANeighborGenerator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author Thibault Debatty
 * @param <T> Type of processed data
 */
public class AbstractVaryT0<T>  {

    private final Case test = new Case();


    /**
     * @param args the command line arguments
     * @param similarity
     * @param reader
     */
    public AbstractVaryT0(
            final String[] args,
            final Similarity<T> similarity,
            final DatasetReader<T> reader) {

        OptionParser parser = new OptionParser("d:s:r:t:g:");
        OptionSet options = parser.parse(args);

        long similarities = Long.valueOf((String) options.valueOf("s"));
        double gamma = Double.valueOf((String) options.valueOf("g"));
        String dataset_path = (String) options.valueOf("d");

        List<String> t0s_list = (List<String>) options.valuesOf("t");
        double[] t0s = new double[t0s_list.size()];
        for (int i = 0; i < t0s.length; i++) {
            t0s[i] = Double.valueOf(t0s_list.get(i));
        }

        // Reduce Spark output logs
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("info.debatty.spark.kmedoids").setLevel(Level.INFO);

        test.setDescription(AbstractVaryT0.class.getName() + " : "
                + String.join(" ", Arrays.asList(args)));
        test.setIterations(20);
        test.setParallelism(1);
        test.commitToGit(false);
        test.setBaseDir((String) options.valueOf("r"));
        test.setParamValues(t0s);

        test.addTest(() -> new VaryT0Test<>(
                dataset_path, gamma, similarities, similarity, reader));

    }

    /**
     * Run the test.
     * @throws Exception if something goes wrong
     */
    public final void run() throws Exception {
        test.run();
    }
}

/**
 * Actual test.
 *
 * @author tibo
 * @param <T>
 */
class VaryT0Test<T> implements TestInterface {

    private final String dataset_path;
    private final double gamma;
    private final long similarities;
    private final Similarity<T> similarity;
    private final DatasetReader<T> reader;

    VaryT0Test(
            final String dataset_path,
            final double gamma,
            final long similarities,
            final Similarity<T> similarity,
            final DatasetReader<T> reader) {

        this.dataset_path = dataset_path;
        this.gamma = gamma;
        this.similarities = similarities;
        this.similarity = similarity;
        this.reader = reader;
    }

    @Override
    public final double[] run(final double t0) throws Exception {

        SparkConf conf = new SparkConf();
        conf.setAppName("Spark k-medoids clusterer");
        conf.setIfMissing("spark.master", "local[*]");
        Solution<T> solution;

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            JavaRDD<T> data = reader.readData(sc, dataset_path);

            Clusterer<T> clusterer = new Clusterer<>();
            clusterer.setK(10);
            clusterer.setSimilarity(similarity);
            clusterer.setNeighborGenerator(
                    new SANeighborGenerator<>(t0, gamma));
            clusterer.setBudget(new SimilaritiesBudget(similarities));
            solution = clusterer.cluster(data);
        }

        return new double[]{solution.getTotalSimilarity()};
    }
}