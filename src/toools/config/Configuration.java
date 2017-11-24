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

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import toools.collections.Collections;
import toools.collections.relation.HashRelation;
import toools.collections.relation.RelationIO;
import toools.io.file.RegularFile;
import toools.math.Interval;

/*
 * Created on Jun 4, 2005
 */

/**
 * @author luc.hogie
 */
public class Configuration
{
    private HashRelation<String, String> rel = new HashRelation();
    private static final long serialVersionUID = 1L;
    private Collection<String> readKeys = new ArrayList<String>();
    private boolean imposeKey = true;

    public boolean isImposeKey()
    {
	return imposeKey;
    }

    public boolean isDefined(String key)
    {
	return getKeys().contains(key);
    }

    public void setImposeKey(boolean imposeKey)
    {
	this.imposeKey = imposeKey;
    }

    public final Collection<String> createContainer()
    {
	return new ArrayList<String>();
    }

    public long getID()
    {
	long id = hashCode();
	id += (long) Integer.MAX_VALUE;
	return id;
    }

    public Collection<String> getUnredKeys()
    {
	return Collections.difference(rel.getKeys(), this.readKeys);
    }

    public String getFirstString(String key) throws ConfigurationKeyException
    {
	List<String> values = getStrings(key);
	return values.isEmpty() ? null : values.get(0);
    }

    public List<String> getStrings(String key) throws ConfigurationKeyException
    {
	Collection<String> v = rel.getValues(key);

	if (v == null)
	{
	    if (imposeKey)
	    {
		throw new ConfigurationKeyException(key, "configuration key " + key + " cannot be found");
	    }
	    else
	    {
		return new ArrayList<String>();
	    }
	}
	else
	{
	    readKeys.add(key);
	    return (List<String>) v;
	}
    }

    public Class<?> getClass(String key) throws ConfigurationKeyException
    {
	String className = getFirstString(key);

	if (className.equals("none"))
	{
	    return null;
	}
	else
	{
	    try
	    {
		return Class.forName(className);
	    }
	    catch (ClassNotFoundException e)
	    {
		throw new ConfigurationKeyException("class not found: " + key, e.getClass().getName() + ": "
			+ e.getMessage());
	    }
	    catch (NoClassDefFoundError e)
	    {
		throw new ConfigurationKeyException("class not found: " + key, e.getClass().getName() + ": "
			+ e.getMessage());
	    }
	}
    }

    public Color getColor(String key) throws ConfigurationKeyException
    {
	List<String> colorValues = getStrings(key);

	if (colorValues.size() == 1)
	{
	    String colorName = colorValues.get(0);

	    try
	    {
		Field field = Color.class.getField(colorName);
		return (Color) field.get(null);
	    }
	    catch (Throwable t)
	    {
		throw new ConfigurationKeyException(key, "cannot find color java.awt.Color" + colorName);
	    }
	}
	else if (colorValues.size() == 3)
	{
	    int r = (int) Double.valueOf(colorValues.get(0)).doubleValue();
	    int g = (int) Double.valueOf(colorValues.get(1)).doubleValue();
	    int b = (int) Double.valueOf(colorValues.get(2)).doubleValue();
	    return new Color(r, g, b);
	}
	else
	{
	    throw new ConfigurationKeyException(key,
		    "a color should be defined either by its name of by its RGB values");
	}
    }

    public double getDouble(String key) throws ConfigurationKeyException
    {
	String value = getFirstString(key);

	try
	{
	    return Double.valueOf(value);
	}
	catch (NumberFormatException ex)
	{
	    throw new ConfigurationKeyException(key, "'" + value + "' does not look like a decimal number");
	}
    }

    public int getInteger(String key) throws ConfigurationKeyException
    {
	String value = getFirstString(key);

	try
	{
	    return Integer.valueOf(value);
	}
	catch (NumberFormatException ex)
	{
	    throw new ConfigurationKeyException(key, "'" + value + "' does not look like a integer number");
	}
    }

