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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import toools.Clazz;
import toools.math.MathsUtilities;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class Lists
{

	public static <T> List<T> flatten(List<? extends Collection<T>> l)
	{
		List<T> r = new ArrayList<T>();

		for (Collection<T> c : l)
		{
			r.addAll(c);
		}

		return r;
	}

	public static <T> void reverseInPlace(List<T> list)
	{
		for (int i = 0; i < list.size() / 2; ++i)
		{
			swap(list, i, list.size() - 1 - i);
		}
	}

	public static <T> void swap(List<T> list, int a, int b)
	{
		T tmp = list.get(a);
		list.set(a, list.get(b));
		list.set(b, tmp);
	}

	public static <T> T pickRandomElement(List<T> l, double[] weight, Random r)
	{
		if (l.size() == 0)
			throw new IllegalArgumentException();
		
		if (l.size() != weight.length)
			throw new IllegalArgumentException();
		
		double sum = MathsUtilities.sum(weight);

		for (int i = 0; i < weight.length; ++i)
		{
			if (weight[i] < 0)
				throw new IllegalArgumentException();

			weight[i] /= sum;
		}

		double n = r.nextDouble();
		double a = 0;

		for (int i = 0; i < weight.length; ++i)
		{
			if (a < n && n < a + weight[i])
			{
				return l.get(i);
			}

			a += weight[i];
		}

		throw new IllegalStateException();
	}

	public static void main(String[] args)
	{
		List<Integer> l = new ArrayList();
		l.add(0);
		l.add(1);
		l.add(2);

		List<Integer> o = new ArrayList();
		o.add(0);
		o.add(0);
		o.add(0);

		for (int i = 0; i < 10000; ++i)
		{
			int e = pickRandomElement(l, new double[] { 1, 3, 2 }, new Random());
			o.set(e, o.get(e) + 1);
			System.out.println(o);
		}

	}

	/**
	 * 
	 * @param <T>
	 * @param l
	 * @param n
	 * @param direction
	 *            defines the direction of the rotation. 1 means that the second
	 *            element comes first and -1 means that the first element goes
	 *            second.
	 */
	public static <T> void rotate(List<T> l, int n)
	{
		if ( ! l.isEmpty())
		{
			if (n == 0)
			{
			}
			else if (n > 0)
			{
				while (n-- > 0)
				{
					T last = l.get(l.size() - 1);

					// indexes increase
					for (int i = l.size() - 1; i > 0; --i)
					{
						l.set(i, l.get(i - 1));
					}

					// last becomes first
					l.set(0, last);
				}
			}
			else if (n < 0)
			{
				while (n++ < 0)
				{
					T first = l.get(0);

					// elements index decrease
					for (int i = 0; i < l.size() - 1; ++i)
					{
						l.set(i, l.get(i + 1));
					}

					// first goes to the end
					l.set(l.size() - 1, first);
				}
			}
		}
	}

	public static <T> T pickRandomObject(List<T> c, Random random)
	{
		if (random == null)
			throw new NullPointerException();

		if (c.isEmpty())
			throw new IllegalArgumentException("list is empty");

		int randomIndex = random.nextInt(c.size());
		return c.get(randomIndex);
	}

	public static <T> List<T> concatene(List<T>... lists)
	{
		List<T> r = new ArrayList<T>();

		for (List<T> l : lists)
		{
			r.addAll(l);
		}

		return r;
	}

	public static <E> List<E> singleton(E element)
	{
		List<E> c = new ArrayList();
		c.add(element);
		return c;
	}

	public static <E> List<E> reverse(List<E> l)
	{
		List<E> r = Clazz.makeInstance(l.getClass());
		r.addAll(l);
		Collections.reverse(l);
		return r;
	}

	public static <T> List<T> getRandomSubset(List<T> elements,
			int numberOfElementsRequested, Random random)
	{
		if (numberOfElementsRequested > elements.size())
			throw new IllegalArgumentException("you ask for too much");

		return getElementAtIndexes(elements,
				getRandomIndexes(elements.size(), numberOfElementsRequested, random));
	}

	public static <T> List<T> getElementAtIndexes(List<T> elements,
			Collection<Integer> indexes)
	{
		List<T> res = new ArrayList<T>();

		for (int i : indexes)
		{
			res.add(elements.get(i));
		}

		return res;
	}

	private static <T> Collection<Integer> getRandomIndexes(int numberOfElements,
			int numberOfIndicesRequested, Random random)
	{
		Collection<Integer> indexes = new HashSet<Integer>();

		while (indexes.size() < numberOfIndicesRequested)
		{
			indexes.add(
					(int) MathsUtilities.pickRandomBetween(0, numberOfElements, random));
		}

		return indexes;
	}

	public static <T> Set<T> findDuplicatesIn(List<T> c)
	{
		int sz = c.size();
		Set<T> duplicates = new HashSet<>();

		for (int i = 0; i < sz; ++i)
		{
			T o = c.get(i);

			for (int j = i + 1; j < sz; ++j)
			{
				if (c.get(j) == o)
				{
					duplicates.add(o);
					break;
				}
			}
		}

		return duplicates;
	}
}
