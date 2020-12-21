package toools.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Q<E> {
	private final BlockingQueue<E> q;
	public QListener<E> listener;
	private Thread thread;

	public Q(int capacity) {
		this.q = new ArrayBlockingQueue<>(capacity);
	}

	public int size() {
		return q.size();
	}

	public E get_non_blocking() {
		return q.poll();
	}

	public E get_blocking() {
		try {
			return q.take();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public E get_blocking(double timeout) {
		try {
			long tns = (long) (timeout * 1000000000);
			E e = q.poll(tns, TimeUnit.NANOSECONDS);
			return e;
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public E get_blockingOrFail(double timeout) {
		E e = get_blocking(timeout);

		if (e != null) {
			return e;
		}

		throw new TimeoutException();
	}

	public void add_blocking(E in) {
		try {
			q.put(in);

			if (listener != null) {
				listener.newElement(this, in);
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public Thread onMsg(Consumer<E> l, BooleanSupplier runCondition) {
		if (thread != null)
			throw new IllegalStateException("thread already exists");

		thread = new Thread(() -> {
			while (runCondition.getAsBoolean()) {
				E msg = get_blocking();
				l.accept(msg);
			}
		});

		thread.start();
		return thread;
	}


	public void cancelEventisation() {
		if (thread != null) {
			thread.interrupt();
		}
	}
}
