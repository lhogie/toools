package toools.math;

import java.io.IOException;
import java.util.Scanner;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class stat
{
	public static void main(String[] args) throws IOException
	{
		Scanner r = new Scanner(System.in);
		DoubleList l = new DoubleArrayList();

		while (r.hasNext())
		{
			double n = r.nextDouble();
			l.add(n);
		}

		System.out.println("count: " + l.size());
		System.out.println("avg: " + MathsUtilities.avg(l.toDoubleArray()));
		System.out.println(
				"std dev: " + MathsUtilities.stdDev(l.toDoubleArray()));
	}
}
