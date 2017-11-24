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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of the Relation interface. This implementation relies
 * on 2 symmetric java.util.HashMap.
 * 
 * Be very careful when you use some object that have not a fixed hash code:
 * this is the case, for instance, of all the collections for which the hash
 * code depends on the content.
 * 
 * If the hash code of some of the contained objects changes, the relation WILL
 * NOT WORK anymore! Be very careful.
 * 
 * @author Luc Hogie
 * @version 1.0
 */

public class HashRelation<A, B> extends AbstractRelation<A, B>
{


	private final Collection<?> emptyCollection = Collections.unmodifiableCollection(new HashSet());

	/* to get the values set with the key */
	private final Map<A, Collection<B>> key_values;

	/* to get the keys set with the value */
	private final Map<B, Collection<A>> value_keys;

	/* the inverse relation */
	private Relation<B, A> inverse;

	public HashRelation()
	{
		key_values = createMaps();
		value_keys = createMaps();
		inverse = new HashRelation<B, A>(this);
	}

	protected Map createMaps()
	{
		return new HashMap();
	}
	
	public static final <A, B> Relation<A, B> create(Map<A, B> map)
	{
		Relation<A, B> r = new HashRelation<A, B>();

		for (A key : map.keySet())
		{
			r.add(key, map.get(key));
		}

		return r;
	}

	/**
	 * Used to construct inverse relation.
	 */
	private HashRelation(HashRelation<B, A> inverse)
	{
		key_values = inverse.value_keys;
		value_keys = inverse.key_values;
		this.inverse = inverse;
	}

	@Override
	public int hashCode()
	{
		return this.key_values.hashCode();
	}

	/**
	 * @return the inverse of the table. Be careful. Changing the content of the
	 *         inverse table will affect the content of this relation.
	 */
	@Override
	public Relation<B, A> getInverseRelation()
	{
		return inverse;
	}

	/**
	 * @return the keys of the relation.
	 */
	@Override
	public Collection<A> getKeys()
	{
		return Collections.unmodifiableSet(key_values.keySet());
	}

	/**
	 * @return the objects associated to the given key.
	 */
	@Override
	public Collection<B> getValues(A key)
	{
		Collection<B> values = key_values.get(key);

		if (values == null)
		{
			return null;
		}
		else
		{
			if (values instanceof List)
			{
				return Collections.unmodifiableList((List) values);
			}
			else if (values instanceof Set)
			{
				return Collections.unmodifiableSet((Set) values);
			}
			else
			{
				return Collections.unmodifiableCollection(values);
			}
		}
	}

	@Override
	public void add(A key)
	{
		if (getValues(key) != null) throw new IllegalArgumentException("key is already defined: " + key);

		key_values.put(key, (Collection<B>) createContainer());
	}

	/**
	 * Adds the key/value relation in the table.
	 */
	@Override
	public void add(A key, B value)
	{
		// gets the values that are referred by the key
		Collection<B> values = (Collection<B>) key_values.get(key);

		// if no value is associated to the key, the
		// values set does not exist, have to be created
		if (values == null)
		{
			values = (Collection<B>) createContainer();
			key_values.put(key, values);
		}

		values.add(value);

		// gets the keys that refer the value
		Collection<A> keys = value_keys.get(value);

		// if the value was not already associated to a key
		if (keys == null)
		{
			keys = (Collection<A>) createContainer();
			value_keys.put(value, keys);
		}

		keys.add(key);
	}

	/**
	 * Removes the (key, value) pair from the relation.
	 */
	@Override
	public void remove(A key, B value)
	{
		Collection<B> values = key_values.get(key);

		// if key is not defined
		if (values == null)
		{
			throw new IllegalArgumentException("key " + key + " is undefined");
		}
		else
		{
			// if an association has been found
			if (values.remove(value))
			{
				// remove the value->key relation in the symmetric table
				if (!value_keys.get(value).remove(key))
					throw new IllegalStateException();

				// if there's no other value referred by the key
				// the entry can be removed
				if (value_keys.get(value).isEmpty())
				{
					value_keys.remove(value);
				}
			}
			else
			{
				throw new IllegalArgumentException("key " + key + " is not related to " + value);
			}
		}
	}

	@Override
	public void remove(A key)
	{
		if (!isDefined(key))
			throw new IllegalArgumentException("key " + key + " is undefined");

		Collection<B> values = key_values.remove(key);

		for (B value : values)
		{
			Collection<A> keys = value_keys.get(value);
			keys.remove(key);

			if (keys.isEmpty())
			{
				value_keys.remove(value);
			}
		}
	}

	@Override
	public Collection<A> getKeysReferingTheEmptySet()
	{
		Collection<A> c = new ArrayList<A>();
		
		for (A k : getKeys())
		{
			if (getValues(k).isEmpty())
			{
				c.add(k);
			}
		}

		return c;
	}

	
	@Override
	public void clear()
	{
		key_values.clear();
		value_keys.clear();
	}

	public static void main(String... args)
	{
		Relation<String, String> r = new HashRelation<String, String>();
		r.add("luc", "courtrai");
		r.add("luc", null);
		r.add("nadege", "hogie");
		r.add(null, ":)");
		r.add("elisa", "hogie");
		r.remove("elisa", "hogie");
		r.add("elisa", "hogie");
		r.add("luc", "hogie");
		r.add("nadine", "hogie");
		r.remove("nadine", "hogie");
//		r.remove("nadine");
		r.remove(null);
		r.remove("luc", "hogie");
		r.remove("luc");

//		System.out.println(r.getKeysReferingTheEmptySet());
		System.out.println(r);

//		System.out.println(r.getInverseRelation());

	}

}
