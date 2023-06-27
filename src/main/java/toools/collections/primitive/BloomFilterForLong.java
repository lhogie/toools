package toools.collections.primitive;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import toools.SizeOf;

public class BloomFilterForLong implements SizeOf {
	public LongArrayList l = new LongArrayList();
	private int len = 100;

	public BloomFilterForLong(int size) {
		this.len = size;
	}

	public void add(long n) {
		l.add(n);

		if (l.size() > len) {
			//shrinkTo(len);
		}
	}

	private void shrinkTo(int n) {
		l.removeElements(0, l.size() - n);
	}

	public boolean contains(long n) {
		return l.contains(n);
	}

	@Override
	public long sizeOf() {
		return l.size() * 8;
	}
}
