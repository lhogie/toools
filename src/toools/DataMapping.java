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
 
 package toools;

import java.util.List;
import java.util.Vector;

public class DataMapping
{
	public final int offset;
	public final int length;

	public DataMapping(int i, int length)
	{
		if (i < 0)
			throw new IllegalArgumentException("i must be >= 0");

		if (length <= 0)
			throw new IllegalArgumentException("length must be > 0");

		this.offset = i;
		this.length = length;
	}

	public long decode(long bits)
	{
		bits <<= 63 - offset - length;
		bits >>= 63 - length;
		return bits;
	}

	public long encode(long bits, long v)
	{
		if (Long.numberOfLeadingZeros(v) < 64 - bits)
			throw new IllegalArgumentException("cannot be stored on " + length
					+ " bits: " + v);

		v <<= 63 - length;
		v >>= 63 - length + offset;
		bits |= v;
		return bits;
	}

	public static long[] decodeAll(long bit, List<DataMapping> mappings)
	{
		long[] values = new long[mappings.size()];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = mappings.get(i).decode(bit);
		}

		return values;
	}

	public static List<DataMapping> createContiguousMappings(
			int... elementsLength)
	{
		List<DataMapping> mappingList = new Vector<DataMapping>();
		int currentOffset = 0;

		for (int i = 0; i < elementsLength.length; ++i)
		{
			DataMapping mapping = new DataMapping(currentOffset,
					elementsLength[i]);
			mappingList.add(mapping);
			currentOffset += elementsLength[i];
		}

		return mappingList;
	}

	// public String toString()
	// {
	// StringBuilder builder = new StringBuilder();
	// builder.append(TextUtilities.repeat(" ", getI()));
	// builder.append('|');
	// String printName = getName().substring(0, Math.min(getName().length(),
	// getLength() - 2));
	// builder.append(printName);
	// builder.append(TextUtilities.repeat(".", getLength() - 2 -
	// printName.length()));
	// builder.append('|');
	// builder.append(TextUtilities.repeat(" ", ca.getColumnCount() - getI() -
	// getLength()));
	// builder.append(" = " + TextUtilities.flushLeft(" " + decode(ca), 15,
	// ' '));
	// builder.append(TextUtilities.flushLeft(getI() + " -> "+ (getI() +
	// getLength()), 10, ' ') + " (" + getLength() + " cells)");
	// return builder.toString();
	// }

	public static void main(String[] args)
	{
		long l = 45;
		DataMapping m = new DataMapping(0, 8);
		l = m.encode(l, 78954);
		System.out.println(m.decode(l));
	}

}
