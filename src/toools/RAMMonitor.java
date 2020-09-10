package toools;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.math.MathsUtilities;
import toools.thread.Threads;

public class RAMMonitor
{
	public static final List<MemoryInfo> l = new ArrayList<>();

	public static class MemoryInfo
	{
		final long totalMemoryInJVM;
		final long freeMemoryInJVM;
		final String extraMsg;
		final long timeNs = System.nanoTime();

		public MemoryInfo(Supplier<String> extraMsg)
		{
			Runtime rt = Runtime.getRuntime();
			this.totalMemoryInJVM = rt.totalMemory();
			this.freeMemoryInJVM = rt.freeMemory();
			this.extraMsg = extraMsg == null ? null : extraMsg.get();
		}

		public long usedMemoryInJVM()
		{
			return totalMemoryInJVM - freeMemoryInJVM;
		}

		public String getStatusFile() throws IOException
		{
			String s = "";
			FileInputStream fis = new FileInputStream("/proc/self/status");

			while (true)
			{
				int c = fis.read();

				if (c == - 1)
					break;

				s += (char) c;
			}

			fis.close();
			return s;
		}

		@Override
		public String toString()
		{
			try
			{
				for (String line : getStatusFile().split("\n"))
				{
					if (line.startsWith("VmSize:"))
					{
						long sizeK = Long.valueOf(line.substring("VmSize:".length() + 1, line.length() - 2).trim());
						long sizeG = sizeK / 1000000;
						return sizeG + "GB";
					}
				}
				
				throw new IllegalStateException();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "can't get process size";
			}
		}

		public String toString2()
		{
			double usedMemGB = MathsUtilities
					.round(usedMemoryInJVM() / 1024 / 1024 / 1024d, 2);
			long usageRatio = (100 * usedMemoryInJVM()) / ramSizeInComputer();
			long totalRAMGB = ramSizeInComputer() / 1024 / 1024 / 1024;
			String s = usedMemGB + "GB --- " + usageRatio + "% of " + totalRAMGB + "GB";

			if (extraMsg != null)
				s += "\t" + extraMsg;

			return s;
		}

	}

	public static String processSize()
	{
		if ( ! new Directory("/proc").exists())
			return "can't process size of current process under non-Linux operating system";

		int pid = Integer.valueOf(
				new RegularFile("/proc/self/stat").getContentAsText().split(" ")[0]);
		RegularFile f = new RegularFile("/proc/" + pid + "/status");
		for (String line : f.getContentAsText().split("\n"))
		{
			if (line.contains("VmSize"))
			{
				return line.substring("VmSize".length() + 1);
			}
		}

		throw new IllegalStateException();
	}

	public static long ramSizeInComputer()
	{
		return ((com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
	}

	public static long freeRAMInComputer()
	{
		return ((com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
	}

	public static long usedMemoryInComputer()
	{
		return ramSizeInComputer() - freeRAMInComputer();
	}

	public static void start(int periodMs, Supplier<String> extraMsg)
	{
		start(periodMs, extraMsg, msg -> System.out.println("Size of the process " + msg));
	}

	public static void start(int periodMs, Supplier<String> extraMsg,
			Consumer<MemoryInfo> out)
	{
		Threads.newThread_loop_periodic(periodMs, () -> true, () -> {
			while (true)
			{
				MemoryInfo u = new MemoryInfo(extraMsg);
				l.add(u);
				out.accept(u);
				Threads.sleepMs(5000);
			}
		});

	}
}
