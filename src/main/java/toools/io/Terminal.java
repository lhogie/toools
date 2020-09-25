package toools.io;

import java.io.PrintStream;

import toools.util.Date;

public class Terminal
{
	private final PrintStream ps;
	private final String prefix;

	public Terminal(PrintStream ps, String prefix)
	{
		this.ps = ps;
		this.prefix = prefix;
	}

	public void print(String s)
	{
		s = s.replaceAll("\n", "\n" + prefix + Date.now() + "\t");

		synchronized (Terminal.class)
		{
			ps.print(s);
		}
	}

	public void println(String s)
	{
		print(s + '\n');
	}
}
