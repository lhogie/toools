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

import java.util.Arrays;

/**
 * A matrix made of primitive ints. Indices for lines and columns begin to 0. It
 * is implemented using a two-dimensional float array: [x, x, x, x, x] [x, x,
 * x, x, x] [x, x, x, x, x] [x, x, x, x, x]
 * 
 * @author Gregory Morel, lhogie
 * 
 */
public class FloatMatrix
{
	public final float[][] array;
	public final int width;
	public final int height;

	public FloatMatrix(int width, int height)
	{
		assert width > 0;
		assert height > 0;
		this.array = new float[height][width];
		this.height = height;
		this.width = width;
	}

	public FloatMatrix(float[][] tab)
	{
		height = tab.length;
		width = tab[0].length;
		array = new float[height][width];

		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				array[i][j] = tab[i][j];
	}

	/**
	 * Fills all entries of the matrix with <code>value</code>
	 * 
	 * @param value
	 */
	public void fill(float value)
	{
		for (int i = 0; i < height; i++)
		{
			Arrays.fill(array[i], value);
		}
	}

	public void setColumn(int i, float[] c)
	{
		assert i >= 0 : i;
		assert i < width : i;
		assert c.length == height : c.length;

		for (int j = 0; j < height; ++j)
			array[j][i] = c[j];
	}

	public float[] getColumn(int i)
	{
		float[] t = new float[height];

		for (int j = 0; j < height; j++)
			t[j] = array[j][i];

		return t;
	}

	public float get(int i, int j)
	{
		assert i >= 0 : i;
		assert j >= 0 : j;
		assert i < height : i;
		assert j < width : j;

		return array[i][j];
	}

	public void set(int i, int j, float v)
	{
		assert i >= 0 : i;
		assert j >= 0 : j;
		assert i < height : i;
		assert j < width : j;

		array[i][j] = v;
	}

	public float[][] toIntArray()
	{
		return array.clone();
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < height; ++i)
			b.append(Arrays.toString(array[i]) + "\n");

		return b.toString();
	}

	public static FloatMatrix multiplication(FloatMatrix ma, FloatMatrix mb)
	{
		assert ma.width == mb.height;

		FloatMatrix m = new FloatMatrix(ma.width, mb.height);

		for (int i = 0; i < ma.height; i++)
			for (int j = 0; j < mb.width; j++)
				for (int k = 0; k < mb.height; k++)
					m.array[i][j] += ma.array[i][k] * mb.array[k][j];

		return m;
	}

	public static void main(String[] args)
	{
		FloatMatrix m = new FloatMatrix(3, 2);
		System.out.println(m);
		m.set(1, 0, 50);
		System.out.println(m);
		System.out.println(Arrays.toString(m.getColumn(0)));
		m.fill(9);
		System.out.println(m);
	}
}
