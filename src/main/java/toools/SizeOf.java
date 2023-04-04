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

import java.util.Collection;
import java.util.Map;

public interface SizeOf {
	long sizeOf();

	public static <A extends SizeOf> long sizeOf(Iterable<A> c) {
		if (c == null) {
			return 0;
		}

		long sum = 8;

		for (var o : c) {
			sum += o.sizeOf();
		}

		return sum;
	}

	public static long sizeOf(String s) {
		if (s == null) {
			return 0;
		}

		return 8 + s.length() * 2;
	}

	public static long sizeOf(Object s) {
		if (s == null) {
			return 0;
		}

		throw new IllegalArgumentException();
	}

	public static <A extends SizeOf, C extends Collection<? extends SizeOf>> long sizeOfM(Map<A, C> m) {
		long r = 0;

		for (var k : m.keySet()) {
			r += 8 + k.sizeOf();
		}

		for (var v : m.values()) {
			r += 8 + sizeOf(v);
		}

		return r;
	}
	
	public static <C extends Collection<? extends SizeOf>> long sizeOf(Map<String, C> m) {
		long r = 0;

		for (var k : m.keySet()) {
			r += 8 + sizeOf(k);
		}

		for (var v : m.values()) {
			r += 8 + sizeOf(v);
		}

		return r;
	}
}
