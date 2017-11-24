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

import java.util.ArrayList;

public class AutoGrowingArrayList<V> extends ArrayList<V>
{
	public AutoGrowingArrayList()
	{
		super(5);
	}

	public AutoGrowingArrayList(int initialCapacity)
	{
		super(initialCapacity);
	}

	@Override
	public void add(int index, V o)
	{
		ensureIndexCanBeStored(index);
		super.add(index, o);
	}

	@Override
	public V get(int i)
	{
		// if the index is within the bounds
		if (i < size())
		{
			// return the value in the indexed cell
			return super.get(i);
		}
		else
		{
			return null;
		}
	}

	@Override
	public V set(int index, V o)
	{
		ensureIndexCanBeStored(index);
		super.set(index, o);
		return o;
	}

	public int ensureIndexCanBeStored(int index)
	{
		if (index < 0)
			throw new IllegalArgumentException();

		int oldsize = size();

		if (index < oldsize)
		{
			return 0;
		}
		else
		{
			ensureCapacity(index);
			int newsize = index + 1;

			for (int n = oldsize; n < newsize; ++n)
			{
				super.add(null);
			}

			return newsize - oldsize;
		}
	}

	// returns the number of null elements deleted
	public int shrink()
	{
		int initialSize = size();
		int i = size();

		// if the previous cell is empty, shift the index to it
		while (i > 0 && get(i - 1) == null)
		{
			--i;
		}

		if (i == 0)
		{
			clear();
		}
		else
		{
			removeRange(i, size());
		}

		return initialSize - i;
	}
}
