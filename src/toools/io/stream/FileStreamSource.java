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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileStreamSource extends AbstractStreamSource
{
	/* the source file */
	private File file;

	private FileStreamSource()
	{
/*	    for (int i = 0; i < 1000; ++i)
	    {
		File f = new File( "file_stream_source_" + i + ".bin" );

		if ( !f.exists() )
		{
		    try
		    {
			f.createNewFile();
			setFile( f );
			break;
		    }
		    catch ( IOException ex )
		    {
			// the file could be create, another uniq filename
			// has to be found
		    }
	    	}
	    }
*/	}

	public FileStreamSource( File file )
	{
	    setFile( file );
	}

    @Override
	public String getName()
	{
		return getFile().getPath();
	}

    @Override
	public boolean isReadable()
	{
		return file.exists() && file.canRead();
	}

    @Override
	public boolean isWritable()
	{
		return !file.exists() || file.canWrite();
	}

    @Override
	public InputStream createInputStreamImpl()
		throws IOException
	{
		return new FileInputStream( file );
	}

    @Override
	public OutputStream createOutputStreamImpl()
		throws IOException
	{
		return new FileOutputStream( file );
	}


	/**
	 * @return the file for the document.
	 */
	public File getFile()
	{
		return file;
	}


	/**
	 * Sets the file for the document.
	 */
	public void setFile( File file )
	{
		if ( file == null )
			throw new IllegalArgumentException( "file cannot be set to null" );

		this.file = file;
	}


	/**
	 * Sets the filename for this document.
	 */
    @Override
	public void setAsText( StreamSource refStreamSource, String fileName )
	{
		boolean isRelative = isRelativeTo( refStreamSource, fileName );

		if ( fileName.startsWith( "file://" ) )
		{
			fileName = fileName.substring( 7 );
		}

		if ( isRelative )
		{
			File refFile = ((FileStreamSource) refStreamSource).getFile();
			setFile( new File( refFile.getParent(), fileName ) );
		}
		else
		{
			setFile( new File( fileName ) );
		}
	}

	/**
	 * A file stream source is relative if the reference stream source
	 * exists and is also a file stream source, if there's no protocol
	 * defined (it is implicitely "file://") and if the name of the stream
	 * source is not absolute.
	 */
	private boolean isRelativeTo( StreamSource refStreamSource, String name )
	{
		if ( refStreamSource == null )
		{
			return false;
		}
		else if ( refStreamSource instanceof FileStreamSource )
		{
		    if ( name.startsWith( "file://" ) )
		    {
			return false;
		    }
		    else if ( (new File( name )).isAbsolute() )
		    {
		    	return false;
		    }
		    else
		    {
			return true;
		    }
		}
		else
		{
		    return false;
		}
	}

    @Override
	public String getProtocol()
	{
		return "file";
	}
}
