package toools.spreadsheet;

public class Sum extends Formula<Double>
{
	@Override
	protected Double compute()
	{
		double sum = 0;

		for (int r = 0; r < getRow(); ++r)
		{
			Object e = getSpreadSheet().get(r, getColumn());

			if (e != null)
			{
				try
				{
					sum += Double.parseDouble(e.toString());
				}
				catch (NumberFormatException err)
				{
					// cell is not a number, ignore it
				}
			}
		}

		return sum;
	}
}