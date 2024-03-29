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

package toools.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import toools.collections.Maps;
import toools.exceptions.CodeShouldNotHaveBeenReachedException;
import toools.math.MathsUtilities;

public class Utilities {

	public static void grabLines(InputStream in, Consumer<String> newLineConsumer, Consumer<IOException> error) {
		Thread t = new Thread(() -> {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(in));

				while (true) {
					String line = r.readLine();

					// EOF
					if (line == null) {
						r.close();
						break;
					}

					newLineConsumer.accept(line);
				}
			} catch (IOException e) {
				error.accept(e);
			}
		});

//		t.setDaemon(true);
		t.start();
	}

	public BigInteger md5(File f) throws IOException, NoSuchAlgorithmException {
		byte[] data = Files.readAllBytes(f.toPath());
		byte[] hash = MessageDigest.getInstance("MD5").digest(data);
		return new BigInteger(1, hash);
	}

	public static Map<String, String> csv2map(String s) {
		s.replace(',', '\n');
		var p = new Properties();
		try {
			p.load(new StringReader(s));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		var m = new HashMap<String, String>();

		for (var e : p.entrySet()) {
			m.put((String) e.getKey(), (String) e.getValue());
		}

		return m;
	}

	public static byte getNbBytesRequireToEncode(long n) {
		if (n < 256)
			return 1;
		else if (n < 65536)
			return 2;
		else if (n < 16777216)
			return 3;
		else if (n < 4294967296L)
			return 4;
		else if (n < 1099511627776L)
			return 5;
		else if (n < 281474976710656L)
			return 6;
		else if (n < 72057594037927936L)
			return 7;
		else
			return 8;
	}

	public long getOffsetofFirstDifference(InputStream a, InputStream b) throws IOException {
		for (long i = 0;; ++i) {
			int ia = a.read();
			int ib = b.read();

			if (ia != ib) {
				return i;
			} else if (ia == -1) {
				return i;
			}
		}
	}

	public static boolean operatingSystemIsUNIX() {
		return new File("/etc/passwd").exists();
	}

	public static Map<String, String> loadPropertiesToMap(String text) {
		try {
			Properties properties = new Properties();
			properties.load(new ByteArrayInputStream(text.getBytes()));
			return Maps.propertiesToMap(properties);
		} catch (IOException e) {
			throw new CodeShouldNotHaveBeenReachedException();
		}
	}

	public static byte[] gzip(byte[] data) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(bos);
			gos.write(data);
			gos.close();
			return bos.toByteArray();
		} catch (IOException ex) {
			throw new IllegalStateException();
		}
	}

	public static byte[] gunzip(byte[] data) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gis = new GZIPInputStream(bis);
			byte[] uncompressedData = readUntilEOF(gis);
			return uncompressedData;
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static class ReadUntilResult {
		public final ByteList bytes = new ByteArrayList();
		public boolean eof;
	}

	public static ReadUntilResult readUntil(InputStream is, byte endChar) throws IOException {
		if (is == null)
			throw new NullPointerException("null stream");

		ReadUntilResult r = new ReadUntilResult();

		while (true) {
			int i = is.read();

			if (i == -1) {
				r.eof = true;
				return r;
			}

			if (i == endChar) {
				return r;
			}

			r.bytes.add((byte) i);
		}
	}

	public static byte[] readUntilEOF(InputStream is) {
		if (is == null)
			throw new NullPointerException("null stream");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copy(is, bos);
		return bos.toByteArray();
	}

	public static void copy(InputStream is, OutputStream bos) {
		if (is == null)
			throw new NullPointerException();

		if (bos == null)
			throw new NullPointerException();

		byte[] buffer = new byte[1024 * 1024];

		try {
			while (true) {
				int nbBytesRead = is.read(buffer);

				if (nbBytesRead == -1) {
					break;
				} else {
					bos.write(buffer, 0, nbBytesRead);
				}
			}
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public static void pressEnterToContinue() {
		Utilities.readUserInput("Press enter to continue", ".*");
	}

	public static void pressEnterToContinue(String msg) {
		Utilities.readUserInput(msg, ".*");
	}

	public static String readUserInput(String invitation, String regexp) {
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				if (invitation != null) {
					System.out.print(invitation);
				}

				String answer = stdin.readLine();

				if (regexp == null || answer == null || answer.matches(regexp)) {
					return answer;
				} else {
					System.err.println("Input error, the string must match " + regexp + "\n");
				}
			}
		} catch (IOException e) {
			System.err.println("Error while reading user input");
			return null;
		}
	}

	public static InputStream ensureBufferred(InputStream is) {
		return is instanceof BufferedInputStream ? is : new BufferedInputStream(is);
	}

	public static Reader ensureBufferred(Reader r) {
		return r instanceof BufferedReader ? r : new BufferedReader(r);
	}

	public static OutputStream ensureBufferred(OutputStream os) {
		return os instanceof BufferedOutputStream ? os : new BufferedOutputStream(os);
	}

	public static Writer ensureBufferred(Writer w) {
		return w instanceof BufferedWriter ? w : new BufferedWriter(w);
	}

	public static <E> E select(String prompt, E... choices) {
		return select(prompt, Arrays.asList(choices));
	}

	public static <E> E select(String prompt, List<E> choices) {
		if (choices.isEmpty()) {
			throw new IllegalArgumentException("No choice possible!");
		} else if (choices.size() == 1) {
			System.out.println("Only 1 choice available: " + choices.get(0));
			return choices.get(0);
		} else {
			for (int i = 0; i < choices.size(); ++i) {
				System.out.println((i + 1) + ") " + choices.get(i));
			}

			while (true) {
				String in = readUserInput("#? ", ".+");

				if (MathsUtilities.isNumber(in)) {
					int n = Integer.valueOf(in);
					return choices.get(n - 1);
				} else {
					for (E c : choices) {
						if (c.toString().equals(in)) {
							return c;
						}
					}

					System.err.println("'" + in + "' is not an valid answser");
				}
			}
		}
	}

	public static int skipUntilEndOfLine(InputStream is) throws IOException {
		int nbRead = 0;

		while (true) {
			int c = is.read();

			if (c == -1) {
				return nbRead;
			} else {
				++nbRead;

				if (c == '\n')
					return nbRead;
			}

		}
	}

	public static class ReadLong {
		public long v;
		public int n;
		public int lastChar;
	}

	public static ReadLong readLong(InputStream is) throws IOException {
		ReadLong r = new ReadLong();

		while (true) {
			r.lastChar = is.read();
			++r.n;

			if (r.lastChar == -1) {
				return r;
			} else if (Character.isDigit(r.lastChar)) {
				r.v = r.v * 10 + Character.digit(r.lastChar, 10);
			} else {
				return r;
			}
		}

	}

	public static void skip(InputStream is, long n) throws IOException {
		while ((n -= is.skip(n)) > 0)
			;
	}

}
