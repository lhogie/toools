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

import java.util.Random;

import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import toools.reflect.Clazz;
import toools.thread.Threads;

public class SelfAdaptiveIntSet extends AbstractIntSet implements LucIntSet
{
	// BitVectorSet needs to access this for optimization purpose
	LucIntSet underlyingSet;
	private double hashLoadFactor = 1.5;
	private double histeresis = 2;
	private final int expected;

	public SelfAdaptiveIntSet(int expected)
	{
		this.expected = expected;
	}

	@Override
	public boolean add(int e)
	{
		if (underlyingSet == null)
		{
			if (e < 64)
			{
				underlyingSet = new BitVectorSet(expected, 0);
			}
			else
			{
				underlyingSet = new LucIntHashSet(expected);
			}
		}

		double sizeIfBitSet = isEmpty() ? e / 8 : Math.max(e, getGreatest()) / 8;
		double sizeIfHashSet = (size() + 1) * hashLoadFactor;

		if (sizeIfHashSet * histeresis < sizeIfBitSet)
		{
			ensureHashset();
		}
		else if (sizeIfBitSet * histeresis < sizeIfHashSet)
		{
			ensureBitset();
		}

		return underlyingSet.add(e);
	}

	@Override
	public boolean remove(int e)
	{
		if (underlyingSet == null)
		{
			throw new IllegalArgumentException("this set doesn't contains element " + e);
		}
		else
		{
			boolean couldBeRemoved = underlyingSet.remove(e);

			if (couldBeRemoved)
			{
				if (underlyingSet.isEmpty())
				{
					underlyingSet = null;
				}
				else
				{
					double sizeIfBitSet = getGreatest() / 8;
					double sizeIfHashSet = (size() + 1) * hashLoadFactor;

					if (sizeIfHashSet * histeresis < sizeIfBitSet)
					{
						ensureHashset();
					}
					else if (sizeIfBitSet * histeresis < sizeIfHashSet)
					{
						ensureBitset();
					}
				}
			}

			return couldBeRemoved;
		}
	}

	@Override
	public boolean contains(int id)
	{
		return underlyingSet != null && underlyingSet.contains(id);
	}

	@Override
	public int size()
	{
		return underlyingSet == null ? 0 : underlyingSet.size();
	}

	@Override
	public boolean isEmpty()
	{
		return underlyingSet == null || underlyingSet.isEmpty();
	}

	@Override
	public IntIterator iterator()
	{
		return underlyingSet == null ? IntSets.EMPTY_SET.iterator()
				: underlyingSet.iterator();
	}

	@Override
	public void clear()
	{
		if (underlyingSet != null)
		{
			underlyingSet = null;
		}
	}

	@Override
	public int pickRandomElement(Random prng)
	{
		return underlyingSet.pickRandomElement(prng);
	}

	private void ensureHashset()
	{
		if (underlyingSet.getClass() != LucIntHashSet.class)
		{
			LucIntSet newset = new LucIntHashSet(size());
			newset.addAll(underlyingSet);
			underlyingSet = newset;
		}
	}

	private void ensureBitset()
	{
		if (underlyingSet.getClass() != BitVectorSet.class)
		{
			LucIntSet newset = new BitVectorSet(size(), 0);
			newset.addAll(underlyingSet);
			underlyingSet = newset;
		}
	}

	public static void main(String[] args)
	{
		Clazz.makeInstance(SelfAdaptiveIntSet.class);

		SelfAdaptiveIntSet set = new SelfAdaptiveIntSet(4);
		set.add(2);
		Random r = new Random();

		System.out.println("****************");

		for (int i = 0; i < 10000000; ++i)
		{
			int b = r.nextInt(100);
			set.add(b);
			System.out.println("adding " + b);

			System.out.print(set);

			System.out.println(set.getImplementationClass());
			System.out.println(" size=" + set.size());
			System.out.println(" density=" + set.getDensity());
			Threads.sleepMs(100);
		}
	}

	public Class<? extends IntSet> getImplementationClass()
	{
		return underlyingSet == null ? null : underlyingSet.getClass();
	}

	@Override
	public void addAll(int... a)
	{
		for (int n : a)
		{
			add(n);
		}
	}

	@Override
	public double getDensity()
	{
		return underlyingSet.getDensity();
	}

	@Override
	public int getGreatest()
	{
		return underlyingSet.getGreatest();
	}
}
