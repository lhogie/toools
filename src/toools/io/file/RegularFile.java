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
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toools.extern.Proces;
import toools.io.IORuntimeException;
import toools.io.Utilities;
import toools.reflect.Clazz;
import toools.text.TextUtilities;

public class RegularFile extends AbstractFile
{
	public static final Map<String, Class> extension_class = new HashMap<>();

	public static RegularFile instanciate(String path)
	{
		String type = FileNameDecomposition.getExtension(path);
		Class c = extension_class.get(type);

		try
		{
			return (RegularFile) Clazz.makeInstance(c.getConstructor(String.class), path);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public RegularFile(String path)
	{
		super(path);
	}

	public RegularFile(Directory parent, String name)
	{
		this(parent.getPath() + '/' + name);
	}

	public static RegularFile createTempFile()
	{
		return createTempFile("toools-", ".tmp");
	}

	public static RegularFile createTempFile(String prefix, String suffix)
	{
		try
		{
			return createTempFile(Directory.getSystemTempDirectory(), prefix, suffix);
		}
		catch (IOException e)
		{
			throw new IllegalStateException("cannot create file in temp directory: "
					+ Directory.getSystemTempDirectory());
		}
	}

	public static RegularFile createTempFile(Directory location, String prefix,
			String suffix) throws IOException
	{
		RegularFile f = new RegularFile(location,
				AbstractFile.findUnusedNameIn(location, prefix, suffix));
		f.setTemporary(true);
		f.create();
		return f;
	}

	public void setContent(byte[] bytes)
	{
		try
		{
			OutputStream bos = createWritingStream();
			bos.write(bytes);
			bos.flush();
			bos.close();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void setContent(String text, Charset charset) throws IOException
	{
		setContent(text.getBytes(charset));
	}

	public void setContentAsUTF8(String text) throws IOException
	{
		setContent(text.getBytes("UTF-8"));
	}

	public void setContentAsUTF16(String text) throws IOException
	{
		setContent(text.getBytes("UTF-16"));
	}

	public void setContentAsASCII(String text) throws IOException
	{
		setContent(text.getBytes("US-ASCII"));
	}

	public void setContentAsMacRoman(String text) throws IOException
	{
		setContent(text.getBytes("MacRoman"));
	}

	public byte[] getContent()
	{
		InputStream bis = createReadingStream();
		byte[] bytes = Utilities.readUntilEOF(bis);
		try
		{
			bis.close();
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
		return bytes;
	}

	public void copyTo(RegularFile destination, boolean overwrite)
			throws FileNotFoundException, IOException
	{
		if ( ! exists())
			throw new IllegalStateException(
					"cannot copy a non-existing file " + this.getPath());

		if ( ! overwrite && destination.exists())
			throw new IllegalStateException(
					"don't want to overwrite " + destination.getPath());

		InputStream is = createReadingStream();
		OutputStream os = destination.createWritingStream();
		Utilities.copy(is, os);
		is.close();
		os.close();
	}

	public void moveTo(Directory destDir, boolean overwrite)
	{
		moveTo(destDir.getPath() + "/" + getName(), overwrite);
	}

	public void moveTo(String path, boolean overwrite)
	{
		if ( ! exists())
			throw new IllegalStateException(
					"cannot move a non-existing file " + this.getPath());

		if (new File(path).exists() && ! overwrite)
			throw new IllegalStateException(
					"dest file already exists: " + this.getPath());

		if ( ! javaFile.renameTo(new File(path)))
			throw new IORuntimeException("can't rename file to " + path);
	}

	@Override
	public void delete()
	{
		// PosixFileAttributes attrs = Files.readAttributes(path,
		// PosixFileAttributes.class, NOFOLLOW_LINKS);
		// if (!exists())
		// throw new IllegalStateException("cannot delete a non-existing file "
		// + this.getPath());

		if ( ! javaFile.delete())
			throw new IllegalStateException(
					"fail to delete file " + javaFile.getAbsolutePath());
	}

	@Override
	public long getSize()
	{
		if ( ! exists())
			throw new IllegalStateException(
					"cannot get the size of a non-existing file " + this.getPath());

		return javaFile.length();
	}

	public OutputStream createWritingStream()
	{
		return createWritingStream(false, 65536 * 256);
	}

	public OutputStream createWritingStream(boolean append)
	{
		return createWritingStream(append, 1024 * 1024);
	}

	public OutputStream createWritingStream(boolean append, int bufSize)
	{
		try
		{
			if (bufSize > 0)
			{
				return new BufferedOutputStream(new FileOutputStream(javaFile, append),
						bufSize);
			}
			else
			{
				return new FileOutputStream(javaFile, append);
			}
		}
		catch (FileNotFoundException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public InputStream createReadingStream()
	{
		return createReadingStream(1024 * 1024);
	}

	public InputStream createReadingStream(int bufSize)
	{
		try
		{
			InputStream is = new FileInputStream(javaFile);

			if (bufSize > 0)
			{
				is = new BufferedInputStream(is, bufSize);
			}

			return is;
		}
		catch (FileNotFoundException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public BufferedReader createLineReadingStream()
	{
		if ( ! exists())
			throw new IORuntimeException(
					"cannot read a non-existing file " + this.getPath());

		try
		{
			return new BufferedReader(new FileReader(javaFile), 1024 * 1024);
		}
		catch (FileNotFoundException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public static boolean sameContents(RegularFile a, RegularFile b)
	{
		if (a.getSize() != b.getSize())
		{
			return false;
		}
		else
		{
			return compareFileContentsLexicographically(a, b) == 0;
		}
	}

	public static int compareFileContentsLexicographically(RegularFile a, RegularFile b)
	{
		if (a.getSize() == 0 && b.getSize() == 0)
		{
			return 0;
		}
		else if (a.getSize() == 0)
		{
			return - 1;
		}
		else if (b.getSize() == 0)
		{
			return 1;
		}
		else
		{
			return TextUtilities.compareLexicographically(a.createReadingStream(),
					b.createReadingStream());
		}
	}

	public String getNameWithoutExtension()
	{
		String extension = getExtension();

		if (extension == null)
		{
			return getName();
		}
		else
		{
			return getName().substring(0, getName().length() - extension.length() - 1);
		}
	}

	@Override
	public void create()
	{
		try
		{
			boolean ok = javaFile.createNewFile();

			if ( ! ok)
				throw new IORuntimeException("can't create file " + this);
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public void create(boolean createParentIfNeeded)
	{
		if ( ! getParent().exists() && createParentIfNeeded)
		{
			getParent().mkdirs();
		}

		create();
	}

	public List<String> getLines()
	{
		return TextUtilities.splitInLines(new String(getContent()));
	}

	public boolean isArchive()
	{
		Collection<String> extensions = Arrays.asList("tgz", "tar.gz", "zip");
		return extensions.contains(getExtension());
	}

	public FileNameDecomposition getNameDecomposition()
	{
		return new FileNameDecomposition(getName());
	}

	public List<String> getExtensions()
	{
		return getNameDecomposition().extensions;
	}

	public String getExtension()
	{
		List<String> extensions = getNameDecomposition().extensions;

		if (extensions.isEmpty())
		{
			return null;
		}
		else
		{
			return extensions.get(extensions.size() - 1);
		}
	}

	public void setExtension(String newExtension) throws IOException
	{
		String ext = getExtension();
		String newName = getPath().replaceFirst(ext + "$", newExtension);
		renameTo(newName);
	}

	public void append(byte[] bytes) throws IOException
	{
		OutputStream os = createWritingStream(true);
		os.write(bytes);
		os.close();
	}

	@Override
	public void rsyncTo(String remotePath)
	{
		Proces.exec("rsync", getPath(), remotePath);
	}

	public static long sumSize(Iterable<RegularFile> files)
	{
		long size = 0;

		for (RegularFile f : files)
		{
			size += f.getSize();
		}

		return size;
	}

	public RegularFile getRegularFileInSameDirectory(String name)
	{
		return getParent().getChildRegularFile(name);
	}

	public RegularFile getSibbling(String extension)
	{
		return getRegularFileInSameDirectory(getNameWithoutExtension() + extension);
	}
}
