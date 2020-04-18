package hierarchy_generator_wrap;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HkRunner {
	private static final Logger log = LogManager.getLogger(HkRunner.class);

	public static void main(String[] args) {
		int repats = 30;
		String folderPath = "out";

		// disableStreams();

		for (String filePath : getFiles(folderPath)) {
			for (int i = 0; i < repats; i++) {
				try {
					System.out.println(filePath);
					long runTime = runHk(createArgs(filePath));
					log.info("{} - {}", filePath, runTime);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private static void disableStreams() {

		System.setOut(new PrintStream(new OutputStream() {
			public void close() {
			}

			public void flush() {
			}

			public void write(byte[] b) {
			}

			public void write(byte[] b, int off, int len) {
			}

			public void write(int b) {
			}
		}));

	}

	private static List<String> getFiles(String folderPath) {
		File file = Paths.get(folderPath).toFile();
		Collection<File> c = FileUtils.listFiles(file, null, true);
		return c.stream().map(File::getPath).collect(Collectors.toList());
	}

	private static long runHk(String[] args) {
		long start = System.nanoTime();
		runner.Main.main(args);
		long stop = System.nanoTime();
		return (stop - start);
	}

	private static String[] createArgs(String filePath) {

		return new String[] { "-lgmm", "-cf", "1.0", "-rf", "1.0", "-e", "1", "-l", "1", "-k", "2", "-n", "10", "-r",
				"10", "-s", "2", "-w", "2147483600", "-c", "-i", filePath, "-o", "hk" };
	}
}
