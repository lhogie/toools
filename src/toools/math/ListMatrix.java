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
 
 package toools.math;

import java.util.Collection;
import java.util.HashSet;

import toools.collections.AutoGrowingArrayList;


public class ListMatrix<X, Y, V> extends Matrix<X, Y, V>
{
	private AutoGrowingArrayList<AutoGrowingArrayList<V>> map = new AutoGrowingArrayList<AutoGrowingArrayList<V>>();
	Collection<X> xs = new HashSet<X>();
	Collection<Y> ys = new HashSet<Y>();

	@Override
	public Collection<X> getXs()
	{
		return xs;
	}

	@Override
	public Collection<Y> getYs()
	{
		return ys;
	}

	@Override
	public void set(X x, Y y, V value)
	{
		xs.add(x);
		ys.add(y);
		map.ensureIndexCanBeStored(x.hashCode());
		AutoGrowingArrayList<V> l = map.get(x.hashCode());
		
		if (l == null)
		{
			map.set(x.hashCode(), l = new AutoGrowingArrayList<V>());
		}
		
		l.set(y.hashCode(), value);
	}

	@Override
	public V get(X a, Y b)
	{
		int x = a.hashCode();
		
		if (x < map.size())
		{
			AutoGrowingArrayList<V> l = map.get(x);
			
			if (l == null)
			{
				return null;
			}
			else
			{
				int y = b.hashCode();

				if (y < l.size())
				{
					return l.get(y);
				}
				else
				{
					return null;
				}
			}
		}
		else
		{
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		Matrix<Long, Integer, Double> m = new HashMatrix<Long, Integer, Double>();
		m.set(2L, 4, 5.3);
		m.set(2L, 1, 6.2);
		m.set(5L, 2, 9.5);
		m.set(1L, 5, 0.3);
		System.out.println(m);
	}

}
