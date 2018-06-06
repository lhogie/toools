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

/*
* Created on Jan 16, 2004
*
* To change the template for this generated file go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
package toools.text;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleToLongFunction;
import java.util.function.LongToDoubleFunction;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.longs.LongIterator;

/**
 * @author luc.hogie
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSV
{
	public static List<List<String>> disassemble(String text, String separator)
	{
		List<List<String>> lines = new LinkedList<List<String>>();

		for (String line : text.split("\n"))
		{
			if (line.length() > 0)
			{
				List<String> cols = new LinkedList<String>();

				for (String col : line.split(separator))
				{
					cols.add(col);
				}

				lines.add(cols);
			}
		}

		return lines;
	}

	public static String assemble(List<List<String>> list, String separator)
	{
		StringBuffer buf = new StringBuffer();

		for (List<String> thisLine : list)
		{
			for (int i = 0; i < thisLine.size(); ++i)
			{
				String thisToken = thisLine.get(i);
				buf.append(thisToken);

				if (i < thisLine.size() - 1)
				{
					buf.append(separator);
				}
			}

			buf.append('\n');
		}

		return buf.toString();
	}

	public static void print(List<List<String>> l, PrintStream ps)
	{
		int li = 1;

		for (List<String> thisLine : l)
		{
			ps.print(li++);

			for (String thisElement : thisLine)
			{
				ps.print(thisElement);
			}

			ps.print('\n');
		}
	}

	public static void main(String... args)
	{
		String s = TextUtilities.generateRandomString("ab,\n", 50, new Random());
		System.out.println(assemble(disassemble(s, ","), ":"));
	}

	public static String from(Long2LongMap m)
	{
		StringBuilder b = new StringBuilder();

		for (Entry e : m.long2LongEntrySet())
		{
			b.append(e.getLongKey());
			b.append(' ');
			b.append(e.getLongValue());
			b.append('\n');
		}

		return b.toString();
	}

	public static void print(LongIterator i, LongToDoubleFunction f, PrintStream ps)
	{
		while (i.hasNext())
		{
			long l = i.nextLong();
			ps.print(l);
			ps.print(' ');
			ps.print(f.applyAsDouble(l));
			ps.print('\n');
		}
	}

	public static void print(DoubleIterator i, DoubleToLongFunction f, PrintStream ps)
	{
		while (i.hasNext())
		{
			double l = i.nextDouble();
			ps.print(l);
			ps.print(' ');
			ps.print(f.applyAsLong(l));
			ps.print('\n');
		}
	}
}
