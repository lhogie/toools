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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class URLStreamSource extends AbstractStreamSource
{
	/* the source url */
	private URL url = null;

	public boolean isReadable()
	{
		return true;
	}

	public boolean isWritable()
	{
		return true;
	}


	public InputStream createInputStreamImpl()
		throws IOException
	{
		return getURL().openConnection().getInputStream();
	}

	public OutputStream createOutputStreamImpl()
		throws IOException
	{
		URLConnection connection = getURL().openConnection();
		connection.setDoOutput( true );
		return connection.getOutputStream();
	}


	public String getName()
	{
		return url.toExternalForm();
	}


	/**
	 * @return the URL for the document.
	 */
	public URL getURL()
	{
		return url;
	}


	/**
	 * Sets the URL for the document.
	 */
	public void setURL( URL url )
	{
		if ( url == null )
			throw new IllegalArgumentException( "url cannot be set to null" );

		this.url = url;
	}


	/**
	 * Sets the filename for this document.
	 */
	public void setAsText( StreamSource refStreamSource, String url )
	{
		boolean isRelative = isRelativeTo( refStreamSource, url );

		if ( isRelative )
		{
			try
			{
				URL refUrl = ((URLStreamSource) refStreamSource).getURL();
				setURL( new URL( refUrl, url ) );
			}
			catch ( MalformedURLException ex )
			{
				throw new IllegalArgumentException( url + " is a malformed URL" );
			}
		}
		else
		{
			setAsText( url );
		}
	}

	private boolean isRelativeTo( StreamSource ref, String url )
	{
		if ( ref == null )
		{
			return false;
		}
		else if ( !(ref instanceof URLStreamSource) )
		{
			return false;
		}
		else
		{
			return true;
		}
	}



	private void setAsText( String name )
	{
		try
		{
			setURL( new URL( name ) );
		}
		catch ( MalformedURLException ex )
		{
			throw new IllegalArgumentException( name + " is a malformed URL" );
		}
	}

	public String getProtocol()
	{
		return getURL().getProtocol();
	}
}
