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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import toools.extern.Proces;
import toools.io.file.AbstractFile;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.io.stream.FileStreamSource;
import toools.math.MathsUtilities;
import toools.text.TextUtilities;

public class FileUtilities
{
	public static <F extends AbstractFile> F lastModifiedFile(Collection<F> files)
	{
		if (files.isEmpty())
			throw new IllegalArgumentException();

		Iterator<F> i = files.iterator();
		F l = i.next();

		while (i.hasNext())
		{
			F t = i.next();

			if (t.getLastModificationDateMs() > l.getLastModificationDateMs())
			{
				l = t;
			}
		}

		return l;
	}

	public static File copy(File src, File dest, boolean overwrite) throws IOException
	{
		if (dest.isDirectory())
		{
			return copy(src, FileUtilities.getChildFile(dest, src.getName()), overwrite);
		}
		else
		{
			if (dest.exists() && !overwrite)
				throw new IllegalArgumentException("destination exists, cannot overwrite");

			FileInputStream fis = new FileInputStream(src);
			BufferedInputStream bis = new BufferedInputStream(fis);
			FileOutputStream fos = new FileOutputStream(dest);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte[] buf = new byte[1024];

			while (true)
			{
				int n = bis.read(buf);

				if (n > 0)
				{
					bos.write(buf, 0, n);
				}
				else
				{
					break;
				}
			}

			bos.flush();
			fos.close();
			fis.close();
			return dest;
		}

	}

	public static File getUniqFile(String prefix, String suffix)
	{
		for (int i = 0; i < Integer.MAX_VALUE; ++i)
		{
			File file = new File(prefix + i + suffix).getAbsoluteFile();

			if (!file.exists())
			{
				return file;
			}
		}

		throw new IllegalStateException("Cannot find uniq file");
	}

	public static byte[] computeMD5(File f) throws IOException
	{

		return computeMD5(getFileContent(f));
	}

	public static byte[] computeMD5(byte[] b)
	{
		try
		{
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(b);
			return digest.digest();

		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException();
		}
	}

	public static String getNameRelativeCurrentDir(File f)
	{
		return getNameRelativeTo(f, getCurrentDirectory());
	}

	public static String getNameRelativeTo(File f, File ref)
	{
		if (f.getAbsolutePath().startsWith(ref.getAbsolutePath()))
		{
			return f.getAbsolutePath().substring(ref.getAbsolutePath().length() + File.separator.length());
		}
		else
		{
			return f.getAbsolutePath();
		}
	}

	public static String getFileNameExtension(String fileName)
	{
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		int pos = fileName.lastIndexOf('.');
		return pos < 0 ? null : fileName.substring(pos + 1);
	}

	public static String getFileNameRadical(String fileName)
	{
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		int pos = fileName.lastIndexOf('.');
		return pos < 0 ? fileName : fileName.substring(0, pos);
	}

	public static byte[] getFileContent(File file) throws IOException
	{
		FileStreamSource fss = new FileStreamSource(file);
		return fss.readItAll();
	}

	public static List<String> getFileLines(File file) throws IOException
	{
		return Arrays.asList(new String(getFileContent(file)).split("\n"));
	}

	public static void setFileContent(File file, byte[] bytes) throws IOException
	{
		FileStreamSource fss = new FileStreamSource(file);
		fss.writeItAll(bytes);
	}

	public static Collection<File> getChildFiles(File parent)
	{
		return new HashSet<File>(Arrays.asList(parent.listFiles()));
	}

	public static File getChildFile(File parent, String childName)
	{
		if (parent == null)
			throw new NullPointerException("null parent file");

		return new File(parent.getAbsolutePath() + "/" + childName).getAbsoluteFile();
	}

	public static void zip(RegularFile zipFile, Map<String, byte[]> map) throws IOException
	{
		ZipOutputStream out = new ZipOutputStream(zipFile.createWritingStream());

		for (String entry : map.keySet())
		{
			out.putNextEntry(new ZipEntry(entry));
//			System.out.println("writing " + entry);
			out.write(map.get(entry));
			out.closeEntry();
		}

		out.close();
	}

