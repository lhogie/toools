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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;

import toools.io.ser.JavaSerializer;

public class ObjectInputStream2 extends DataInputStream implements ObjectInput
{
	private final JavaSerializer s = new JavaSerializer();

	public ObjectInputStream2(InputStream is) 
	{
		super(is);
	}

	public byte[] readByteArray()
	{
		int size = readInt2();
		byte[] bytes = new byte[size];
		readFully2(bytes, 0, bytes.length);
		boolean compressed = readBoolean2();

		if (compressed)
		{
			bytes = Utilities.gunzip(bytes);
		}

		return bytes;
	}



	public int readByte2()
	{
		try
		{
			return readByte();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public int readInt2()
	{
		try
		{
			return readInt();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public boolean readBoolean2()
	{
		try
		{
			return readBoolean();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public long readLong2()
	{
		try
		{
			return readLong();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public Object readObject2()
	{
		try
		{
			return readObject();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
		catch (ClassNotFoundException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public long[] readLongArray()
	{
		try
		{
			long[] a = new long[readInt()];

			for (int i = 0; i < a.length; ++i)
			{
				a[i] = readLong();
			}

			return a;
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public float readFloat2()
	{
		try
		{
			return readFloat();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	@Override
	public Object readObject() throws ClassNotFoundException, IOException
	{
		return s.fromBytes(readByteArray());
	}

	public void readFully2(byte[] buf, int offset, int length)
	{
		try
		{
			  readFully(buf, offset, length);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}	
	}
}
