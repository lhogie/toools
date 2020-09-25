package toools.collections;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.IntPredicate;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import toools.collections.primitive.LucIntHashSet;
import toools.collections.primitive.LucIntSet;
import toools.collections.primitive.SelfAdaptiveIntSet;
import toools.math.MathsUtilities;
import toools.reflect.Clazz;

public class LucIntSets
{
	public static IntOpenHashSet create(int... elements)
	{
		return create(IntOpenHashSet.class, elements);
	}

	public static <S extends IntSet> S create(Class<S> clazz, int... elements)
	{
		return create(clazz, IntIterators.wrap(elements));
	}

	public static <S extends IntSet> S create(Class<S> clazz, IntIterator elements)
	{
		S s = Clazz.makeInstance(clazz);

		IntIterator i = s.iterator();

		while (i.hasNext())
		{
			s.add(i.nextInt());
		}

		return s;
	}

	@SafeVarargs
	public static <S extends IntSet> S unionTo(S t, IntSet... sets)
	{
		for (IntSet set : sets)
		{
			t.addAll(set);
		}

		return t;
	}

	public static LucIntSet union(IntSet... sets)
	{
		int n = 0;

		for (IntSet s : sets)
		{
			n += s.size();
		}

		LucIntSet t = new LucIntHashSet(n);
		return unionTo(t, sets);
	}

	public static <T> IntSet intersection(IntSet... sets)
	{
		IntSet t = new IntOpenHashSet();
		LucIntSets.intersectionToTarget(t, sets);
		return t;
	}

	@SafeVarargs
	static <T> void intersectionToTarget(IntSet target, IntSet... sets)
	{
		target.addAll(sets[0]);

		for (int i = 1; i < sets.length; ++i)
		{
			target.retainAll(sets[i]);
		}
	}

	public static <R extends IntSet, T> R intersectionToTarget(Class<R> target,
			IntSet... sets)
	{
		R r = Clazz.makeInstance(target);
		r.addAll(sets[0]);

		for (int i = 1; i < sets.length; ++i)
		{
			r.retainAll(sets[i]);
		}

		return r;
	}

	public static <T> IntSet difference(IntSet a, IntSet... b)
	{
		IntSet c = new IntOpenHashSet(a);

		for (IntSet _b : b)
		{
			c.removeAll(_b);
		}

		return c;
	}

	public static int pickRandomInt(IntCollection c, Random r)
	{
		int i = MathsUtilities.pickIntBetween(0, c.size(), r);

		IntIterator it = c.iterator();
		it.skip(i);
		return it.next();
	}

	public static int pickRandomInt(IntArrayList l, Random r)
	{
		int i = MathsUtilities.pickIntBetween(0, l.size(), r);
		return l.getInt(i);
	}

	public static IntSet filter(IntSet vertices, IntPredicate filter)
	{
		IntSet r = Clazz.makeInstance(vertices.getClass());
		IntIterator i = vertices.iterator();

		while (i.hasNext())
		{
			int n = i.nextInt();

			if (filter.test(n))
			{
				r.add(n);
			}
		}

		return r;
	}

	/**
	 * Sorts a list of sets by ascending cardinality.
	 * 
	 * @param tab
	 *            a list of IntSets
	 * @param s
	 *            start index
	 * @param e
	 *            end index
	 */
	public static void quickSortSet(ArrayList<IntSet> tab, int s, int e)
	{
		if (s < e)
		{
			int m = LucIntSets.partitionSet(tab, s, e);
			quickSortSet(tab, s, m);
			quickSortSet(tab, m + 1, e);
		}
	}

	static int partitionSet(ArrayList<IntSet> tab, int s, int e)
	{
		int i = s - 1;
		int j = e + 1;
		int pivot = tab.get(s).size();

		IntSet temp;
		while (true)
		{
			do
				j--;
			while (tab.get(j).size() > pivot);

			do
				i++;
			while (tab.get(i).size() < pivot);

			if (i < j)
			{
				temp = new SelfAdaptiveIntSet(tab.get(i).size());
				temp.addAll(tab.get(i));
				tab.set(i, tab.get(j));
				tab.set(j, temp);
			}
			else
				return j;
		}
	}

	public static IntSet pickRandomSubIntset(IntSet s, Random prng, boolean remove)
	{
		return LucIntSets.pickRandomSubIntset(s, prng, prng.nextInt(s.size()), remove);
	}

	public static IntSet pickRandomSubIntset(IntSet s, Random prng, int numberOfElements,
			boolean remove)
	{
		if (numberOfElements > s.size())
			throw new IllegalArgumentException("cannot pick that many elements");

		IntSet r = null;

		if (numberOfElements == s.size())
		{
			r = new IntOpenHashSet(s);

			if (remove)
			{
				s.clear();
			}
		}
		else
		{
			r = new IntOpenHashSet();

			while (numberOfElements > r.size())
			{
				r.add(LucIntSets.pickRandomInt(s, prng, r, remove));
			}
		}

		return r;
	}

	public static int pickRandomInt(IntSet s, Random prng, int excludedElement,
			boolean remove)
	{
		while (true)
		{
			int e = pickRandomInt(s, prng);

			if (e != excludedElement)
			{
				if (remove)
				{
					s.remove(e);
				}

				return e;
			}
		}
	}

	public static int pickRandomInt(IntSet s, Random prng, IntSet excludedElements,
			boolean remove)
	{
		while (true)
		{
			int e = pickRandomInt(s, prng);

			if ( ! excludedElements.contains(e))
			{
				if (remove)
				{
					s.remove(e);
				}

				return e;
			}
		}
	}

	/**
	 * Parse the given strings, expecting that is describes a set of ints.
	 * Non-numerical characters are ignored. This enables the parsing of: [4, 5,
	 * 6] [4 5 6] 4, 5, 6 4 5 6 {4 5 6}
	 * 
	 * @param a
	 *            string describing a set of positive integers.
	 * @return the set of positive integers
	 */
	public static <A extends IntSet> A from(Class<A> targetClass, String s)
	{
		A r = Clazz.makeInstance(targetClass);

		for (String t : s.replaceAll("[^0-9]", " ").trim().split(" +"))
		{
			r.add(Integer.parseInt(t));
		}

		return r;
	}

}
