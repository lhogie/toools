package toools.io.fast_input_stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Demo_pages {
	public static void main(String[] args) throws IOException {
		byte[] csv = "Hey how are you doing?".getBytes();

		InputStream bos = new ByteArrayInputStream(csv);
		PagingInputStream in = new PagingInputStream(bos, 3);

		for (Page page : in) {
			System.out.print(page.available() + ": ");

			while (page.available() > 0) {
				byte b = page.next();
				System.out.print((char) b);
			}

			System.out.println();
		}

		in.close();

	}
}
