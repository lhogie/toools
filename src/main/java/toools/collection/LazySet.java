package toools.collection;

public class LazySet {
	private LazyArray m = new LazyArray();

	public void add(int e) {
		m.put(e, 1);
	}

	public void remove(int e) {
		m.put(e, 0);
	}

	public boolean contains(int e) {
		return m.get(e) == 1;
	}
}
