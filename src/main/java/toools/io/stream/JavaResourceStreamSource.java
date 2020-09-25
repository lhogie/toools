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

import toools.io.JavaResource;

public class JavaResourceStreamSource extends AbstractStreamSource
{
	private JavaResource resource = null;

	public String getName()
	{
		return getJavaResource().getPath();
	}

	public boolean isReadable()
	{
		InputStream is = resource.getInputStream();

		if ( is == null )
		{
			return false;
		}
		else
		{
			try
			{
				is.close();
			}
			catch ( IOException ex )
			{
			}

			return true;
		}
	}

	public boolean isWritable()
	{
		return false;
	}

	public InputStream createInputStreamImpl()
		throws IOException
	{
		return resource.getInputStream();
	}

	public OutputStream createOutputStreamImpl()
		throws IOException
	{
		throw new IOException( "resource " + resource.getPath() + " cannot be written" );
	}

	/**
	 * @return the resource.
	 */
	public JavaResource getJavaResource()
	{
		return resource;
	}


	/**
	 * Sets the resource.
	 */
	public void setJavaResource( JavaResource resource )
	{
		if ( resource == null )
			throw new IllegalArgumentException( "java resource cannot be set to null" );

		this.resource = resource;
	}


	/**
	 * Sets the resource name for this document.
	 * resource:///ref
	 *
	 *
	 */
	public void setAsText( StreamSource refStreamSource, String name )
	{
		boolean isRelative = isRelativeTo( refStreamSource, name );

		if ( name.startsWith( "resource://" ) )
		{
			name = name.substring( 11 );
		}

		if ( isRelative )
		{
			JavaResource ref = ((JavaResourceStreamSource) refStreamSource).getJavaResource();
			String refName = ref.getPath();
			int pos = refName.lastIndexOf( '/' );

			if ( pos == -1 )
			{
				setJavaResource( new JavaResource( name ) );
			}
			else
			{
				setJavaResource( new JavaResource( refName.substring( 0, pos + 1 ) + name ) );
			}
		}
		else
		{
			setJavaResource( new JavaResource( name ) );
		}
	}

	private boolean isRelativeTo( StreamSource ref, String name )
	{
		if ( ref == null )
		{
			return false;
		}
		else if ( !(ref instanceof JavaResourceStreamSource) )
		{
			return false;
		}
		else if ( name.startsWith( "resource://" ) )
		{
			return false;
		}
		else if ( name.startsWith( "/" ) )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public String getProtocol()
	{
		return "resource";
	}
}
