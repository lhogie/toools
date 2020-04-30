package toools;

import java.lang.management.ManagementFactory;

import toools.extern.Proces;
import toools.io.Cout;
import toools.text.TextUtilities;
import toools.thread.Threads;

public class SystemMonitor
{
	public static final SystemMonitor defaultMonitor = new SystemMonitor(5000);

	private final Thread t;
	private boolean run = true;
	final boolean mpstatIsAvailable;

	public SystemMonitor(int periodMs)
	{
		this.mpstatIsAvailable = Proces.commandIsAvailable("mpstat");

		t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (run)
				{
					Threads.sleepMs(periodMs);
					Cout.sys(getMessage());
				}
			}
		});

		t.setDaemon(true);
	}

	public void start()
	{
		t.start();
	}

	public void stop()
	{
		run = false;
	}

	private String getMessage()
	{
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();

		return TextUtilities.toHumanString(total - free) + "B RAM used, CPU="
				+ getCPUUtilization() + "%, load avg="
				+ ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()
				+ ", nbCores="
				+ ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	}

	public int getCPUUtilization()
	{
		if (false)// mpstatIsAvailable)
		{
			double idle = Double
					.valueOf(new String(Proces.exec("mpstat", "1", "1")).split("\n")[4]
							.substring(90).trim());
			return (int) (100 - idle);
		}
		else
		{
			double loadAvg = ManagementFactory.getOperatingSystemMXBean()
					.getSystemLoadAverage();
			int nbCore = ManagementFactory.getOperatingSystemMXBean()
					.getAvailableProcessors();
			double cpuUtilization = loadAvg / nbCore;
			return (int) cpuUtilization;
		}
	}

}
