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
 
 /**
 * Grph
 * Initial Software by Luc HOGIE, Issam TAHIRI, Aurélien LANCIN, Nathann COHEN, David COUDERT.
 * Copyright © INRIA/CNRS/UNS, All Rights Reserved, 2011, v0.9
 *
 * The Grph license grants any use or destribution of both binaries and source code, if
 * a prior notification was made to the Grph development team.
 * Modification of the source code is not permitted. 
 * 
 *
 */

package toools.collections.primitive;

import java.util.ArrayList;
import java.util.List;

public class IntQueue
{
	private int size = 0;
	private int first = 0;
	private final List<int[]> blocks = new ArrayList<int[]>();
	private final int blockSize;

	public enum ACCESS_MODE {
		QUEUE, STACK
	};

	public IntQueue()
	{
		this(1024);
	}

	public IntQueue(int blockSize)
	{
		this.blockSize = blockSize;
	}

	/**
	 * Append the given element at the end of the array.
	 * 
	 * @param e
	 */
	public void add(int e)
	{
		int globalIndex = first + size;
		int blockIndex = globalIndex / blockSize;

		// if the index of the block refers to a non-existing block
		if (blockIndex == blocks.size())
		{
			blocks.add(new int[blockSize]);
		}

		int[] block = blocks.get(blockIndex);
		int indexInBlock = globalIndex % blockSize;
		block[indexInBlock] = e;
		++size;
	}

	/**
	 * The number of elements in the array
	 * 
	 * @return
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Removes and return the element at the beginning
	 * 
	 * @return
	 */
	public int poll()
	{
		if (size == 0)
			throw new IllegalStateException("no element to poll");

		// get the value
		int e = blocks.get(0)[first];

		// remove it
		++first;
		--size;

		// if the first block is no longer used, remove it
		if (first == blockSize)
		{
			blocks.remove(0);
			first = 0;
		}

		return e;
	}

	/**
	 * Removes and return the element at the end
	 * 
	 * @return
	 */
	public int pop()
	{
		if (size == 0)
			throw new IllegalStateException("no element to pop");

		int globalIndex = first + size - 1;
		int blockIndex = globalIndex / blockSize;
		assert blockIndex == blocks.size() - 1;
		int indexInBlock = globalIndex % blockSize;

		// get the value
		int e = blocks.get(blockIndex)[indexInBlock];

		// remove it
		--size;

		// if the element was the first of the last block, the block is no
		// longer used, remove it
		if (indexInBlock == 0)
		{
			blocks.remove(blockIndex);
		}

		return e;
	}

	/**
	 * The element at the end
	 * 
	 * @return
	 */
	public int peek()
	{
		return get(getSize() - 1);
	}

	/**
	 * The element at the given position
	 * 
	 * @return
	 */
	public int get(int index)
	{
		if (size == 0)
			throw new IllegalStateException("no element to peek");

		if (index < 0 || index >= getSize())
			throw new IndexOutOfBoundsException();

		int globalIndex = first + index;
		int blockIndex = globalIndex / blockSize;
		int indexInBlock = globalIndex % blockSize;
		return blocks.get(blockIndex)[indexInBlock];
	}

	public int indexOf(int e)
	{
		for (int i = 0; i < getSize(); ++i)
		{
			if (get(i) == e)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean contains(int e)
	{
		return indexOf(e) >= 0;
	}
	
	public int extract(ACCESS_MODE mode)
	{
		if (mode == null)
			throw new NullPointerException();

		return mode == ACCESS_MODE.QUEUE ? poll() : pop();
	}

	public void clear()
	{
		blocks.clear();
		first = 0;
		size = 0;
	}

	public static void main(String[] args)
	{
		IntQueue q = new IntQueue(4);

		for (int i = 0; i < 20; ++i)
		{
			q.add(i);
		}

		System.out.println("*** " + q.get(12));

		while (q.getSize() > 0)
		{
			int e = q.pop();
			System.out.println(e);
		}

		System.out.println("done");
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < getSize(); ++i)
		{
			b.append(get(i));

			if (i < getSize() - 1)
				b.append(' ');
		}

		return b.toString();
	}
}
