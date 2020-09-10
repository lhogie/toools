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

package toools.src;

import java.util.Arrays;
import java.util.List;

import toools.io.IORuntimeException;
import toools.io.JavaResource;
import toools.text.TextUtilities;

/**
 * @author luc
 * 

 */
public class Source
{
	private static List<StackTraceElement> createStack()
	{
		List<StackTraceElement> elements = Arrays.asList(new Error().getStackTrace());
		int i = elements.size() - 1;

		while ( ! elements.get(i).getClassName().equals(Source.class.getName()))
		{
			--i;
		}

		return elements.subList(i + 1, elements.size());
	}

	public static String getSourceFileName()
	{
		return createStack().get(0).getFileName();
	}

	public static String getSourceResourceName()
	{
		String fileName = getSourceFileName();
		String packageName = getPackageName();
		return '/' + packageName.replace('.', '/') + '/' + fileName;
	}

	public static String getPackageName()
	{
		String className = getClassName();
		int pos = className.lastIndexOf('.');

		if (pos == - 1)
		{
			return "";
		}
		else
		{
			return className.substring(0, pos);
		}
	}

	public static String getClassName()
	{
		return createStack().get(0).getClassName();
	}

	public static int getLineNumber()
	{
		return createStack().get(0).getLineNumber();
	}

	public static String getSourceLine()
	{
		return getSourceCodeLines().get(getLineNumber() - 1);
	}

	public static List<String> getSubsequentSourceLines(int n)
	{
		int l = getLineNumber();
		return getSourceCodeLines().subList(l, l + n);
	}

	public static List<String> getSourceCodeLines()
	{
		return TextUtilities.splitInLines(getSourceCode());
	}

	public static String getSourceCode()
	{
		JavaResource resource = new JavaResource(getSourceResourceName());

		try
		{
			byte[] bytes = resource.getByteArray();
			return new String(bytes);
		}
		catch (IORuntimeException ex)
		{
			return null;
		}
	}

	public static String getClassSourceCode(Class<?> clazz)
	{
		JavaResource r = getClassSourceCodeAsResource(clazz);

		if (r.exists())
		{
			try
			{
				return new String(r.getByteArray());
			}
			catch (IORuntimeException ex)
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	public static Class<?> getThisClass()
	{
		String className = getClassName();

		try
		{
			return Class.forName(className);
		}
		catch (ClassNotFoundException ex)
		{
			throw new IllegalStateException("class " + className + " cannot be found");
		}
	}

	public static JavaResource getClassBytecodeAsJavaResource(Class<?> thisClass)
	{
		return new JavaResource("/" + thisClass.getName().replace('.', '/') + ".class");
	}

	public static JavaResource getClassSourceCodeAsResource(Class<?> thisClass)
	{
		return new JavaResource("/" + thisClass.getName().replace('.', '/') + ".java");
	}

	public static void main(String[] args)
	{
		System.out.println(createStack());
		System.out.println(getClassName());
	}
}