	public static Map<String, byte[]> unzip(RegularFile zipFile) throws IOException
	{
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		ZipInputStream is = new ZipInputStream(zipFile.createReadingStream());

		while (true)
		{
			ZipEntry ze = is.getNextEntry();

			if (ze == null)
			{
				break;
			}
			else
			{
				String name = ze.getName();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Utilities.copy(is, bos);
				bos.close();
				map.put(name, bos.toByteArray());
			}
		}

		is.close();
		return map;
	}

	public static void zip(RegularFile zipFile, Directory dir, toools.io.file.FileFilter fileFilter) throws IOException
	{
		ZipOutputStream out = new ZipOutputStream(zipFile.createWritingStream());

		for (AbstractFile f : dir.retrieveTree(fileFilter, null))
		{
			if (f instanceof RegularFile)
			{
				out.putNextEntry(new ZipEntry(f.getNameRelativeTo(dir)));
				byte[] data = ((RegularFile) f).getContent();
				out.write(data);
				out.closeEntry();
			}
		}

		out.close();
	}

	public static void zip(RegularFile zipFile, Collection<RegularFile> files) throws IOException
	{
		ZipOutputStream out = new ZipOutputStream(zipFile.createWritingStream());

		for (RegularFile f : files)
		{
			out.putNextEntry(new ZipEntry(f.getName()));
			out.write(f.getContent());
			out.closeEntry();
		}

		out.close();

	}

	public static List<String> computeExtensions(List<RegularFile> files)
	{
		List<String> l = new ArrayList<String>();

		for (RegularFile f : files)
		{
			l.add(f.getExtension());
		}

		return l;
	}

	public static class RegularFileFilter implements JavaFileFilter
	{
		@Override
		public boolean accept(File f)
		{
			return f.isFile();
		}

	}

	public static interface JavaFileFilter
	{
		public boolean accept(File f);
	}

	public static class DirectoryFilter implements JavaFileFilter
	{
		@Override
		public boolean accept(File f)
		{
			return f.isDirectory();
		}
	}

	public static class RegexFilter implements JavaFileFilter
	{
		private String re;

		public RegexFilter(String re)
		{
			this.re = re;
		}

		@Override
		public boolean accept(File f)
		{
			return f.getName().matches(re);
		}
	}

	public static void remove(File... files)
	{
		for (File f : files)
		{
			if (f.isDirectory())
			{
				remove(f.listFiles());
			}

			if (!f.delete())
			{
				throw new IllegalStateException("cannot delete file " + f.getAbsolutePath() + " (writable=" + f.canWrite() + " exist=" + f.exists() + ")");
			}
		}
	}

	public static File getCurrentDirectory()
	{
		return new File(System.getProperty("user.dir"));
	}

	public static File getHomeDirectory()
	{
		return new File(System.getProperty("user.home"));
	}

	public static List<RegularFile> computeExistingFiles(Collection<RegularFile> inputFiles)
	{
		List<RegularFile> outputFiles = new ArrayList<RegularFile>();

		for (RegularFile file : inputFiles)
		{
			if (file.exists())
			{
				outputFiles.add(file);
			}
		}

		return outputFiles;
	}

	public static List<RegularFile> convertFilesToRegularFiles(Collection<File> inputFiles)
	{
		List<RegularFile> outputFiles = new ArrayList<RegularFile>();

		for (File file : inputFiles)
		{
			outputFiles.add(new RegularFile(file.getAbsolutePath()));
		}

		return outputFiles;
	}

	public static RegularFile[] convertFilesToRegularFiles(File... inputFiles)
	{
		RegularFile[] outputFiles = new RegularFile[inputFiles.length];

		for (int i = 0; i < inputFiles.length; ++i)
		{
			outputFiles[i] = new RegularFile(inputFiles[i].getAbsolutePath());
		}

		return outputFiles;
	}

	public static List<RegularFile> computeExistingFiles2(Collection<File> inputFiles)
	{
		return computeExistingFiles(convertFilesToRegularFiles(inputFiles));
	}

