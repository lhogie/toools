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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import toools.extern.Proces;
import toools.io.IORuntimeException;
import toools.io.ScannerListener;
import toools.io.Utilities;
import toools.text.TextUtilities;

@SuppressWarnings("serial")
public class Directory extends AbstractFile
{
	private static final Directory tempDirectory = new Directory(
			System.getProperty("java.io.tmpdir"));

	static
	{
		if ( ! tempDirectory.exists())
		{
			tempDirectory.mkdirs();
		}
	}

	public static Directory getSystemTempDirectory()
	{
		return tempDirectory;
	}

	public static Directory createTempDirectory(Directory location, String prefix,
			String suffix)
	{
		return new Directory(location,
				AbstractFile.findUnusedNameIn(location, prefix, suffix));
	}

	public Directory(String path)
	{
		super(path);

		if (javaFile.exists() && ! javaFile.isDirectory())
			throw new IllegalArgumentException(
					path + " is not a directory " + this.getPath());
	}

	public Directory(Directory parent, String name)
	{
		this(parent.getPath() + '/' + name);
	}

	@Override
	public boolean isEmpty()
	{
		return getChildren().isEmpty();
	}

	@Override
	public void delete()
	{
		if (Files.isSymbolicLink(javaFile.toPath()))
		{
			javaFile.delete();
		}
		else
		{
			if ( ! exists())
				throw new IllegalStateException(
						"cannot delete a non-existing directory " + this.getPath());

			if ( ! isEmpty())
				throw new IllegalStateException("cannot delete a non-empty directory "
						+ this.getPath() + ". Please use deleteRecursively()");

			javaFile.delete();

			if (exists())
				throw new IllegalStateException(
						"directory should no longer exist " + this.getPath());
		}

	}

	public void deleteRecursively()
	{
		if ( ! exists())
			throw new IllegalStateException(
					"cannot delete a non-existing directory " + this.getPath());

		for (AbstractFile thisChild : getChildren())
		{
			if (thisChild instanceof Directory)
			{
				((Directory) thisChild).deleteRecursively();
			}
			else
			{
				thisChild.delete();
			}
		}

		javaFile.delete();

		if (exists())
			throw new IllegalStateException(
					"directory should no longer exist " + this.getPath());
	}

	public void copyTo(Directory destination, boolean overwrite)
			throws FileNotFoundException, IOException
	{
		if ( ! exists())
			throw new IllegalStateException(
					"cannot copy a non-existing directory " + this.getPath());

		if ( ! overwrite && destination.exists())
			throw new IllegalStateException("don't want to overwrite " + destination);

		destination.mkdirs();

		for (AbstractFile c : getChildren())
		{
			if (c instanceof RegularFile)
			{
				((RegularFile) c).copyTo(
						new RegularFile(destination.getPath() + '/' + c.getName()),
						overwrite);
			}
			else if (c instanceof Directory)
			{
				((Directory) c).copyTo(
						new Directory(destination.getPath() + '/' + c.getName()),
						overwrite);
			}
			else
			{
				throw new IllegalStateException(
						"don't know this king of file " + c.getClass());
			}
		}
	}

	@Override
	public long getSize()
	{
		if ( ! exists())
			throw new IllegalStateException(
					"cannot get the size of a non-existing directory " + this.getPath());

		long sum = 0;

		for (AbstractFile thisChild : getChildren())
		{
			if (thisChild.exists())
			{
				sum += thisChild.getSize();
			}
		}

		return sum;
	}

	public List<AbstractFile> getChildFiles(FileFilter... ffs)
	{
		if ( ! exists())
			throw new IllegalStateException(
					"cannot get the children of a non-existing directory "
							+ this.getPath());

		List<AbstractFile> children = new ArrayList<AbstractFile>();

		for (String s : javaFile.list())
		{
			AbstractFile f = AbstractFile.map(getPath() + '/' + s, null);

			if (accept(f, ffs))
			{
				children.add(f);
			}
		}

		return children;
	}

	public List<AbstractFile> getChildren()
	{
		return getChildFiles(new FileFilter()
		{

			@Override
			public boolean accept(AbstractFile f)
			{
				return true;
			}
		});
	}

	public List<Directory> getChildDirectories()
	{
		return (List<Directory>) (List<?>) getChildFiles(FileFilter.directoryFilter);
	}

