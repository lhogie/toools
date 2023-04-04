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

package toools.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import toools.io.file.AbstractFile;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.reflect.ClassContainer;
import toools.reflect.ClassPath;
import toools.thread.Generator;
import toools.thread.GeneratorChain;

public class JavaResource {
	private String name;
	private Class<?> refClass;

	// where is stored the resource
	private ClassContainer container;

	public JavaResource(String name) {
		if (!name.startsWith("/"))
			throw new IllegalArgumentException("resource name should start with a slash: " + name);

		this.name = name;
	}

	public JavaResource(Class<?> clazz, String name) {
		if (name.startsWith("/"))
			throw new IllegalArgumentException(
					"no slash allowed in resource name since its location is already characterized by a class");

		this.refClass = clazz;
		this.name = '/' + clazz.getPackage().getName().replace('.', '/') + '/' + name;
	}

	public String getPath() {
		return name;
	}

	public void setName(String newName) {
		if (newName == null)
			throw new IllegalArgumentException();

		name = newName;
	}

	public InputStream getInputStream() {
		return (refClass == null ? getClass() : refClass).getResourceAsStream(getPath());
	}

	public URL getURL() {
		return refClass.getResource(getPath());
	}

	public byte[] getByteArray() {
		InputStream is = getInputStream();

		if (is == null) {
			throw new IORuntimeException("cannot get an input stream for resource " + getPath());
		} else {
			return Utilities.readUntilEOF(is);
		}
	}

	public static Generator<JavaResource> listResources() {
		return listResources(ClassPath.retrieveSystemClassPath());
	}

	public static Generator<JavaResource> listResources(ClassPath urls) {
		GeneratorChain<JavaResource> gc = new GeneratorChain<>();

		for (ClassContainer thisClassPathEntry : urls) {
			gc.getChain().add(listResources(thisClassPathEntry));
		}

		return gc;
	}

	public static Generator<JavaResource> listResources(final ClassContainer classPathEntry) {
		return new Generator<JavaResource>() {

			@Override
			public void produce() {
				AbstractFile entryFile = classPathEntry.getFile();

				if (!entryFile.exists())
					throw new IllegalStateException("classpath entry not found: " + entryFile.getPath());

				if (entryFile instanceof Directory) {
					((Directory) entryFile).search(sf -> {
						if (toools.io.file.FileFilter.regularFileFilter.test(sf)) {
							String resname = sf.getPath().replace(entryFile.getPath(), "");
							JavaResource res = new JavaResource(resname);
							res.setContainer(classPathEntry);
							deliver(res);
						}

						return true;
					});
				} else if (entryFile.getName().matches(".*\\.(jar|zip)$")) {
					try {
						ZipFile zf = new ZipFile(new File(entryFile.getPath()));
						Enumeration entries = zf.entries();

						while (entries.hasMoreElements()) {
							ZipEntry e = (ZipEntry) entries.nextElement();
							String entryName = e.getName();

							if (!entryName.startsWith("/"))
								entryName = "/" + entryName;

							JavaResource thisResource = new JavaResource(entryName);
							thisResource.setContainer(classPathEntry);
							deliver(thisResource);
						}
					} catch (ZipException e1) {
						System.err.println("File " + entryFile.getPath());
						e1.printStackTrace();
					} catch (IOException e1) {
						System.err.println("File " + entryFile.getPath());
						e1.printStackTrace();
					}
				} else {
					// throw new IllegalStateException("dunno how to handle " +
					// thisClassPathEntry.getName());
					System.err.println(
							"dunno how to handle " + entryFile.getName() + " of class " + entryFile.getClass());
				}

			}
		};
	}

	public ClassContainer getContainer() {
		return this.container;
	}

	public void setContainer(ClassContainer container) {
		this.container = container;
	}

	public static ClassPath findResourceContainer(String name, ClassPath classpath) {
		ClassPath urls = new ClassPath();

		// for all the resources found in the classpath
		for (JavaResource r : JavaResource.listResources(classpath)) {
			// if this resource has the same name as the resource looked for
			if (r.getPath().equals(name)) {
				urls.add(r.getContainer());
			}
		}

		return urls;
	}

	public boolean exists() {
		InputStream is = getInputStream();

		if (is == null) {
			return false;
		} else {
			try {
				is.close();
			} catch (IOException e) {
			}

			return true;
		}
	}

	public void exportToFile(RegularFile f) throws IOException {
		if (!f.getParent().exists())
			f.getParent().mkdirs();

		f.setContent(getByteArray());
	}

	public static void exportToFile(String resourceName, RegularFile dest) throws IOException {
		if (!dest.getParent().exists())
			dest.getParent().mkdirs();

		new JavaResource(resourceName).exportToFile(dest);
	}

	public static void loadLibrary(String resourceName) throws IOException {
		RegularFile f = new RegularFile(Directory.getSystemTempDirectory(), resourceName);
		exportToFile(resourceName, f);
		System.load(f.getPath());
	}

	public String getName() {
		return name.substring(name.lastIndexOf('/') + 1);
	}

	public static JavaResource getAttachedResource(Class<?> referenceClass, String resourceName) {
		return new JavaResource('/' + referenceClass.getPackage().getName().replace('.', '/') + '/' + resourceName);
	}

}
