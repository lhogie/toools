package toools.text;

import java.util.Random;

public class NameList {
	public static String random(Random r) {
		return names[r.nextInt(names.length)];
	}

	public static String[] names;

	static {
		// names = new String(new JavaResource(NameList.class,
		// "names.lst").getByteArray()).split("\\n");
	}

	static String[] names2 = new String[] { "Paul", "John", "George", "Ringo", "Pete", "Mal" };
	static int i = 0;

	public static String nextBeatles() {
		return names2[i++ % names2.length];
	}

	static String alphabet = "abcdefghijklmnopqrstuvwxyz";

	public static String next() {
		int a = i++;

		if (a == 0)
			return "a";

		String s = "";

		while (a > 0) {
			var remainder = a % alphabet.length();
			s = alphabet.charAt(remainder) + s;
			a /= alphabet.length();
		}

		return s;
	}

}
