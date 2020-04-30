package toools.net;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public abstract class UDPStreamer extends Thread
{
	private final int port;

	public UDPStreamer(int port)
	{
		this.port = port;
	}

	@Override
	public void run()
	{
		try
		{
			DatagramSocket socket = new DatagramSocket(port);
			byte[] buf = new byte[60000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			while (true)
			{
				socket.receive(packet);
				InetAddress sender = packet.getAddress();
				PipedOutputStream pos = getPOS(sender);
				pos.write(packet.getData());
			}
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	private final Map<InetAddress, PipedOutputStream> ip_pos = new HashMap<>();

	private PipedOutputStream getPOS(InetAddress sender)
	{
		PipedOutputStream pos = ip_pos.get(sender);

		if (pos == null)
		{
			pos = new PipedOutputStream();
			ip_pos.put(sender, pos);

			try
			{
				PipedInputStream pis = new PipedInputStream(pos);
				newConnection(sender, pis);
			}
			catch (IOException e)
			{
				throw new IllegalStateException(e);
			}
		}

		return pos;
	}

	protected abstract void newConnection(InetAddress sender, PipedInputStream pis);

	public void unregister(InetAddress sender)
	{
		ip_pos.remove(sender);
	}
}
