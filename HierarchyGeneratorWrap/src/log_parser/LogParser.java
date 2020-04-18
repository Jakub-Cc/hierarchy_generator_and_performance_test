package log_parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogParser {
	private static final Logger log = LogManager.getLogger(LogParser.class);

	private Map<String, List<String>> testResults;

	public static void main(String[] args) {
		LogParser logParser = new LogParser();
		logParser.parse(
				"C:\\Users\\JakubC\\git\\hierarchy_generator_and_performance_test\\HierarchyGeneratorWrap\\log\\traceHK2.out",
				"C:\\Users\\JakubC\\git\\hierarchy_generator_and_performance_test\\res.csv");

	}

	public void parse(String inputPath, String outptString) {
		readFile(inputPath);
		saveToCSV(outptString);
	}

	public LogParser() {
		testResults = new HashMap<>();
	}

	private void readFile(String filePath) {
		File inputFile = new File(filePath);
		try (Scanner myReader = new Scanner(inputFile);) {
			while (myReader.hasNextLine()) {
				TestResultObject resultObject = parseLine(myReader.nextLine());
				addToResults(resultObject);
			}

		} catch (FileNotFoundException e) {
			log.error("An error occurred.", e);
		}
	}

	private TestResultObject parseLine(String data) {
		String[] result = data.split(" - ");
		return new TestResultObject(result[0], result[1]);
	}

	private void addToResults(TestResultObject testResultObject) {
		String key = testResultObject.getFileName();
		if (testResults.containsKey(key)) {
			testResults.get(key).add(testResultObject.getTime());
		} else {
			testResults.put(key, new ArrayList<String>(Arrays.asList(testResultObject.getTime())));
		}
	}

	private void saveToCSV(String path) {
		File csvOutputFile = new File(path);

		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			pw.println("N;D;A;L;G;P;Q;MIN;MAX;AVR;MED;VAR");
			for (Entry<String, List<String>> entry : testResults.entrySet()) {
				String parsed = parseResult(entry);
				pw.println(parsed);
			}
		} catch (FileNotFoundException e) {
			log.error(e);
		}

	}

	private String parseResult(Entry<String, List<String>> entry) {
		StringBuilder stringBuilder = new StringBuilder();

		String fileName = entry.getKey().substring(entry.getKey().lastIndexOf('\\'));
		for (String s : fileName.split("_")) {
			String[] param = s.split("-");
			if (param.length > 1)
				stringBuilder.append(param[1] + ";");
		}
		stringBuilder.append(calcStatic(entry.getValue()));
		for (String time : entry.getValue()) {
			stringBuilder.append(time + ";");
		}
		return stringBuilder.toString();
	}

	private String calcStatic(List<String> times) {

		double[] longTimes = times.stream().mapToDouble(e -> Double.valueOf(e)).toArray();

		double avr = 0l;
		for (double time : longTimes) {
			avr += time / times.size();
		}

		Median median = new Median();
		double med = median.evaluate(longTimes);

		Variance variance = new Variance();
		double var = variance.evaluate(longTimes);
		return avr + ";" + med + ";" + var + ";";
	}
}
