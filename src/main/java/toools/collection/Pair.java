package toools.collection;

import java.io.Serializable;

public class Pair<E, F> implements Serializable {
	public E a;
	public F b;

	public Pair(E k, F v) {
		this.a = k;
		this.b = v;
	}

	@Override
	public boolean equals(Object p) {
		return p instanceof Pair && equals((Pair) p);
	}

	public boolean equals(Pair p) {
		return a == p.a && a == p.b;
	}

	@Override
	public int hashCode() {
		int hashCode = (a == null ? 0 : a.hashCode());
		return 31 * hashCode + (b == null ? 0 : b.hashCode());
	}
}
