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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import toools.extern.Proces;
import toools.gui.PDFRenderingAWTComponent;
import toools.io.FileUtilities;
import toools.io.file.RegularFile;

@SuppressWarnings("serial")
public class Distribution<T> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final Map<T, Long> map = new HashMap<T, Long>();
	private long sum = 0;
	private long greatestNumberOfOccurences = 0;
	private T mostOccuringObject = null;

	public long getGreatestNumberOfOccurences()
	{
		return greatestNumberOfOccurences;
	}

	public void addOccurence(T t)
	{
		addNOccurences(t, 1);
	}

	public void addNOccurences(T e, long add)
	{
		if (add < 0)
			throw new IllegalArgumentException();

		Long n = map.get(e);

		if (n == null)
		{
			map.put(e, add);
			sum += add;

			if (add > this.greatestNumberOfOccurences)
			{
				greatestNumberOfOccurences = add;
				mostOccuringObject = e;
			}
		}
		else
		{
			long newValue = n + add;
			map.put(e, newValue);
			sum += add;

			if (newValue > this.greatestNumberOfOccurences)
			{
				greatestNumberOfOccurences = newValue;
				mostOccuringObject = e;
			}
		}
	}

	static private <T> void merge(Distribution<T> d1, Distribution<T> d2,
			Distribution<T> out)
	{
		if (d1 != out)
		{
			for (Entry<T, Long> entry : d1.map.entrySet())
			{
				out.addNOccurences(entry.getKey(), entry.getValue());
			}
		}
		if (d2 != out)
		{
			for (Entry<T, Long> entry : d2.map.entrySet())
			{
				out.addNOccurences(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Merges two distributions (this and other) into a new one.
	 * 
	 * @param other
	 *            the second distribution to merge
	 * @return a new {@link Distribution} object
	 */
	public Distribution<T> merge(Distribution<T> other)
	{
		Distribution<T> output = new Distribution<T>();
		merge(this, other, output);
		return output;
	}

	/**
	 * Merges the this distribution with another.
	 * 
	 * @param other
	 *            the second distribution to merge
	 * @return the this object.
	 */
	public Distribution<T> mergeInPlace(Distribution<T> other)
	{
		merge(this, other, this);
		return this;
	}

	public long getNumberOfOccurences(T t)
	{
		if ( ! map.containsKey(t))
			// throw new IllegalArgumentException("element is not considered");
			return 0;

		return map.get(t);
	}

	public long getTotalNumberOfOccurences()
	{
		return sum;
	}

	public double getRelativeNumberOfOccurences(T t)
	{
		return (double) getNumberOfOccurences(t) / (double) getTotalNumberOfOccurences();
	}

	public List<T> orderObjectsByIncreasingNumberOfOccurences()
	{
		List<T> l = new ArrayList<T>(getOccuringObjects());

		Collections.sort(l, new Comparator<T>()
		{
			@Override
			public int compare(T o1, T o2)
			{
				return new Long(getNumberOfOccurences(o1))
						.compareTo(getNumberOfOccurences(o2));
			}
		});

		return l;
	}

	public Collection<T> getOccuringObjects()
	{
		return Collections.unmodifiableCollection(this.map.keySet());
	}

	public String toGNUPlotData(List<T> list, boolean relative)
	{
		StringBuilder b = new StringBuilder();

		for (T o : list)
		{
			b.append(o.toString() + '\t' + (relative ? getRelativeNumberOfOccurences(o)
					: getNumberOfOccurences(o)) + '\n');
		}

		return b.toString();
	}

	public String toGNUPlotData(boolean relative)
	{
		return toGNUPlotData(new ArrayList<T>(getOccuringObjects()), relative);
	}

	public String getGNUPlotCommands(String datafileName, String title, boolean relative)
	{
		StringBuilder b = new StringBuilder();
		b.append(
				"set term pdf\nset style data histogram\nset style fill solid border -1\nset style data histogram\n");
		b.append("set boxwidth\nset auto x\n");
		// b.append("set boxwidth 1\n");
		b.append("set yrange [0:"
				+ (relative ? 1 : (1.1 * getGreatestNumberOfOccurences())) + "]\n");
		b.append("plot \"" + datafileName + "\" with  lines title \"" + title + "\"\n");
		return b.toString();
	}

	public byte[] toPDF(String title)
	{

		RegularFile datafile = FileUtilities.createTempFile("toools-", ".dat");
		datafile.setContent(toGNUPlotData(true).getBytes());
		// System.out.println(getGNUPlotCommands("hehe", true));
		byte[] pdf = Proces.exec("gnuplot",
				getGNUPlotCommands(datafile.getPath(), title, true).getBytes());
		datafile.delete();
		return pdf;

	}

	public static Distribution<Integer> getDistribution(int[] values)
	{
		Distribution<Integer> distribution = new Distribution<Integer>();

		for (int v : values)
		{
			distribution.addOccurence(v);
		}

		return distribution;
	}

	public static Distribution<Long> getDistribution(long[] values)
	{
		Distribution<Long> distribution = new Distribution<Long>();

		for (long v : values)
		{
			distribution.addOccurence(v);
		}

		return distribution;
	}

	public static void main(String[] args) throws Exception
	{
		Distribution<Integer> d = new Distribution<Integer>();
		Random r = new Random();
		for (int i = 0; i < 20; ++i)
		{
			d.addOccurence(r.nextInt(10));
		}

		d.display("");
	}

	public void display(String title)
	{
		PDFRenderingAWTComponent c = new PDFRenderingAWTComponent();
		toools.gui.Utilities.displayInJFrame(c, "Distribution");
		c.setPDFData(toPDF(title), 0);
	}

	public List<T> sortByNbOccurences()
	{
		List<T> r = new ArrayList<T>(map.keySet());
		Collections.sort(r, (a, b) -> Long.compare(map.get(a), map.get(b)));
		return r;
	}

	@Override
	public String toString()
	{
		return toString(true, true);
	}

	public String toString(boolean singleLine, boolean relative)
	{
		return toString(new ArrayList<>(map.keySet()), singleLine, relative);
	}

	public String toString(List<T> keys, boolean singleLine, boolean relative)
	{
		StringBuilder b = new StringBuilder();

		for (T t : keys)
		{

			if (relative)
			{
				b.append((int) (100 * getRelativeNumberOfOccurences(t)) + "%");
			}
			else
			{
				b.append(getNumberOfOccurences(t));
			}

			b.append(singleLine ? " " : "\t");
			b.append(t);
			b.append(singleLine ? ", " : '\n');
		}

		return b.toString();
	}

	public T getMostOccuringObject()
	{
		return mostOccuringObject;
	}

	public void multiply(double d)
	{
		for (T k : map.keySet())
		{
			long nbO = map.get(k);
			map.put(k, (long) (d * nbO));
		}
	}

}
