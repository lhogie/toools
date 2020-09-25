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
 
 package toools.io.stream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import toools.io.Utilities;


/**
 * This abstract subclass of StreamSource handles stream access restrictions,
 * compression and buffering.
 * 
 * @author Luc Hogie
 */

public abstract class AbstractStreamSource implements StreamSource
{
	/**
	 * @return if the document source is compressed.
	 */
	@Override
	public boolean isCompressed()
	{
		String name = getName();

		if (name == null)
		{
			return false;
		}
		else
		{
			return name.endsWith(".gz");
		}
	}

	@Override
	public final InputStream createInputStream() throws IOException
	{
		if (isReadable())
		{
			InputStream is = createInputStreamImpl();
			BufferedInputStream bis = new BufferedInputStream(is);// , 1024);

			if (isCompressed())
			{
				return new GZIPInputStream(bis);
			}
			else
			{
				return bis;
			}
		}
		else
		{
			String className = getClass().getName();
			className = className.substring(className.lastIndexOf('.') + 1);
			throw new IOException(getName() + " is not readable");
		}
	}

	@Override
	public final OutputStream createOutputStream() throws IOException
	{
		if (isWritable())
		{
			OutputStream os = createOutputStreamImpl();
			BufferedOutputStream bos = new BufferedOutputStream(os);

			if (isCompressed())
			{
				return new GZIPOutputStream(bos);
			}
			else
			{
				return bos;
			}
		}
		else
		{
			String className = getClass().getName();
			className = className.substring(className.lastIndexOf('.') + 1);
			throw new IOException(getName() + " is not writable");
		}
	}

	/**
	 * @return the input stream for the document.
	 */
	public abstract InputStream createInputStreamImpl() throws IOException;

	/**
	 * @return the output stream for the document.
	 */
	public abstract OutputStream createOutputStreamImpl() throws IOException;

	@Override
	public byte[] readItAll() throws IOException
	{
		return Utilities.readUntilEOF(createInputStream());
	}

	public void writeItAll(byte[] bytes) throws IOException
	{
		if (bytes == null) throw new NullPointerException("null byte array");

		OutputStream os = createOutputStream();
		os.write(bytes);
		os.flush();
		os.close();
	}

	public List<String> getLines() throws IOException
	{
		return Arrays.asList(new String(readItAll()).split("\\n"));
	}

	/**
	 * Sets the source as a string, relatively to the given source.
	 */
	@Override
	public abstract void setAsText(StreamSource reference, String name);
}
