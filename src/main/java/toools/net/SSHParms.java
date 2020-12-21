package toools.net;

import java.io.Serializable;

public class SSHParms implements Serializable {
	// SSH hostname may be different than DNS name
	public String hostname;
	public String username;// = System.getProperty("user.name");
	public int port = 22;
	public int timeoutS = 10;

	public static SSHParms fromSSHString(String s) {
		int indexOfAt = s.indexOf('@');
		SSHParms p = new SSHParms();

		if (indexOfAt == -1) {
			p.hostname = s;
		} else {
			p.username = s.substring(0, indexOfAt);
			p.hostname = s.substring(indexOfAt + 1);
		}

		return p;
	}

	@Override
	public String toString() {
		String s = "";

		if (username != null) {
			s += username + "@";
		}

		if (hostname != null) {
			s += hostname;
		}

		if (port != 22) {
			s += ":" + port;
		}

		return s;
	}
}