	public static boolean ensureSameFile(Collection<RegularFile> files)
	{
		if (files.size() == 1)
		{
			return true;
		}
		else
		{
			Map<RegularFile, Long> m = new HashMap<RegularFile, Long>();

			for (RegularFile f : files)
			{
				m.put(f, f.getSize());
			}

			return new HashSet<Long>(m.values()).size() == 1;
		}
	}

	public static String replaceExtensionBy(String filename, String newExtension)
	{
		String oldExtension = getFileNameExtension(filename);

		if (oldExtension == null)
			throw new IllegalArgumentException("file name has no extension: " + filename);

		if (newExtension.startsWith("."))
			newExtension = newExtension.substring(1);

		return filename.substring(0, filename.length() - oldExtension.length() - 1) + '.' + newExtension;
	}

	public static void sortByName(List<File> l)
	{
		Collections.sort(l, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2)
			{
				return o1.getName().compareTo(o2.getName());
			}

		});
	}

	public static void sortByDate(List<RegularFile> l)
	{
		Collections.sort(l, new Comparator<RegularFile>() {

			@Override
			public int compare(RegularFile o1, RegularFile o2)
			{
				return MathsUtilities.compare(o1.getLastModificationDateMs(), o2.getLastModificationDateMs());
			}

		});
	}

	public static <F extends AbstractFile> void sortByAbsolutePath(List<F> l)
	{
		Collections.sort(l, new Comparator<AbstractFile>() {

			@Override
			public int compare(AbstractFile o1, AbstractFile o2)
			{
				return o1.getPath().compareTo(o2.getPath());
			}

		});
	}

	public static void sortBySize(List<File> l)
	{
		Collections.sort(l, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2)
			{
				return MathsUtilities.compare(o1.length(), o2.length());
			}

		});
	}

	@SuppressWarnings("unchecked")
	public static Collection<String> computeCommonFiles(File... a)
	{
		Collection<Collection<String>> sets = new HashSet<Collection<String>>();

		for (File f : a)
		{
			sets.add(new HashSet<String>(Arrays.asList(f.list())));
		}

		return toools.collections.Collections.intersection(sets.toArray((Collection<String>[])new Collection[0]));
	}

	public static RegularFile createTempFile(String prefix, String suffix)
	{
		RegularFile f = new RegularFile(prefix + Math.random() + suffix);
		return f;
	}

	public static void createSymbolicLink(RegularFile src, RegularFile link)
	{
		Proces.exec("ln", Directory.getCurrentDirectory(), "-s", src.getName(), link.getPath());
	}

	public static Collection<File> findChildFilesWhoseTheNameMatches(File baseDir, String string)
	{
		Collection<File> c = new ArrayList<File>();

		for (File f : getChildFiles(baseDir))
		{
			if (f.getName().matches(string))
			{
				c.add(f);
			}
		}

		return c;
	}

	public static Collection<AbstractFile> findChildFilesWhoseTheNameMatches(Directory d, String filename, boolean recursive)
	{
		Collection<AbstractFile> c = new ArrayList<AbstractFile>();

		for (AbstractFile f : recursive ? d.retrieveTree() : d.getChildren())
		{
			if (f.getName().matches(filename))
			{
				c.add(f);
			}
		}

		return c;
	}

	public static List<File> retrieveTree(File root, JavaFileFilter filter)
	{
		List<File> files = new ArrayList<File>();

		if (Utilities.operatingSystemIsUNIX())
		{
			for (String line : TextUtilities.splitInLines(new String(Proces.exec("find", root.getAbsolutePath()))))
			{
				File f = new File(line).getAbsoluteFile();

				if (filter == null || filter.accept(f))
				{
					files.add(f);
				}
			}
		}
		else
		{
			Stack<File> stack = new Stack<File>();
			stack.push(root);

			while (!stack.isEmpty())
			{
				File f = stack.pop();

				for (File child : f.listFiles())
				{
					if (filter == null || filter.accept(child))
					{
						files.add(child);
					}

					if (child.isDirectory() && child.canRead())
					{
						stack.push(child);
					}
				}
			}
		}

		return files;
	}

}
