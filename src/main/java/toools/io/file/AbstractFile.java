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
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import toools.io.Hasher;

@SuppressWarnings("serial")
public abstract class AbstractFile implements Serializable {

	public File javaFile;
	private boolean temporary = false;

	public static String findUnusedNameIn(Directory location, String prefix, String suffix) {
		for (int i = 0;; ++i) {
			String filename = prefix + i + suffix;
			RegularFile f = new RegularFile(location, filename);

			if (!f.exists()) {
				return filename;
			}
		}
	}

	public AbstractFile(String path) {
		if (path.trim().isEmpty())
			throw new IllegalArgumentException("empty file name");

		if (path.startsWith("~/")) {
			path = Directory.getHomeDirectory().getPath() + '/' + path.substring(2);
		} else if (path.startsWith("$HOME/")) {
			path = Directory.getHomeDirectory().getPath() + '/' + path.substring(6);
		}

		javaFile = new File(path).getAbsoluteFile();
	}

	public boolean isChildOf(Directory d) {
		return getPath().startsWith(d.getPath());
	}

	public void createLink(AbstractFile f) throws IOException {
		Files.createSymbolicLink(f.javaFile.toPath(), javaFile.toPath());
	}

	public boolean isTemporary() {
		return temporary;
	}

	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}

	public boolean isNewerThan(AbstractFile f) {
		return getLastModificationDateMs() > f.getLastModificationDateMs();
	}

	public static <F extends AbstractFile> F map(String path, Class<F> defaultClass) {
		if (path.trim().isEmpty())
			throw new IllegalArgumentException("empty file name");

		return map(new File(path).getAbsoluteFile(), defaultClass);
	}

	public static <F extends AbstractFile> F map(File file, Class<F> defaultClass) {
		if (file.exists()) {
			if (file.isDirectory()) {
				return (F) new Directory(file.getAbsolutePath());
			} else {
				return (F) new RegularFile(file.getAbsolutePath());
			}
		} else {
			// we may be in the situation of a symbolic link targeting a
			// non-existing file, so let's suppose this non-existing file was a
			// regular file
			if (defaultClass == null) {
				return (F) new RegularFile(file.getAbsolutePath());
				// throw new
				// IllegalArgumentException("file was expected to be found: " +
				// file.getAbsolutePath());
			} else {
				try {
					return defaultClass.getConstructor(String.class).newInstance(file.getAbsolutePath());
				} catch (Throwable err) {
					throw new IllegalStateException(err);
				}
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		if (isTemporary()) {
			delete();
		}
	}

	/**
	 * Computes the age of this file.
	 * 
	 * @return the age of this file, in milliseconds.
	 */
	public long getAgeMs() {
		return System.currentTimeMillis() - getLastModificationDateMs();
	}

	/*
	 * public boolean isAbsolute() { return jf.isAbsolute(); }
	 */
	public long getLastModificationDateMs() {
		if (!exists())
			throw new IllegalStateException("cannot get the date of a non-existing file " + this);

		return javaFile.lastModified();
	}

	public void setLastModifiedMs(long i) {
		if (!this.javaFile.setLastModified(i))
			throw new IllegalStateException();
	}

	public String getName() {
		return javaFile.getName();
	}

	public String getPathRelativeToCurrentDir() {
		return getNameRelativeTo(Directory.getCurrentDirectory());
	}

	public String getNameRelativeTo(Directory d) {
		// if this file is somewhere in a subdirection of d
		if (getPath().startsWith(d.getPath())) {
			// simply remove the path to d
			return getPath().substring(d.getPath().length() + File.separator.length());
		} else {
			return getPath();
		}
	}

	public abstract long getSize();

	public abstract void delete();

	public Directory getParent() {
		var p = javaFile.getParent();
		return p == null ? null : new Directory(p);
	}

	@Override
	public String toString() {
		var p = getPath();
		var h = Directory.getHomeDirectory().getPath();

		if (p.startsWith(h)) {
			return "$HOME" + p.substring(h.length());
		} else {
			return p;
		}
	}

	public String toString2() {
		Map<String, Object> l = new HashMap<String, Object>();
		l.put("name", getPath());
		l.put("type", this instanceof Directory ? "directory" : "plain file");
		l.put("exist", exists());

		if (exists()) {
			l.put("size", getSize());
		}

		return l.toString();
	}

	public String getPath() {
		return javaFile.getAbsolutePath();
	}

	public boolean exists() {
		return javaFile.exists();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof AbstractFile && equals((AbstractFile) obj);
	}

	public boolean equals(AbstractFile f) {
		return getPath().equals(f.getPath());
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	public void hashContents(Hasher h) {
		h.add(getPath());
	}

	public void renameTo(String newName) throws IOException {
		try {
			renameTo(getClass().getConstructor(String.class).newInstance(newName));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void renameTo(AbstractFile newPath) throws IOException {
		boolean ok = javaFile.renameTo(newPath.javaFile);

		// if renaming failed, try copying+deleting
		if (!ok) {
			throw new IOException("failed at renaming file " + getPath() + " to " + newPath);
		}

		javaFile = newPath.javaFile;
	}

	public static void main(String[] args) throws Throwable {
		RegularFile f = new RegularFile("/tmp/vfkdsjh");
		f.setContent("coucou".getBytes());
		System.out.println(f);
		f.renameTo("/sdfsd");
		System.out.println(f);
	}

	public File toFile() {
		return this.javaFile;
	}

	public boolean canRead() {
		if (!exists())
			throw new IllegalStateException("cannot get the status of a non-existing file " + this.getPath());

		return javaFile.canRead();
	}

	public void setWriteable(boolean b) {
		javaFile.setWritable(b);
	}

	public void setReadable(boolean b) {
		javaFile.setWritable(b);
	}

	public boolean canWrite() {
		if (!exists()) {
			return getParent().canWrite();
		} else {
			return javaFile.canWrite();
		}
	}

	public boolean isExecutable() {
		if (!exists())
			throw new IllegalStateException("cannot get the status of a non-existing file " + this.getPath());

		return javaFile.canExecute();
	}

	public void setExecutable(boolean b) {
		javaFile.setExecutable(b);
	}

	public URL toURL() {
		try {
			return this.javaFile.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}

	public boolean isEmpty() {
		return getSize() == 0;
	}

	public abstract void create();

	public abstract void rsyncTo(String remotePath);

	public boolean isSymbolicLink() {
		return Files.isSymbolicLink(javaFile.toPath());
	}

	public void open() {
		try {
			Desktop.getDesktop().open(javaFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
