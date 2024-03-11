package toools.security;

import java.io.Serializable;
import java.security.PublicKey;

public class SecuredData implements Serializable {
	PublicKey publicKey;
	byte[] encryptedAESKey;
	public byte[] data;
}