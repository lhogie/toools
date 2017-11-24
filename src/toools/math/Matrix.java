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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toools.text.TextUtilities;


public abstract class Matrix<X, Y, V>
{
	private String xLabel = "x";
	private String yLabel = "y";

	public String getXLabel()
	{
		return xLabel;
	}

	public void setXLabel(String label)
	{
		xLabel = label;
	}

	public String getYLabel()
	{
		return yLabel;
	}

	public void setYLabel(String label)
	{
		yLabel = label;
	}

	public abstract Collection<X> getXs();

	public abstract Collection<Y> getYs();

	public abstract void set(X x, Y y, V value);

	public abstract V get(X a, Y b);

	int pad = 4;

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		int ynameWidth = getLongestItemIn(getYs()) + pad;
		Map<X, Integer> colWidth = getColumsWidth();

		b.append(TextUtilities.repeat(' ', ynameWidth));

		for (X x : getXs())
		{
			String s = x.toString();
			b.append(TextUtilities.repeat(' ', colWidth.get(x) - s.length()) + s);
		}

		b.append('\n');
		b.append(TextUtilities.repeat('-', b.length()));
		b.append('\n');

		for (Y y : getYs())
		{
			String s = y.toString();
			b.append(s + TextUtilities.repeat(' ', ynameWidth - s.length()));

			for (X x : getXs())
			{
				V v = get(x, y);
				int w = colWidth.get(x);
				String xs = v == null ? "null" : v.toString();
				b.append(TextUtilities.repeat(' ', w - xs.length()) + xs);
			}
			
			b.append('\n');

		}

		return b.toString();
	}

	private Map<X, Integer> getColumsWidth()
	{
		Map<X, Integer> map = new HashMap();

		for (X x : getXs())
		{
			map.put(x, Math.max(getLongestItemIn(getColumn(x)), x.toString().length()) + pad);
		}

		return map;
	}

	private List<V> getColumn(X x)
	{
		List<V> c = new ArrayList();

		for (Y y : getYs())
		{
			c.add(get(x, y));
		}

		return c;
	}

	private int getLongestItemIn(Collection c)
	{
		int m = 0;

		for (Object o : c)
		{
			String s = o == null ? "null" : o.toString();

			if (s.length() > m)
			{
				m = s.length();
			}
		}

		return m;
	}

	public String toLaTeX()
	{
		List<X> elements = new ArrayList<X>(getXs());
		String s = "\\begin{tabular}{|" + TextUtilities.repeat("r", elements.size()) + "|}";

		for (X a : elements)
		{
			s += "\t";

			for (Y b : getYs())
			{
				V c = get(a, b);
				s += c + " & ";
			}

			s += " \\\n";
		}

		s += "\\end{tabular}";
		return s;
	}
}
