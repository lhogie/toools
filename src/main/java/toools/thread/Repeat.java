package toools.thread;

import java.util.function.BooleanSupplier;

public class Repeat
{

	/*
	 * public static Thread f(Callable<Long> r) { Thread t = new Thread(() -> {
	 * while (true) { try { long wait = r.call();
	 * 
	 * if (wait < 0) return;
	 * 
	 * Threads.sleepMs(wait); } catch (Exception e) { e.printStackTrace();
	 * return; } } });
	 * 
	 * t.start(); return t; }
	 */
}
