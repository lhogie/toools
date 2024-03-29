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

package toools.math;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import toools.collections.Collections;
import toools.io.FileUtilities;
import toools.progression.LongProcess;
import toools.text.TextUtilities;

/**
 * @author luc.hogie Created on Jun 14, 2004
 */
public class MathsUtilities {
	public static long[] partialSums(long[] weights) {
		LongProcess lp = new LongProcess("computing partial sums", " vertex", weights.length);
		long[] partialSums = new long[weights.length];
		long currentSum = 0;

		for (int i = 0; i < weights.length; ++i) {
			if (weights[i] < 0)
				throw new IllegalArgumentException("weight should be positive");

			long trySum = currentSum + weights[i];

			if (trySum < currentSum)
				throw new IllegalArgumentException("long overflow while adding " + weights[i] + " to " + currentSum);

			partialSums[i] = currentSum = trySum;

			lp.sensor.progressStatus++;
		}

		lp.end();
		return partialSums;
	}

	public static double[] partialSums(double[] weights) {
		double[] partialSums = new double[weights.length];
		double currentSum = 0;

		for (int i = 0; i < weights.length; ++i)
			partialSums[i] = currentSum += weights[i];

		return partialSums;
	}

	public static int pick(double[] partialSums, Random prng) {
		double r = prng.nextDouble() * partialSums[partialSums.length - 1];

		int pos = DoubleArrays.binarySearch(partialSums, r);

		if (0 <= pos && pos < partialSums.length) {
			return pos;
		} else {
			return -pos - 1;
		}
	}

	public static int pick(long[] partialSums, Random prng) {
		double r = prng.nextDouble() * partialSums[partialSums.length - 1];
		int pos = binarySearch(partialSums, r);

		if (0 <= pos && pos < partialSums.length) {
			return pos;
		} else {
			return -pos - 1;
		}
	}

	private static int binarySearch(long[] partialSums, double d) {
		int min = 0, max = partialSums.length - 1;

		while (max - min > 1) {
			int middle = (max + min) / 2;

			if (d <= partialSums[middle]) {
				max = middle;
			} else if (d > partialSums[middle]) {
				min = middle;
			}
		}

		if (max == min)
			return min;

		if (d < partialSums[min])
			return min;

		return max;
	}

	public static int binomial(int n, int k) {
		if (k == 2) {
			return n * (n - 1) / 2;
		} else {
			return fact(n) / (fact(k) * fact(n - k));
		}
	}

	public static int fact(int n) {
		int r = 0;

		for (int i = 2; i < n; ++i) {
			r *= i;
		}

		return r;
	}

	public static <T> Collection<Couple<T>> computeCouples(Collection<T> c1, Collection<T> c2) {
		Collection<Couple<T>> couples = new HashSet<Couple<T>>();

		for (T t1 : c1) {
			for (T t2 : c2) {
				if (t1 != t2) {
					couples.add(new Couple<T>(t1, t2));
				}
			}
		}

		return couples;
	}

	public static int computeTheNumberOfBitsRequiredToStoreNDifferentValues(int n) {
		if (n < 0)
			throw new IllegalArgumentException();

		return 32 - Integer.numberOfLeadingZeros(n - 1);
	}

	public static int log2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();

