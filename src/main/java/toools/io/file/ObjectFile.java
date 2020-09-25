package toools.io.file;

import toools.io.IORuntimeException;
import toools.io.ser.JavaSerializer;

public class ObjectFile<R> extends RegularFile
{

	public ObjectFile(Directory parent, String name)
	{
		super(parent, name);
	}

	public ObjectFile(String path)
	{
		super(path);
	}

	public R readObject()
	{
		return (R) JavaSerializer.getDefaultSerializer().fromBytes(getContent());
	}

	public void setObject(Object o)
	{
		setContent(JavaSerializer.getDefaultSerializer().toBytes(o));
	}

	public boolean contentIsOk()
	{
		try
		{
			 readObject();
			 return true;
		}
		catch (IORuntimeException ex)
		{
			return false;
		}
	}



}
