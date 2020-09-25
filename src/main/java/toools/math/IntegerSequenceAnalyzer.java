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

public class IntegerSequenceAnalyzer
{

	public void addInts(Iterable<Integer> c)
	{
		for (int i : c)
		{
			add(i);
		}
	}

	public void addLongs(Iterable<Long> c)
	{
		for (long i : c)
		{
			add(i);
		}
	}

	public void add(int... ints)
	{
		for (int i : ints)
		{
			add(i);
		}
	}

	public void add(long... longs)
	{
		for (long i : longs)
		{
			add(i);
		}
	}

	public void add(long i)
	{
		if (i > 0 && sum > 0 && i > Long.MAX_VALUE - sum)
			throw new IllegalArgumentException("overflow");

		else if (i < 0 && sum < 0 && i < Long.MIN_VALUE - sum)
			throw new IllegalArgumentException("overflow");

		this.sum += i;

		if (Math.abs(i) > Math.sqrt(Long.MAX_VALUE))
			throw new IllegalArgumentException("overflow");

		if (i * i > Long.MAX_VALUE - sumOfSquares)
			throw new IllegalArgumentException("overflow");

		this.sumOfSquares += i * i;

		if (i < min)
		{
			min = i;
		}
		else if (i > max)
		{
			max = i;
		}

		++count;
	}

	private long min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = 0;
	private long sum, sumOfSquares;

	public long getMin()
	{
		if (count == 0)
			throw new IllegalStateException("no value yet");

		return min;
	}

	public long getMax()
	{
		if (count == 0)
			throw new IllegalStateException("no value yet");

		return max;
	}

	public long getSum()
	{
		if (count == 0)
			throw new IllegalStateException("no value yet");

		return sum;
	}

	public long getSumOfSquares()
	{
		if (count == 0)
			throw new IllegalStateException("no value yet");

		return sumOfSquares;
	}

	public long avg()
	{
		return sum / count;
	}

	public long variance()
	{
		return sumOfSquares / count - (sum / count) * (sum / count);
	}

	public double stdDeviation()
	{
		return Math.sqrt(variance());
	}
}