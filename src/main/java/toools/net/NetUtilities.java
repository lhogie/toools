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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
import toools.collections.Collections;
import toools.collections.relation.HashRelation;
import toools.collections.relation.Relation;
import toools.exceptions.CodeShouldNotHaveBeenReachedException;
import toools.io.Utilities;
import toools.math.MathsUtilities;
import toools.thread.IndependantObjectMultiThreadProcessing;
import toools.thread.Threads;

public class NetUtilities {
	public static final int TCP_FRAME_LENGTH = 60000;

	public static void notifyUsageForLucSoftware(String softwareName) {
		notifyUsage("http://www.i3s.unice.fr/~hogie/software/register_use.php",
				softwareName);
	}

	public static void notifyUsage(final String webServiceURL, String softwareName) {
		new Thread() {
			@Override
			public void run() {
				try {
					Map<String, String> parms = new HashMap<String, String>();
					parms.put("who", System.getProperty("user.name") + "@"
							+ NetUtilities.getIPAddress().getHostName());
					parms.put("name", softwareName);
					parms.put("os", System.getProperty("os.name") + " "
							+ System.getProperty("os.version"));
					parms.put("java", System.getProperty("java.version"));
					NetUtilities.retrieveURLContent(webServiceURL, parms, null);
				}
				catch (Throwable e) {
					// e.printStackTrace();
				}
			}
		}.start();
	}

	public static byte[] retrieveURLContent(URL url) throws IOException {
		InputStream is = url.openConnection().getInputStream();
		byte[] bytes = toools.io.Utilities.readUntilEOF(is);
		is.close();
		return bytes;
	}

	public List<InetAddress> getInetAddresses(List<String> hostnames)
			throws UnknownHostException {
		List<InetAddress> l = new ArrayList<InetAddress>();

		for (String s : hostnames) {
			l.add(InetAddress.getByName(s));
		}

		return l;
	}

	public static final boolean isLocalhost(InetAddress ip) {
		try {
			return ip.isLoopbackAddress()
					|| NetworkInterface.getByInetAddress(ip) != null;
		}
		catch (SocketException e) {
			return false;
		}
	}

	public static String retrieveURLContentAsString(String url) throws IOException {
		return new String(retrieveURLContent(new URL(url)));
	}

	public static byte[] retrieveURLContent(String url) throws IOException {
		return retrieveURLContent(new URL(url));
	}

	public static byte[] retrieveURLContent(String cgiAddress, byte[] postData)
			throws IOException {
		return retrieveURLContent(cgiAddress, new HashMap<String, String>(), postData);
	}

	public static byte[] retrieveURLContent(String cgiAddress, Map<String, String> parms,
			byte[] postData) throws IOException {
		String rq = "";

		if ( ! parms.isEmpty()) {
			for (String key : parms.keySet()) {
				String value = parms.get(key);
				rq += (rq.isEmpty() ? "?" : "&") + URLEncoder.encode(key, "UTF-8") + "="
						+ URLEncoder.encode(value, "UTF-8");
			}
		}

		URL url = new URL(cgiAddress + rq);
		java.net.URLConnection conn = url.openConnection();

		if (postData != null) {
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(postData);
			os.flush();
		}

		// System.out.println(conn.getHeaderFields());
		InputStream is = conn.getInputStream();
		byte[] response = Utilities.readUntilEOF(is);
		is.close();
		return response;
	}

	public static String determineLocalHostName() {
		List<String> names = new ArrayList<String>();

		try {
			names.add(InetAddress.getLocalHost().getCanonicalHostName());
		}
		catch (UnknownHostException e) {
		}

		try {
			names.add(InetAddress.getLocalHost().getHostName());
		}
		catch (UnknownHostException e) {
		}

		try {
			names.add(InetAddress.getLocalHost().getHostAddress());
		}
		catch (UnknownHostException e) {
		}

		for (String name : names) {
			if (name != null) {
				return name;
			}
		}

		throw new CodeShouldNotHaveBeenReachedException();
	}

