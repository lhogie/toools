package toools.text.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import com.opencsv.CSVReader;

import toools.RAMMonitor;
import toools.io.Cout;
import toools.io.file.RegularFile;

public class unquote
{
	public static void main(String[] args) throws IOException
	{
		RAMMonitor.start(5000, () -> null);

		for (String filename : args)
		{
			Cout.progress("processing " + filename);
			RegularFile inFile = new RegularFile(filename);
			inFile.renameTo(inFile.getPath() + ".old");

			CSVReader reader = new CSVReader(new FileReader(inFile.getPath()));
			Iterator<String[]> iterator = reader.iterator();
			String[] headers = iterator.next();
			PrintWriter writer = new PrintWriter(new FileWriter(filename));

			doit(headers, writer);
			writer.print('\n');

			while (iterator.hasNext())
			{
				String[] line = iterator.next();
				doit(line, writer);

				if (iterator.hasNext())
					writer.print('\n');
			}

			reader.close();
			writer.close();

		}

		Cout.progress("done");
}

	private static void doit(String[] line, PrintWriter out)
	{
		for (int i = 0; i < line.length; ++i)
		{
			String h = line[i];
			h = ensureNotQuoted(h);
			out.print(h);

			if (i < line.length - 1)
				out.print(',');
		}
	}

	private static String ensureNotQuoted(String s)
	{
		if (s.startsWith("\""))
		{
			s = s.substring(1, s.length() - 2);
		}

		s = s.replace('"', ' ');
		s = s.replace(',', ';');
		s = s.replace('\r', ' ');
		s = s.replace('\n', ' ');

		return s;
	}
}
