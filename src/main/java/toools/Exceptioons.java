package toools;

public class Exceptioons {


	public static Throwable cause(Throwable t) {
		while (t.getCause() != null) {
			t = t.getCause();
		}

		return t;
	}
}
