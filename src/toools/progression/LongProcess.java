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

package toools.progression;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import toools.io.Cout;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.math.MathsUtilities;
import toools.text.TextUtilities;
import toools.thread.Threads;

public class LongProcess {
	static class State {
		final double avancement;
		final long date = System.currentTimeMillis();

		State(double p) {
			this.avancement = p;
		}
	}

	private static Stack<LongProcess> stack = new Stack<>();
	private long startDate = System.currentTimeMillis();
	private final List<State> progressHistory = new ArrayList<>();
	public String object;
	private final String unit;
	protected final long refreshInterval = 1000;
	public double target = 0;

	public Sensor sensor = new Sensor();
	public Object temporaryResult;
	int progressPrintCount = 0;
	private int rateWindow = 10;
	private RegularFile file;
	private final Consumer<String> msgOut;
	private Thread thread;

	public int getRateWindow() {
		return rateWindow;
	}

	public void setRateWindow(int rateWindow) {
		this.rateWindow = rateWindow;
	}

	public static class MultiThreadSensor extends Sensor {
		@Override
		public double getProgress() {
			return progressStatus;
		}

		@Override
		public void set(double v) {
			progressStatus = v;
		}
	}

	public LongProcess(String object, String unit, double target) {
		this(object, unit, target, msg -> Cout.progress(msg));
	}

	public LongProcess(String object, String unit, double target,
			Consumer<String> stdout) {
		this.msgOut = stdout;
		if (object != null) {
			stdout("> STARTING " + object);
		}

		stack.push(this);

		// if (unit == null)
		// throw new NullPointerException("it makes no sense to set unit to
		// null. Please give it a name.");

		this.unit = unit;
		this.object = object;
		this.target = target;

		this.thread = new Thread(() -> {
			// initial sleep
			Threads.sleepMs(refreshInterval);

			while (true) {
				Threads.sleepMs(refreshInterval);
				double progressStatus = sensor.getProgress();
				progressHistory.add(new State(progressStatus));

				if (progressStatus < 0 || (target != - 1 && progressStatus >= target))
					break;

				if (stack.peek() == LongProcess.this && progressStatus > 0) {
					String msg = getMessage();
					stdout.accept(msg);

					if (file != null) {
						file.setContent(msg.getBytes());
					}

					++progressPrintCount;
				}
			}

			stack.remove(LongProcess.this);
		});

		thread.setDaemon(true);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public void stdout(String msg) {
		msgOut.accept(TextUtilities.repeat("\t", stack.size()) + msg);
	}

	static public int getStackSize() {
		return stack.size();
	}

	protected String getMessage() {
		double status = progressHistory.get(progressHistory.size() - 1).avancement;

		String s = "\t";

		if (object != null) {
			s += object + "\t";
		}

		if (rateWindow < 0 || progressHistory.size() > rateWindow) {
			String rate = getRateAsString(rateWindow) + unit + "/s";
			s += TextUtilities.flushLeft(rate, 16, ' ');
			s += " ";
		}

		if (target == - 1) {
			s += status + " " + unit + "(s)";
		}
		else {
			s += getPercentage() + "%";

			if (progressHistory.size() > 1) {
				s += " \tremaining "
						+ TextUtilities.seconds2date(getRemainingSeconds(), true);
			}
		}

		if (temporaryResult != null) {
			String r = temporaryResult.toString();

			if (r.indexOf('\n') < 0) {
				s += " \t" + temporaryResult;
			}
			else {
				s += '\n' + TextUtilities.prefixEachLineBy(r, "\t");
			}
		}

		return s;
	}

	protected double getPercentage() {
		double avancement = progressHistory.get(progressHistory.size() - 1).avancement;
		double ratio = avancement / target;
		return MathsUtilities.round(100 * ratio, 1);
	}

	protected double getRate(int windowWidth) {
		if (windowWidth < 0)
			windowWidth = progressHistory.size();

		if (progressHistory.size() < windowWidth)
			throw new IllegalArgumentException();

		if (windowWidth < 1)
			throw new IllegalArgumentException();

		State last = progressHistory.get(progressHistory.size() - 1);
		State previous = progressHistory.get(progressHistory.size() - 1 - windowWidth);
		double progress = last.avancement - previous.avancement;
		long durationMs = last.date - previous.date;
		double rate = 1000 * progress / durationMs;
		return rate;
	}

	public String getRateAsString(int windowWidth) {
		double rate = getRate(windowWidth);

		if (rate < 100) {
			rate = MathsUtilities.round(rate, 1);
		}
		else {
			rate = MathsUtilities.round(rate, 0);
		}

		String s = "";

		if (rate > 1000) {
			s = TextUtilities.toHumanString((long) rate);
		}
		else {
			s = "" + rate;
		}

		return s;
	}

	protected int getRemainingSeconds() {
		double duration = (System.currentTimeMillis() - startDate) / 1000;
		double nbElementProcessed = progressHistory
				.get(progressHistory.size() - 1).avancement;
		double nbElementRemaining = target - nbElementProcessed;
		double ratio = nbElementRemaining / nbElementProcessed;
		return (int) (duration * ratio);
	}

	public void end() {
		end(null);
	}

	public void end(String s) {
		sensor.set(target);

		try {
			thread.join();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if some history has already been printed
		if (progressPrintCount > 0) {
			String msg = getMessage();
			stdout(msg);
		}

		if (object != null) {
			long duration = (System.currentTimeMillis() - startDate) / 1000;
			stdout("> END " + object + " (" + TextUtilities.seconds2date(duration, true)
					+ ")" + (s == null ? "" : ": " + s));
		}
	}

	public static LongProcess getActiveProgressMonitor() {
		return stack.peek();
	}

	public String getDescription() {
		return object;
	}

	public void setDirectory(Directory d) {
		file = new RegularFile(d, object);
	}
}
