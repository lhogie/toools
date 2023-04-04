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
import java.util.Arrays;
import java.util.List;

import toools.extern.Proces;
import toools.io.Cout;
import toools.text.TextUtilities;

public class SSHUtils {
	public static void createSSHTunnelTo(SSHParms sshp, InetAddress remoteHost,
			int remotePort, int localPort) {
		if (NetUtilities.isLocalServerRunningOnPort(localPort, sshp.timeoutS * 1000,
				null))
			throw new IllegalArgumentException(
					"local port " + localPort + " is already in use");

		List<String> args = new ArrayList<>();
		addSSHOptions(args, sshp);
		args.add(remoteHost.getHostName());
		args.add("-L" + localPort + ":localhost:" + remotePort);
		new Thread(() -> Proces.exec(sshCmd(), args.toArray(new String[0]))).start();

		try {
			Cout.debug("waiting for server");
			NetUtilities.waitUntilServerStarts(InetAddress.getLocalHost(), localPort,
					1000, 1000, null);
			Cout.debug("OK");
		}
		catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * public static TunnelConnection createSSHTunnelTo_JSCH(String remoteHost,
	 * int remotePort, String remoteUsername, int localPort, int
	 * timeoutInSecond) throws JSchException { if
	 * (NetUtilities.isLocalServerRunningOnPort(localPort, 1000, null)) throw
	 * new IllegalArgumentException( "local port " + localPort +
	 * " is already in use");
	 * 
	 * DefaultSessionFactory sessionFactory = new DefaultSessionFactory();
	 * sessionFactory.setUsername(remoteUsername); TunnelConnection
	 * tunnelConnection = new TunnelConnection(sessionFactory, new
	 * Tunnel(localPort, remoteHost, remotePort)); tunnelConnection.open();
	 * return tunnelConnection; }
	 * 
	 */
	public static String sshCmd() {
		return "ssh";
	}

	public static void addSSHOptions(List<String> cmdline, SSHParms p) {

		cmdline.add("-p");
		cmdline.add(String.valueOf(p.port));

		if (p.username != null) {
			cmdline.add("-l");
			cmdline.add(p.username);
		}

		cmdline.add("-x");
		cmdline.add("-o");
		cmdline.add("ForwardX11=no");
		cmdline.add("-o");
		cmdline.add("StrictHostKeyChecking=no");
		cmdline.add("-o");
		cmdline.add("BatchMode=yes");
		cmdline.add("-o");
		cmdline.add("ConnectTimeout=" + p.timeoutS);
	}

	public static List<String> execShAndWait(SSHParms sshparms, String shText) {
		List<String> sshOptions = new ArrayList<>();
		addSSHOptions(sshOptions, sshparms);
		sshOptions.add(sshparms.host);
		sshOptions.add("bash");
		sshOptions.add("--posix");
		byte[] r = Proces.exec(sshCmd(), shText.getBytes(),
				sshOptions.toArray(new String[0]));
		return r.length == 0 ? new ArrayList<String>()
				: TextUtilities.splitInLines(new String(r));
	}

	public static Process exec(SSHParms sshparms, String... cmd) throws IOException {
		List<String> args = new ArrayList<>();
		args.add(sshCmd());
		addSSHOptions(args, sshparms);
		args.add(sshparms.host);
		Arrays.stream(cmd).forEach(a -> args.add(a));
		// System.out.println("****" + args);
		return Runtime.getRuntime().exec(args.toArray(new String[0]));
	}
}
