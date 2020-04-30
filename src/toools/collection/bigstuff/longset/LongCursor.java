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

package toools.collection.bigstuff.longset;

import java.util.Iterator;

import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;

public class LongCursor
{
	public long value;

	public static Iterable<LongCursor> fromHPPC(
			Iterable<com.carrotsearch.hppc.cursors.LongCursor> iterable)
	{

		return new Iterable<LongCursor>()
		{
			Iterator<com.carrotsearch.hppc.cursors.LongCursor> i = iterable.iterator();

			@Override
			public Iterator<LongCursor> iterator()
			{

				return new Iterator<LongCursor>()
				{
					final LongCursor cursor = new LongCursor();

					@Override
					public boolean hasNext()
					{
						return i.hasNext();
					}

					@Override
					public LongCursor next()
					{
						cursor.value = i.next().value;
						return cursor;
					}

				};
			}
		};
	}

	public static Iterable<LongCursor> fromFastUtil(LongIterable iterable)
	{
		return new Iterable<LongCursor>()
		{

			@Override
			public Iterator<LongCursor> iterator()
			{
				it.unimi.dsi.fastutil.longs.LongIterator fui = iterable.iterator();

				return new Iterator<LongCursor>()
				{
					final LongCursor cursor = new LongCursor();

					@Override
					public boolean hasNext()
					{
						return fui.hasNext();
					}

					@Override
					public LongCursor next()
					{
						cursor.value = fui.nextLong();
						return cursor;
					}
				};

			};
		};
	}

	public static Iterable<LongCursor> fromLongIterator(LongIterator i)
	{
		return new Iterable<LongCursor>()
		{

			@Override
			public Iterator<LongCursor> iterator()
			{

				return new Iterator<LongCursor>()
				{
					final LongCursor cursor = new LongCursor();

					@Override
					public boolean hasNext()
					{
						return i.hasNext();
					}

					@Override
					public LongCursor next()
					{
						cursor.value = i.next();
						return cursor;
					}
				};

			};
		};
	}
}