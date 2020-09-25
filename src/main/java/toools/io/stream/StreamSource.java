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


public interface StreamSource
{
	/**
	 * @return the name of the stream source. If it's based on a
	 * file, the name of the file is returned, if it is based on
	 * a Java resource, the name of this resource is returned.
	 * The name may be null if the stream source is not based on
	 * a named object, for example a string buffer has no name.
	 */
	String getName();

	/**
	 * @return if the data source is compressed.
	 */
	boolean isCompressed();

	/**
	 * @return if the resource is readable.
	 */
	boolean isReadable();

	/**
	 * @return if the resource is writable.
	 */
	boolean isWritable();

	/**
	 * @return the input stream for the document.
	 */
	InputStream createInputStream()
		throws IOException;

	/**
	 * @return the output stream for the document.
	 */
	OutputStream createOutputStream()
		throws IOException;

	/**
	 * @return a byte array of the data contained in the source.
	 * This is a convenience method.
	 */
	byte[] readItAll() throws IOException;

	/**
	 * Sets the data into the source from the given byte array.
	 * This is a convenience method.
	 */
	void writeItAll(byte[] bytes) throws IOException;

	/**
	 * Sets the resource as a string, relatively to the given resource.
	 */
	void setAsText( StreamSource reference, String name );

	/**
	 * @return the protocol for the data source.
	 */
	String getProtocol();
}
