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

package toools.reflect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import toools.collections.Collections;
import toools.io.FileUtilities;
import toools.io.Utilities;
import toools.io.file.AbstractFile;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.net.SSHParms;
import toools.net.SSHUtils;
import toools.thread.Generator;
import toools.thread.GeneratorChain;

@SuppressWarnings("serial")
public class ClassPath extends ArrayList<ClassContainer> {
	private static ClassPath systemClassPath;
	private RegularFile zipFile;
	List<RegularFile> zipFiles;

	public static synchronized ClassPath retrieveSystemClassPath() {
		if (systemClassPath == null) {
			systemClassPath = new ClassPath();

			for (String entry : System.getProperty("java.class.path")
					.split(File.pathSeparator)) {
				entry = entry.trim();

				if ( ! entry.isEmpty()) {
					AbstractFile f = AbstractFile.map(entry, Directory.class);
					systemClassPath.add(new ClassContainer(f));
				}
			}
		}

		return systemClassPath;
	}

	public Generator<Class<?>> listAllClasses() {
		GeneratorChain<Class<?>> chain = new GeneratorChain<>();

		for (ClassContainer ce : this) {
			chain.getChain().add(ce.listAllClasses());
		}

		return chain;
	}

	public List<ClassContainer> getContainersMatching(String re) {
		List<ClassContainer> matchingContainers = new ArrayList();

		for (ClassContainer ce : this) {
			if (ce.getFile().getPath().matches(re)) {
				matchingContainers.add(ce);
			}
		}

		return matchingContainers;
	}

	public List<RegularFile> getJars() throws IOException {
		if (zipFiles == null) {
			zipFiles = new ArrayList();

			for (ClassContainer cc : this) {
				AbstractFile f = cc.getFile();

				if (f instanceof RegularFile) {
					zipFiles.add((RegularFile) cc.getFile());
				}
				else {
					Directory d = (Directory) f;
					RegularFile zz = new RegularFile(
							"/tmp/" + d.getPath().replace('/', '_') + ".jar");

					if ( ! d.getChildren().isEmpty()) {
						FileUtilities.zip(zz, d, null);
						zipFiles.add(zz);
					}
				}
			}
		}

		return zipFiles;
	}

	public RegularFile getSeftContainedZipFile() throws IOException {
		if (zipFile == null) {
			Map<String, byte[]> map = new HashMap<String, byte[]>();

			for (ClassContainer cc : this) {
				AbstractFile f = cc.getFile();

				if (f instanceof RegularFile) {
					map.put(cc.getFile().getName(),
							((RegularFile) cc.getFile()).getContent());
				}
				else {
					RegularFile zz = new RegularFile("/tmp/sdfsdfjskjfshkjhqg");
					FileUtilities.zip(zz, (Directory) f, null);
					map.put(f.getPath().replace('/', '-') + ".jar", zz.getContent());
					zz.delete();
				}
			}

			zipFile = new RegularFile("/tmp/" + Math.random() + ".zip");

			if (zipFile.exists()) {
				zipFile.delete();
			}

			FileUtilities.zip(zipFile, map);
		}

		return zipFile;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		for (ClassContainer cc : this) {
			b.append(cc.getFile().getPath() + (cc.getFile().exists() ? "" : "(not found)")
					+ "\n");
		}

		return b.toString();
	}

	public long sizeInBytes() {
		long s = 0;

		for (ClassContainer cc : this) {
			if (cc.getFile().exists()) {
				s += cc.getFile().getSize();
			}
		}

		return s;
	}

	public void rsyncTo(String destIP, String destPath) throws IOException {
		rsyncTo(null, destIP, destPath, stdout -> {
		}, stderr -> {
		});
	}

	public int rsyncTo(SSHParms sshParameters, String destIP, String destPath,
			Consumer<String> stdout, Consumer<String> stderr) throws IOException {
		List<String> args = new ArrayList<>();
		args.add("rsync");

		if (sshParameters != null) {
			args.add("-e");
			List<String> ssh = new ArrayList<>();
			ssh.add(SSHUtils.sshCmd());
			SSHUtils.addSSHOptions(ssh, sshParameters);
			args.add(Collections.toString(ssh, " "));
		}

		args.add("-a");
		args.add("--delete");
		args.add("--copy-links");
		args.add("-v");

		for (ClassContainer e : this) {
			if (e.getFile() instanceof Directory) {
				args.add(e.getFile().getPath() + "/");
			}
			else {
				args.add(e.getFile().getPath());
			}
		}

		args.add(destIP + ":" + destPath);

		try {
			// System.out.println(args);
			Process rsync = Runtime.getRuntime().exec(args.toArray(new String[0]));
			Utilities.grabLines(rsync.getInputStream(), stdout, err -> {}) ;
			Utilities.grabLines(rsync.getErrorStream(), stderr, err -> {});
			rsync.waitFor();
			return rsync.exitValue();
		}
		catch (InterruptedException e1) {
			throw new IllegalStateException(e1);
		}
	}
}
