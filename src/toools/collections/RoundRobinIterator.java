package toools.collections;

import java.util.Iterator;

public class RoundRobinIterator<E> implements Iterator<E>
{
	private final Iterable<E> i;
	private Iterator<E> ci;

	public RoundRobinIterator(Iterable<E> iterable)
	{
		this.i = iterable;
		ci = i.iterator();
	}

	@Override
	public boolean hasNext()
	{
		return true;
	}

	@Override
	public E next()
	{
		if ( ! ci.hasNext())
		{
			ci = i.iterator();
		}

		return ci.next();
	}

}
