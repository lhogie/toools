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
 
 package toools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Introspector
{

	private final Map<String, Field> fields = new HashMap<String, Field>();

	private Introspector(Class<?> c)
	{
		for (Field f : c.getDeclaredFields())
		{
			// if this field is neither transient, static, nor excluded by
			// the user
			if ((f.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC)) == 0)
			{
				f.setAccessible(true);
				fields.put(f.getName(), f);
			}
		}

		// adds also the fields declared in the superclass, if any
		if (c.getSuperclass() != null)
		{
			for (Field f : getIntrospector(c.getSuperclass()).getFields().values())
			{
				fields.put(f.getName(), f);
			}
		}
	}

	public Map<String, Field> getFields()
	{
		return fields;
	}

	public Object getFieldValue(Field f, Object o)
	{
		try
		{
			return f.get(o);
		}
		catch (Throwable e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void setFieldValue(Field f, Object targetObject, Object value)
	{
		try
		{
			f.set(targetObject, value);
		}
		catch (Throwable e)
		{
			throw new IllegalStateException(e);
		}
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
