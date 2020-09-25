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

import java.util.Random;

public class DataBinaryEncoding
{
	public static int readInt(byte[] b, int i)
	{
		int n = (b[i] << 24) + ((b[++i] & 255) << 16) + ((b[++i] & 255) << 8)
				+ (b[++i] & 255);
		return n;

	}

	public static long readLong(byte[] b, int i)
	{
		long n = (((long) b[i] << 56) + ((long) (b[++i] & 255) << 48)
				+ ((long) (b[++i] & 255) << 40) + ((long) (b[++i] & 255) << 32)
				+ ((long) (b[++i] & 255) << 24) + ((b[++i] & 255) << 16)
				+ ((b[++i] & 255) << 8) + ((b[++i] & 255) << 0));
		return n;

	}

	public static boolean readBoolean(byte[] b, int i)
	{
		return b[i] != 0;
	}

	public static void writeLong(long v, byte[] b, int i)
	{
		b[i] = (byte) (v >>> 56);
		b[++i] = (byte) (v >>> 48);
		b[++i] = (byte) (v >>> 40);
		b[++i] = (byte) (v >>> 32);
		b[++i] = (byte) (v >>> 24);
		b[++i] = (byte) (v >>> 16);
		b[++i] = (byte) (v >>> 8);
		b[++i] = (byte) (v >>> 0);
	}

	public static void writeInt(int v, byte[] b, int i)
	{
		b[i] = (byte) (v >>> 24);
		b[++i] = (byte) (v >>> 16);
		b[++i] = (byte) (v >>> 8);
		b[++i] = (byte) (v >>> 0);
	}

	public static void writeBoolean(boolean v, byte[] b, int i)
	{
		b[i] = (byte) (v ? 1 : 0);
	}

	public static byte readByte(byte[] buf, int i)
	{
		return buf[i];
	}

	public static void writeByte(byte v, byte[] buf, int i)
	{
		buf[i] = v;
	}

	private static void test()
	{
		byte[] buf = new byte[100];
		Random r = new Random();

		for (int a = 0; a < 1000; ++a)
		{
			long l = r.nextLong();

			DataBinaryEncoding.writeLong(l, buf, 0);
			assert DataBinaryEncoding.readLong(buf, 0) == l;

			boolean b = l % 2 == 0;
			DataBinaryEncoding.writeBoolean(b, buf, 0);
			assert DataBinaryEncoding.readBoolean(buf, 0) == b;

			int i = (int) l;
			DataBinaryEncoding.writeInt(i, buf, 0);
			assert DataBinaryEncoding.readInt(buf, 0) == i;

		}
	}
}
