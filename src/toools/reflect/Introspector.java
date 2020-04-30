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

package toools.reflect;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Introspector
{
	public static class FF
	{
		private final Field f;

		public FF(Field f)
		{
			this.f = f;
		}

		public String getName()
		{
			return f.getName();
		}

		public boolean is(int m)
		{
			return (f.getModifiers() & m) != 0;
		}

		public Object get(Object target)
		{
			try
			{
				return f.get(target);
			}
			catch (Throwable e)
			{
				throw new IllegalStateException(e);
			}
		}

		public void setFieldValue(Object target, Object value)
		{
			try
			{
				f.set(target, value);
			}
			catch (Throwable e)
			{
				throw new IllegalStateException(e);
			}
		}

		public Class<?> getType()
		{
			return f.getType();
		}
	}

	private final Map<String, FF> fields = new HashMap<>();

	private Introspector(Class<?> c)
	{
		for (Field f : c.getDeclaredFields())
		{
			f.setAccessible(true);
			fields.put(f.getName(), new FF(f));
		}

		// adds also the fields declared in the superclass, if any
		if (c.getSuperclass() != null)
		{
			for (FF f : getIntrospector(c.getSuperclass()).getFields().values())
			{
				fields.put(f.getName(), f);
			}
		}
	}

	public Map<String, FF> getFields()
	{
		return fields;
	}

	public static final Map<Class<?>, Introspector> map = new HashMap();

	public static Introspector getIntrospector(Class<?> c)
	{
		Introspector b = map.get(c);

		if (b == null)
		{
			map.put(c, b = new Introspector(c));
		}

		return b;
	}
}
