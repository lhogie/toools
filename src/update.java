/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 
Aurelien Lancin (Coati research team, Inria)
Christian Glacet (LaBRi, Bordeaux)
David Coudert (Coati research team, Inria)
Fabien Crequis (Coati research team, Inria)
Grégory Morel (Coati research team, Inria)
Issam Tahiri (Coati research team, Inria)
Julien Fighiera (Aoste research team, Inria)
Laurent Viennot (Gang research-team, Inria)
Michel Syska (I3S, Université Cote D'Azur)
Nathann Cohen (LRI, Saclay) 
Julien Deantoin (I3S, Université Cote D'Azur, Saclay) 

*/

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class update {
	public static void main(String[] args) throws IOException {
		System.out.println(args[0]);
		retrieveURLContent(new URL(getURL()));
	}

	private static String getURL() throws IOException {
		StringBuilder s = new StringBuilder();
		InputStream is = update.class.getResourceAsStream("url.txt");

		while (true) {
			int i = is.read();

			if (i == - 1) {
				break;
			}
			else {
				s.append((char) i);
			}
		}

		return s.toString();
	}

	public static byte[] retrieveURLContent(URL url) throws IOException {
		InputStream is = url.openConnection().getInputStream();
		byte[] bytes = toools.io.Utilities.readUntilEOF(is);
		is.close();
		return bytes;
	}

	public static byte[] readUntilEOF(InputStream is) throws IOException {
		if (is == null)
			throw new NullPointerException("null stream");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copy(is, bos);
		return bos.toByteArray();
	}

	public static void copy(InputStream is, OutputStream bos) throws IOException {
		if (is == null)
			throw new NullPointerException();

		if (bos == null)
			throw new NullPointerException();

		byte[] bytes = new byte[4 * 1024];

		while (true) {
			int i = is.read(bytes);

			if (i == - 1) {
				break;
			}
			else {
				bos.write(bytes, 0, i);
			}
		}
	}
}
