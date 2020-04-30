package toools.text.csv;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toools.io.file.RegularFile;

public class merge
{
	public static void main(String[] args) throws IOException
	{
		List<String> arguments = new ArrayList<>(Arrays.asList(args));
		List<BufferedReader> readers = new ArrayList<>();

		while (arguments.size() > 0)
		{
			RegularFile f = new RegularFile(arguments.remove(0));
			readers.add(new BufferedReader(
					new InputStreamReader(f.createReadingStream(1000000))));
		}

		System.setOut(new PrintStream(new BufferedOutputStream(System.out, 16000000)));
		merge(readers, System.out);

		for (BufferedReader r : readers)
		{
			r.close();
		}

		System.out.flush();
	}

	private static void merge(List<BufferedReader> readers, PrintStream out)
			throws IOException
	{
		while (true)
		{
			for (BufferedReader r : readers)
			{
				String line = r.readLine();

				if (line == null)
					return;

				out.print(line);

				if (r != readers.get(readers.size() - 1))
				{
					out.print('\t');
				}
			}

			out.println();
		}
	}
}
