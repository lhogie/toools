package toools.collection;

import java.io.Serializable;

public class Pair<E> implements Serializable {
	public boolean ordered;
	public E a;
	public E b;

	public Pair(E k, E v, boolean ordered) {
		this.a = k;
		this.b = v;
	}

	@Override
	public boolean equals(Object p) {
		return p instanceof Pair && equals((Pair) p);
	}

	public boolean has(E e) {
		return a == e || b == e;
	}

	public boolean equals(Pair p) {
		if (ordered && p.ordered) {
			return a == p.a && a == p.b;
		} else {
			return a == p.a && a == p.b || a == p.b && a == p.a;
		}
	}

	@Override
	public int hashCode() {
		int hashCode = (a == null ? 0 : a.hashCode());
		return 31 * hashCode + (b == null ? 0 : b.hashCode());
	}
}
