package toools.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;

import toools.SizeOf;
import toools.io.ser.Serializer;

public class SecureSerializer extends Serializer implements SizeOf {

	public final RSA rsa = new RSA();
	final Serializer ser;

	public SecureSerializer(PublicKey pk, Serializer ser) {
		rsa.keyPair = new KeyPair(pk, null);
		this.ser = ser;
	}

	public SecureSerializer(Serializer ser, boolean enableEncryption) {
		rsa.random(enableEncryption);
		this.ser = ser;
	}

	@Override
	public Object read(InputStream is) throws IOException {
		var o = ser.read(is);

		if (o instanceof SecuredData) {
			SecuredData e = (SecuredData) o;

			if (e.encryptedAESKey == null) {
				return ser.fromBytes(e.data);
			} else {
				var plainAESKey = rsa.decode(e.publicKey, e.encryptedAESKey);
				Key aesKey = (Key) ser.fromBytes(plainAESKey);
				var plainMsg = AES.decode(e.data, aesKey);
				return ser.fromBytes(plainMsg);
			}
		}
		else {
			return o;
		}
	}

	@Override
	public void write(Object o, OutputStream os) throws IOException {
		SecuredData e = new SecuredData();
		e.publicKey = rsa.keyPair.getPublic();

		if (rsa.keyPair.getPrivate() == null) {
			e.data = ser.toBytes(o);
		} else {
			var aesKey = AES.getRandomKey(128);
			e.encryptedAESKey = rsa.encode(ser.toBytes(aesKey));
			e.data = AES.encode(ser.toBytes(o), aesKey);
		}

		ser.write(e, os);
	}

	@Override
	public String getMIMEType() {
		return "idawi";
	}

	@Override
	public boolean isBinary() {
		return true;
	}

	public PublicKey publicKey() {
		return rsa != null && rsa.keyPair != null ? rsa.keyPair.getPublic() : null;
	}

	@Override
	public long sizeOf() {
		return rsa.keyPair.getPublic().getEncoded().length + rsa.keyPair.getPrivate().getEncoded().length;
	}

}