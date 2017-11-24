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

import java.io.ObjectInput;

import toools.NotYetImplementedException;

public class ByteArrayObjectInput implements ObjectInput
{
	public final byte[] buf;
	private int offset = 0;

	public ByteArrayObjectInput(byte buf[], int offset)
	{
		assert buf.length > 0;
		assert offset >= 0;
		this.buf = buf;
		this.offset = offset;
	}

	@Override
	public int read()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public int read(byte b[], int off, int len)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public long skip(long n)
	{
		assert n >= 0;
		return offset += n;
	}

	@Override
	public int available()
	{
		throw new NotYetImplementedException();
//		return buf.length - i;
	}

	@Override
	public int readInt()
	{
		int v = DataBinaryEncoding.readInt(buf, offset);
		offset += 4;
		return v;
	}

	@Override
	public long readLong()
	{
		long v = DataBinaryEncoding.readLong(buf, offset);
		offset += 8;
		return v;
	}

	@Override
	public boolean readBoolean()
	{
		return DataBinaryEncoding.readBoolean(buf, offset++);
	}

	@Override
	public void readFully(byte[] b)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void readFully(byte[] b, int off, int len)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public int skipBytes(int n)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public byte readByte()
	{
		return DataBinaryEncoding.readByte(buf, offset++);
	}

	@Override
	public int readUnsignedByte()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public short readShort()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public int readUnsignedShort()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public char readChar()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public float readFloat()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public double readDouble()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public String readLine()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public String readUTF()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public Object readObject()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public int read(byte[] b)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void close()
	{
		throw new NotYetImplementedException();
	}

}
