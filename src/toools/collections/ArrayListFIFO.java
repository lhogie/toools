package toools.collections;

import java.util.ArrayList;

public class ArrayListFIFO<E>
{
	private static class A<E> extends ArrayList<E>
	{
		@Override
		public void removeRange(int a, int b)
		{
			super.removeRange(a, b);
		}
	};

	private int indexOfFirst = 0;
	private final A<E> l = new A<>();

	public void push(E e)
	{
		l.add(e);
	}

	public E poll()
	{
		E e = l.get(indexOfFirst++);

		if (indexOfFirst == 1000)
		{
			l.removeRange(0, indexOfFirst);
			indexOfFirst = 0;
		}

		return e;
	}

	public E peek()
	{
		if (size() == 0)
		{
			return null;
		}
		else
		{
			return l.get(indexOfFirst);
		}
	}

	public int size()
	{
		return l.size() - indexOfFirst;
	}
}