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
import java.io.StringBufferInputStream;

public class StringBufferStreamSource extends AbstractStreamSource
{
	/* the source buffer */
	private StringBuffer buffer = null;

	public StringBufferStreamSource()
	{
	    this( new StringBuffer() );
	}

	public StringBufferStreamSource( StringBuffer buffer )
	{
	    setStringBuffer( buffer );
	}


	public boolean isReadable()
	{
		return true;
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
		return false;
	}

	public InputStream createInputStreamImpl()
		throws IOException
	{
		return new StringBufferInputStream( getStringBuffer().toString() );
	}

	public OutputStream createOutputStreamImpl()
		throws IOException
	{
	        buffer.setLength( 0 );
		return new StringBufferOutputStream( buffer );
	}

	public byte[] readItAll()
		throws IOException
	{
		return getStringBuffer().toString().getBytes();
	}



	public void setAsText( StreamSource ref, String s )
	{
		setStringBuffer( new StringBuffer( s ) );
	}

	/**
	 * @return the string buffer for the document.
	 */
	public StringBuffer getStringBuffer()
	{
		return buffer;
	}


	/**
	 * Sets the buffer for the document.
	 */
	public void setStringBuffer( StringBuffer buffer )
	{
		if ( buffer == null )
			throw new IllegalArgumentException( "string buffer cannot be set to null" );

		this.buffer = buffer;
	}

	private class StringBufferOutputStream extends OutputStream
	{
		private StringBuffer buffer;

		public StringBufferOutputStream( StringBuffer buffer )
		{
			this.buffer = buffer;
		}

		public void write( int c )
			throws IOException
		{
			buffer.append( (char) c );
		}
	}

	public String getProtocol()
	{
		return null;
	}
}
