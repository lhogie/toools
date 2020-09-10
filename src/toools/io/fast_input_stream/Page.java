package toools.io.fast_input_stream;

public class Page {
	public final byte[] buf;
	public int len;
	public int cursor;

	public Page(int pageSize) {
		buf = new byte[pageSize];
	}

	public int available() {
		return len - cursor;
	}

	public byte next() {
		if (available() == 0) {
			throw new IllegalStateException("End of page");
		}

		return buf[cursor++];
	}

}
