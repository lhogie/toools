package toools.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.progression.LongProcess;

public class SSFile extends RegularFile
{

	public static interface ValueExtractor
	{
		String extract(int l);
	}

	public SSFile(String name)
	{
		super(name);
	}

	public SSFile(Directory parent, String name)
	{
		super(parent, name);
	}

	public void saveValues(int nbItems, ValueExtractor f) throws IOException
	{
		LongProcess writing = new LongProcess("writing " + this, "B", nbItems);
		OutputStream os = createWritingStream();

		for (int l = 0; l < nbItems; ++l)
		{
			String value = f.extract(l);
			value = value.replace("\n", "\\n") + '\n';
			os.write(value.getBytes());
			os.write('\n');
			++writing.sensor.progressStatus;
		}

		os.close();
		writing.end();
	}


	public List<String> readValues(int nbThreads) throws IOException
	{
		List<String> lines = getLines();
		
		for (int i = 0; i < lines.size(); ++i)
		{
			lines.set(i, lines.get(i).replace("\\n", "\n"));
		}

		return lines;
	}
}
