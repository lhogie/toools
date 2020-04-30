package toools.text;

public class Text
{
	public static int toInt(String s)
	{
		return Integer.valueOf(s);
	}

	public static long toLong(String s)
	{
		return Long.valueOf(s);
	}

	public static double toDouble(String s)
	{
		return Double.valueOf(s);
	}

	public static boolean toBoolean(String s)
	{
		return TextUtilities.toBoolean(s);
	}
}
