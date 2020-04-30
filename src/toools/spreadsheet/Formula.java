package toools.spreadsheet;

public abstract class Formula<T>
{
	private SpreadSheet spreadSheet;

	private int row, column;

	public SpreadSheet getSpreadSheet()
	{
		return spreadSheet;
	}

	public int getRow()
	{
		return row;
	}

	public int getColumn()
	{
		return column;
	}

	void set(SpreadSheet ss, int r, int c)
	{
		this.spreadSheet = ss;
		this.row = r;
		this.column = c;
	}

	protected abstract T compute();

	@Override
	public String toString()
	{
		return compute().toString();
	}
}