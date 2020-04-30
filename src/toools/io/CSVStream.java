package toools.io;

import java.io.OutputStream;
import java.io.PrintStream;

public class CSVStream extends PrintStream
{
	String sep = "\t";

	public CSVStream(OutputStream out)
	{
		super(out);
	}

	public void println(Object... o)
	{
		for (int i = 0; i < o.length; ++i)
		{
			print(o[i]);

			if (i < o.length - 1)
				print(sep);
		}

		println();
	}

}
