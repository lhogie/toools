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

public class CellularAutomata32 {
	public int cells = 0;
	private static final int[] rule30 = new int[] { 0, 1, 1, 1, 1, 0, 0, 0 };

	public void evolve() {
		int next = 0;

		next |= rule30[((cells << 1) | (cells >> 31)) & 7];

		for (int i = 1; i < 31; ++i) {
			int k = (cells >> i - 1) & 7;
			next |= rule30[k] << i;
		}

		next |= (rule30[((cells >> 30) | ((cells & 1) << 2)) & 7]) << 31;
		cells = next;
	}

	public int getCells() {
		return cells;
	}

	public void setCells(int cells) {
		this.cells = cells;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('[');

		for (int i = 0; i < 32; ++i) {
			b.append(((cells >> i) & 1) == 0 ? ' ' : '*');
		}

		b.append(']');
		return b.toString();
	}

	public static void main(String[] args) {
		CellularAutomata32 ca = new CellularAutomata32();
		ca.setCells(567);
		System.out.println(ca);

		for (int i = 0; i < 2000; ++i) {
			ca.evolve();
			System.out.println(ca);
		}
	}
}
