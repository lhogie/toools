package toools.spreadsheet;

public class Demo
{
	public static void main(String[] args)
	{
		SpreadSheet ss = new SpreadSheet(3, 2);
		ss.set(0, 0, 4d);
		ss.set(0, 1, 2d);
		ss.set(1, 0, 9d);
		ss.set(1, 1, 1d);
		ss.set(2,  1,  new Sum());
		
		System.out.println(ss);
	}
}
