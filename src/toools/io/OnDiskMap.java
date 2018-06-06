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

package toools.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import toools.exceptions.NotYetImplementedException;
import toools.io.file.AbstractFile;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.io.serialization.Serializer;

public class OnDiskMap<K, V> implements Map<K, V>
{

	private final Directory location;

	public OnDiskMap(Directory location)
	{
		if (location == null)
			throw new NullPointerException();

		this.location = location;

		if ( ! location.exists())
		{
			location.mkdirs();
		}
	}

	@Override
	public void clear()
	{
		for (AbstractFile f : location.getChildren())
		{
			f.delete();
		}
	}

	@Override
	public boolean containsKey(Object arg0)
	{
		return location.getChild(arg0.toString(), RegularFile.class).exists();
	}

	@Override
	public boolean containsValue(Object value)
	{
		throw new NotYetImplementedException();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public V get(Object key)
	{
		RegularFile f = getFileForObject(key);

		if (f == null || ! f.exists())
		{
			return null;
		}
		else
		{
			return (V) Serializer.getDefaultSerializer().fromBytes(f.getContent());
		}
	}

	public static void main(String[] args) throws IOException
	{
		OnDiskMap<Object, Object> db = new OnDiskMap<Object, Object>(
				new Directory(System.getProperty("user.home") + "/coucou"));
		db.put("coucou", new ArrayList());
		System.out.println(db.get("coucou"));
		db.delete();
	}

	public void delete()
	{
		getLocation().delete();
	}

	private RegularFile getFileForObject(Object id)
	{
		return new RegularFile(getLocation().getPath() + '/' + id.toString());
	}

	public Directory getLocation()
	{
		return location;
	}

	@Override
	public boolean isEmpty()
	{
		return getLocation().getChildren().isEmpty();
	}

	@Override
	public Set<K> keySet()
	{
		throw new NotYetImplementedException();
	}

	@Override
	public V put(K key, V value)
	{
		getFileForObject(key)
				.setContent(Serializer.getDefaultSerializer().toBytes(value));
		return value;

	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{

	}

	@Override
	public V remove(Object key)
	{
		V v = get(key);
		getFileForObject(key).delete();
		return v;
	}

	@Override
	public int size()
	{
		return getLocation().getChildren().size();
	}

	@Override
	public Collection<V> values()
	{
		throw new NotYetImplementedException();
	}

}
