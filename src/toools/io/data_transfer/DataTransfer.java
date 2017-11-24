/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 
Aurelien Lancin (Coati research team, Inria)
Christian Glacet (LaBRi, Bordeaux)
David Coudert (Coati research team, Inria)
Fabien Crequis (Coati research team, Inria)
Grégory Morel (Coati research team, Inria)
Issam Tahiri (Coati research team, Inria)
Julien Fighiera (Aoste research team, Inria)
Laurent Viennot (Gang research-team, Inria)
Michel Syska (I3S, Université Cote D'Azur)
Nathann Cohen (LRI, Saclay) 
Julien Deantoin (I3S, Université Cote D'Azur, Saclay) 

*/

package toools.io.data_transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataTransfer
{
	/* the input stream the data is read from */
	private final InputStream inputStream;

	/* the output stream the data is written to */
	private final OutputStream outputStream;

	/* the listeners */
	private final List<DataTransferListener> listeners = new ArrayList<>();

	/* the thread */
	private final Thread thread;

	public DataTransfer(InputStream in, OutputStream out,
			DataTransferListener... listeners)
	{
		inputStream = in;
		outputStream = out;

		for (DataTransferListener l : listeners)
			this.listeners.add(l);

		// no thread is required
		thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					for (DataTransferListener l : listeners)
						l.transferStarted(DataTransfer.this);

					// allocate the buffer
					byte[] buffer = new byte[1024];

					while (true)
					{
						// will store the length of the buffer after a read
						// process
						int len = inputStream.read(buffer);

						if (len == - 1)
							break;

						outputStream.write(buffer, 0, len);

						for (DataTransferListener l : listeners)
							l.transferred(DataTransfer.this, len);
					}

					for (DataTransferListener l : listeners)
						l.transferTerminated(DataTransfer.this);
				}
				catch (IOException e)
				{
					for (DataTransferListener l : listeners)
						l.error(DataTransfer.this, e);
				}
			}
		});

		thread.start();
	}

	public void blockUntilCompletion()
	{
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
