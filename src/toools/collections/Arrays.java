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

public class Arrays
{
	public static final int[] emptyIntArray = new int[0];
	public static final long[] emptyLongArray = new long[0];

	public static <T> int numberOfDiffernces(T[] a, T[] b)
	{
		int differenceInSize = Math.abs(a.length - b.length);
		int shortestLength = Math.min(a.length, b.length);
		int differenceInContent = 0;

		for (int i = 0; i < shortestLength; ++i)
		{
			if ( ! a[i].equals(b[i]))
			{
				++differenceInContent;
			}
		}

		return differenceInContent + differenceInSize;
	}

	public static <T> int numberOfDiffernces(int[] a, int[] b)
	{
		int differenceInSize = Math.abs(a.length - b.length);
		int shortestLength = Math.min(a.length, b.length);
		int differenceInContent = 0;

		for (int i = 0; i < shortestLength; ++i)
		{
			if (a[i] != b[i])
			{
				++differenceInContent;
			}
		}

		return differenceInContent + differenceInSize;
	}

	public static void reverse(int[] array)
	{
		int sz = array.length;
		int t = sz / 2;

		for (int i = 0; i < t; ++i)
		{
			int tmp = array[i];
			array[i] = array[sz - i - 1];
			array[sz - i - 1] = tmp;
		}
	}

	public static void reverse(long[] array)
	{
		int sz = array.length;
		int t = sz / 2;

		for (int i = 0; i < t; ++i)
		{
			long tmp = array[i];
			array[i] = array[sz - i - 1];
			array[sz - i - 1] = tmp;
		}
	}

	public static boolean contains(int[] a, int e)
	{
		return indexOf(a, e) >= 0;
	}

	public static boolean contains(long[] a, long e)
	{
		return indexOf(a, e) >= 0;
	}

	public static int indexOf(int[] a, int e)
	{
		for (int i = 0; i < a.length; ++i)
		{
			if (a[i] == e)
			{
				return i;
			}
		}

		return - 1;
	}

	public static int indexOf(long[] a, long e)
	{
		for (int i = 0; i < a.length; ++i)
		{
			if (a[i] == e)
			{
				return i;
			}
		}

		return - 1;
	}

	public static Object[] concatene(Object[] a, Object[] b)
	{
		Object[] r = java.util.Arrays.copyOf(a, a.length + b.length);

		for (int i = 0; i < b.length; ++i)
		{
			r[i + a.length] = b[i];
		}

		return r;
	}

}
