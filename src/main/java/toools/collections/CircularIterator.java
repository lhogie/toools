package toools.collections;

import java.util.Iterator;

public class CircularIterator<E> implements Iterator<E> {
	private final Iterable<E> src;
	private Iterator<E> i;
	private final boolean empty;

	public CircularIterator(Iterable<E> src) {
		this.src = src;
		this.i = src.iterator();

		this.empty = !i.hasNext();
	}

	@Override
	public boolean hasNext() {
		return !empty;
	}

	@Override
	public E next() {
		if (!i.hasNext()) {
			i = src.iterator();
		}

		return i.next();
	}

}
