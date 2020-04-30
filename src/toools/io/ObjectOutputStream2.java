/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 
Aurelien Lancin (Coati research team, Inria)
Christian Glacet (LaBRi, Bordeaux)
David Coudert (Coati research team, Inria)
Fabien Crequis (Coati research team, Inria)
Grégory Morel (Coati research team, Inria)
Issam Tahiri (Coati research team, Inria)
Julien Fighiera (Aoste research team, Inria)
Laurent Viennot (Gang research-team, Inria)
Michel Syska (I3S, Université Cote D'Azur)
Nathann Cohen (LRI, Saclay) 
Julien Deantoin (I3S, Université Cote D'Azur, Saclay) 

*/
 
 package toools.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.Serializable;

import toools.StopWatch;
import toools.StopWatch.UNIT;
import toools.io.ser.JavaSerializer;

public class ObjectOutputStream2 extends DataOutputStream implements ObjectOutput
{
	private static final JavaSerializer s = new JavaSerializer();

	public ObjectOutputStream2(OutputStream os)
	{
		super(os);
	}

	public TransmissionInfo writeByteArray2(byte[] bytes)
	{
		try
		{
			return writeByteArray(bytes);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void writeObject2(Object o)
	{
		try
		{
			writeObject(o);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public TransmissionInfo writeByteArray(byte[] bytes) throws IOException
	{
		TransmissionInfo info = new TransmissionInfo();
		boolean compression = needCompression(bytes);

		if (compression)
		{
			info.compressionInfo = new CompressionInfo();
			info.compressionInfo.sizeBefore = bytes.length;
			StopWatch sw = new StopWatch(UNIT.ms);
			bytes = Utilities.gzip(bytes);
			info.compressionInfo.sizeAfter = bytes.length;
			info.compressionInfo.compressionDurationMs = sw.getElapsedTime();
			// System.out.println("compressing time " +
			// info.compressionInfo.compressionDurationMs + " ratio=" +
			// info.compressionInfo.getCompressionratio());
		}

		writeInt(bytes.length);
		info.size = bytes.length;

		write(bytes);
		writeBoolean(compression);
		return info;
	}

	public boolean needCompression(byte[] b)
	{
		// compress if greater than a TCP frame
		// return b.length > NetUtilities.TCP_FRAME_LENGTH;
		return false;
	}

	public void writeIntArray(int[] a) throws IOException
	{
		writeInt(a.length);

		for (int i : a)
		{
			writeInt(i);
		}
	}

	public void writes(Serializable... objects) throws IOException
	{
		for (Serializable o : objects)
		{
			final Class c = o.getClass();

			if (c.isPrimitive())
			{
				if (c == int.class)
				{
					writeInt((int) o);
				}
				else if (c == long.class)
				{
					writeLong((long) o);
				}
				else if (c == short.class)
				{
					writeShort((short) o);
				}
				else if (c == byte.class)
				{
					writeByte((byte) o);
				}
				else if (c == double.class)
				{
					writeDouble((double) o);
				}
				else if (c == float.class)
				{
					writeFloat((float) o);
				}
				else if (c == char.class)
				{
					writeChar((char) o);
				}
				else if (c == boolean.class)
				{
					writeBoolean((boolean) o);
				}
				else
					throw new IllegalStateException();
			}
			else
			{
				writeObject2(o);
			}
		}
	}

	public void writeLong2(long l)
	{
		try
		{
			writeLong(l);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void writeInt2(int l)
	{
		try
		{
			writeInt(l);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void writeShort2(short l)
	{
		try
		{
			writeShort(l);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void writeByte2(byte l)
	{
		try
		{
			writeByte(l);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void writeBoolean2(boolean b)
	{
		try
		{
			writeBoolean(b);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void flush2()
	{
		try
		{
			flush();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void writeFloat2(float value)
	{
		try
		{
			writeFloat(value);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	@Override
	public void writeObject(Object obj) throws IOException
	{
		writeByteArray(s.toBytes(obj));
	}

	public void write2(byte[] buf, int off, int len)
	{
		try
		{
			write(buf, off, len);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public static void main(String[] args)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream2 oos = new ObjectOutputStream2(b);
		oos.writeLong2(5675);

		oos.flush2();

		ObjectInputStream2 ios = new ObjectInputStream2(
				new ByteArrayInputStream(b.toByteArray()));
		long l = ios.readLong2();

		System.out.println(l);
	}

}
