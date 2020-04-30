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
 
 package toools.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import toools.io.FullDuplexDataConnection2;
import toools.io.IORuntimeException;
import toools.io.ObjectInputStream2;
import toools.io.ObjectOutputStream2;

public class TCPConnection extends FullDuplexDataConnection2
{
	private final Socket socket;
	private final boolean bufferred;

	public TCPConnection(InetAddress hostname, int port, int timeoutms)
			throws UnknownHostException, IOException
	{
		this(hostname, port, timeoutms, false);
	}

	public TCPConnection(InetAddress hostname, int port, int timeoutms, boolean buffered)
			throws UnknownHostException, IOException
	{
		this(NetUtilities.connect(hostname, port, timeoutms));
	}

	public TCPConnection(Socket s) throws IOException
	{
		this(s, false);
	}

	public TCPConnection(Socket s, boolean buffered) throws IOException
	{
		// super(new ObjectInputStream2(s.getInputStream()), new
		// ObjectOutputStream2(s.getOutputStream()));
		super(new ObjectInputStream2(s.getInputStream()),
				createOutputStream(s, buffered));
		this.socket = s;
		this.bufferred = buffered;

		// s.setPerformancePreferences(0, 100, 1);
		// s.setSendBufferSize(10);
		// s.setTcpNoDelay(false);
	}

	private static ObjectOutputStream2 createOutputStream(Socket s, boolean buffered)
			throws IOException
	{
		OutputStream os = s.getOutputStream();

		if (buffered)
		{
			os = new BufferedOutputStream(os);
		}

		return new ObjectOutputStream2(os);
	}

	public boolean isBufferred()
	{
		return bufferred;
	}

	public Socket getSocket()
	{
		return socket;
	}

	@Override
	public void close()
	{
		super.close();

		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static TCPConnection create2(InetAddress inetAddress, int port, int timeoutms)
	{
		try
		{
			return new TCPConnection(inetAddress, port, timeoutms);
		}
		catch (IOException e)
		{

			throw new IORuntimeException(e);
		}
	}
}
