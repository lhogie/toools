package toools.collection;

import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import toools.StopWatch;

public class LazyArray
{
	protected final int[][] slots;
	protected final int slotLength;
	protected final int paddingValue;

	public LazyArray()
	{
		this(128, 0);
	}

	public LazyArray(int slotLength, int impossibleValue)
	{
		this.slotLength = slotLength;
		this.slots = new int[Integer.MAX_VALUE / slotLength][];
		this.paddingValue = impossibleValue;
	}

	public LazyArray(LazyArray a)
	{
		this.slotLength = a.slotLength;
		this.slots = new int[a.slots.length][];
		this.paddingValue = a.paddingValue;

		for (int i = 0; i < slots.length; ++i)
			if (a.slots[i] != null)
				slots[i] = IntArrays.copy(a.slots[i]);
	}

	public int get(int k)
	{
		int slotIndex = k / slotLength;
		int[] slot = slots[slotIndex];

		if (slot == null)
		{
			return paddingValue;
		}
		else
		{
			int indexInSlot = k % slotLength;
			int value = slot[indexInSlot];
			return value;
		}
	}

	public boolean containsKey(int k)
	{
		return get(k) != paddingValue;
	}

	public void put(int k, int v)
	{
		if (v == paddingValue)
			throw new IllegalArgumentException();

		assert v != paddingValue;

		int slotIndex = k / slotLength;
		int[] slot = slots[slotIndex];

		if (slot == null)
		{
			slot = slots[slotIndex] = new int[slotLength];

			if (paddingValue != 0)
			{
				Arrays.fill(slot, paddingValue);
			}
		}

		int indexInSlot = k % slotLength;
		slot[indexInSlot] = v;
	}

	public int countDefinedCells()
	{
		int c = 0;

		for (int[] s : slots)
		{
			if (s != null)
			{
				for (int a : s)
				{
					if (a != paddingValue)
					{
						++c;
					}

				}
			}
		}

		return c;
	}

	public int countSlots()
	{
		int r = 0;

		for (int[] s : slots)
		{
			if (s != null)
			{
				++r;
			}
		}

		return r;
	}

	public double computeDensity()
	{
		return countDefinedCells() / (double) (countSlots() * slotLength);
	}

	public int[] toIntArray()
	{
		int sz = countDefinedCells();
		int[] r = new int[sz];
		int i = 0;

		for (int[] s : slots)
		{
			if (s != null)
				for (int a : s)
				{
					if (a != paddingValue)
					{
						r[i++] = a;
					}
				}
		}

		assert i == sz;
		return r;
	}

	public void addAllTo(IntCollection c)
	{
		for (int[] s : slots)
		{
			if (s != null)
				for (int a : s)
				{
					if (a != paddingValue)
					{
						c.add(a);
					}
				}
		}
	}

	public static void main(String[] args)
	{
		int n = 10000000;
		// LazyArray m = new LazyArray();
		// Int2IntMap m = new Int2IntOpenHashMap(n);
		int[] m = new int[n];

		for (int r = 0; r < 10; ++r)
		{
			StopWatch sw = new StopWatch();

			for (int i = 0; i < n; ++i)
			{
				m[i] = i;

				if (m[i] != i)
					throw new IllegalStateException();
			}

			System.out.println(sw);
		}
	}

}
