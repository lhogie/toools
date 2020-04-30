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
 
 package toools.collections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class BitSet implements Serializable
{
	public long[] bits = new long[0];
	private long cardinality = 0;

	public long cardinality()
	{
		return cardinality;
	}

	public boolean get(long i)
	{
		if (i > Integer.MAX_VALUE * 64L)
			throw new IllegalArgumentException();

		int wordIndex = (int) (i >> 6);
		int indexInWord = (int) (i & 0x3f);
		long word = bits[wordIndex];
		return ((word >> indexInWord) & 1L) == 1;
	}

	public void set(long i)
	{
		if (i > Integer.MAX_VALUE * 64L)
			throw new IllegalArgumentException();

		int wordIndex = (int) (i >> 6);
		int indexInWord = (int) (i & 0x3f);
		long word = bits[wordIndex];
		bits[wordIndex] |= 1L << indexInWord;

		if (bits[wordIndex] != word)
			++cardinality;
	}

	public void clear(long i)
	{
		if (i > Integer.MAX_VALUE * 64L)
			throw new IllegalArgumentException();

		int wordIndex = (int) (i >> 6);
		int indexInWord = (int) (i & 0x3f);
		long word = bits[wordIndex];

		bits[wordIndex] &= ~(1L << indexInWord);

		if (bits[wordIndex] != word)
			--cardinality;
	}

	@Override
	public String toString()
	{
		String s = "";

		for (long l : bits)
		{
			if (!s.isEmpty())
			{
				s += " ";
			}

			if (l == 0)
			{
				s += ".";
			}
			else
			{

				for (int i = 0; i < 64; ++i)
				{
					s += ((l >> i) & 1) == 1 ? "1" : "0";
				}
			}
		}

		return s;
	}

	public static void main(String[] args)
	{
		BitSet b = new BitSet();

		for (int i = 0; i < 1000; ++i)
		{
			int n = new Random().nextInt(1000);
			b.ensureCapacity(n + 1);
			b.set(n);
		}

		System.out.println("ok");

	}

	public BitSet clone()
	{
		BitSet r = new BitSet();
		r.bits = Arrays.copyOf(bits, bits.length);
		r.cardinality = cardinality;
		return r;
	}

	public void ensureCapacity(long numberOfBits)
	{
		if (numberOfBits > Integer.MAX_VALUE * 64L)
			throw new IllegalArgumentException();

		int numberOfWordsRequired = ((int) (numberOfBits / 64L)) + 1;

		if (numberOfWordsRequired > bits.length)
		{
			bits = Arrays.copyOf(bits, numberOfWordsRequired);
		}
	}

	public void trimToSize(long newSize)
	{
		bits = Arrays.copyOf(bits, ((int) (newSize / 64L)) + 1);
		computeCardinality();
	}

	public long length()
	{
		return bits.length * 64L;
	}

	private void computeCardinality()
	{
		cardinality = 0;

		for (long l : bits)
		{
			cardinality += Long.bitCount(l);
		}
	}

	public void clear()
	{
		bits = new long[0];
		cardinality = 0;
	}

	public void or(BitSet b)
	{
		ensureCapacity(b.length());

		for (int i = 0; i < bits.length; ++i)
		{
			bits[i] |= b.bits[i];
		}

		computeCardinality();
	}

	public void and(BitSet b)
	{
		long n = Math.min(b.length(), length());

		for (int i = 0; i < n; ++i)
		{
			bits[i] &= b.bits[i];
		}

		computeCardinality();
	}
}
