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

import toools.exceptions.CodeShouldNotHaveBeenReachedException;

public class Interval
{
	private double lowerBound, upperBound;
	private boolean includeLowerBound;

	public boolean includeLowerBound()
	{
		return includeLowerBound;
	}

	public void setIncludeLowerBound(boolean includeLowerBound)
	{
		this.includeLowerBound = includeLowerBound;
	}

	public boolean includeUpperBound()
	{
		return includeUpperBound;
	}

	public void setIncludeUpperBound(boolean includeUpperBound)
	{
		this.includeUpperBound = includeUpperBound;
	}

	private boolean includeUpperBound;

	public Interval(double lowerBound, double upperBound)
	{
		this(lowerBound, upperBound, true, true);
	}

	public Interval(double lowerBound, double upperBound, boolean includeLowerBound,
			boolean includeUpperBound)
	{
		setBounds(lowerBound, upperBound);
		this.includeLowerBound = includeLowerBound;
		this.includeUpperBound = includeUpperBound;
	}

	public static Interval valueOf(String s)
	{
		s = s.trim();

		if (( ! s.startsWith("]") && ! s.startsWith("["))
				|| ( ! s.endsWith("]") && ! s.startsWith("[")))
			throw new IllegalArgumentException(
					"an interval definition should start and end with either '[' or ']'");

		String[] numbers = s.substring(1, s.length() - 1).trim().split(" +");

		if (numbers.length == 2)
		{
			return new Interval(Double.valueOf(numbers[0]), Double.valueOf(numbers[1]),
					s.startsWith("["), s.endsWith("]"));
		}
		else
		{
			throw new IllegalArgumentException(
					"an interval definition should consist of 2 numbers");
		}
	}

	public void setBounds(double lowerBound, double upperBound)
	{
		if (lowerBound > upperBound)
			throw new IllegalArgumentException("bounds are not acceptable");

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public double getLowerBound()
	{
		return lowerBound;
	}

	public double getUpperBound()
	{
		return upperBound;
	}

	public double getWidth()
	{
		return getUpperBound() - getLowerBound();
	}

	public boolean include(Interval i)
	{
		return include(i.lowerBound) && include(i.upperBound);
	}

	public boolean include(double o)
	{
		if (o < lowerBound || o > upperBound)
		{
			return false;
		}
		else if (o > lowerBound && o < upperBound)
		{
			return true;
		}
		else if (o == lowerBound && includeLowerBound)
		{
			return true;
		}
		else if (o == upperBound && includeUpperBound)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return (includeLowerBound ? "[" : "]") + lowerBound + " " + upperBound
				+ ( ! includeUpperBound ? "[" : "]");
	}

	public String toGNUPlot()
	{
		return (includeLowerBound ? "[" : "]") + lowerBound + ":" + upperBound
				+ ( ! includeUpperBound ? "[" : "]");
	}

	public static void main(String[] args)
	{
		System.out.println(valueOf("[  6 9["));
	}

	public double minmax(double i)
	{
		if (include(i))
		{
			return i;
		}
		else if (i < lowerBound)
		{
			if ( ! includeLowerBound)
				throw new IllegalStateException("lower bound is not included");

			return lowerBound;
		}
		else if (i > upperBound)
		{
			if ( ! includeUpperBound)
				throw new IllegalStateException("upper bound is not included");

			return upperBound;
		}

		throw new CodeShouldNotHaveBeenReachedException();
	}
}