	public static byte[] retrieveLucHogieData(String name) {
		try {
			return retrieveURLContent(new URL("http://luc.hogie.fr/" + name));
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Socket connect(InetAddress ip, int port, int timeoutms)
			throws IOException {
		Socket s = new Socket();

		try {
			s.connect(new InetSocketAddress(ip, port), timeoutms);

		}
		catch (SocketTimeoutException e) {
			SocketTimeoutException c = new SocketTimeoutException(
					e.getMessage() + " to host " + ip.getHostName() + " on port " + port);
			c.setStackTrace(e.getStackTrace());
			throw c;
		}
		catch (ConnectException e) {
			ConnectException c = new ConnectException(
					e.getMessage() + " to host " + ip.getHostName() + " on port " + port);
			c.setStackTrace(e.getStackTrace());
			throw c;
		}
		return s;
	}

	public static boolean isServerRunningOnPort(InetAddress host, int port, int timeoutms,
			ConnectionCloser closer) {
		try {
			Socket s = connect(host, port, timeoutms);

			if (closer != null) {
				try {
					closer.closeCleanly(new TCPConnection(s));
				}
				catch (Throwable t) {
					throw new IllegalStateException(t);
				}
			}

			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	public static boolean waitUntilServerStarts(InetAddress host, int port,
			int testTimeoutMs, int globalTimeoutMs, ConnectionCloser closer) {
		long peremptionDate = System.currentTimeMillis() + globalTimeoutMs;

		if (isServerRunningOnPort(host, port, testTimeoutMs, closer)) {
			return true;
		}
		else {
			while (System.currentTimeMillis() < peremptionDate) {
				Threads.sleepMs(100);

				if (isServerRunningOnPort(host, port, testTimeoutMs, closer)) {
					return true;
				}
			}

			return false;
		}
	}

	public static boolean isLocalServerRunningOnPort(int port, int timeoutms,
			ConnectionCloser closer) {
		try {
			return isServerRunningOnPort(InetAddress.getLocalHost(), port, timeoutms,
					closer);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
	}

	// public static boolean isLocalSMTPServerRunning()
	// {
	// return isLocalServerRunningOnPort(25, 1000, null);
	// }
	//
	// public static void sendEmailUsingLocalSMTPServer(String sender, String
	// recipient, String subject, String content) throws MessagingException
	// {
	// System.setProperty("mail.smtp.starttls.enable", "true");
	// System.setProperty("mail.smtp.host", "localhost");
	// Session session = Session.getDefaultInstance(System.getProperties(),
	// null);
	// MimeMessage message = new MimeMessage(session);
	// message.setFrom(new
	// InternetAddress(System.getProperties().getProperty("user.name")));
	// message.addRecipient(Message.RecipientType.TO, new
	// InternetAddress(recipient));
	// message.setSubject(subject);
	// message.setText(content);
	// Transport.send(message);
	// }
	//
	// public static void sendEmail(String sender, String recipient, String
	// subject, String content, String smtpServer, String username, String
	// passwd)
	// throws MessagingException
	// {
	// System.setProperty("mail.smtp.starttls.enable", "true");
	// Session session = Session.getDefaultInstance(System.getProperties(),
	// null);
	// MimeMessage message = new MimeMessage(session);
	// message.setFrom(new InternetAddress(sender));
	// message.addRecipient(Message.RecipientType.TO, new
	// InternetAddress(recipient));
	// message.setSubject(subject);
	// message.setText(content);
	// Transport tr = session.getTransport("smtp");
	// tr.connect(smtpServer, username, passwd);
	// message.saveChanges(); // don't forget this
	// tr.sendMessage(message, message.getAllRecipients());
	// tr.close();
	//
	// }

	public static Collection<Socket> getWorkingServersInLAN(int port, int timeout) {
		return getWorkingServersInLANOf(port, getIPAddress(), timeout);
	}

	public static Collection<Socket> getWorkingServersInLANOf(int port,
			InetAddress localAddress, int timeout) {
		return getWorkingServers(port, getAllIPAddressesInLAN(localAddress), timeout);
	}

	public static Collection<Socket> getWorkingServers(final int port,
			Collection<InetAddress> candidateServers, final int timeoutms) {
		final Collection<Socket> servers = new ArrayList<Socket>();
		@SuppressWarnings("unused")
		final List<InetAddress> l = new ArrayList<InetAddress>(candidateServers);

		new IndependantObjectMultiThreadProcessing<InetAddress>(candidateServers) {
			@Override
			protected void process(InetAddress canditate) {
				try {
					servers.add(connect(canditate, port, timeoutms));
				}
				catch (IOException e) {
				}
			}
		};

		return servers;
	}

	public static void closeAll(Collection<Socket> sockets) {
		for (Socket s : sockets) {
			try {
				s.close();
			}
			catch (IOException t) {
			}
		}
	}

	public static NetworkInterface getNetworkInterfaceByDisplayName(String name) {
		for (NetworkInterface ni : getNICs().keySet()) {
			if (ni.getDisplayName().equals(name)) {
				return ni;
			}
		}

		return null;
	}

	private static Relation<NetworkInterface, InetAddress> networkInterfaces;

	public static Relation<NetworkInterface, InetAddress> getNICs() {
		if (networkInterfaces == null) {
			networkInterfaces = new HashRelation<NetworkInterface, InetAddress>();

			try {
				for (NetworkInterface netInterface : Collections.convertEnumerationToList(
						NetworkInterface.getNetworkInterfaces())) {
					networkInterfaces.addAll(netInterface, Collections
							.convertEnumerationToList(netInterface.getInetAddresses()));
				}
			}
			catch (SocketException e) {
				throw new IllegalStateException(e);
			}
		}

		return networkInterfaces;
	}

	public static Collection<InetAddress> getAllIPAddressesInLAN(byte[] bytes) {
		Collection<InetAddress> addresses = new ArrayList<InetAddress>();

		for (int i = 1; i < 255; ++i) {
			bytes[3] = (byte) i;

			try {
				addresses.add(InetAddress.getByAddress(bytes));
			}
			catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		return addresses;
	}

	public static Collection<InetAddress> getAllIPAddressesInLAN(String prefix) {
		byte[] b = new byte[4];
		String[] strings = prefix.split("\\.");

		for (int i = 0; i < 3; ++i) {
			b[i] = (byte) (int) Integer.valueOf(strings[i]);
		}

		return getAllIPAddressesInLAN(b);
	}

	public static Collection<InetAddress> getAllIPAddressesInLAN(InetAddress ip) {
		if (ip.isLoopbackAddress())
			throw new IllegalArgumentException(ip + " is a loopback address");

		if (ip.getAddress().length != 4)
			throw new IllegalArgumentException(ip + " is not an IPv4 address");

		return getAllIPAddressesInLAN(ip.getAddress());
	}

	public static Collection<? extends InetAddress> getAllIPAddressesInLAN() {
		return getAllIPAddressesInLAN(getIPAddress());
	}

	public static boolean isURL(String sourceFileName) {
		try {
			new URL(sourceFileName);
			return true;
		}
		catch (MalformedURLException e) {
			return false;
		}
	}

	public static Set<NetworkInterface> getHardwareNetworkInterfaces() {
		Set<NetworkInterface> r = new HashSet<>();

		for (NetworkInterface nic : getNICs().keySet()) {
			if (isHardware(nic)) {
				r.add(nic);
			}
		}

		return r;
	}

	public static Set<InetAddress> getHardwareIPv4Addresses() {
		Set<InetAddress> r = new HashSet<>();
		Relation<NetworkInterface, InetAddress> nic_ips = getNICs();

		for (NetworkInterface nic : nic_ips.keySet()) {
			if (isHardware(nic)) {
				for (InetAddress ip : nic_ips.getValues(nic)) {
					if (ip instanceof Inet4Address) {
						r.add(ip);
					}
				}
			}
		}

		return r;
	}

	public static boolean isHardware(NetworkInterface ni) {
		try {
			return ni.getHardwareAddress() != null;
		}
		catch (SocketException e) {
			throw new IllegalStateException(e);
		}
	}

	public static InetAddress getVPNAddress() {
		Relation<NetworkInterface, InetAddress> nic_ips = getNICs();

		for (NetworkInterface ni : nic_ips.keySet()) {
			if ( ! isHardware(ni)) {
				for (InetAddress a : nic_ips.getValues(ni)) {
					if (a instanceof Inet4Address && ! a.isLoopbackAddress()) {
						return a;
					}
				}
			}
		}

		return null;
	}

	public static InetAddress getIPAddress() {
		// try
		// {
		// return InetAddress.getByName(new
		// String(Proces.exec("hostname")).trim());
		// }
		// catch (UnknownHostException e)
		// {
		// throw new IllegalStateException(e);
		// }

		return getHardwareIPv4Addresses().iterator().next();
	}

	public static void main(String[] args) {
		System.out.println(getIPAddress());
	}

	public static ServerSocket findFreePort(int basePort, int maxTries)
			throws IOException {
		for (int port = basePort; port < basePort + maxTries; ++port) {
			try {
				return new ServerSocket(port);
			}
			catch (IOException e) {
			}
		}

		throw new IOException("no free port found");
	}

	public static int findAvailablePort(int timeoutMs) {
		int start = 49152;
		int end = 65535;
		int maxNbAttempt = 1000;

		for (int attempt = 0; attempt < maxNbAttempt; ++attempt) {
			int p = ThreadLocalRandom.current().nextInt(end - start) + start;

			if ( ! isLocalServerRunningOnPort(p, timeoutMs, null)) {
				return p;
			}
		}

		throw new IllegalStateException();
	}

	public static int randomUserPort() {
		return MathsUtilities.pickRandomBetween(49152, 65535, new Random());
	}

}