		return 31 - Integer.numberOfLeadingZeros(n);
		// return (int) (Math.log(n) / Math.log(2));
	}

	public static double log2(double n) {
		if (n <= 0)
			throw new IllegalArgumentException();

		return (int) (Math.log(n) / Math.log(2));
	}

	public static long computeLongAverage(long[] values) {
		if (values.length == 0)
			throw new IllegalArgumentException("tab is empty");

		return sum(values) / values.length;
	}

	public static double computeAverage(IntArrayList l) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static long sum(long... values) {
		if (values.length == 0)
			throw new IllegalArgumentException("tab is empty");

		long sum = 0;

		for (long n : values) {
			sum += n;
		}

		return sum;
	}

	public static int computeMinimum(int[] a) {
		int min = a[0];

		for (int i = 1; i < a.length; ++i) {
			int b = a[i];

			if (b < min) {
				min = b;
			}

		}

		return min;
	}

	public static long computeMinimum(long[] a) {
		long min = a[0];

		for (int i = 1; i < a.length; ++i) {
			long b = a[i];

			if (b < min) {
				min = b;
			}

		}

		return min;
	}

	public static double max(double[] a) {
		double max = a[0];

		for (int i = 1; i < a.length; ++i) {
			double b = a[i];

			if (b > max) {
				max = b;
			}

		}

		return max;
	}

	public static String toGNUPlotText(List<? extends Number> list) {
		StringBuffer buf = new StringBuffer();

		for (Number n : list) {
			buf.append(n.toString());
			buf.append('\n');
		}

		return buf.toString();
	}

	public static byte[] toPDF(File inputFile, File outputFile) {
		String gnuplotText = "set term postscript" + "set output \"${OUTPUT}\"" + "set xlabel \"${XLABEL}"
				+ "set ylabel \"${YLABEL}\"" + "set key left top" + "plot \"${INPUT}\" with linespoints";

		gnuplotText.replace("${INPUT}", inputFile.getAbsolutePath());
		gnuplotText.replace("${OUTPUT}", outputFile.getAbsolutePath());

		File gnuplotFile = FileUtilities.getUniqFile("kj", "kj.gnuplot");
		// FileUtilities.setFileContent(gnuplotFile, gnuplotText.getBytes());
		return null;

	}

	public static double getCumulatedVariations(List<Double> values) {
		double variation = 0;

		for (int i = 1; i < values.size(); ++i) {
			variation += Math.abs(values.get(i) - values.get(i - 1));
		}

		return variation;
	}

	public static double nPercentOf(double percentage, double amount) {
		return (amount * percentage) / 100d;
	}

	public static double round(double n, int precision) {
		double a = Math.pow(10, precision);
		n *= a;
		n = Math.round(n);
		n /= a;
		return n;
	}

	public static double pickRandomBetween(double min, double max, Random random) {
		if (min == max) {
			return min;
		} else if (min < max) {
			return random.nextDouble() * (max - min) + min;
		} else {
			throw new IllegalArgumentException("min=" + min + ", max=" + max);
		}
	}

	public static long pickLongBetween(long min, long max, Random random) {
		if (min == max) {
			return min;
		} else if (min < max) {
			return Math.abs(random.nextLong()) % (max - min) + min;
		} else {
			throw new IllegalArgumentException("min=" + min + ", max=" + max);
		}
	}

	public static int pickIntBetween(int min, int max, Random random) {
		if (min == max) {
			return min;
		} else if (min < max) {
			return Math.abs(random.nextInt()) % (max - min) + min;
		} else {
			throw new IllegalArgumentException("min=" + min + ", max=" + max);
		}
	}

	/**
	 * in inclusive out exclusive
	 * 
	 * @param min
	 * @param max
	 * @param random
	 * @return
	 */
	public static int pickRandomBetween(int min, int max, Random random) {
		if (min >= max)
			throw new IllegalArgumentException("min=" + min + ", max=" + max);

		return random.nextInt(max - min) + min;
	}

	public static double cut(double a, int precision) {
		double m = Math.pow(10, precision);
		a *= m;
		a = (int) a;
		a /= m;
		return a;
	}

	/*
	 * Return the angle of the given vector
	 */
	public static double getAngleForVector(double i, double j) {
		return Math.atan(j / i);
	}

	public static int min(int... array) {
		if (array.length == 0)
			throw new IllegalArgumentException("set is empty");

		int min = array[0];

		for (int i = 1; i < array.length; ++i) {
			if (array[i] < min) {
				min = array[i];
			}
		}

		return min;
	}

	public static int max(int... array) {
		if (array.length == 0)
			throw new IllegalArgumentException("set is empty");

		int max = array[0];

		for (int e : array) {
			if (e > max) {
				max = e;
			}
		}

		return max;
	}

	public static long max(long... array) {
		if (array.length == 0)
			throw new IllegalArgumentException("array is empty");

		long max = array[0];

		for (long l : array) {
			if (l > max) {
				max = l;
			}
		}

		return max;
	}

	public static double sum(Iterable<Double> c) {
		Iterator<Double> i = c.iterator();

		if (!i.hasNext())
			throw new IllegalArgumentException("set is empty");

		double sum = 0;

		for (double n : c) {
			sum += n;
		}

		return sum;
	}

	public static double avg(int... array) {
		return IntStream.of(array).average().getAsDouble();
	}

	public static double avg(long... array) {
		return LongStream.of(array).average().getAsDouble();
	}

	public static double avg(double... array) {
		return sum(array) / array.length;
	}

	public static double min(double... a) {
		double min = a[0];

		for (int i = 1; i < a.length; ++i) {
			double b = a[i];

			if (b < min) {
				min = b;
			}

		}

		return min;
	}

	public static long computeMinimum(LongIterator p) {
		if (!p.hasNext())
			throw new IllegalArgumentException("no value to compute");

		long min = p.next();

		while (p.hasNext()) {
			long b = p.next();

			if (b < min) {
				min = b;
			}
		}

		return min;
	}

	public static int computeMinimum(IntIterator p) {
		if (!p.hasNext())
			throw new IllegalArgumentException("no value to compute");

		int min = p.next();

		while (p.hasNext()) {
			int b = p.next();

			if (b < min) {
				min = b;
			}
		}

		return min;
	}

	public static long max(LongIterator p) {
		if (!p.hasNext())
			throw new IllegalArgumentException("no value to compute");

		long max = p.nextLong();

		while (p.hasNext()) {
			long b = p.nextLong();

			if (b > max) {
				max = b;
			}
		}

		return max;
	}

	public static int max(IntIterator p) {
		if (!p.hasNext())
			throw new IllegalArgumentException("no value to compute");

		int max = p.nextInt();

		while (p.hasNext()) {
			int b = p.nextInt();

			if (b > max) {
				max = b;
			}
		}

		return max;
	}

	public static double sum(double... array) {
		double s = 0;

		for (double a : array) {
			if (s < 0 && a < Double.MIN_VALUE - s)
				throw new ArithmeticException("overflow");

			if (s > 0 && a > Double.MAX_VALUE - s)
				throw new ArithmeticException("overflow");

			s += a;
		}

		return s;
	}

	public static long sum(int... array) {
		long s = 0;

		for (int i : array) {
			s += i;
		}

		return s;
	}

	public static int long2int(long n) {
		if (n < Integer.MIN_VALUE || n > Integer.MAX_VALUE)
			throw new IllegalArgumentException("overflow");

		return (int) n;
	}

	public static double stdDev(double... c) {
		double avg = avg(c);
		double diffSum = 0;

		for (double n : c) {
			diffSum += Math.abs(avg - n);
		}

		return diffSum / c.length;
	}

	public static double stdDev(int... c) {
		double avg = avg(c);
		double diffSum = 0;

		for (double n : c) {
			diffSum += Math.abs(avg - n);
		}

		return diffSum / c.length;
	}

	public static double stdDev(long... c) {
		double avg = avg(c);
		double diffSum = 0;

		for (double n : c) {
			diffSum += Math.abs(avg - n);
		}

		return diffSum / c.length;
	}

	public static Int2IntMap distribution(int[] values) {
		Int2IntMap r = new Int2IntOpenHashMap();

		for (int v : values) {
			r.put(v, r.get(v) + 1);
		}

		return r;
	}

	public static double computeStandardDeviationOfSuperiorDeviation(double... c) {
		double avg = avg(c);
		double diffSum = 0;
		double count = 0;

		for (double n : c) {
			if (n > avg) {
				diffSum += Math.abs(n);
				++count;
			}
		}

		return diffSum / count;
	}

	public static double computeStandardDeviationOfInferiorDeviation(double[] c) {
		double avg = avg(c);
		double diffSum = 0;
		double count = 0;

		for (double n : c) {
			if (n < avg) {
				diffSum += Math.abs(n);
				++count;
			}
		}

		return diffSum / count;
	}

	public static List<Double> getAverageList(Collection<List<Double>> lists) {
		List<Double> resList = new Vector<Double>();

		if (lists.size() == 0) {
			throw new IllegalArgumentException("no lists");
		} else {
			int size = lists.iterator().next().size();

			for (int i = 0; i < size; ++i) {
				Collection<Double> values = Collections.getElementsAt(lists, i);
				DoubleSequenceAnalyzer analyzer = new DoubleSequenceAnalyzer();
				analyzer.addDoubles(values);
				double avg = analyzer.avg();
				resList.add(new Double(avg));
			}
		}

		return resList;
	}

	public static List<Double> getStandardDeviationList(Collection<List<Double>> lists) {
		List<Double> resList = new Vector<Double>();

		if (lists.size() == 0) {
			throw new IllegalArgumentException("no lists");
		} else {
			int size = lists.iterator().next().size();

			for (int i = 0; i < size; ++i) {
				Collection<Double> values = Collections.getElementsAt(lists, i);
				DoubleSequenceAnalyzer analyzer = new DoubleSequenceAnalyzer();
				analyzer.addDoubles(values);

				double avg = analyzer.stdDeviation();
				resList.add(new Double(avg));
			}
		}

		return resList;
	}

	public static boolean collectionsHaveSameSize(Collection<Collection<?>> lists) {
		if (lists.isEmpty()) {
			throw new IllegalArgumentException("no lists");
		} else {
			Iterator<Collection<?>> i = lists.iterator();
			int ref = i.next().size();

			while (i.hasNext()) {
				if (i.next().size() != ref) {
					return false;
				}
			}

			return true;
		}
	}

	public static boolean isNumber(String s) {
		return TextUtilities.isDouble(s);
	}

	public static double getClosestValue(double ref, Iterable<Double> c) {
		Iterator<Double> i = c.iterator();

		if (!i.hasNext()) {
			throw new IllegalArgumentException("no values to choose from");
		} else {
			double closest = i.next();

			while (i.hasNext()) {
				double d = i.next();

				// if the difference is smaller
				if (Math.abs(ref - d) < Math.abs(ref - closest)) {
					closest = d;
				}
			}

			return closest;
		}
	}

	public static boolean isInteger(double value) {
		int intValue = (int) value;
		return value == (double) intValue;
	}

	/**
	 * Compare the two given object, if possible.
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 */
	public static int compare(Object e1, Object e2) {
		Class<?> clazz = e1.getClass();

		if (clazz == e2.getClass()) {

			if (clazz == Integer.class) {
				return ((Integer) e1).compareTo((Integer) e2);
			} else if (clazz == Double.class) {
				return ((Double) e1).compareTo((Double) e2);
			} else if (clazz == String.class) {
				return ((String) e1).compareTo((String) e2);
			} else {
				throw new IllegalArgumentException("elements are not comparable: " + e1.getClass());
			}
		} else {
			throw new IllegalArgumentException("not the same class: " + e1.getClass() + " != " + e2.getClass());
		}
	}

	public static boolean next(int[] values, int[] sizes) {
		if (values.length != sizes.length)
			throw new IllegalArgumentException("not same array size");

		if (values.length == 0)
			throw new IllegalArgumentException("empty arrays are not allowed");

		for (int s : sizes)
			if (s <= 0)
				throw new IllegalArgumentException("size must be > 0");

		for (int i : values)
			if (i < 0)
				throw new IllegalArgumentException("negative indices are not allowed");

		int i = 0;

		// while the value at this index is max
		while (values[i] == sizes[i] - 1) {
			if (i == values.length - 1) {
				return false;
			} else {
				values[i++] = 0;
			}
		}

		++values[i];
		return true;
	}

	// Returns the new average after including x
	static double avg(double previousAverage, int previousNbElements, double newElement) {
		return (previousAverage * previousNbElements + newElement) / (previousNbElements + 1);
	}

	static double computeAverage2(double previousAverage, int previousNbElements, double newElement) {
		return previousAverage * (previousNbElements / (previousNbElements + 1d))
				+ newElement / (previousNbElements + 1);
	}

	public static void main(String[] args) {
		System.out.println(avg(65700246546544d, 355546603, 6d));
		System.out.println(computeAverage2(65700246546544d, 355546603, 6d));
	}

	public static int[] pickNValues(Random r, int n) {
		int[] a = new int[n];

		for (int i = 0; i < n; ++i) {
			a[i] = r.nextInt(50);
		}
		return a;
	}

	public static Long2IntMap distribution(LongIterator i) {
		Long2IntMap r = new Long2IntOpenHashMap();

		while (i.hasNext()) {
			long v = i.nextLong();
			int n = r.getOrDefault(v, 0);
			r.put(v, n + 1);
		}

		return r;

	}

	// randomize number v by +- ratio 
	public static double randomize(double v, double ratio, Random r) {
		return v - r.nextDouble(2 * v * ratio);
	}

}
