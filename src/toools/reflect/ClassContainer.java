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

import java.net.URL;
import java.net.URLClassLoader;

import toools.io.JavaResource;
import toools.io.file.AbstractFile;
import toools.thread.Generator;

/**
 * This represents either a ZIP/JAR file or a directory
 * 
 * @author lhogie
 * 
 */

public class ClassContainer
{
	private final AbstractFile file;
	private final ClassLoader classLoader;

	public ClassContainer(AbstractFile file)
	{
		this(file, URLClassLoader.newInstance(new URL[] { file.toURL() }));
	}

	public ClassContainer(AbstractFile f, ClassLoader classLoader)
	{
		if (f == null)
			throw new NullPointerException();

		this.file = f;
		this.classLoader = classLoader;
	}

	public Generator<Class<?>> listAllClasses()
	{
		return new Generator<Class<?>>()
		{

			@Override
			public void produce()
			{
				Generator<JavaResource> resources = JavaResource
						.listResources(ClassContainer.this);

				for (JavaResource thisResource : resources)
				{
					String resname = thisResource.getPath();

					// if the resource is a class entry
					if (resname.endsWith(".class"))
					{
						// removes the .class extension
						String classname = resname.substring(0, resname.length() - 6);

						// remove the first /
						if (classname.startsWith("/"))
						{
							classname = classname.substring(1);
						}

						// convert separators from / to .
						classname = classname.replace('/', '.');

						try
						{
							Class<?> foundClass = classLoader.loadClass(classname);
							deliver(foundClass);
						}
						catch (ClassNotFoundException e)
						{
							throw new NoClassDefFoundError(e.getMessage());
						}
					}
				}
			}
		};
	}

	@Override
	public String toString()
	{
		return file.getPath();
	}

	public AbstractFile getFile()
	{
		return file;
	}

}
