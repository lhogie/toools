package toools.io.ser;

import toools.io.file.RegularFile;

public class decode
{
	public static void main(String[] args)
	{
		for (String filename : args)
		{
			RegularFile f = new RegularFile(filename);
			Object o = f.getContentAsJavaObject();
			System.out.println("Object class: " + o.getClass().getName());
			System.out.println("Object toString():");
			System.out.println(o);
		}
	}
}
