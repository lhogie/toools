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

import java.io.IOException;
import java.io.ObjectOutput;

import toools.exceptions.NotYetImplementedException;

public class ByteArrayObjectOutput implements ObjectOutput
{
	public final byte buf[];
	private int i = 0;

	public ByteArrayObjectOutput(byte[] buf)
	{
		this.buf = buf;
	}

	@Override
	public void write(int b)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void write(byte b[], int off, int len)
	{
		throw new NotYetImplementedException();
	}

	public byte[] getByteArray()
	{
		return buf;
	}

	@Override
	public void writeBoolean(boolean v)
	{
		DataBinaryEncoding.writeBoolean(v, buf, i);
		i += 1;
	}

	@Override
	public void writeByte(int v)
	{
		DataBinaryEncoding.writeByte((byte) v, buf, i);
		i += 1;
	}

	@Override
	public void writeShort(int v)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void writeChar(int v)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void writeInt(int v)
	{
		DataBinaryEncoding.writeInt(v, buf, i);
		i += 4;
	}

	@Override
	public void writeLong(long v)
	{
		DataBinaryEncoding.writeLong(v, buf, i);
		i += 8;
	}

	@Override
	public void writeFloat(float v)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void writeDouble(double v)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void writeBytes(String s)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void writeChars(String s)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void writeUTF(String s)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void writeObject(Object obj)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void flush() throws IOException
	{
		throw new NotYetImplementedException();
	}

	@Override
	public void close() throws IOException
	{
		throw new NotYetImplementedException();
	}

	public int getIndex()
	{
		return i;
	}

}
