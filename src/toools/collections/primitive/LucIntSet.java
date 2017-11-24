package toools.collections.primitive;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import toools.Clazz;
import toools.collections.ElementPrinter;

public interface LucIntSet extends IntSet, Serializable
{
	int pickRandomElement(Random r);

	Class<?> getImplementationClass();

	void addAll(int... a);

	double getDensity();

	int getGreatest();

	public default String toString_numbers_only()
	{
		StringBuilder b = new StringBuilder();
		Iterator<IntCursor> i = IntCursor.fromFastUtil(this).iterator();

		while (i.hasNext())
		{
			b.append(i.next().value);

			if (i.hasNext())
			{
				b.append(' ');
			}
		}

		return b.toString();
	}

	public default StringBuilder toString(ElementPrinter elementPrinter)
	{
		StringBuilder b = new StringBuilder();

		for (IntCursor i : IntCursor.fromFastUtil(this))
		{
			b.append(i.value);
		}

		return b;
	}

	public static LucIntSet singleton(int a)
	{
		LucIntSet r = new LucIntHashSet();
		r.add(a);
		return r;
	}

	public default void writeTo(PrintStream os)
	{
		IntIterator i = iterator();

		while (i.hasNext())
		{
			os.append(String.valueOf(i.nextInt()));

			if (i.hasNext())
			{
				os.append(' ');
			}
		}
	}

	public default UnmodifiableLucSet unmodifiable()
	{
		return new UnmodifiableLucSet(this);
	}
	
	public static UnmodifiableLucSet unmodifiable(LucIntSet s)
	{
		return new UnmodifiableLucSet(s);
	}

	public static class UnmodifiableLucSet extends IntSets.UnmodifiableSet
			implements LucIntSet
	{
		private final LucIntSet s;

		public UnmodifiableLucSet(LucIntSet s)
		{
			super(s);
			this.s = s;
		}

		@Override
		public int pickRandomElement(Random r)
		{
			return s.pickRandomElement(r);
		}

		@Override
		public Class<?> getImplementationClass()
		{
			return s.getImplementationClass();
		}

		@Override
		public void addAll(int... a)
		{
			throw new IllegalStateException();
		}

		@Override
		public double getDensity()
		{
			return s.getDensity();
		}

		@Override
		public int getGreatest()
		{
			return s.getGreatest();
		}
	}

	public static final EmptyLucSet EMPTY_SET = new EmptyLucSet();

	public static class EmptyLucSet extends IntSets.EmptySet implements LucIntSet
	{
		@Override
		public int pickRandomElement(Random r)
		{
			throw new IllegalStateException();
		}

		@Override
		public Class<?> getImplementationClass()
		{
			return IntSets.EmptySet.class;
		}

		@Override
		public void addAll(int... a)
		{
			throw new IllegalStateException();
		}

		@Override
		public double getDensity()
		{
			throw new IllegalStateException();
		}

		@Override
		public int getGreatest()
		{
			throw new IllegalStateException();
		}
	}

	public static LucIntSet difference(LucIntSet a, LucIntSet b)
	{
		LucIntSet r = Clazz.makeInstance(a.getClass());
		r.addAll(a);
		r.removeAll(b);
		return r;
	}

	public default boolean equals(int... i)
	{
		LucIntSet s = new LucIntHashSet();
		s.addAll(i);
		return equals(s);
	}

}
