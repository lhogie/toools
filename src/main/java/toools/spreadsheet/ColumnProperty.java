package toools.spreadsheet;

import toools.text.TextUtilities.HORIZONTAL_ALIGNMENT;

public class ColumnProperty
{
	private int width= -1;
	private HORIZONTAL_ALIGNMENT a = HORIZONTAL_ALIGNMENT.RIGHT;
	private String title;

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public HORIZONTAL_ALIGNMENT getHorizontalAlignment()
	{
		return a;
	}

	public void setA(HORIZONTAL_ALIGNMENT a)
	{
		this.a = a;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
}
