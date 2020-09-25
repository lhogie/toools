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
 
 package toools.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class UDPServerSocket
{
	private final int port;
	private final byte[] buf = new byte[60000];
	private final DatagramSocket socket;
	Map<String, UDPSocket> map = new HashMap();

	public UDPServerSocket(int port) throws SocketException
	{
		this.port = port;
		this.socket = new DatagramSocket(port);
	}

	public UDPSocket accept() throws IOException
	{
		while (true)
		{
			DatagramPacket p = new DatagramPacket(buf, buf.length);
			socket.receive(p);

			String clientID = p.getAddress().getHostName() + "-" + p.getPort();
			UDPSocket clientSocket = map.get(clientID);

			// this is a new client
			if (clientSocket == null)
			{
				clientSocket = new UDPSocket( p.getAddress(), p.getPort());
				((UDPInputStream) clientSocket.getInputStream()).pos.write(buf);
				map.put(clientID, clientSocket);
				return clientSocket;
			}
			// this is a message for an already connected client
			else
			{
				((UDPInputStream) clientSocket.getInputStream()).pos.write(buf);
			}
		}
	}

	public int getPort()
	{
		return port;
	}

}
