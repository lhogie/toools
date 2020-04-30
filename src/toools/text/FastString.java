package toools.text;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FastString implements Externalizable
{
	public FastString(int initialSize)
	{
		buf = new byte[10000];
	}

	public byte[] buf;
	public int len = 0;

	public String substring(int start, int end)
	{
		return new String(buf, start, end - start);
	}

	public int indexOf(char c, int start)
	{
		for (int i = start; i < len; ++i)
		{
			if (buf[i] == c)
				return i;
		}

		return - 1;
	}

	@Override
	public String toString()
	{
		return new String(buf, 0, len);
	}

	public long parseNumber(int start, int end)
	{
		long n = 0;

		for (int i = start; i < end; ++i)
		{
			n = n * 10 + buf[i] - '0';
		}

		return n;
	}

	public boolean readLine(InputStream is) throws IOException
	{
		len = 0;

		while (true)
		{
			int c = is.read();

			if (c == - 1)
				return len > 0;

			if (c == '\r')
				continue;

			if (c == '\n')
				return true;

			buf[len++] = (byte) c;
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(buf.length);
		out.writeInt(len);
		out.write(buf, 0, len);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		buf = new byte[in.readInt()];
		len = in.readInt();
		in.read(buf, 0, buf.length);
	}

}
