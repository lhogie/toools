package toools.text.csv;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.opencsv.CSVReader;

import toools.io.file.RegularFile;
import toools.progression.LongProcess;

public class intcsv2bin
{
	public static void main(String[] args) throws IOException
	{
		for (String filename : args)
		{
			RegularFile f = new RegularFile(filename);
			Reader is = new InputStreamReader(f.createReadingStream());
			CSVReader csvR = new CSVReader(is);

			RegularFile outFile = new RegularFile(f.getPath() + ".bin");
			DataOutputStream oos = new DataOutputStream(outFile.createWritingStream());

			LongProcess lp = new LongProcess("converting " + f.getName(), "line", - 1);
			int lineNumber = 0;

			for (String[] l : csvR)
			{
				if (lineNumber++ == 0)
					continue;

				for (String e : l)
				{
					oos.writeInt(Integer.valueOf(e));
				}

				lp.sensor.progressStatus++;
			}

			lp.end();

			is.close();
			oos.close();
		}
	}
}
