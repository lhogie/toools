package toools;

import java.util.function.Supplier;

public class SimpleCache<T>
{
	private T value;
	private final T invalidValue;
	private final Supplier<T> supplier;

	public SimpleCache(T iv, Supplier<T> supplier)
	{
		this.invalidValue = iv;
		this.supplier = supplier;
	}

	public T get()
	{
		if ( ! isValid())
		{
			T computedValue = supplier.get();
			set(computedValue);
		}

		return value;
	}

	public T set(T t)
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