    public boolean getBoolean(String key) throws ConfigurationKeyException
    {
	String value = getFirstString(key);

	if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
	{
	    return true;
	}
	else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))
	{
	    return false;
	}
	else
	{
	    throw new ConfigurationKeyException(key, "'" + value + "' does not look like a boolean object");
	}
    }

    public Interval getInterval(String key) throws ConfigurationKeyException
    {
	return Interval.valueOf(getFirstString(key));
    }

    public static Configuration readFromFile(RegularFile... file) throws IOException
    {
	Configuration config = new Configuration();
	RelationIO.load(config.rel, file);
	return config;
    }

    public static Configuration readFromFile(File... file) throws IOException
    {
	Configuration config = new Configuration();

	for (File f : file)
	{
	    RelationIO.load(config.rel, new RegularFile(f.getAbsolutePath()));
	}

	return config;
    }

    public static Configuration readFromStream(InputStream is) throws IOException
    {
	Configuration config = new Configuration();
	RelationIO.load(config.rel, new InputStreamReader(is));
	return config;
    }

    public void saveToFile(File file) throws IOException
    {
	RelationIO.save(this.rel, new FileOutputStream(file));
    }

    public void saveToFile(RegularFile file) throws IOException
    {
	RelationIO.save(this.rel, file.createWritingStream());
    }

    public Object getInstantiatedClass(String key) throws ConfigurationKeyException
    {
	if (getStrings(key).size() == 1)
	{
	    List<?> objects = getInstantiatedClasses(key);

	    if (objects.size() == 1)
	    {
		return objects.get(0);
	    }
	    else
	    {
		return null;
	    }
	}
	else
	{
	    throw new ConfigurationKeyException(key, "only one class name is allowed");
	}
    }

    public Collection<String> getKeysMatching(String prefix)
    {
	Collection<String> c = new HashSet<String>();

	for (String k : rel.getKeys())
	{
	    if (k.matches(prefix))
	    {
		c.add(k);
	    }
	}

	return c;
    }

    public Collection<String> getRootBlocs()
    {
	return getKeysInBloc(null);
    }

    public Collection<String> getKeysInBloc(String bloc)
    {
	Collection<String> c = new HashSet<String>();

	for (String k : rel.getKeys())
	{
	    if (k.startsWith((bloc == null ? "" : bloc + ".")))
	    {
		k = k.substring(bloc.length(), k.length());

		if (!k.contains("."))
		{
		    c.add(k);
		}
	    }
	}

	return c;
    }

    public static List<String> getBlocks(String key)
    {
	return Arrays.asList(key.split(" *. *"));
    }

    public static String getParentBlock(String key)
    {
	int pos = key.lastIndexOf(".");
	return pos == -1 ? null : key.substring(0, pos);
    }

    public Collection<String> getSibblingKeys(String key)
    {
	Collection<String> children = getKeysInBloc(getParentBlock(key));
	children.remove(key);
	return children;
    }

    public Object getInstantiatedClasseWithConfiguration(String classToInstantiate) throws ConfigurationKeyException
    {
	return ConfigurationUtilities.create(this, getClass(classToInstantiate));
    }

    public List<?> getInstantiatedClasses(String key) throws ConfigurationKeyException
    {
	Collection<String> classNames = getStrings(key);
	List<Object> objects = new ArrayList<Object>();

	for (String className : classNames)
	{
	    if (!className.equals("none"))
	    {
		try
		{
		    Class clazz = Class.forName(className);
		    objects.add(clazz.newInstance());
		}
		catch (ClassNotFoundException ex)
		{
		    throw new ConfigurationKeyException(key, "Cannot find class '" + className + "'");
		}
		catch (InstantiationException ex)
		{
		    throw new ConfigurationKeyException(key, "Error while instantiating class '" + className + "'");
		}
		catch (IllegalAccessException ex)
		{
		    throw new ConfigurationKeyException(key, "No access to class '" + className + "'");
		}
	    }
	}

	return objects;
    }

    public URL getURL(String key) throws ConfigurationException
    {
	String url = getFirstString(key);

	try
	{
	    return url == null ? null : new URL(url);
	}
	catch (MalformedURLException e)
	{
	    throw new ConfigurationException("malformed url: " + key, e);
	}
    }

    public static void main(String[] args) throws IOException
    {
	Configuration c = Configuration.readFromFile(new RegularFile("src/lucci/config/example.config"));
	System.out.println(c.getValue("a"));
	// c.getCl(key)
    }

    public Collection<String> getValues(String k)
    {
	return rel.getValues(k);
    }

    public String getValue(String k)
    {
	return rel.getValue(k);
    }

    public Collection<String> getKeys()
    {
	return rel.getKeys();
    }

    public void remove(String key)
    {
	rel.remove(key);

    }

    public void add(String key, String value)
    {
	rel.add(key, value);
    }

    public void addAll(String key, Collection<String> values)
    {
	for (String v : values)
	{
	    add(key, v);
	}
    }

    public void addAll(Configuration c)
    {
	for (String k : c.getKeys())
	{
	    addAll(k, c.getValues(k));
	}
    }

}
