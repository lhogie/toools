package toools.text.csv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Iterator;

import toools.io.file.RegularFile;
import toools.progression.LongProcess;
import toools.text.csv.csv2bin.END;

public class decode
{

	public static void main(String[] args) throws IOException
	{
		for (String filename : args)
		{
			RegularFile f = new RegularFile(filename);
			Iterator<String[]> i = iterator(f);
			LongProcess lp = new LongProcess("converting " + f.getName(), "line", - 1);

			while (i.hasNext())
			{
				System.out.println(Arrays.toString(i.next()));
				lp.sensor.progressStatus++;
			}

			lp.end();
		}
	}

	public static Iterator<String[]> iterator(RegularFile f) throws IOException
	{
		ObjectInputStream is = new ObjectInputStream(f.createReadingStream());

		return new Iterator<String[]>()
		{
			Object next;

			{
				go();
			}

			@Override
			public boolean hasNext()
			{
				return next.getClass() != END.class;
			}

			@Override
			public String[] next()
			{
				if ( ! hasNext())
					throw new IllegalStateException();

				Object next = this.next;
				go();
				return (String[]) next;
			}

			private void go()
			{
				try
				{
					next = is.readObject();
					
					if (!hasNext())
					{
						is.close();
					}
				}
				catch (ClassNotFoundException | IOException e)
				{
					e.printStackTrace();
				}
			}
		};
	}
}
