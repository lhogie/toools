package toools.src;

public class PositionInSource {

	private String filename;
	private int line;

	public PositionInSource(String filename, int line) {
		this.filename = filename;
		this.line = line;
	}

	@Override
	public String toString() {
		return filename + ":" + line;
	}
}
