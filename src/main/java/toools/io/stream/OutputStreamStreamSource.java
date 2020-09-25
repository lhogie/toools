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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OutputStreamStreamSource extends AbstractStreamSource
{
	private OutputStream outputStream;
	private boolean isCompressed = false;

	public OutputStreamStreamSource( OutputStream outputStream )
	{
	    setOutputStream( outputStream );
	}

	public boolean isReadable()
	{
		return false;
	}

	public boolean isWritable()
	{
		return true;
	}

	public String getName()
	{
		return null;
	}

	public boolean isCompressed()
	{
		return isCompressed;
	}

	public void setCompressed( boolean compressed )
	{
	    this.isCompressed = compressed;
	}

	public InputStream createInputStreamImpl()
		throws IOException
	{
		throw new IllegalStateException();
	}

	public OutputStream createOutputStreamImpl()
		throws IOException
	{
		return outputStream;
	}

	public void setAsText( StreamSource ref, String s )
	{
		throw new IllegalStateException( "operation has no meaning" );
	}

	/**
	 * @return the output stream.
	 */
	public OutputStream getOutputStream()
	{
		return outputStream;
	}


	/**
	 * Sets the output stream.
	 */
	public void setOutputStream( OutputStream outputStream )
	{
		if ( outputStream == null )
			throw new IllegalArgumentException( "outputStream cannot be set to null" );

		this.outputStream = outputStream;
	}

	public String getProtocol()
	{
		return null;
	}
}