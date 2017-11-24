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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ListSet<E> extends ArrayList<E> implements Set<E>
{
	public ListSet()
	{
	}

	public ListSet(final Collection<E> list)
	{
		if (list == null)
			throw new NullPointerException("list");

		// make sure there are no duplicates
		addAll(new HashSet<E>(list));
	}

	@Override
	public boolean add(E e)
	{
		if (this.contains(e))
		{
			return false;
		}
		else
		{
			return super.add(e);
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> collection)
	{
		Collection<E> copy = new ArrayList<E>(collection);
		copy.removeAll(this);
		return super.addAll(copy);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> collection)
	{
		Collection<E> copy = new ArrayList<E>(collection);
		copy.removeAll(this);
		return super.addAll(index, copy);
	}

	@Override
	public void add(int index, E element)
	{
		if (this.contains(element))
		{
			return;
		}
		else
		{
			super.add(index, element);
		}
	}
}
