package toools.collections.primitive;

import java.util.Random;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import toools.collections.LucIntSets;
import toools.math.MathsUtilities;

public class LucIntHashSet extends IntOpenHashSet implements LucIntSet
{
	private int greatest;

	public LucIntHashSet(int capacity)
	{
		super(capacity);
	}

	@Override
	public int pickRandomElement(Random r)
	{
		return LucIntSets.pickRandomInt(this, r);
	}

	@Override
	public Class<?> getImplementationClass()
	{
		return IntOpenHashSet.class;
	}

	@Override
	public void addAll(int... a)
	{
		for (int n : a)
		{
			add(n);
		}
	}

	@Override
	public boolean add(int n)
	{
		super.add(n);

		if (n > greatest)
		{
			greatest = n;
		}

		return true;
	}

	@Override
	public boolean remove(int n)
	{
		if (super.remove(n))
		{
			if (n == greatest && !isEmpty())
			{
				greatest = MathsUtilities.computeMaximum(this.iterator());
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public double getDensity()
	{
		return size() / (double) key.length;
	}

	public static void main(String[] args)
	{
		LucIntHashSet s = new LucIntHashSet(0);
		s.add(5);
		s.add(7);
		System.out.println(s.getDensity());
		s.add(3);
		s.add(1);
		System.out.println(s.getDensity());
		s.add(56);
		s.add(54);
		s.add(58);
		System.out.println(s.getDensity());
		System.out.println(s);
	}

	@Override
	public int getGreatest()
	{
		assert ! isEmpty() : "cannot get the greatest of an empty set";
		return greatest;
	}
}
