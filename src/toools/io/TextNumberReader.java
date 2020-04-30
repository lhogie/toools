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

package toools.io;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import toools.io.block.BlockReader;
import toools.io.block.SimpleBlockReader;

public class TextNumberReader extends NumberReader
{
	private int indexInBlock = 0;
	private boolean nextIsDefined = false;
	private long next;
	private boolean eof = false;
	private byte c;
	private StringBuilder comment;
	private byte commentMarker = '#';

	public TextNumberReader(InputStream is, int bufSize) throws IOException
	{
		this(new SimpleBlockReader(is, bufSize));
	}

	public TextNumberReader(BlockReader blockReader) throws IOException
	{
		super(blockReader);
		tryToScanNext();
	}

	public boolean hasNext() throws IOException
	{
		if (nextIsDefined)
		{
			return true;
		}
		else
		{
			if (eof)
			{
				return false;
			}
			else
			{
				tryToScanNext();
				return nextIsDefined;
			}
		}
	}

	@Override
	public int nextInt() throws IOException
	{
		return (int) nextLong();
	}

	public byte getCommentMarker()
	{
		return commentMarker;
	}

	public void setCommentMarker(byte commentMarker)
	{
		this.commentMarker = commentMarker;
	}

	@Override
	public boolean nextBoolean() throws IOException
	{
		return nextLong() != 0;
	}

	@Override
	public long nextLong() throws IOException
	{
		if (nextIsDefined)
		{
			nextIsDefined = false;
			return next;
		}
		else
		{
			if (eof)
			{
				throw new EOFException();
			}
			else
			{
				tryToScanNext();

				if (nextIsDefined)
				{
					nextIsDefined = false;
					return next;
				}
				else
				{
					throw new EOFException();
				}
			}
		}
	}

	private void tryToScanNext() throws IOException
	{
		if (eof)
			throw new EOFException();

		nextIsDefined = false;
		next = 0;

		while (true)
		{
			// if there's no data available
			while (currentBlock == null || indexInBlock == currentBlock.size)
			{
				// read some, if any
				currentBlock = reader.getNextBlock();

				// that's the end of the stream
				if (currentBlock.size == - 1)
				{
					eof = true;
					return;
				}

				indexInBlock = 0;
			}

			// read the next byte available
			c = currentBlock.buf[indexInBlock++];

			// if (c == '\n')
			{

			}
			// if we were in a comment
			if (comment != null)
			{
				// comments end at the end the line
				if (c == '\n')
				{
					System.out.println("Dataset comment: # " + comment);
					comment = null;
				}
				else
				{
					comment.append((char) c);
				}
			}
			else
			{
				// if the character is a digit
				if ('0' <= c && c <= '9')
				{
					next = next * 10 + c - '0';
					nextIsDefined = true;
				}
				// else if its a comment starting
				else if (c == commentMarker)
				{
					comment = new StringBuilder();
				}
				// else if its a space
				else if (c == ' ' || c == '\t' || c == '\n' || c == '\r')
				{
					// a number was already parsed, this is its end
					if (nextIsDefined)
						return;
				}
				// invalid character
				else
				{
					throw new IOException("invalid character: " + (char) c);
				}
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		String s = "45 564  35 34\n  453 453 345 2886165809 2886165589";
		TextNumberReader sc = new TextNumberReader(
				new SimpleBlockReader(new ByteArrayInputStream(s.getBytes()), 65000));

		while (sc.hasNext())
		{
			System.out.println(sc.nextLong() + "\t" + sc.eof);
		}
	}

}
