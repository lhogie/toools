package toools.io;

import java.io.PrintStream;

public class PrintStreamCounter
{
	private PrintStream out;

	public PrintStreamCounter(PrintStream ps)
	{
		this.out = ps;
	}

	public int print(long n)
	{
		String s = String.valueOf(n);
//		Cout.debug(n);
		out.print(s);
		return s.length();
	}

	public int println()
	{
		out.println();
		return 1;
	}

	public int print(char c)
	{
		out.print(c);
		return 1;
	}

	public void flush()
	{
		out.flush();

	}

}
