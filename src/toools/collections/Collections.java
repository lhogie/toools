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
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import toools.math.MathsUtilities;
import toools.reflect.Clazz;
import toools.reflect.beans.Bean;
import toools.reflect.beans.BeanProperty;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public class Collections
{
	public static final List<?> emptyCollection = java.util.Collections
			.unmodifiableList(new ArrayList<>());

	@SafeVarargs
	public static <V> Collection<V> getFirstSetContaining(V v, Collection<V>... set)
	{
		for (Collection<V> s : set)
		{
			if (s.contains(v))
			{
				return s;
			}
		}

		return null;
	}

	public static <E> Collection<E> singleton(E element)
	{
		Collection<E> c = new HashSet<E>();
		c.add(element);
		return c;
	}

	public static <T> Collection<Collection<T>> getLargestCollections(
			Collection<? extends Collection<T>> collections)
	{
		Iterator<? extends Collection<T>> i = collections.iterator();
		int largestSize = i.next().size();

		while (i.hasNext())
		{
			Collection<T> thisCollection = i.next();

			if (thisCollection.size() > largestSize)
			{
				largestSize = thisCollection.size();
			}
		}

		return getCollectionsOfSize(collections, largestSize);
	}

	public static <T> Collection<Collection<T>> getCollectionsOfSize(
			Collection<? extends Collection<T>> collections, int requestedSize)
	{
		Collection<Collection<T>> result = new ArrayList<Collection<T>>();

		for (Collection<T> thisCollection : collections)
		{
			if (thisCollection.size() == requestedSize)
			{
				result.add(thisCollection);
			}
		}

		return result;
	}

	public static <E> List<Collection<Collection<E>>> combine(Collection<E> elements,
			int stop)
	{
		List<Collection<Collection<E>>> combinaisons = new ArrayList<Collection<Collection<E>>>();

		// adds the combinaisons with 0 elements
		Collection<Collection<E>> a0 = new HashSet<Collection<E>>();
		a0.add(new HashSet<E>());
		combinaisons.add(a0);

		for (int i = 1; i <= stop; ++i)
		{
			Collection<Collection<E>> newCombinaisons = new ArrayList<Collection<E>>();

			for (E e : elements)
			{
				for (Collection<E> thisCombinaison : combinaisons.get(i - 1))
				{
					Collection<E> c = new ArrayList<E>();
					c.addAll(thisCombinaison);

					// if (!c.contains(e))
					{
						c.add(e);
						newCombinaisons.add(c);
					}
				}
			}

			combinaisons.set(i - 1, null);
			combinaisons.add(newCombinaisons);
		}

		return combinaisons;
	}

	/*
	 * Returns a list of the elements at the given index in the given lists
	 */
	public static <T> List<T> getElementsAt(Collection<List<T>> lists, int index)
	{
		List<T> res = new Vector<T>();

		for (List<T> list : lists)
		{
			res.add(list.get(index));
		}

		return res;
	}

	public static List<Collection<?>> sortBySize(Collection<Collection<?>> collections)
	{
		List<Collection<?>> list = new Vector<Collection<?>>(collections);
		java.util.Collections.sort(list, new Comparator<Collection<?>>()
		{

			@Override
			public int compare(Collection<?> c1, Collection<?> c2)
			{
				return new Integer(c1.size()).compareTo(new Integer(c2.size()));
			}

		});

		return list;
	}

	public static <T> List<T> convertEnumerationToList(Enumeration<T> e)
	{
		List<T> c = new ArrayList<T>();

		while (e.hasMoreElements())
		{
			c.add(e.nextElement());
		}

		return c;
	}

	public static <T> List<T> convertIteratorToList(Iterator<T> e)
	{
		List<T> c = new ArrayList<T>();

		while (e.hasNext())
		{
			c.add(e.next());
		}

		return c;
	}

	public static <T> Collection<T> filter(Collection<T> inputSet, Filter<T> filter)
	{
		@SuppressWarnings("unchecked")
		Collection<T> output = Clazz.makeInstance(inputSet.getClass());
		filter(inputSet, filter, output);
		return output;
	}

	public static <T> void filter(Collection<T> inputSet, Filter<T> filter,
			Collection<T> output)
	{
		for (T thisElement : inputSet)
		{
			if (filter.accept(thisElement))
			{
				output.add(thisElement);
			}
		}
	}

	@SafeVarargs
	public static <T, E extends Collection<T>> Set<T> union(E... sets)
	{
		Set<T> c = new HashSet<T>();

		for (Collection<T> set : sets)
		{
			c.addAll(set);
		}

		return c;
	}

	@SafeVarargs
	public static <T, E extends Collection<T>> Set<T> unionTo(
			Class<? extends Collection<T>> targetClass, E... sets)
	{
		Set<T> target = (Set<T>) Clazz.makeInstance(targetClass);

		for (Collection<T> set : sets)
		{
			target.addAll(set);
		}

		return target;
	}

	/*
	 * slow method!!!
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T> Collection<T> intersection(Collection<T>... sets)
	{
		Set<T> t = new HashSet<T>();
		intersectionToTarget(t, sets);
		return t;
	}

	@SafeVarargs
	private static <T> void intersectionToTarget(Collection<T> target,
			Collection<T>... sets)
	{
		target.addAll(sets[0]);

		for (int i = 1; i < sets.length; ++i)
		{
			target.retainAll(sets[i]);
		}
	}

	@SafeVarargs
	public static <T> Set<T> difference(Collection<T> a, Collection<T>... b)
	{
		Set<T> c = new HashSet<T>(a);

		for (Collection<T> _b : b)
		{
			c.removeAll(_b);
		}

		return c;
	}

	public static <T> Set<T> difference(Collection<T> a, T e)
	{
		Set<T> c = new HashSet<T>(a);
		c.remove(e);
		return c;
	}

	public static <T> Collection<T> pickRandomSubset(Collection<T> c, Random random)
	{
		int n = (int) MathsUtilities.pickRandomBetween(0, c.size(), random);
		return pickRandomSubset(c, n, false, random);
	}

	public static <T> Collection<T> pickRandomSubset(Collection<T> c, int size,
			boolean allowDuplicates, Random random)
	{
		if (size > c.size())
			throw new IllegalArgumentException("too many elements requested");

		Collection<T> result = allowDuplicates ? new ArrayList<T>() : new HashSet<T>();

		while (size-- > 0)
		{
			result.add(pickRandomObject(c, random));
		}

		return result;
	}

	public static <T> T getRandomObjectNot(Collection<T> c, Random random,
			Collection<T> forbiddenValues)
	{
		while (true)
		{
			T randomElement = pickRandomObject(c, random);

			if ( ! forbiddenValues.contains(randomElement))
			{
				return randomElement;
			}
		}
	}

	public static <T> T pickRandomObject(Collection<T> c, Random random)
	{
		if (random == null)
			throw new NullPointerException();

		if (c.isEmpty())
			throw new IllegalArgumentException("collection is empty");

		int pos = (int) MathsUtilities.pickRandomBetween(0, c.size(), random);
		Iterator<T> i = c.iterator();

		while (pos-- > 0)
		{
			i.next();
		}

		return i.next();
	}

	public static <E> Collection<E> find(Collection<E> set, String propertyName,
			Object value)
	{
		Collection<E> c = new Vector<E>();

		for (E e : set)
		{
			Object v = new Bean(e.getClass()).getProperties().get(propertyName)
					.getValue(e);

			if (v.equals(value))
			{
				c.add(e);
			}
		}

		return c;
	}

	public static <E> E ensureSingleton(Collection<E> set)
	{
		if (set.size() == 1)
		{
			return set.iterator().next();
		}
		else
		{
			throw new IllegalArgumentException("set is not a singleton");
		}
	}

	/*
	 * public static <T> Collection<T> getRandomSubset(Collection<T> elements,
	 * int numberOfElementsRequested, Random random) { if
	 * (numberOfElementsRequested > elements.size()) throw new
	 * IllegalArgumentException("you ask for too much");
	 * 
	 * return getElementAtIndexes(elements, getRandomIndexes(elements.size(),
	 * numberOfElementsRequested, random)); }
	 */

	@SuppressWarnings("unused")
	private static <T> Collection<T> getElementAtIndexes(Collection<T> elements,
			Collection<Integer> indexes)
	{
		List<T> res = new ArrayList<T>();
		int i = 0;

		for (T t : elements)
		{
			if (indexes.contains(i++))
			{
				res.add(t);
			}
		}

		return res;
	}

	/**
	 * Sort the given lists according to their element of given index.
	 * 
	 * @param src
	 * @param index
	 * @return
	 */
	public static List<List<?>> sort(Collection<List<?>> src, final int index)
	{
		List<List<?>> dest = new ArrayList<List<?>>(src);

		java.util.Collections.sort(dest, new Comparator<List<?>>()
		{
			@Override
			public int compare(List<?> o1, List<?> o2)
			{
				return MathsUtilities.compare(o1.get(index), o2.get(index));
			}
		});

		return dest;
	}

	/**
	 * Sort the given list according to their element of given property.
	 * 
	 * @param src
	 * @param index
	 * @return
	 */
	public static <E> List<E> sort(List<E> src, String propertyName)
	{
		final BeanProperty prop = new Bean(src.iterator().next().getClass())
				.getProperties().get(propertyName);
		List<E> dest = new ArrayList<E>(src);

		java.util.Collections.sort(dest, new Comparator<E>()
		{
			@Override
			public int compare(E e, E f)
			{
				return MathsUtilities.compare(prop.getValue(e), prop.getValue(f));
			}
		});

		return dest;
	}

	public static <E> List<E> findElementsWhosePropertyValueMatches(Collection<E> src,
			String propertyName, String value)
	{
		final BeanProperty prop = new Bean(src.iterator().next().getClass())
				.getProperties().get(propertyName);
		List<E> dest = new ArrayList<E>(src);

		for (E e : src)
		{
			if (prop.getValue(e).equals(value))
			{
				dest.add(e);
			}
		}

		return dest;
	}

	public static void reverse(int[] b, int a, int z)
	{
		for (int left = a, right = z - 1; left < right; left++, right--)
		{
			// exchange the first and last
			int temp = b[left];
			b[left] = b[right];
			b[right] = temp;
		}

	}

	public static void reverse(long[] b, int a, int z)
	{
		for (int left = a, right = z - 1; left < right; left++, right--)
		{
			// exchange the first and last
			long temp = b[left];
			b[left] = b[right];
			b[right] = temp;
		}

	}

	public static void reverse(int[] b)
	{
		reverse(b, 0, b.length);
	}

	public static void reverse(long[] b)
	{
		reverse(b, 0, b.length);
	}

	public static String toString(Collection<?> c, String separator)
	{
		StringBuilder b = new StringBuilder();

		Iterator<?> i = c.iterator();

		while (i.hasNext())
		{
			b.append(i.next().toString());

			if (i.hasNext())
			{
				b.append(separator);
			}
		}

		return b.toString();
	}

	public static IntArrayList toArrayList(String s)
	{
		IntArrayList r = new IntArrayList();

		for (String t : s.replaceAll("[^0-9]", " ").trim().split(" +"))
		{
			r.add(Integer.parseInt(t));
		}

		return r;
	}

}
