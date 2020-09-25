package toools;

import java.util.function.Supplier;

public class SimpleCache<E>
{
	private E value;
	private final E invalidValue;
	private final Supplier<E> supplier;

	public SimpleCache(E iv, Supplier<E> supplier)
	{
		this.invalidValue = iv;
		this.supplier = supplier;
	}

	public E get()
	{
		if ( ! isValid())
		{
			E computedValue = supplier.get();
			set(computedValue);
		}

		return value;
	}

	public E set(E t)
	{
		return this.value = t;
	}

	public boolean isValid()
	{
		if (invalidValue == null)
		{
			return value != null;
		}
		else
		{
			return ! invalidValue.equals(value);
		}
	}

	public void invalidate()
	{
		set(invalidValue);
	}
}
