package toools.text.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;

import com.opencsv.CSVReader;

import toools.io.file.RegularFile;
import toools.progression.LongProcess;

public class csv2bin
{
	static final class END implements Serializable
	{
	};

	public static void main(String[] args) throws IOException
	{
		for (String filename : args)
		{
			RegularFile f = new RegularFile(filename);
			Reader is = new InputStreamReader(f.createReadingStream());
			CSVReader csvR = new CSVReader(is);

			RegularFile outFile = new RegularFile(f.getPath() + ".bin");
			ObjectOutputStream oos = new ObjectOutputStream(
					outFile.createWritingStream());

			LongProcess lp = new LongProcess("converting " + f.getName(), "line", - 1);

			for (String[] l : csvR)
			{
				oos.writeObject(l);
				lp.sensor.progressStatus++;
			}

			oos.writeObject(new END());
			lp.end();

			is.close();
			oos.close();
		}
	}
}
