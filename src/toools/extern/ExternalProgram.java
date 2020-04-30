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
 
 package toools.extern;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import toools.io.JavaResource;
import toools.io.file.AbstractFile;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.log.Logger;
import toools.net.NetUtilities;

public abstract class ExternalProgram
{
	public static void ensureCommandsAreAvailable(String... commands)
	{
		Set<String> availableCommands = listAvailableCommands().keySet();

		for (String cmd : commands)
		{
			if (!availableCommands.contains(cmd))
			{
				throw new IllegalStateException("Command \"" + cmd + "\" is not available on this computer.");
			}
		}
	}

	public static Map<String, RegularFile> listAvailableCommands()
	{
		Map<String, RegularFile> cmds = new HashMap();

		for (Directory dir : retrieveSystemPath())
		{
			if (dir.exists())
			{
				for (AbstractFile file : dir.getChildren())
				{
					if (file instanceof RegularFile && file.exists() && file.isExecutable())
					{
						cmds.put(file.getName(), (RegularFile) file);
					}
				}
			}
		}

		return cmds;
	}

	public static List<Directory> retrieveSystemPath()
	{
		List<Directory> files = new ArrayList<Directory>();

		for (String filename : System.getenv("PATH").split(System.getProperty("path.separator")))
		{
			if (filename.trim().length() > 0)
			{
				files.add(new Directory(filename));
			}
		}

		return files;
	}

	public static RegularFile locateCommand(String cmd)
	{
		for (Directory dir : retrieveSystemPath())
		{
			RegularFile test = new RegularFile(dir.getPath() + '/' + cmd);

			if (test.exists() && test instanceof RegularFile && test.isExecutable())
			{
				return (RegularFile) test;
			}
		}

		return null;
	}

	public static boolean commandIsAvailable(String name)
	{
		return locateCommand(name) != null;
	}

	public static void unarchiveInPlace(RegularFile f, Logger log)
	{
		log.log("Unarchiving");

		if (f.getName().matches(".*\\.(tar\\.gz|tgz)"))
		{
			Proces.exec("tar", f.getParent(), "xzf", f.getName());
		}
		else if (f.getName().matches(".*\\.zip"))
		{
			Proces.exec("unzip", f.getParent(), f.getName());
		}
	}

	public static RegularFile compileCSource(JavaResource res, Directory compilationDirectory, Logger log) throws IOException
	{
		RegularFile src = new RegularFile(compilationDirectory, res.getName());
		res.exportToFile(src);
		return compileCSource(src, log);
	}

	public static RegularFile compileCSource(RegularFile src, Logger log)
	{
		String cc = src.getExtension().equals("c") ? "gcc" : "g++";
		log.log("Compiling with " + cc);
		RegularFile exeFile = new RegularFile(src.getParent(), src.getNameWithoutExtension());
		Proces.exec(cc, src.getParent(), "-O3", src.getName(), "-o", exeFile.getName());
		return exeFile;
	}

	public static RegularFile compileTarball(RegularFile archive, String directoryNameAfterUnarchiving, String pathToExecutableRelativeToDirectory,
			Logger log)
	{
		unarchiveInPlace(archive, log);
		Directory srcDir = new Directory(archive.getParent(), directoryNameAfterUnarchiving);

		if (new RegularFile(srcDir, "configure").exists())
		{
			log.log("Configure");
			Proces.exec("./configure", srcDir);
		}

		log.log("Make");
		Proces.exec("make", srcDir);
		return new RegularFile(srcDir, pathToExecutableRelativeToDirectory);
	}

	public static RegularFile compileTarball(Directory directory, JavaResource res, String directoryNameAfterUnarchiving,
			String pathToExecutableRelativeToDirectoryRoot, Logger log) throws IOException
	{
		System.out.println(res.getName());
		RegularFile tarball = new RegularFile(directory, res.getName());
		res.exportToFile(tarball);
		return compileTarball(tarball, directoryNameAfterUnarchiving, pathToExecutableRelativeToDirectoryRoot, log);
	}

	public static RegularFile compileTarball(Directory directory, String url, String directoryNameAfterUnarchiving,
			String pathToExecutableRelativeToDirectory, Logger log) throws IOException
	{
		String file = new URL(url).getFile();
		file = file.substring(file.lastIndexOf('/'));
		RegularFile tarball = new RegularFile(directory, file);
		tarball.setContent(NetUtilities.retrieveURLContent(url));
		return compileTarball(tarball, directoryNameAfterUnarchiving, pathToExecutableRelativeToDirectory, log);
	}
}
