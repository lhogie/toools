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
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import toools.extern.Proces;
import toools.math.MathsUtilities;

public class Threads {
	public static Thread newThread_loop_periodic(long periodMs, BooleanSupplier run,
			Consumer<Long> warningTooSlow, Runnable r) {
		Thread t = new Thread(() -> {
			while (run.getAsBoolean()) {
				long startMs = System.currentTimeMillis();
				r.run();
				long endMs = System.currentTimeMillis();
				long durationMs = endMs - startMs;

				if (durationMs > periodMs) {
					warningTooSlow.accept(durationMs);
				}
				else {
					sleepMs(periodMs - durationMs);
				}
			}
		});

		t.setDaemon(true);
		t.start();
		return t;
	}

	public static Thread newThread_loop(long pauseMs, BooleanSupplier run, Runnable r) {
		Thread t = new Thread(() -> {
			while (run.getAsBoolean()) {
				r.run();
				sleepMs(pauseMs);
			}
		});

		t.setDaemon(true);
		t.start();
		return t;
	}

	public static Thread newThread_loop(BooleanSupplier run, Runnable r) {
		return newThread_loop(0, run, r);
	}

	public static Thread newThread_loop_periodic(long periodMs, BooleanSupplier run, Runnable r) {
		return newThread_loop_periodic(periodMs, run,
				durationMs -> System.err
						.println("the run() method of the cylic task (" + r + ") took "
								+ durationMs + " ms to execute, this is too long."),
				r);
	}

	public static Thread newThread_loop(Runnable r) {
		return newThread_loop(() -> true, r);
	}

	public static long sleepMs(long ms) {
		if (ms > 0) {
			// new IllegalStateException().printStackTrace();
			long a = System.currentTimeMillis();

			try {
				Thread.sleep(ms);
			}
			catch (InterruptedException e) {
				return System.currentTimeMillis() - a;
			}

		}

		return ms;
	}
	
	public static double sleep(double s) {
		return sleepMs((long) (1000*s))/1000d;
	}

	public static void uninterruptibleSleepMs(long ms) {
		while (ms > 0) {
			ms -= sleepMs(ms);
		}
	}

	public static void sleepForever() {
		uninterruptibleSleepMs(Long.MAX_VALUE);
	}

	private static double currentLoadAvg = 1;
	private static long loadAvgLastSenseDate = 0;

	public static double getLastMinuteLoadAverage() {
		long now = System.currentTimeMillis();

		if (now - loadAvgLastSenseDate > 10000d) {
			String uptimeOutput = new String(Proces.exec("uptime"));
			String[] values = uptimeOutput.split(" +");
			loadAvgLastSenseDate = now;
			currentLoadAvg = Double.valueOf(values[values.length - 3].replace(',', '.'));
		}

		return currentLoadAvg;
	}

	private static final Thread computerLoadMonitorThread = new Thread() {

		@Override
		public void run() {
			double oldValue = getNumberOfUnusedProcessors();

			while (true) {
				try {
					double newValue = getNumberOfUnusedProcessors();

					if (oldValue != newValue) {
						for (ComputerLoadListener cl : computerLoadListeners) {
							cl.numberOfUnusedProcessorsChanged(oldValue, newValue);
						}

						oldValue = newValue;
					}

					uninterruptibleSleepMs(1000);
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	};

	private static List<ComputerLoadListener> computerLoadListeners = new ArrayList<ComputerLoadListener>();

	public static void addComputerLoadListener(ComputerLoadListener l) {
		computerLoadListeners.add(l);

		// if this is the first listener to come
		if (computerLoadListeners.size() == 1) {
			computerLoadMonitorThread.start();
		}
	}

	public static int getNumberOfProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	public static double getNumberOfUnusedProcessors() {
		return getNumberOfProcessors() - getLastMinuteLoadAverage();
	}

	public static boolean isCPUOverloaded() {
		return getNumberOfUnusedProcessors() <= 0;
	}

	/**
	 * If CPU usage is already 100%, there's no room for a new threads but it is
	 * necessary to create at least one to perform the task.
	 * 
	 * @return
	 */
	public static int getNumberOfThreadsANewTaskCanUseToTakeAllComputationalResources() {
		return Math.max(1, (int) MathsUtilities.round(getNumberOfUnusedProcessors(), 0));
	}

	public static void main(String[] args) {
		addComputerLoadListener(new ComputerLoadListener() {

			@Override
			public void numberOfUnusedProcessorsChanged(double oldValue,
					double newValue) {
				System.out.println("nouveau load: " + newValue + " ancien=" + oldValue);
			}
		});

		sleepForever();
	}

	public static void wait(Object lock) {
		try {
			synchronized (lock) {
				lock.wait();
			}
		}
		catch (InterruptedException e) {
			throw new IllegalStateException();
		}
	}

	public static void notify(Object lock) {
		synchronized (lock) {
			lock.notify();
		}
	}
}
