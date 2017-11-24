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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import toools.collections.Lists;

/**
 * Launch several threads to execute the {@link #runThread(int, List)} function
 * in parallel. Correct usage of this class is to create a subclass of it, which
 * defines the runThread() function. The processing is synchronous with respect
 * to the caller of the constructor of this class. The constructor exits when
 * all the threads have exited.<br>
 * 
 * If only one thread is required, the computation will happen in the current
 * thread: no new thread will be created.
 * 
 * @author lhogie
 *
 */

public abstract class MultiThreadProcessing
{
	private final List<Thread> threads = new ArrayList<Thread>();
	private AtomicInteger finishedThreads = new AtomicInteger(0);

	private final MultiThreadingException errors = new MultiThreadingException();

	public MultiThreadProcessing()
	{
		this(new NCoresNThreadsPolicy(2));
	}

	public MultiThreadProcessing(MultiThreadPolicy policy)
	{
		this(policy.getNbThreads());
	}

	/**
	 * 
	 * @param rank
	 *            the rank of the thread, from 0 to the number of threads minus
	 *            one.
	 * @param threads
	 *            the list of all threads in the set. The current thread is a
	 *            member of this list.
	 * @throws Throwable
	 */
	protected abstract void runThread(int rank,
			@SuppressWarnings("hiding") List<Thread> threads) throws Throwable;

	public MultiThreadProcessing(int nbThreads)
	{
		this(nbThreads, MultiThreadProcessing.class.getName());
	}

	public MultiThreadProcessing(int nbThreads, String threadName)
	{
		// if only 1 thread is required, we can use the current thread
		if (nbThreads == 1)
		{
			try
			{
				runThread(0, Lists.singleton(Thread.currentThread()));
			}
			catch (Throwable t)
			{
				errors.getThreadLocalExceptions().add(t);
				throw errors;
			}
		}
		else
		{
			// first instantiate the threads
			for (int rank = 0; rank < nbThreads; ++rank)
			{
				_Thread thread = new _Thread(rank);
				thread.setName(threadName + "-" + rank);
				threads.add(thread);
			}

			// then start them later because they need the complete thread list
			for (Thread thread : threads)
			{
				thread.start();
			}

			synchronized (this)
			{
				while (finishedThreads.get() < nbThreads)
				{
					try
					{
						this.wait();
					}
					catch (InterruptedException e)
					{
						errors.getThreadLocalExceptions().add(e);
						throw errors;
					}
				}
			}
			// All threads are finished, join with them to cleanup.
			for (Thread thread : threads)
			{
				try
				{
					thread.join();
				}
				catch (InterruptedException e)
				{
					errors.getThreadLocalExceptions().add(e);
					throw errors;
				}
			}
			// if some errors happened in threads
			if ( ! errors.getThreadLocalExceptions().isEmpty())
			{
				throw errors;
			}
		}
	}

	private class _Thread extends Thread
	{
		private final int rank;

		public _Thread(int rank)
		{
			this.rank = rank;
		}

		@Override
		public void run()
		{
			try
			{
				runThread(rank, threads);
			}
			catch (Throwable t)
			{
				synchronized (MultiThreadProcessing.this)
				{
					errors.getThreadLocalExceptions().add(t);
				}
			}
			finally
			{
				synchronized (MultiThreadProcessing.this)
				{
					finishedThreads.incrementAndGet();
					// There is only one waiter use notify() rather than
					// notifyAll()
					MultiThreadProcessing.this.notify();
				}
			}
		}
	}

	public static void main(String[] args) throws Throwable
	{
		for (int i = 0; i < 1000; ++i)
		{
			new MultiThreadProcessing(2)
			{
				@Override
				protected void runThread(int rank, List<Thread> threads) throws Throwable
				{
					// Threads.sleepMs(500);
				}
			};

			System.out.println(i + " completed");
		}

		System.out.println("it works!");
	}
}
