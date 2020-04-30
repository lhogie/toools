package toools.text.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.opencsv.CSVReader;

import toools.io.Cout;
import toools.io.file.RegularFile;

public class clean
{
	public static void main(String[] args) throws IOException
	{
		ArrayList<String> l = new ArrayList<>(Arrays.asList(args));

		for (String filename : l)
		{
			RegularFile inFile = new RegularFile(filename);
			inFile.renameTo(inFile.getPath() + ".old");

			CSVReader reader = new CSVReader(new FileReader(inFile.getPath()));
			Iterator<String[]> iterator = reader.iterator();
			String[] headers = iterator.next();
			PrintWriter writer = new PrintWriter(new FileWriter(filename));

			while (iterator.hasNext())
			{
				String[] line = iterator.next();

				for (int i = 0; i < line.length; ++i)
				{
					// if (line.con)
				}
			}

			reader.close();
			writer.close();

		}

		Cout.progress("done");
	}

}
