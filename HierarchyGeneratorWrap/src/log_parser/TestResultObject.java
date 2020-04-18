package log_parser;

public class TestResultObject {

	String fileName;
	String time;

	public TestResultObject(String fileName, String time) {
		if (fileName == null || time == null) {
			throw new IllegalArgumentException("parameters Can not be null");
		}

		this.fileName = fileName;
		this.time = time;
	}

	String getFileName() {
		return fileName;
	}

	String getTime() {
		return time;
	}
}
