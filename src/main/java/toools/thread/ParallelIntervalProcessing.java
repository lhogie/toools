package toools.thread;

import java.util.Random;

import toools.progression.LongProcess;
import toools.text.TextUtilities;

public abstract class ParallelIntervalProcessing extends MultiThreadProcessing
{
	private final double segmentSize;
	private final int intervalSize;

	public ParallelIntervalProcessing(int intervalSize, int nbThreads)
	{
		this(null, intervalSize, nbThreads, null);
	}

	public ParallelIntervalProcessing(String name, int intervalSize, int nbThreads,
			LongProcess lp)
	{
		super(nbThreads, name, lp);
		this.intervalSize = intervalSize;
		this.segmentSize = intervalSize / (double) nbThreads;
	}

	@Override
	protected final void runInParallel(ThreadSpecifics s) throws Throwable
	{
		int start = (int) (segmentSize * s.rank);
		int end = s.rank == s.threads.size() - 1 ? intervalSize
				: (int) (segmentSize * (s.rank + 1));

		if (start < end)
		{
			processInterval(s, start, end);
		}
	}

	protected abstract void processInterval(ThreadSpecifics s, int lowerBound,
			int upperBound) throws Throwable;

	public static void main(String[] args)
	{
		byte[] b = TextUtilities.pickRandomString(new Random(), 10, 10).getBytes();

		new ParallelIntervalProcessing("demo", b.length,
				MultiThreadProcessing.NB_THREADS_TO_USE, null)
		{

			@Override
			protected void processInterval(ThreadSpecifics s, int lowerBound,
					int upperBound)
			{
				System.out.println(s.rank + ": " + lowerBound + " =>" + upperBound);
			}

		}.execute();
	}
}
