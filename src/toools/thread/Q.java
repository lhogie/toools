package toools.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Q<E>
{
	private final ArrayBlockingQueue<E> q;
	public QListener<E> listener;

	public Q(int capacity)
	{
		this.q = new ArrayBlockingQueue<>(capacity);
	}

	public int size()
	{
		return q.size();
	}

	public E get_non_blocking()
	{
		return q.poll();
	}

	public E get_blocking()
	{
		try
		{
			return q.take();
		}
		catch (InterruptedException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public E get_blocking(long timeoutMs)
	{
		try
		{
			E e = q.poll(timeoutMs, TimeUnit.MILLISECONDS);
			return e;
		}
		catch (InterruptedException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void add_blocking(E in)
	{
		try
		{
			q.put(in);

			if (listener != null)
				listener.newElement(this, in);
		}
		catch (InterruptedException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public Thread msg2event(Consumer<E> l, BooleanSupplier runCondition)
	{
		Thread t = new Thread(() -> {
			while (runCondition.getAsBoolean())
			{
				E msg = get_blocking();
				l.accept(msg);
			}
		});

		t.start();
		return t;
	}
}
