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

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import toools.collections.primitive.LongCursor;

/**
 * A matrix made of primitive longs. Indices for lines and columns begin to 0.
 */
public class LongMatrix
{
	private final Long2ObjectMap<Long2LongMap> lines = new Long2ObjectOpenHashMap<>();
	private final int nbRows;
	private final int nbColumns;
	private  long defaultValue;


	public LongMatrix(int nbRows, int nbColumns)
	{
		this(nbRows, nbColumns, 0);
	}

	public LongMatrix(int nbRows, int nbColumns, long defaultValue)
	{
		assert nbColumns > 0;
		assert nbRows > 0;

		this.nbRows = nbRows;
		this.nbColumns = nbColumns;
		this.defaultValue = defaultValue;
	}
	

	public LongMatrix(int[][] tab, long defaultValue)
	{
		this(tab.length, tab[0].length, defaultValue);

		for (int i = 0; i < nbRows; i++)
			for (int j = 0; j < nbColumns; j++)
				set(i, j, tab[i][j]);
	}

	public LongMatrix(LongMatrix m)
	{
		this(m.getNbRows(), m.getNbColumns(), m.getDefaultValue());

		for (int i = 0; i < nbRows; i++)
			for (int j = 0; j < nbColumns; j++)
				set(i, j, m.get(i, j));
	}

	public long getDefaultValue()
	{
		return defaultValue;
	}

	public void clear()
	{
		lines.clear();
	}

	public boolean contains(long i, long j)
	{
		return lines.containsKey(i) && lines.get(i).containsKey(j);
	}

	public void remove(long i, long j)
	{
		Long2LongMap line = lines.get(i);

		if (line != null)
		{
			line.remove(j);
		}
	}

	public int getNbColumns()
	{
		return nbColumns;
	}

	public int getNbRows()
	{
		return nbRows;
	}


	/**
	 * Fills all entries of the matrix with <code>value</code>
	 * 
	 * @param value
	 */
	public void fill(long value)
	{
		defaultValue = value;
		clear();
	}

	public void setColumnFromValuePerLine(long columnIndex, Long2LongMap line_value)
	{
		assert columnIndex >= 0 : columnIndex;
		assert columnIndex < nbColumns : columnIndex;
		assert line_value.size() == nbColumns : "line_value.size()=" + line_value.size()
				+ ", nbColumns=" + nbColumns;

		for (LongCursor lineCursor : LongCursor.fromFastUtil(line_value.keySet()))
		{
			set(lineCursor.value, columnIndex, line_value.get(lineCursor.value));
		}
	}

	public LongList extractColumn(long i)
	{
		LongList r = new LongArrayList(nbRows);

		for (long j = 0; j < nbRows; j++)
		{
			r.add(get(i, j));
		}

		return r;
	}

	public long get(long i, long j)
	{
		assert i >= 0 : i;
		assert j >= 0 : j;
		assert i < nbRows : i;
		assert j < nbColumns : j;

		if (contains(i, j))
		{
			return lines.get(i).get(j);
		}
		else
		{
			return defaultValue;
		}
	}

	public void set(long i, long j, long v)
	{
		assert i >= 0 : i;
		assert j >= 0 : j;
		assert i < nbRows : i;
		assert j < nbColumns : j;

		// clears the cell if it's not necessary
		if (v == defaultValue)
		{
			remove(i, j);
		}
		else
		{
			Long2LongMap line = lines.get(i);

			if (line == null)
			{
				lines.put(i, line = new Long2LongOpenHashMap());
			}

			line.put(j, v);
		}
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < nbRows; ++i)
		{
			for (int j = 0; j < nbColumns; ++j)
			{
				b.append('\t');
				b.append(get(i, j));
			}

			b.append('\n');
		}

		return b.toString();
	}

	public static LongMatrix multiplication(LongMatrix a, LongMatrix b)
	{
		assert a.nbColumns == b.nbRows;

		long defaultValue = defaultValue(a, b);
		LongMatrix r = new LongMatrix(a.getNbColumns(), b.getNbRows(), defaultValue);

		for (long i = 0; i < a.nbRows; i++)
			for (long j = 0; j < b.nbColumns; j++)
				for (long k = 0; k < b.nbRows; k++)
					r.set(i, j, r.get(i, j) + a.get(i, k) * r.get(k, j));

		return r;
	}
	
	private static long defaultValue(LongMatrix a, LongMatrix b)
	{
		return a.getDefaultValue() == b.getDefaultValue() ? a.getDefaultValue() : 0;
	}

	public static LongMatrix sum(LongMatrix a, LongMatrix b)
	{
		assert a.nbColumns == b.nbRows;

		long defaultValue = defaultValue(a, b);

		LongMatrix r = new LongMatrix(a.nbColumns, b.nbRows, defaultValue);

		for (long i = 0; i < a.nbRows; i++)
			for (long j = 0; j < b.nbColumns; j++)
				r.set(i, j, a.get(i, j) * b.get(i, j));

		return r;
	}

	public LongMatrix transpose()
	{
		LongMatrix r = new LongMatrix(nbColumns, nbRows, defaultValue);

		for (long i = 0; i < nbRows; i++)
			for (long j = 0; j < nbColumns; j++)
				r.set(j, i, get(i, j));

		return r;
	}

	public static LongMatrix power(LongMatrix A, int k)
	{
		LongMatrix R = new LongMatrix(A);

		for (int i = 1; i < k; i++)
			R = LongMatrix.multiplication(R, A);

		return R;
	}

	public boolean isSquare()
	{
		return nbColumns == nbRows;
	}

	public void assertSquare()
	{
		if ( ! isSquare())
			throw new IllegalStateException("this matrix is expected to be square");
	}

	public static void main(String[] args)
	{
		LongMatrix m = new LongMatrix(3, 2);
		System.out.println(m);
		m.set(1, 0, 50);
		System.out.println(m);
		System.out.println(m.extractColumn(0));
		m.fill(9);
		System.out.println(m);
	}
}
