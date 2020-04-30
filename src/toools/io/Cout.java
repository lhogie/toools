package toools.io;

import java.io.PrintStream;

import toools.io.file.RegularFile;
import toools.text.TextUtilities;
import toools.util.Date;

public class Cout
{
	public static String processName = null;


	public static String allowedPrefices = "REPWIMD";
	public static boolean showLetter = true;
	public static boolean showDate = true;
	public static int leftShit = 0;

	public static void result(Object... s)
	{
		stdout(prefix('R', s), System.out);
	}

	public static void error(Object... s)
	{
		stdout(prefix('E', s), System.out);
	}

	public static void warning(Object... s)
	{
		stdout(prefix('W', s), System.out);
	}

	public static void progress(Object... s)
	{
		stdout(prefix('P', s), System.out);
	}

	public static void info(Object... s)
	{
		stdout(prefix('I', s), System.out);
	}

	public static void sys(Object... s)
	{
		stdout(prefix('M', s), System.out);
	}

	public static void debug(Object... s)
	{
		stdout(prefix('D', s), System.out);
	}

	private static synchronized void stdout(String s, PrintStream os)
	{
		if (os == null)
		{
			String filename = (processName == null ? "" : processName + " - ")
					+ Date.now(Date.DATE_AND_TIME) + ".log";

			filename = filename.replace(":", "_");
			filename = filename.replace(" ", "_");

			RegularFile f = new RegularFile("$HOME/luclogs/" + filename);
			f.getParent().ensureExists();

			os = new PrintStream(f.createWritingStream(false, 1024));
		}

		os.println(s);
		os.flush();
	}

	private static String prefix(char mark, Object... s)
	{
		if (allowedPrefices.indexOf(mark) < 0)
			throw new IllegalArgumentException(
					"mark '" + mark + "' is not allowed. Valid marks: " + allowedPrefices);

		String prefix = (showLetter ? mark + " \t" : "");
		prefix += (showDate ? Date.now() + " \t" : "");
		// prefix += TextUtilities.repeat('\t', leftShit);
		String r = prefix
				+ TextUtilities.concat(", ", ennice(s)).replace("\n", "\n" + prefix);
		return r;
	}

	private static String[] ennice(Object[] s)
	{
		if (s == null)
			throw new NullPointerException();
		
		String[] r = new String[s.length];

		for (int i = 0; i < r.length; ++i)
		{
			r[i] = TextUtilities.toString(s[i]);
		}

		return r;
	}

	public static void debugSuperVisible(Object... os)
	{
		for (Object o : os)
		{
			debug(TextUtilities.box(TextUtilities.toString(o)));
		}

	}

}
