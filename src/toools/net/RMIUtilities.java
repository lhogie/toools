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

import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import toools.collections.relation.HashRelation;
import toools.collections.relation.Relation;


public class RMIUtilities
{
	public static Relation<InetAddress, String> getRegistriesContent(Collection<InetAddress> ips)
	{
		Relation<InetAddress, String> r = new HashRelation<InetAddress, String>();
		
		for (InetAddress ip : ips)
		{
			try
			{
				Registry registry = LocateRegistry.getRegistry(ip.getHostAddress());
				r.addAll(ip, Arrays.asList(registry.list()));
			}
			catch (AccessException e)
			{
				e.printStackTrace();
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

		return r;
	}
	
	
	/*
	 * Assumes that the remove object was bound to its classname. E.g.   registry.bind("java.util.ArrayList", new ArrayList());
	 */
	public static <T extends Remote> Map<InetAddress, T> getRMIRemotesByClass(Class<T> clazz, Collection<InetAddress> ips)
	{
		return (Map<InetAddress, T>) getRMIRemotesByName(clazz.getName(), ips);
	}

	public static Map<InetAddress, Remote> getRMIRemotesByName(String name, Collection<InetAddress> ips)
	{
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}

		Map<InetAddress, Remote> remotes = new HashMap<InetAddress, Remote>();

		for (InetAddress ip : ips)
		{
			try
			{
				Registry registry = LocateRegistry.getRegistry(ip.getHostAddress());
				remotes.put(ip, registry.lookup(name));
			}
			catch (NotBoundException ex)
			{
				ex.printStackTrace();
			}
			catch (AccessException e)
			{
				e.printStackTrace();
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

		return remotes;
	}

	public final static int DEFAULT_RMI_PORT = 1099;
	/*
	public static Collection<InetAddress> getRMIServersInLANs()
	{
		Collection<InetAddress> ips = new ArrayList<InetAddress>();

		for (InetAddress ip : NetUtilities.getIPAddresses().getInverseRelation().getKeys())
		{
			if (!ip.isLoopbackAddress() && ip.getAddress().length == 4)
			{
				ips.addAll(NetUtilities.getWorkingServersInLANOf(DEFAULT_RMI_PORT, ip, 1000));
			}
		}

		return ips;
	}*/
}
