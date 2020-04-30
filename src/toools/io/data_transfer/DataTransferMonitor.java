package toools.io.data_transfer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toools.io.NullOutputStream;
import toools.io.file.RegularFile;
import toools.thread.Threads;

public class DataTransferMonitor implements DataTransferListener
{
	long startTimeMs = - 1, endTimeMs = - 1;
	long nbBytes;

	@Override
	final public void transferStarted(DataTransfer dt)
	{
		startTimeMs = System.currentTimeMillis();
	}

	@Override
	final public void transferTerminated(DataTransfer dt)
	{
		endTimeMs = System.currentTimeMillis();
	}

	@Override
	final public void transferred(DataTransfer dt, int len)
	{
		nbBytes += len;
	}

	@Override
	final public void error(DataTransfer simpleDataTransfer, IOException e)
	{
		endTimeMs = System.currentTimeMillis();
	}

	/**
	 * @return the average transfer rate (byte/s). This is the average transfer
	 *         rate of the whole transfer.
	 */
	public int getNbBytesPerSecond()
	{
		if (startTimeMs == - 1)
			throw new IllegalStateException("transfer has not yet begun");

		long duration = getDuration();

		if (duration == 0)
			throw new IllegalStateException("too early to know");

		return (int) ((1000L * nbBytes) / duration);
	}

	public long getDuration()
	{
		if (startTimeMs == - 1)
			throw new IllegalStateException("transfer has not started yet");

		if (endTimeMs == - 1)
		{
			return System.currentTimeMillis() - startTimeMs;
		}
		else
		{
			return endTimeMs - startTimeMs;
		}
	}

	public long getNbBytes()
	{
		return nbBytes;
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		InputStream in = new RegularFile("$HOME/biggrph/datasets/acc2008_1.txt.anon")
				.createReadingStream();
		OutputStream out = new NullOutputStream();
		DataTransferMonitor m = new DataTransferMonitor();

		new DataTransfer(in, out, m);

		while (true)
		{
			Threads.sleepMs(100);
			System.out.println(m.getDuration() + "ms => " + m.getNbBytesPerSecond());
		}
	}

	public boolean transferTerminated()
	{
		return endTimeMs != -1;
	}

}
