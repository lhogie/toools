package toools.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public abstract class StreamIterator<E> implements Iterator<E>
{
	protected final InputStream unbufferredInputStream;

	public StreamIterator(InputStream is)
	{
		unbufferredInputStream = is;
	}

	public void close()
	{
		try
		{
			unbufferredInputStream.close();
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
