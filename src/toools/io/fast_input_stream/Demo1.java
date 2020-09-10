package toools.io.fast_input_stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Demo1 {
	public static void main(String[] args) throws IOException {
		InputStream bos = new ByteArrayInputStream(
				"38420827,8830abcde73e951dc712c80a91d2854352ac2ee5,Engineer Software Engineer,4a7f123a-a06a-4148-9e1d-97abf098b448,32,631152000,725846400,652147200,738892800,1990,1993,false,false,false,true,false,false\n10190060,8da5785bcd44ce5ffa7d5a470354df3fe9f63a2a,diplôme d'Ingénieur en Industrie Agroalimentaire,dc4f6bb6-7a94-492d-8656-fe09bda50693,32,1251763200,1338508800,1251763200,1338508800,2009,2012,false,false,true,false,false,true"
						.getBytes());

		PagingInputStream in = new PagingInputStream(bos, 4);

		while ( ! in.eof()) {
			System.out.print((char) in.read());
		}

		in.close();

	}
}
