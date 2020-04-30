package toools.io;

import java.awt.Color;
import java.io.Serializable;

import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.io.ser.JavaSerializer;
import toools.io.ser.Serializer;
import toools.io.ser.TextSerializer;

public abstract class InDirectoryObject
{
	private final Directory directory;

	public InDirectoryObject(Directory d)
	{
		this.directory = d;

		if ( ! this.directory.exists())
		{
			this.directory.mkdirs();
		}

	}

	public void clear()
	{
		if (getDirectory().exists())
		{
			getDirectory().deleteRecursively();
		}
	}

	public Directory getDirectory()
	{
		return directory;
	}

	public String getName()
	{
		return getDirectory().getName();
	}

	private Object read(String propName, Serializer s)
	{
		RegularFile f = getFile(propName);

		if (f.exists())
		{
			return s.fromBytes(f.getContent());
		}
		else
		{
			return null;
		}
	}

	private void write(String propName, Object value, Serializer s)
	{
		RegularFile f = getFile(propName);

		if (value == null)
		{
			if (f.exists())
			{
				f.delete();
			}
		}
		else
		{
			if ( ! f.getParent().exists())
			{
				f.getParent().mkdirs();
			}

			f.setContent(s.toBytes(value));
		}
	}

	protected RegularFile getFile(String propName)
	{
		RegularFile f = new RegularFile(directory, propName);

		if ( ! f.getParent().exists())
			f.getParent().mkdirs();

		return f;
	}

	public void serialize(String propName, Serializable value)
	{
		write(propName, value, JavaSerializer.getDefaultSerializer());
	}

	public Object deserialize(String propName)
	{
		return read(propName, JavaSerializer.getDefaultSerializer());
	}

	public Boolean readBoolean(String propName)
	{
		return (Boolean) read(propName, TextSerializer.Bool);
	}

	public void writeBoolean(String propName, boolean b)
	{
		write(propName, b, TextSerializer.Bool);
	}

	public Color readColor(String propName)
	{
		return (Color) read(propName, TextSerializer.Color);
	}

	public void writeColor(String propName, Color color)
	{
		write(propName, color, TextSerializer.Color);
	}

	public String readString(String propName)
	{
		return (String) read(propName, TextSerializer.String);
	}

	public void writeString(String propName, String s)
	{
		write(propName, s, TextSerializer.String);
	}

	public double readDouble(String propName)
	{
		return (Double) read(propName, TextSerializer.Float64);
	}

	public void writeDouble(String propName, double d)
	{
		write(propName, d, TextSerializer.Float64);
	}
}
