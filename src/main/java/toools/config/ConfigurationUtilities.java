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
 
 package toools.config;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import toools.exceptions.ExceptionUtilities;
import toools.io.FileUtilities;
import toools.text.xml.DNode;

public class ConfigurationUtilities
{
	/**
	 * Instantiate a class of the given type that takes a configuration as a
	 * constructor parameter.
	 * 
	 * @param configuration
	 * @param clazz
	 * @return
	 */
	public static Object create(Configuration configuration, Class<?> clazz)
	{
		try
		{
			return clazz.getConstructor(configuration.getClass()).newInstance(configuration);
		}
		catch (Throwable e)
		{
			throw new IllegalStateException("cannot instantiate class " + clazz.getName() + " with argument " + configuration.getClass() + ExceptionUtilities.toString(e));
		}
	}

	public static Object createWithRandom(Configuration configuration, Random random, Class<?> clazz)
	{
		try
		{
			return clazz.getConstructor(Configuration.class, Random.class).newInstance(configuration, random);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new IllegalStateException("instantiation has failed for class: " + clazz.getName());
	}

	public static String xmlToConfiguration(String xml) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException
	{

		DNode root = toools.text.xml.XML.parseXML(xml, false);

		if (!root.getName().equals("configuration")) throw new IllegalArgumentException("root element shoudl be <configuration>");

		StringBuilder buf = new StringBuilder();

		for (DNode node : root.getChildren())
		{
			if (!node.getName().equals("#text"))
			{
				buf.append(xml(node));
			}
		}

		return buf.toString();

	}

	private static String xml(DNode node)
	{
		StringBuilder buf = new StringBuilder();
		buf.append("section " + node.getName() + "\n");

		for (String k : node.getAttributes().keySet())
		{
			buf.append(k + " = {" + node.getAttributes().get(k) + "}\n");
		}

		for (DNode n : node.getChildren())
		{
			buf.append(xml(n));
		}

		buf.append("end of section\n");
		return buf.toString();
	}

	public static void main(String... args) throws IOException, ParserConfigurationException, SAXException
	{
		System.out.println(FileUtilities.getCurrentDirectory());
		String xml = new String(FileUtilities.getFileContent(new File("src/lucci/config/example.xml")));
		System.out.println(xmlToConfiguration(xml));
	}
}
