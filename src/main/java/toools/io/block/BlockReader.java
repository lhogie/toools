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

package toools.io.block;

import java.io.IOException;
import java.io.InputStream;

import toools.io.IORuntimeException;

public abstract class BlockReader
{
	final private InputStream is;
	final private int blockSize;
	private long nbBytesRead = 0;

	public BlockReader(InputStream is, int blockSize)
	{
		this.is = is;
		this.blockSize = blockSize;
	}

	public long getNbBytesRead()
	{
		return nbBytesRead;
	}

	public abstract DataBlock getNextBlock();

	protected final DataBlock readBlock()
	{
		try
		{
			DataBlock b = new DataBlock(blockSize);
			b.size = is.read(b.buf);
			nbBytesRead += b.size;
			return b;
		}
		catch (IOException e)
		{
			throw new IORuntimeException(e);
		}
	}
}
