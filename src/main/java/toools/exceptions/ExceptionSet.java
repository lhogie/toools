package toools.exceptions;

import java.util.HashSet;
import java.util.Set;

public class ExceptionSet extends RuntimeException {
	public Set<Throwable> exceptions = new HashSet<>();
}
