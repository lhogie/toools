package toools.thread;

import java.util.Iterator;

public class ConcurrentIterator<T>
{
	private final Iterator<T> i;

	public ConcurrentIterator(Iterable<T> iterable)
	{
		this.i = iterable.iterator();
	}

	public synchronized T next()
	{
		return i.hasNext() ? i.next() : null;
	}
}