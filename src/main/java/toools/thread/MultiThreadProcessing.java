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

import toools.io.Cout;
import toools.progression.LongProcess;
import toools.progression.Sensor;

/**
 * Launch several threads to execute the {@link #runInParallel(int, List)}
 * function in parallel. Correct usage of this class is to create a subclass of
 * it, which defines the runThread() function. The processing is synchronous
 * with respect to the caller of the constructor of this class. The constructor
 * exits when all the threads have exited.<br>
 * 
 * If only one thread is required, the computation will happen in the current
 * thread: no new thread will be created.
 * 
 * @author lhogie
 *
 */

public abstract class MultiThreadProcessing {
	public static int NB_THREADS_TO_USE = Runtime.getRuntime().availableProcessors() * 2;

	private final List<Thread> threads = new ArrayList<Thread>();
	private final MultiThreadingException errors = new MultiThreadingException();
	private final String threadNamePrefix;
	private final int nbThreads;

	public MultiThreadProcessing() {
		this(NB_THREADS_TO_USE, null);
	}

	public MultiThreadProcessing(int nbThreads, LongProcess lp) {
		this(nbThreads, lp == null ? null : lp.getDescription(), lp);
	}

	public MultiThreadProcessing(int nbThreads, String threadName, LongProcess lp) {
		if (lp != null) {
			lp.sensor = new Sensor() {
				boolean terminated = false;

				@Override
				public double getProgress() {
					return terminated ? progressStatus : progress();
				}

				@Override
				public void set(double target) {
					terminated = true;
					super.set(target);
				}
			};
		}

		this.threadNamePrefix = threadName;
		this.nbThreads = nbThreads;
	}

	public void execute() {
		// first instantiate the threads
		for (int rank = 0; rank < nbThreads; ++rank) {
			ThreadSpecifics s = new ThreadSpecifics(rank);
			s.threads = threads;
			_Thread thread = new _Thread(s);
			thread.setName(threadNamePrefix + "-" + rank);
			threads.add(thread);
		}

		// then start them later because they need the complete thread list
		for (Thread thread : threads) {
			thread.start();
		}

		// All threads are finished, join with them to cleanup and collect
		// exceptions
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				errors.getThreadLocalExceptions().add(e);
				throw errors;
			}
		}

		// if some errors happened in threads
		if (!errors.getThreadLocalExceptions().isEmpty()) {
			throw errors;
		}
	}

	private class _Thread extends Thread {
		private final ThreadSpecifics specifics;

		public _Thread(ThreadSpecifics s) {
			this.specifics = s;
		}

		@Override
		public void run() {
			try {
				runInParallel(specifics);
			} catch (Throwable t) {
				synchronized (MultiThreadProcessing.this) {
					errors.getThreadLocalExceptions().add(t);
				}
			}
		}
	}

	/**
	 * 
	 * @param threadRank the rank of the thread, from 0 to the number of threads
	 *                   minus one.
	 * @param threads    the list of all threads in the set. The current thread is a
	 *                   member of this list.
	 * @throws Throwable
	 */
	protected abstract void runInParallel(ThreadSpecifics s) throws Throwable;

	public static class ThreadSpecifics {
		ThreadSpecifics(int rank) {
			this.rank = rank;
		}

		public final int rank;
		public double progressStatus = 0;
		public List<Thread> threads;
	}

	public double progress() {
		double p = 0;

		for (Thread t : threads) {
			p += ((_Thread) t).specifics.progressStatus;
		}

		return p;
	}

	public static void main(String[] args) throws Throwable {
		for (int i = 0; i < 1; ++i) {
			new MultiThreadProcessing(20, null) {
				@Override
				protected void runInParallel(ThreadSpecifics s) throws Throwable {
					s.progressStatus++;
					Cout.debug(progress());
				}
			};

			System.out.println(i + " completed");
		}

		System.out.println("it works!: ");
	}

}
