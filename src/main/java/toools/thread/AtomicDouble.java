package toools.thread;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicDouble
{
	AtomicLong bits = new AtomicLong(0);

	public final boolean compareAndSet(float expect, float update)
	{
		return bits.compareAndSet(Double.doubleToLongBits(expect),
				Double.doubleToLongBits(update));
	}

	public final void set(double newValue)
	{
		bits.set(Double.doubleToLongBits(newValue));
	}

	public final double get()
	{
		return Double.longBitsToDouble(bits.get());
	}

	public final double getAndSet(double newValue)
	{
		return Double.longBitsToDouble(bits.getAndSet(Double.doubleToLongBits(newValue)));
	}

	public final boolean weakCompareAndSet(double expect, double update)
	{
		return bits.weakCompareAndSet(Double.doubleToLongBits(expect),
				Double.doubleToLongBits(update));
	}
}