	public List<RegularFile> getChildRegularFiles()
	{
		return (List<RegularFile>) (List<?>) getChildFiles(FileFilter.regularFileFilter);
	}

	public boolean mkdirs()
	{
		if (exists())
			throw new IllegalStateException(
					"cannot create and already-existing directory " + this.getPath());

		return javaFile.mkdirs();
	}

	public List<AbstractFile> retrieveTree()
	{
		return retrieveTree(null, null);
	}

	public List<AbstractFile> retrieveTree(FileFilter filter, ScannerListener l)
	{
		List<AbstractFile> files = new ArrayList<AbstractFile>();

		if (Utilities.operatingSystemIsUNIX())
		{
			for (String line : TextUtilities
					.splitInLines(new String(Proces.exec("find", getPath()))))
			{
				AbstractFile f = AbstractFile.map(line, null);

				if (filter == null || filter.accept(f))
				{
					files.add(f);

					if (l != null)
					{
						l.foundFile(f);
					}
				}
			}
		}
		else
		{
			Stack<Directory> stack = new Stack<Directory>();
			stack.push(this);

			while ( ! stack.isEmpty())
			{
				Directory f = stack.pop();

				for (AbstractFile child : f.getChildren())
				{
					if (filter == null || filter.accept(child))
					{
						files.add(child);

						if (l != null)
						{
							l.foundFile(child);
						}
					}

					if (child instanceof Directory)
					{
						stack.push((Directory) child);
					}
				}
			}
		}

		return files;
	}

	private boolean accept(AbstractFile f, FileFilter... ffs)
	{
		for (FileFilter ff : ffs)
		{
			if ( ! ff.accept(f))
			{
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Directory> listDirectories()
	{
		return (List<Directory>) (List) getChildFiles(FileFilter.directoryFilter);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<RegularFile> listRegularFiles()
	{
		return (List<RegularFile>) (List) getChildFiles(FileFilter.regularFileFilter);
	}

	public <F extends AbstractFile> F getChild(String string, Class<F> defaultClass)
	{
		return AbstractFile.map(getPath() + File.separator + string, defaultClass);
	}

	public List<AbstractFile> findChildFilesWhoseTheNameMatches(String re)
	{
		return getChildFiles(new FileFilter.RegexFilter(re));
	}

	public String getUniqFileName(String prefix, String suffix)
	{
		for (int i = 0;; ++i)
		{
			String name = prefix + i + suffix;

			if ( ! new File(getPath(), name).exists())
			{
				return name;
			}
		}
	}

	public Directory getChildDirectory(String name)
	{
		return getChild(name, Directory.class);
	}

	public RegularFile getChildRegularFile(String name)
	{
		return getChild(name, RegularFile.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<RegularFile> getChildRegularFilesMatching(String re)
	{
		return (List<RegularFile>) (List) getChildFiles(FileFilter.regularFileFilter,
				new FileFilter.RegexFilter(re));
	}

	public static Directory getCurrentDirectory()
	{
		return new Directory(System.getProperty("user.dir"));
	}

	public static Directory getHomeDirectory()
	{
		return new Directory(System.getProperty("user.home"));
	}

	@Override
	public void create()
	{
		boolean b = mkdirs();

		if ( ! b)
			throw new IORuntimeException("can't create directory " + this);
	}

	@Override
	public void rsyncTo(String remotePath)
	{
		if ( ! remotePath.endsWith("/"))
		{
			remotePath += '/';
		}

		Proces.exec("rsync", "-a", "--delete", getPath() + '/', remotePath);
	}

	public void ensureExists()
	{
		if ( ! exists())
			mkdirs();
	}

	public String pickOneFileOrNull()
	{
		try
		{
			DirectoryStream<Path> s = Files.newDirectoryStream(javaFile.toPath());
			Iterator<Path> i = s.iterator();
			String f = i.hasNext() ? i.next().getFileName().toString() : null;
			s.close();
			return f;
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public String pickOneFileOrNull(Random r)
	{
		String[] files = javaFile.list();

		if (files.length == 0)
			return null;

		return files[r.nextInt(files.length)];
	}

	public long getNbFiles()
	{
		if (!exists())
			throw new IllegalStateException("directory " + this + " does not exist");
		
		return javaFile.list().length;
	}

}
