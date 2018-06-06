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
 
 package toools.os;

import java.io.Serializable;
import java.lang.management.ManagementFactory;

import toools.exceptions.NotYetImplementedException;
import toools.extern.Proces;
import toools.io.file.AbstractFile;
import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class OperatingSystem implements Serializable
{
	private static OperatingSystem localOS;

	public static OperatingSystem getLocalOperatingSystem()
	{
		if (localOS == null)
		{
			if (new RegularFile("/etc/passwd").exists())
			{
				if (new Directory("/proc").exists())
				{
					if (new RegularFile("/etc/fedora-release").exists())
					{
						localOS = new FedoraLinux();
					}
					else if (Proces.commandIsAvailable("ubuntu-bug"))
					{
						localOS = new UbuntuLinux();
					}
					else
					{
						localOS = new Linux();
					}
				}
				else if (new Directory("/Applications").exists() && new Directory("/Users").exists())
				{
					localOS = new MacOSX();
				}
				else
				{
					localOS = new Unix();
				}
			}
			else if (System.getProperty("os.name").startsWith("Windows"))
			{
				localOS = new Windows();
			}
			else
			{
				localOS = new OperatingSystem();
			}
		}

		return localOS;
	}

	public String getName()
	{
		return System.getProperty("os.name");
	}

	public String getVersion()
	{
		return System.getProperty("os.version");
	}

	public long getMemoryAvailableInBytes()
	{
		throw new NotYetImplementedException();
	}

	public void open(AbstractFile f)
	{
		throw new NotYetImplementedException();
	}

	public double getLoadAverage()
	{
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}

}
