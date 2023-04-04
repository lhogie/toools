package fr.cnrs.i3s;

import java.util.function.Supplier;

import toools.io.Cout;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.io.ser.Serializer;
import toools.util.Date;

public class Cache<T> {
	private T value;
	private final T invalidValue;
	private final RegularFile file;
	private final Serializer<T> serializer;
	private final Supplier<T> supplier;
	public int nbThreads;
	private double duration;
	private double lastComputation;

	public Cache(double duration, Supplier<T> s) {
		this(null, duration, null, null, s);
	}

	public Cache(T invalidValue, double duration, String name, Directory d, Supplier<T> s) {
		this(invalidValue, duration, name, d, Serializer.getDefaultSerializer(), s);
	}

	public Cache(T invalidValue, double duration, String name, Directory d, Serializer<T> serializer,
			Supplier<T> supplier) {
		this.duration = duration;
		this.invalidValue = invalidValue;
		this.serializer = serializer == null ? Serializer.getDefaultSerializer() : serializer;
		this.supplier = supplier;

		if (d == null) {
			this.file = null;
			this.value = invalidValue;
		} else {
			this.file = new RegularFile(d, name + "." + serializer.getMIMEType());

			if (file.exists()) {
				byte[] fileContent = file.getContent();

				try {
					this.value = deserialize(fileContent);
				} catch (Exception e) {
					this.value = invalidValue;
					Cout.warning("file " + file + " cannot be read, it's value will have to be recomputed");
				}
			} else {
				this.value = invalidValue;
			}
		}
	}

	protected T deserialize(byte[] bytes) {
		return (T) serializer.fromBytes(bytes);
	}

	protected byte[] serialize(T e) {
		return serializer.toBytes(e);
	}

	public T get() {
		if (!isValid()) {
			set(supplier.get());
		}

		return value;
	}

	public T set(T t) {
		if (file != null) {
			byte[] bytes = serialize(t);

			try {
				file.getParent().ensureExists();
				file.setContent(bytes);
			} catch (Throwable e) {
				Cout.warning(e.getMessage());
			}
		}

		this.value = t;
		this.lastComputation = Date.time();
		return t;
	}

	public boolean isValid() {
		double age = Date.time() - lastComputation;
		boolean outOfDate = age > duration;

		if (outOfDate) {
			return false;
		} else if (invalidValue == null) {
			return value != null;
		} else {
			return !invalidValue.equals(value);
		}
	}

	public void invalidate() {
		set(invalidValue);
	}
}
