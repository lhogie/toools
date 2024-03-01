package toools;

import java.lang.management.ManagementFactory;

public class Systeem {


	public static double loadRatio() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()
				/ (double) Runtime.getRuntime().availableProcessors();
	}

}
