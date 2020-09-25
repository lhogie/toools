package toools.extern;

public class ProcesException extends RuntimeException
{

	public ProcesException(String msg)
	{
		super(msg);
	}

	public ProcesException(Exception e)
	{
		super(e);
	}

}
