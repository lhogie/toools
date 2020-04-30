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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class IndependantObjectMultiThreadProcessing<T>
{
	public IndependantObjectMultiThreadProcessing(Collection<T> input)
	{
		this(input, new NCoresNThreadsPolicy(2));
	}

	public IndependantObjectMultiThreadProcessing(Collection<T> input,
			MultiThreadPolicy policy)
	{
		this(input, policy.getNbThreads());
	}

	public IndependantObjectMultiThreadProcessing(Collection<T> input, int nbThreads)
	{
		final ConcurrentIterator<T> i = new ConcurrentIterator<>(input);

		new MultiThreadProcessing(nbThreads, null)
		{
			@Override
			protected void runInParallel(ThreadSpecifics s) throws Throwable
			{
				while (true)
				{
					T next = i.next();

					if (next == null)
						break;

					process(next);
				}
			}
		}.execute();
	}

	protected abstract void process(T element) throws Throwable;

	public static void main(String[] args) throws Throwable
	{
		List<String> l = new ArrayList<>();

		l.add("couc");
		l.add("luc");
		l.add("anne");

		new IndependantObjectMultiThreadProcessing<String>(l, new NThreadsPolicy(2))
		{

			@Override
			protected void process(String o)
			{
				System.out.println(o.length());
				Threads.sleepMs(1000);
			}
		};

		System.out.println("completed");
	}

}
