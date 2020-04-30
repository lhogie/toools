package toools;


public interface LongChanger
{
	long alter(long v);
	
	public static final LongChanger noChange = new LongChanger()
	{
		@Override
		public long alter(long v)
		{
			return v;
		}
	};
}