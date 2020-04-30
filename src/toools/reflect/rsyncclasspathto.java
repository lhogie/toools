package toools.reflect;

public class rsyncclasspathto
{
	public static void main(String[] args)
	{
		for (String d : args)
		{
			ClassPath.retrieveSystemClassPath().rsyncTo(d);
		}
	}
}
