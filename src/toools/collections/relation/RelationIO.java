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
 
 package toools.collections.relation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

import toools.io.FileUtilities;
import toools.io.file.RegularFile;
import toools.text.TextUtilities;


public class RelationIO
{

    public static Relation<String, String> load(String text) throws IOException
    {
	StringReader reader = new StringReader(text);
	Relation<String, String> relation = load(reader);
	reader.close();
	return relation;
    }

    public static Relation<String, String> load(Reader reader) throws IOException
    {
	Relation<String, String> relation = new HashRelation<String, String>();
	load(relation, reader);
	return relation;
    }

    public static void load(Relation<String, String> relation, Reader is) throws IOException
    {
	if (relation == null)
	    throw new NullPointerException();

	if (is == null)
	    throw new NullPointerException();

	BufferedReader bufferedReader = new BufferedReader(is);
	Stack<String> sectionStack = new Stack<String>();

	for (int lineNumber = 0;; ++lineNumber)
	{
	    String line = bufferedReader.readLine();

	    if (line == null)
	    {
		break;
	    }
	    else
	    {
		line = line.trim();

		if (line.isEmpty())
		{
		    // empty line, do nothing
		}
		else if (line.startsWith("#"))
		{
		    // this is a comment, simply ignore
		}
		else if (line.startsWith("section"))
		{
		    sectionStack.push(line.substring("section".length()).trim());
		}
		else if (line.endsWith("{"))
		{
		    sectionStack.push(line.substring(0, line.length() - 1).trim());
		}
		else if (line.equals("end of section"))
		{
		    sectionStack.pop();
		}
		else if (line.equals("}"))
		{
		    sectionStack.pop();
		}
		else
		{
		    int equalOperatorPosition = line.indexOf('=');

		    // if there is no "=" in the line
		    if (equalOperatorPosition == -1)
		    {
			throw new IllegalArgumentException("line " + lineNumber + " does not match '.*=.*'" + ": \""
				+ line + "\"");
		    }
		    else
		    {
			String key = line.substring(0, equalOperatorPosition).trim();

			// if the key belongs to a section
			if (!sectionStack.isEmpty())
			{
			    // build the complete key
			    key = TextUtilities.concatene(sectionStack, ".") + "." + key;
			}

			if (relation.isDefined(key))
			{
			    relation.remove(key);
			    System.err.println("Warning: key \"" + key + "\" is already defined");
			}

			String valueSetAsText = line.substring(equalOperatorPosition + 1).trim();

			// if the value is surrounded by {}, remove them
			if (valueSetAsText.charAt(0) == '{'
				&& valueSetAsText.charAt(valueSetAsText.length() - 1) == '}')
			{
			    valueSetAsText = valueSetAsText.substring(1, valueSetAsText.length() - 1);
			}

			// if we are in the "a={}" case
			if (valueSetAsText.trim().isEmpty())
			{
			    relation.add(key);
			}
			else
			{
			    // get the values in separated fields
			    for (String v : Arrays.asList(valueSetAsText.split(" *, *")))
			    {
				if (v.startsWith("$"))
				{
				    relation.addAll(key, relation.getValues(v.substring(1)));
				}
				else
				{
				    relation.add(key, v);
				}
			    }
			}

		    }
		}
	    }
	}
    }

    public static <A, B> void save(Relation<A, B> relation, OutputStream os) throws IOException
    {
	if (os == null)
	    throw new NullPointerException("null output stream");

	PrintStream ps = new PrintStream(os);

	for (A key : relation.getKeys())
	{
	    ps.print(key);
	    ps.print("={");
	    Iterator<B> iterator = relation.getValues(key).iterator();

	    while (iterator.hasNext())
	    {
		B next = iterator.next();
		String s = next == null ? "null" : next.toString();
		ps.print(s);

		if (iterator.hasNext())
		{
		    ps.print(", ");
		}
	    }

	    ps.println('}');
	}
    }

    public static void load(Relation<String, String> relation, File... file) throws IOException
    {
	load(relation, FileUtilities.convertFilesToRegularFiles(file));
    }
    
    public static void load(Relation<String, String> relation, RegularFile... file) throws IOException
    {
	if (relation == null)
	    throw new NullPointerException("null relation");

	if (file == null)
	    throw new NullPointerException("null file");

	for (RegularFile f : file)
	{
	    InputStream is = f.createReadingStream();
	    RelationIO.load(relation, new InputStreamReader(is));
	    is.close();
	}
    }

    public static void main(String... args) throws IOException
    {
	System.out.println(load("a={a,b}\nb={$a}"));
    }
}
