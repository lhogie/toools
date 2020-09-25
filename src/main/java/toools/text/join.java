package toools.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class join
{
	public static void main(String[] args) throws IOException
	{
		List<BufferedReader> l = new ArrayList<BufferedReader>();

		for (String fn : args)
		{
			l.add(new BufferedReader(new FileReader(fn)));
		}

		for (;;)
		{
			for (BufferedReader in : l)
			{
				String line = in.readLine();
				System.out.print(line);
				System.out.print('\t');
			}

			System.out.println(); 
}
	}
}
