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
 
 package toools.collections.relation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractRelation<A, B> implements Relation<A, B>
{

	/* the name of the relation */
	private String name;

	@Override
	public String getDescription()
	{
		return name;
	}

	@Override
	public void setDescription(String name)
	{
		this.name = name;
	}

	@Override
	synchronized public boolean isDefined(A key, B value)
	{
		return getValues(key).contains(value);
	}

	@Override
	public boolean isDefined(A key)
	{
		return getValues(key) != null;
	}

	@Override
	public void addAll(A key, Collection<B> collection)
	{
		if (collection == null) throw new NullPointerException("null set");

		if (collection.isEmpty())
		{
			add(key);
		}
		else
		{
			for (B v : collection)
			{
				add(key, v);
			}
		}
	}

	@Override
	public void addAll(Relation<A, B> r)
	{
	    for (A k : r.keySet())
	    {
		addAll(k, r.getValues(k));
	    }
	}
	
	@Override
	synchronized public B getValue(A key)
	{
		Collection<B> values = getValues(key);

		if (values == null)
		{
			return null;
		}
		else
		{
			if (values.isEmpty())
			{
				throw new IllegalStateException("no value is available for key '" + key);
			}
			else
			{
				if (values.size() > 1)
				{
					throw new IllegalStateException("several values are available for key '" + key + "'. don't know which to return");
				}
				else
				{
					return values.iterator().next();
				}
			}
		}
	}

	public Collection<Collection<B>> getValues()
	{
		Collection<Collection<B>> c = new HashSet<Collection<B>>();

		for (A key : keySet())
		{
			c.add(getValues(key));
		}

		return c;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Relation)
		{
			Relation relation = (Relation) object;

			if (relation == this)
			{
				return true;
			}
			else
			{
				Collection<A> keys = keySet();
				Collection<A> relationKeys = relation.keySet();

				if (keys.equals(relationKeys))
				{
					Iterator<A> iterator = keys.iterator();

					while (iterator.hasNext())
					{
						A key = iterator.next();
						Collection<B> values = getValues(key);
						Collection<B> relationValues = relation.getValues(key);

						if (!values.equals(relationValues))
						{
							return false;
						}
					}

					return true;
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			RelationIO.save(this, bos);
			return new String(bos.toByteArray());
		}
		catch (IOException ex)
		{
			throw new IllegalStateException();
		}
	}

	@Override
	public Map<A, B> toMap(Map<A, B> map)
	{
		for (A k : keySet())
		{
			Collection<B> v = getValues(k);

			if (v.size() == 1)
			{
				map.put(k, v.iterator().next());
			}
			else
			{
				throw new IllegalStateException("more than one value for key: " + k);
			}
		}

		return map;
	}

	public boolean isInjective()
	{
		Relation<B, A> inverse = getInverseRelation();
		Iterator<B> i = inverse.keySet().iterator();

		while (i.hasNext())
		{
			Collection c = inverse.getValues(i.next());

			if (c.size() > 1)
			{
				return false;
			}
		}

		return true;
	}

	public boolean isFunction()
	{
		Iterator<A> i = keySet().iterator();

		while (i.hasNext())
		{
			Collection<B> values = getValues(i.next());

			if (values.size() > 1)
			{
				return false;
			}
		}

		return true;
	}

	// @Override
	// public Object clone()
	// {
	// try
	// {
	// return super.clone();
	// }
	// catch (CloneNotSupportedException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// }
	// }

	@Override
	public Object clone()
	{
		try
		{
			Relation<A, B> relation = (Relation<A, B>) getClass().newInstance();

			for (A key : keySet())
			{
				relation.add(key);

				for (B value : getValues(key))
				{
					relation.add(key, value);
				}
			}

			return relation;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			throw new IllegalStateException("cannot clone");
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			throw new IllegalStateException("cannot clone");
		}
	}

	/**
	 * The value container is user-definable because there is different needs: -
	 * if you need very fast access that do not depend on the amount of stored
	 * objets, you may not override this method and so use a java.util.HashSet
	 * container. - if you need a relation order (on the add() invocations), you
	 * will have to use a container that handle order, such as java.util.Vector
	 * or java.util.LinkedList.
	 * 
	 * Generics cannot be used here because one container will used for storing
	 * the keys, and another one will be used to store the values.
	 * 
	 * @return a collection that will be used to store the values for a given
	 *         key.
	 */
	public Collection<?> createContainer()
	{
		return new ArrayList();
	}
}