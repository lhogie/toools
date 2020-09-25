package toools.io.fast_input_stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Demo_readLine2 {
	public static void main(String[] args) throws IOException {
		byte[] text = "Hey!\nThis is the second line.\nAre you sure?\nDefinitely."
				.getBytes();

		InputStream bos = new ByteArrayInputStream(text);
		PagingInputStream in = new PagingInputStream(bos);

		for (String line : in.lines()) {
			System.out.println(line);
		}

		in.close();

	}
}
