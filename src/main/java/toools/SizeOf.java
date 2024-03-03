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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface SizeOf {
	long sizeOf();

	public static long sizeOf(Iterable<?> c) {
		if (c == null) {
			return 0;
		}

		long sum = 8;

		for (var o : c) {
			sum += sizeOf(o);
		}

		return sum;
	}

	public static long sizeOf(String s) {
		if (s == null) {
			return 0;
		}

		return 8 + s.length() * 2;
	}

	public static List<Function<Object, Integer>> sizeEvaluators = new ArrayList<>();

	public static long sizeOf(Object o) {
		if (o == null) {
			return 0;
		} else if (o instanceof SizeOf) {
			return ((SizeOf) o).sizeOf();
		} else if (o instanceof Iterable) {
			return sizeOf((Iterable) o);
		} else if (o instanceof String) {
			return sizeOf((String) o);
		} else if (o instanceof Long) {
			return 8;
		} else if (o instanceof Integer) {
			return 4;
		} else if (o instanceof Short) {
			return 2;
		} else if (o instanceof Character) {
			return 2;
		} else if (o instanceof Byte) {
			return 1;
		} else if (o instanceof Double) {
			return 8;
		} else if (o instanceof Float) {
			return 4;
		} else if (o instanceof Throwable) {
			return 1;
		} else if (o instanceof Map.Entry) {
			var e = (Map.Entry) o;
			return sizeOf(e.getKey()) + sizeOf(e.getValue());
		} else if (o instanceof Class) {
			return 8;
		}

		for (var f : sizeEvaluators) {
			try {
				return f.apply(o);
			} catch (Throwable err) {
			}
		}

		throw new IllegalArgumentException("cannot sizeof " + o.getClass());
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
