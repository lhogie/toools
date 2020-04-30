package toools.spreadsheet;

import toools.text.TextUtilities;

public class SpreadSheet
{
	private final Object[][] data;
	private final ColumnProperty[] colProps;

	public SpreadSheet(int nbRow, int nbColumn)
	{
		this.data = new Object[nbRow][nbColumn];
		this.colProps = new ColumnProperty[nbColumn];

		for (int c = 0; c < nbColumn; ++c)
		{
			colProps[c] = new ColumnProperty();
		}
	}

	public void set(int r, int c, Object value)
	{
		data[r][c] = value;

		// tells the formula where it is
		if (value instanceof Formula)
		{
			((Formula) value).set(this, r, c);
		}
	}

	public Object get(int r, int c)
	{
		Object[] row = data[r];
		return row[c];
	}

	@Override
	public String toString()
	{
		return toString(false);
	}

	public String toString(boolean title)
	{
		StringBuilder s = new StringBuilder();

		if (title)
		{
			s.append(getRowAsString(createTitleRow()));
			s.append('\n');
			s.append(getRowAsString(createBarRow()));
			s.append('\n');
		}

		for (int r = 0; r < getNbRow(); ++r)
		{
			s.append(getRowAsString(r));
			s.append('\n');
		}

		return s.toString();
	}

	public ColumnProperty getColumnProperty(int c)
	{
		return colProps[c];
	}

	public void setRow(int r, Object[] values)
	{
		for (int c = 0; c < values.length; ++c)
		{
			set(r, c, values[c]);
		}
	}

	public Object[] createTitleRow()
	{
		Object[] r = new Object[getNbColumn()];

		for (int c = 0; c < r.length; ++c)
		{
			r[c] = getColumnProperty(c).getTitle();
		}

		return r;
	}

	public int getColumnWidth(int c)
	{
		ColumnProperty columnProperties = getColumnProperty(c);

		if (columnProperties.getWidth() > 0)
		{
			return columnProperties.getWidth();
		}
		else
		{
			return idealWidth(c);
		}
	}

	public Object[] createBarRow()
	{
		Object[] r = new Object[getNbColumn()];

		for (int c = 0; c < r.length; ++c)
		{
			r[c] = TextUtilities.repeat('-', getColumnWidth(c));
		}

		return r;
	}

	public String getRowAsString(int r)
	{
		return getRowAsString(data[r]);
	}

	public int idealWidth(int c)
	{
		int width = 0;
		width = toString(getColumnProperty(c).getTitle()).length();

		for (int r = 0; r < getNbRow(); ++r)
		{
			width = Math.max(width, toString(get(r, c)).length());
		}

		return width;
	}

	private String toString(Object s)
	{
		if (s == null)
		{
			return "-";
		}
		else
		{
			return s.toString();
		}
	}

	public String getRowAsString(Object[] row)
	{
		StringBuilder s = new StringBuilder();

		for (int c = 0; c < row.length; ++c)
		{
			Object e = toString(row[c]);
			ColumnProperty columnProperties = getColumnProperty(c);
			String rowAsString = " " + TextUtilities.flush(e,
					columnProperties.getHorizontalAlignment(), getColumnWidth(c), ' ')
					+ " ";

			s.append(rowAsString);
		}

		return s.toString();
	}

	public int getNbRow()
	{
		return data.length;
	}

	public int getNbColumn()
	{
		return colProps.length;
	}
}
