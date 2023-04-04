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

package toools.io.file;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.input.CountingInputStream;

import toools.extern.Proces;
import toools.io.CSVStream;
import toools.io.Hasher;
import toools.io.IORuntimeException;
import toools.io.Utilities;
import toools.reflect.Clazz;
import toools.text.TextUtilities;
import toools.thread.Generator;
import toools.thread.Threads;

public class RegularFile extends AbstractFile {
	public static final Map<String, Class> extension_class = new HashMap<>();

	public static RegularFile instanciate(String path) {
		String type = FileNameDecomposition.getExtension(path);
		Class c = extension_class.get(type);

		try {
			return (RegularFile) Clazz.makeInstance(c.getConstructor(String.class), path);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException(e);
		}
	}

	public RegularFile(String path) {
		super(path);
	}

	public RegularFile(Directory parent, String name) {
		this(parent.getPath() + '/' + name);
	}

	public static RegularFile createTempFile() {
		return createTempFile("toools-", ".tmp");
	}

	public static RegularFile createTempFile(String prefix, String suffix) {
		try {
			return createTempFile(Directory.getSystemTempDirectory(), prefix, suffix);
		} catch (IOException e) {
			throw new IllegalStateException(
					"cannot create file in temp directory: " + Directory.getSystemTempDirectory());
		}
	}

	public static RegularFile createTempFile(Directory location, String prefix, String suffix) throws IOException {
		RegularFile f = new RegularFile(location, AbstractFile.findUnusedNameIn(location, prefix, suffix));
		f.setTemporary(true);
		f.create();
		return f;
	}

