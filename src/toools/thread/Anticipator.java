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
 
 package toools.thread;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * An antipator object represents a function can compute things asynchronously to the requesting threads. Computed values are made available through a blocking queue.
 * The capacity of the queue is user-defined.  
 * @author lhogie
 *
 * @param <T>
 */

public abstract class Anticipator<T> extends Producer<T>
{
	private final ArrayBlockingQueue<T> queue;

	public Anticipator(int size)
	{
		queue = new ArrayBlockingQueue<>(size);
	}

	@Override
	public final void deliver(T v)
	{
		try
		{
			queue.put(v);
		}
		catch (InterruptedException e)
		{
			throw new IllegalStateException();
		}
	}

	@Override
	public final Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			{
				internalThread.start();
			}

			private T currentValue;

			@Override
			public boolean hasNext()
			{
				try
				{
					this.currentValue = queue.take();
				}
				catch (InterruptedException e)
				{
					throw new IllegalStateException();
				}

				return ! isTerminaisonValue(currentValue);
			}

			@Override
			public T next()
			{
				return currentValue;
			}
		};
	}
}
