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

package toools.collections.primitive;

import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import toools.UnitTests;

public class BitVectorSet extends AbstractIntSet implements LucIntSet
{
	private final BitSet bitset;
	private final int offset;
	private long len = 0;

	public BitVectorSet(int expected, int offset)
	{
		this.offset = offset;
		this.bitset = new BitSet(expected);
	}

	@Override
	public boolean contains(int id)
	{
		assert id >= 0;
		return bitset.get(id - offset);

		// if (id / 64 >= bitset.bits.length)
		// return false;
		//
		// long cell = bitset.bits[id / 64];
		//
		// if (cell == 0)
		// return false;
		//
		// return ((cell >> (id % 64)) & 1) > 0;
	}

	@Override
	public IntIterator iterator()
	{

		return new IntIterator()
		{
			int indexOfNextSetBit = bitset.nextSetBit(0);

			@Override
			public boolean hasNext()
			{
				return indexOfNextSetBit >= 0;
			}

			@Override
			public Integer next()
			{
				return nextInt();
			}

			@Override
			public void remove()
			{
				throw new IllegalStateException();
			}

			@Override
			public int nextInt()
			{
				if (indexOfNextSetBit == - 1)
					throw new NoSuchElementException();

				int r = indexOfNextSetBit;
				indexOfNextSetBit = bitset.nextSetBit(indexOfNextSetBit + 1);
				return r;
			}

			@Override
			public int skip(int arg0)
			{
				throw new IllegalStateException();
			}
		};
	}

	@Override
	public int size()
	{
		return (int) len;
	}

	@Override
	public boolean isEmpty()
	{
		return len == 0;
	}

	@Override
	public boolean add(int id)
	{
		assert id >= 0;
		id -= offset;

		// if the element was not already in
		if ( ! bitset.get(id))
		{
			++len;
		}

		bitset.set(id);
		return true;
	}

	@Override
	public boolean addAll(IntCollection s)
	{
		BitVectorSet bvs = getUnderlyingBitSet_if_any(s);

		if (bvs == null || bvs.offset != offset)
		{
			return super.addAll(s);
		}
		else
		{
			bitset.or(bvs.bitset);
			len = bitset.cardinality();
			return true;
		}
	}

	/**
	 * Returns the bitSet underlying in the set implementation, if any.
	 * 
	 * @param s
	 * @return
	 */
	private static BitVectorSet getUnderlyingBitSet_if_any(IntCollection s)
	{
		if (s.getClass() == BitVectorSet.class)
		{
			return (BitVectorSet) s;
		}
		else if (s instanceof SelfAdaptiveIntSet)
		{
			return getUnderlyingBitSet_if_any(((SelfAdaptiveIntSet) s).underlyingSet);
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean retainAll(IntCollection s)
	{
		BitVectorSet bvs = getUnderlyingBitSet_if_any(s);

		// the s is NOT implemented using a bitset
		if (bvs == null || bvs.offset != offset)
		{
			return super.retainAll(s);
		}
		else
		{
			bitset.and(bvs.bitset);
			len = bitset.cardinality();
			return true;
		}
	}

	@Override
	public boolean removeAll(IntCollection s)
	{
		BitVectorSet bvs = getUnderlyingBitSet_if_any(s);

		if (bvs == null || bvs.offset != offset)
		{
			return super.removeAll(s);
		}
		else
		{
			BitSet sb = (BitSet) bvs.bitset.clone();
			sb.flip(0, bitset.length());
			bitset.and(sb);
			len = bitset.cardinality();
			return true;
		}
	}

	@Override
	public boolean remove(int id)
	{
		assert id >= 0;
		id -= offset;

		if ( ! bitset.get(id))
			throw new IllegalArgumentException("this set doesn't contains element " + id);

		bitset.clear(id);
		--len;
		return true;
	}

	@Override
	public double getDensity()
	{
		double c = bitset.cardinality();
		double l = bitset.length();
		return c / l;
	}

	@Override
	public void clear()
	{
		bitset.clear();
		len = 0;
	}

	@Override
	public int pickRandomElement(Random prng)
	{
		while (true)
		{
			int i = prng.nextInt((int) bitset.size());

			if (bitset.get(i))
			{
				return i + offset;
			}
		}
	}

	@Override
	public int getGreatest()
	{
		return (int) bitset.length() - 1 + offset;
	}

	public String toBitString()
	{
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < bitset.length(); ++i)
		{
			if (i % 4 == 0)
			{
				b.append(' ');
			}

			if (i % 8 == 0)
			{
				b.append(' ');
			}

			b.append(bitset.get(i) ? '1' : '0');
		}

		return b.toString();
	}

	private static void testHashcode()
	{
		LucIntSet s = new BitVectorSet(0, 0);
		s.addAll(1, 2, 3);

		LucIntSet s2 = new LucIntHashSet(0);
		s2.addAll(1, 3, 2);
		UnitTests.ensure(s.hashCode() == s2.hashCode());

	}

	@Override
	public Class<?> getImplementationClass()
	{
		return BitSet.class;
	}

	@Override
	public void addAll(int... a)
	{
		for (int n : a)
		{
			add(n);
		}
	}
}