	public void setContent(byte[] bytes) {
		try {
			OutputStream bos = createWritingStream();
			bos.write(bytes);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public void setContent(String text, Charset charset) {
		setContent(text.getBytes(charset));
	}

	public void setContent(String text, String charset) {
		try {
			setContent(text.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void setContentAsUTF8(String text) {
		setContent(text, "UTF-8");
	}

	public void setContentAsUTF16(String text) {
		setContent(text, "UTF-16");
	}

	public void setContentAsASCII(String text) {
		setContent(text, "US-ASCII");
	}

	public void setContentAsMacRoman(String text) {
		setContent(text, "MacRoman");
	}

	public byte[] getContent() {
		InputStream bis = createReadingStream();
		byte[] bytes = Utilities.readUntilEOF(bis);
		try {
			bis.close();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		return bytes;
	}

	public Generator<Object> getContentAsJavaObjects() {
		return new Generator<Object>() {

			@Override
			public void produce() {
				try {
					ObjectInputStream in = new ObjectInputStream(createReadingStream());
					Object o = in.readObject();

					if (o == null)
						return;

					deliver(o);
					in.close();
				} catch (IOException | ClassNotFoundException e) {
					throw new IORuntimeException(e);
				}
			}
		};
	}

	public Object getContentAsJavaObject() {
		return getContentAsJavaObject(count -> {
		});
	}

	public <A> void alterContent(Consumer<A> f) {
		A a = (A) getContentAsJavaObject();
		f.accept(a);
		setContentAsJavaObject(a);
	}

	public Object getContentAsJavaObject(Consumer<Long> c) {
		try {
			CountingInputStream cin = new CountingInputStream(createReadingStream());
			ObjectInputStream in = new ObjectInputStream(cin);
			AtomicBoolean run = new AtomicBoolean(true);
			Threads.newThread_loop_periodic(1000, () -> run.get(), () -> c.accept(cin.getByteCount()));
			Object o = in.readObject();
			in.close();
			run.set(false);
			return o;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, String> getContentAsJavaProperties() {
		try {
			Reader r = createReader();
			Properties p = new Properties();
			p.load(r);
			r.close();

			Map<String, String> m = new HashMap<>();

			for (Entry<Object, Object> e : p.entrySet()) {
				m.put(e.getKey().toString(), e.getValue().toString());
			}

			return m;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setContentAsJavaProperties(Map<String, String> m) {
		try {
			Properties p = new Properties();

			for (Entry<String, String> e : m.entrySet()) {
				p.put(e.getKey(), e.getValue());
			}

			OutputStream w = createWritingStream();
			p.store(w, "");
			w.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void copyTo(RegularFile destination, boolean overwrite) throws FileNotFoundException, IOException {
		if (!exists())
			throw new IllegalStateException("cannot copy a non-existing file " + this.getPath());

		if (!overwrite && destination.exists())
			throw new IllegalStateException("don't want to overwrite " + destination.getPath());

		InputStream is = createReadingStream();
		OutputStream os = destination.createWritingStream();
		Utilities.copy(is, os);
		is.close();
		os.close();
	}

	public void moveTo(Directory destDir, boolean overwrite) {
		moveTo(destDir.getPath() + "/" + getName(), overwrite);
	}

	public synchronized void moveTo(String path, boolean overwrite) {
		if (!exists())
			throw new IllegalStateException("cannot move a non-existing file " + this.getPath());

		File dest = new File(path);

		if (dest.exists()) {
			if (!overwrite)
				throw new IllegalStateException("dest file already exists: " + this.getPath());

			dest.delete();
		}

		if (!javaFile.renameTo(dest))
			throw new IORuntimeException("can't rename file " + javaFile + " to " + path);

		javaFile = new File(path).getAbsoluteFile();
	}

	@Override
	public void delete() {
		// PosixFileAttributes attrs = Files.readAttributes(path,
		// PosixFileAttributes.class, NOFOLLOW_LINKS);
		// if (!exists())
		// throw new IllegalStateException("cannot delete a non-existing file "
		// + this.getPath());

		if (!javaFile.delete())
			throw new IllegalStateException("fail to delete file " + javaFile.getAbsolutePath());
	}

	@Override
	public long getSize() {
		if (!exists())
			throw new IllegalStateException("cannot get the size of a non-existing file " + this.getPath());

		return javaFile.length();
	}

	public OutputStream createWritingStream() {
		return createWritingStream(false, 65536 * 256);
	}

	public OutputStream createWritingStream(boolean append) {
		return createWritingStream(append, 65536 * 256);
	}

	public OutputStream createWritingStream(boolean append, int bufSize) {
		try {
			if (bufSize > 0) {
				return new BufferedOutputStream(new FileOutputStream(javaFile, append), bufSize);
			} else {
				return new FileOutputStream(javaFile, append);
			}
		} catch (FileNotFoundException e) {
			throw new IORuntimeException(e);
		}
	}

	public InputStream createReadingStream() {
		return createReadingStream(16 * 1024 * 1024);
	}

	public InputStream createReadingStream(int bufSize) {
		try {
			InputStream is = new FileInputStream(javaFile);

			String ext = getExtension();

			if (ext != null && ext.equalsIgnoreCase("GZ")) {
				is = new GZIPInputStream(is);
			}

			if (bufSize > 0) {
				is = new BufferedInputStream(is, bufSize);
			}

			return is;
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public Reader createReader() {
		return new InputStreamReader(createReadingStream());
	}

	public BufferedReader createLineReadingStream() {
		if (!exists())
			throw new IORuntimeException("cannot read a non-existing file " + this.getPath());

		try {
			return new BufferedReader(new FileReader(javaFile), 1024 * 1024);
		} catch (FileNotFoundException e) {
			throw new IORuntimeException(e);
		}
	}

	public static boolean sameContents(RegularFile a, RegularFile b) {
		if (a.getSize() != b.getSize()) {
			return false;
		} else {
			return compareFileContentsLexicographically(a, b) == 0;
		}
	}

	public static int compareFileContentsLexicographically(RegularFile a, RegularFile b) {
		if (a.getSize() == 0 && b.getSize() == 0) {
			return 0;
		} else if (a.getSize() == 0) {
			return -1;
		} else if (b.getSize() == 0) {
			return 1;
		} else {
			return TextUtilities.compareLexicographically(a.createReadingStream(), b.createReadingStream());
		}
	}

	public String getNameWithoutExtension() {
		String extension = getExtension();

		if (extension == null) {
			return getName();
		} else {
			return getName().substring(0, getName().length() - extension.length() - 1);
		}
	}

	@Override
	public void create() {
		try {
			boolean ok = javaFile.createNewFile();

			if (!ok)
				throw new IORuntimeException("can't create file " + this);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public void create(boolean createParentIfNeeded) {
		if (!getParent().exists() && createParentIfNeeded) {
			getParent().mkdirs();
		}

		create();
	}

	public List<String> getLines() {
		return TextUtilities.splitInLines(new String(getContent()));
	}

	public boolean isArchive() {
		Collection<String> extensions = Arrays.asList("tgz", "tar.gz", "zip");
		return extensions.contains(getExtension());
	}

	public FileNameDecomposition getNameDecomposition() {
		return new FileNameDecomposition(getName());
	}

	public List<String> getExtensions() {
		return getNameDecomposition().extensions;
	}

	public String getExtension() {
		List<String> extensions = getNameDecomposition().extensions;

		if (extensions.isEmpty()) {
			return null;
		} else {
			return extensions.get(extensions.size() - 1);
		}
	}

	public void setExtension(String newExtension) throws IOException {
		String ext = getExtension();
		String newName = getPath().replaceFirst(ext + "$", newExtension);
		renameTo(newName);
	}

	public void append(byte[] bytes) {
		OutputStream os = createWritingStream(true);

		try {
			os.write(bytes);
			os.close();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	@Override
	public void rsyncTo(String remotePath) {
		Proces.exec("rsync", getPath(), remotePath);
	}

	public static long sumSize(Iterable<RegularFile> files) {
		long size = 0;

		for (RegularFile f : files) {
			size += f.getSize();
		}

		return size;
	}

	public RegularFile getRegularFileInSameDirectory(String name) {
		return getParent().getChildRegularFile(name);
	}

	public RegularFile getSibbling(String extension) {
		return getRegularFileInSameDirectory(getNameWithoutExtension() + extension);
	}

	public PrintStream createPrintStream() {
		return new PrintStream(createWritingStream());
	}

	public CSVStream createCSVStream(Object... format) {
		CSVStream s = new CSVStream(createWritingStream());
		s.println(format);
		return s;
	}

	public String getContentAsText() {
		return new String(getContent());
	}

	public int hash() throws IOException {
		int nbValues = getSize() < 1000 ? (int) getSize() : (int) Math.sqrt(getSize());
		return hash(nbValues);
	}

	public Integer hash(int nbValues) throws IOException {
		long len = getSize();
		int[] values = new int[nbValues];
		InputStream is = createReadingStream(0);

		for (int i = 0; i < nbValues; ++i) {
			values[i] = is.read();
			is.skip(len / nbValues);
		}

		is.close();
		return Arrays.hashCode(values);
	}

	public void touch() {
		if (exists()) {
			setLastModifiedMs(System.currentTimeMillis());
		} else {
			create();
		}
	}

	public void ensureNotExists() {
		if (exists())
			throw new IllegalStateException("file exists!");
	}

	public void setContentAsJavaObject(Object o) {
		try {
			ObjectOutputStream os = new ObjectOutputStream(createWritingStream());
			os.writeObject(o);
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setLines(Collection<String> lines) {
		PrintStream out = new PrintStream(createWritingStream());
		Iterator<String> i = lines.iterator();

		while (i.hasNext()) {
			out.print(i.next());

			if (i.hasNext()) {
				out.println();
			}
		}

		out.close();
	}

	@Override
	public void hashContents(Hasher h) {
		var is = createReadingStream();

		while (true) {
			try {
				int i = is.read();

				if (i == -1)
					break;

				h.add(i);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public byte[] read(int offset, int len) {
		RandomAccessFile fs;
		try {
			fs = new RandomAccessFile(javaFile, "r");
			var b = new byte[len];
			fs.read(b, offset, len);
			return b;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
