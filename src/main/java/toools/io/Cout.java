package toools.io;

import toools.src.PositionInSource;
import toools.text.TextUtilities;
import toools.util.Date;

public abstract class Cout {
	public abstract void add(Object o);

	public static Cout stdout = new Stdout();
	public static Cout stderr = new Stderr();

	public static void timestamp() {
		stdout = new TimestampedOut(stdout);
		stderr = new TimestampedOut(stderr);
	}

	public static class Stdout extends Cout {

		@Override
		public void add(Object o) {
			System.out.println(TextUtilities.toString(o));
		}
	};

	public static class Stderr extends Cout {

		@Override
		public void add(Object o) {
			System.err.println(TextUtilities.toString(o));
		}
	};

	public static abstract class DelegLineOutput extends Cout {
		protected final Cout out;

		public DelegLineOutput(Cout out) {
			this.out = out;
		}
	};

	public static class TimestampedOut extends DelegLineOutput {

		public TimestampedOut(Cout out) {
			super(out);
		}

		@Override
		public void add(Object o) {
			out.add(Date.now() + " \t" + o);
		}
	};

	public static void progress(Object o) {
		stdout.add("PGRS \t" + o);
	}

	public static void warning(Object o) {
		stderr.add("WARN \t" + o);
	}

	public static void error(Object o) {
		stderr.add("ERROR \t" + o);
	}

	public static void sys(Object o) {
		stdout.add("SYS \t" + o);
	}

	public static void info(Object o) {
		stdout.add("I \t" + o);
	}

	public static void result(Object o) {
		stdout.add("R \t" + o);
	}

	public static void debug(PositionInSource here, Object... o) {
		for (Object a : o) {
			stdout.add("D\t" + here + "\t" + a);
		}
	}

	public static void debug(Object... o) {
		for (Object a : o) {
			stdout.add("D\t" + a);
		}
	}

	public static void debugSuperVisible(Object o) {
		var s = TextUtilities.toString(o);
		var box = TextUtilities.box(s);

		for (String line : box.split("\n")) {
			debug(line);
		}
	}
}
