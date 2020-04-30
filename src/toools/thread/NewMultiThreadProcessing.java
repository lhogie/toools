package toools.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class NewMultiThreadProcessing
{

	
	public static void main(String[] args) throws InterruptedException
	{
		ExecutorService pool = Executors.newFixedThreadPool(10, new ThreadFactory()
		{
			
			@Override
			public Thread newThread(Runnable r)
			{
				return new Thread(r);
			}
		});
		
		for (int i = 0; i < 10; ++i)
		{
			pool.submit(new Callable<Integer>()
			{

				@Override
				public Integer call() throws Exception
				{
					System.out.println("ok");
					return null;
				}
			});
		}
		
		pool.awaitTermination(10, TimeUnit.SECONDS);
		
		System.out.println("completed");
	}
}
