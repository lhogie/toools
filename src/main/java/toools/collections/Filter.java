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

import toools.math.Interval;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author luc.hogie
 */
public interface Filter<T>
{
	boolean accept(T o);

	public static Filter ACCEPT_ALL = new Filter()
	{

		public boolean accept(Object o)
		{
			return true;
		}
	};

	public static class ClassFilter<T> implements Filter<Class<T>>
	{
		private Class<T> clazz;

		public ClassFilter(Class<T> c)
		{
			this.clazz = c;
		}

		public boolean accept(Class<T> o)
		{
			return clazz.isInstance(o);
		}
	}

	public static class FilterObjectByClass<T> implements Filter<Object>
	{
		private final Class<T> clazz;

		public FilterObjectByClass(Class<T> c)
		{
			this.clazz = c;
		}

		public boolean accept(Object o)
		{
			return clazz.isAssignableFrom(o.getClass());
		}
	}

	public static class StringFilter implements Filter<String>
	{
		private String re;

		public StringFilter(String re)
		{
			this.re = re;
		}

		public boolean accept(String o)
		{
			return o.matches(re);
		}
	}

	public static class IntervalFilter implements Filter<Double>
	{
		private Interval i;

		public IntervalFilter(Interval i)
		{
			this.i = i;
		}

		public boolean accept(Double o)
		{
			return i.include(o);
		}
	}

}
