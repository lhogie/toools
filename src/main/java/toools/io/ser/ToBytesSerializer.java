package toools.io.ser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toools.util.Conversion;

public class ToBytesSerializer<E> extends Serializer<E> {

	@Override
	public E read(InputStream is) throws IOException {
		return (E) is.readAllBytes();
	}

	@Override
	public void write(E o, OutputStream os) throws IOException {
		os.write((byte[]) Conversion.convert(o, byte[].class));
	}

	@Override
	public String getMIMEType() {
		return "raw data";
	}
	@Override
	public boolean isBinary() {
		return true;
	}

}
