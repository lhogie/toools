package toools.io;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import toools.text.TextUtilities;
import toools.util.Date;

public abstract class Cout {
	public static final PrintStream raw_stdout = System.out, raw_stderr = System.err;
	public static Cout out = new Stdout();
	public static Cout err = new Stderr();

	static {
		if (false) {
			try {
				{
					PipedInputStream pis = new PipedInputStream();
					PipedOutputStream pos = new PipedOutputStream(pis);
					Utilities.grabLines(pis, line -> out.add(line), err -> {
					});
					System.setOut(new PrintStream(pos));
				}

				{
					PipedInputStream pis = new PipedInputStream();
					PipedOutputStream pos = new PipedOutputStream(pis);
					Utilities.grabLines(pis, line -> err.add(line), err -> {
					});
					System.setErr(new PrintStream(pos));
				}
			}
			catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static void activate() {
		out = new TimestampedOut(new Beautifyer(out));
		err = new TimestampedOut(new Beautifyer(err));
	}

	public static class Stdout extends Cout {

		@Override
		public void add(Object o) {
			raw_stdout.println(o);
		}
	};

	public static class Stderr extends Cout {

		@Override
		public void add(Object o) {
			raw_stderr.println(o);
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

	public static class Beautifyer extends DelegLineOutput {

		public Beautifyer(Cout out) {
			super(out);
		}

		@Override
		public void add(Object o) {
			out.add(TextUtilities.toString(o));
		}
	};

	public static void progress(Object o) {
		out.add("PGRS \t" + o);
	}

	public static void warning(Object o) {
		err.add("WARN \t" + o);
	}

	public static void error(Object o) {
		err.add("ERROR \t" + o);
	}

	public static void sys(Object o) {
		out.add("SYS \t" + o);
	}

	public static void info(Object o) {
		out.add("I \t" + o);
	}

	public static void result(Object o) {
		out.add("R \t" + o);
	}

	public static void debug(Object... o) {
		for (Object a : o) {
			out.add("D\t" + a);
		}
	}

	public static void debugSuperVisible(Object o) {
		for (String s : TextUtilities.box(TextUtilities.toString(o)).split("\n")) {
			out.add("D\t" + s);
		}
	}

	public abstract void add(Object o);


}
