/////////////////////////////////////////////////////////////////////////////////////////
// 
//                 Université de Nice Sophia-Antipolis  (UNS) - 
//                 Centre National de la Recherche Scientifique (CNRS)
//                 Copyright © 2015 UNS, CNRS All Rights Reserved.
// 
//     These computer program listings and specifications, herein, are
//     the property of Université de Nice Sophia-Antipolis and CNRS
//     shall not be reproduced or copied or used in whole or in part as
//     the basis for manufacture or sale of items without written permission.
//     For a license agreement, please contact:
//     <mailto: licensing@sattse.com> 
//
//
//
//     Author: Luc Hogie – Laboratoire I3S - luc.hogie@unice.fr
//
//////////////////////////////////////////////////////////////////////////////////////////

package toools.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import toools.extern.Proces;
import toools.io.Cout;
import toools.text.TextUtilities;

public class SSHUtils
{
	public static void createSSHTunnelTo(String remoteHost, int remotePort,
			String username, int localPort, int timeoutInSecond)
	{
		if (NetUtilities.isLocalServerRunningOnPort(localPort, 1000, null))
			throw new IllegalArgumentException(
					"local port " + localPort + " is already in use");
		
		List<String> args = getSSHOptions(timeoutInSecond);
		args.add(username + "@" + remoteHost);
		args.add("-L" + localPort + ":localhost:" + remotePort);
		new Thread(() -> Proces.exec(sshCmd(), args.toArray(new String[0]))).start();

		try
		{
			Cout.debug("waiting for server");
			NetUtilities.waitUntilServerStarts(InetAddress.getLocalHost(), localPort,
					1000, 1000, null);
			Cout.debug("OK");
		}
		catch (UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
	}
/*
	public static TunnelConnection createSSHTunnelTo_JSCH(String remoteHost,
			int remotePort, String remoteUsername, int localPort, int timeoutInSecond)
			throws JSchException
	{
		if (NetUtilities.isLocalServerRunningOnPort(localPort, 1000, null))
			throw new IllegalArgumentException(
					"local port " + localPort + " is already in use");

		DefaultSessionFactory sessionFactory = new DefaultSessionFactory();
		sessionFactory.setUsername(remoteUsername);
		TunnelConnection tunnelConnection = new TunnelConnection(sessionFactory,
				new Tunnel(localPort, remoteHost, remotePort));
		tunnelConnection.open();
		return tunnelConnection;
	}

*/
	public static String sshCmd()
	{
		return "ssh";
	}

	public static List<String> getSSHOptions(int timeoutInSecond)
	{
		List<String> options = new ArrayList<>();
		options.add("-o");
		options.add("ForwardX11=no");
		options.add("-o");
		options.add("StrictHostKeyChecking=no");
		options.add("-o");
		options.add("BatchMode=yes");
		options.add("-o");
		options.add("ConnectTimeout=" + timeoutInSecond);
		return options;
	}

	public static List<String> execSh(int timeoutInSecond, String node, String shText)
	{
		List<String> args = getSSHOptions(timeoutInSecond);
		args.add(node);
		args.add("bash");
		args.add("--posix");
		byte[] r = Proces.exec(sshCmd(), shText.getBytes(), args.toArray(new String[0]));
		return r.length == 0 ? new ArrayList<String>()
				: TextUtilities.splitInLines(new String(r));
	}

	public static void createSSHTunnelTo(InetAddress remoteHost, int remotePort,
			String username, int localPort, int timeoutInSecond)
	{
		List<String> args = getSSHOptions(timeoutInSecond);
		args.add(username + "@" + remoteHost.getHostName());
		args.add("-L" + localPort + ":localhost:" + remotePort);
		new Thread(() -> Proces.exec(sshCmd(), args.toArray(new String[0]))).start();
	}

	public static Process exec(int timeoutInSecond, String node, String... cmd)
			throws IOException
	{
		List<String> args = getSSHOptions(timeoutInSecond);
		args.add(0, sshCmd());
		args.add(node);

		for (String a : cmd)
		{
			args.add(a);
		}

		return Runtime.getRuntime().exec(args.toArray(new String[0]));
	}
}
