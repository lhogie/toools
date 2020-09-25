package toools.text.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.opencsv.CSVReader;

import toools.io.Cout;

public class print
{
	public static void main(String[] args) throws IOException
	{
		ArrayList<String> l = new ArrayList<>(Arrays.asList(args));
		String p = l.remove(0);
		boolean printLineNumbers = p.contains("numbers");
		boolean printLines = p.contains("lines");

		for (String filename : l)
		{
			CSVReader reader = new CSVReader(new FileReader(filename));
			Iterator<String[]> iterator = reader.iterator();
			String[] headers = iterator.next();
			System.out.println(Arrays.toString(headers));

			for (int lineNumber = 2; iterator.hasNext(); ++lineNumber)
			{
				String[] line = iterator.next();

				if (printLineNumbers)
				{
					System.out.print(lineNumber);
					System.out.print('\t');
				}

				if (printLines)
				{
					System.out.print(Arrays.toString(line));
				}

				System.out.println();
			}

			reader.close();

			Cout.progress("done");
		}
	}

}
