package toools.io;

public class Hasher {
	private int result = 1;
	private long count;

	public long count()
	{
		return count;
	}
	
	public int result() {
		return result;
	}
	
	public void add(byte b) {
		result = 31 * result + b;
		++count;
	}

	public void add(int i) {
		add((byte) (i));
		add((byte) (i << 8));
		add((byte) (i << 16));
		add((byte) (i << 24));
	}
	
	public void add(Object o) {
		add(o.hashCode());
	}
	
	
	public static void main(String[] args) {
		var h = new Hasher();
		h.add(5);
		System.out.println(h.result);
		h.add(5);
		System.out.println(h.result);
		h.add("cocuufc");
		System.out.println(h.result);
	}
}
