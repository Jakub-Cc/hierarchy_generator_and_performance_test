package hierarchy_generator_wrap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hctools.GenericHCTreeModel;
import hctools.kernels.IRVKernel;

public class Runner {
	private static final Logger log = LogManager.getLogger(Runner.class);

	public static void main(String[] args) {

		/// 1000 ,2000, 5000, 10000,20000,50000, 1000000
		int[] ns = new int[] { 10000, 20000, 50000, 1000000 };
		int[] ds = new int[] { 2, 5, 10, 25, 50, 100, 250, 500, 1000 };
		double[] alpha0s = new double[] { 1, 5 };
		double[] lambdas = new double[] { 1, 0.5 };
		double[] gammas = new double[] { 0.2 };
		double[] ps = new double[] { 1 };
		double[] qs = new double[] { 5 };
		double[] minSds = new double[] { 0.05 };
		double[] maxSds = new double[] { 10.0 };
		int repeats = 1;

		for (int n : ns)
			for (int d : ds)
				for (double alpha0 : alpha0s)
					for (double lambda : lambdas)
						for (double gamma : gammas)
							for (double p : ps)
								for (double q : qs)
									for (double minSd : minSds)
										for (double maxSd : maxSds)
											for (int i = 0; i < repeats; i++) {
												String basicFileName = "N-" + n + "_D-" + d + "_A-" + alpha0 + "_L-"
														+ lambda + "_G-" + gamma + "_P-" + p + "_Q-" + q + "_MIN-"
														+ minSd + "_MAX-" + maxSd;
												try {
													if (!(alpha0 == 25.0 && lambda == 1.0 && gamma == 1.0))
														run(basicFileName, n, d, alpha0, lambda, gamma, p, q, minSd,
																maxSd, i);
												} catch (OutOfMemoryError e) {
													log.error("OutOfMemoryError-i:{}- {}", i, basicFileName);
												} catch (IOException e) {
													log.error(e.getMessage(), e);
												}
											}
		log.info("DONE!");
	}

	static void run(String modelName, int n, int d, double alpha0, double lambda, double gamma, double p, double q,
			double minSd, double maxSd, int iterI) throws IOException {

		log.info("Generating dataset... {}: {}", iterI, modelName);
		GenericHCTreeModel genericHCTreeModel = new GenericHCTreeModel(alpha0, lambda, gamma,
				new IRVKernel(d, p, q, minSd, maxSd, false, false));
		genericHCTreeModel.Populate(n);
		genericHCTreeModel.Root.ResetClassesToAuto();
		log.info("Generating {}: {} : [DONE]. Started Saving.", iterI, modelName);

		genericHCTreeModel.SaveData(crateFile(n, d, modelName, iterI).getAbsolutePath());
		log.info("Saving {}: {} : [DONE] ", iterI, modelName);
	}

	private static File crateFile(int n, int d, String basicName, int i) throws IOException {
		Path dir = crateOutPath(n, d);
		for (int iter = i;; iter++) {
			String outFileName = basicName + "_" + iter + ".csv";
			Path expected = Paths.get(dir.toString(), outFileName);
			if (!expected.toFile().exists())
				return expected.toFile();
		}
	}

	private static Path crateOutPath(int n, int d) throws IOException {
		Path expected = Paths.get("out", +n + "_" + d);
		if (!expected.toFile().exists()) {
			return Files.createDirectory(expected);
		}
		return expected;
	}
}
