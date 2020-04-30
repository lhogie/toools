package toools;

public class Longs
{
	public static void apply(long[] a, LongChanger c)
	{
		for (int i = 0; i < a.length; ++i)
		{
			a[i] = c.alter(a[i]);
		}
	}

}
