/////////////////////////////////////////////////////////////////////////////////////////
// 
//                 Université de Nice Sophia-Antipolis  (UNS) - 
//                 Centre National de la Recherche Scientifique (CNRS)
//                 Copyright © 2015 UNS, CNRS All Rights Reserved.
// 
//     These computer program listings and specifications, herein, are
//     the property of Université de Nice Sophia-Antipolis and CNRS
//     shall not be reproduced or copied or used in whole or in part as
//     the basis for manufacture or sale of items without written permission.
//     For a license agreement, please contact:
//     <mailto: licensing@sattse.com> 
//
//
//
//     Author: Luc Hogie – Laboratoire I3S - luc.hogie@unice.fr
//
//////////////////////////////////////////////////////////////////////////////////////////

package toools.io;

import java.io.IOException;
import java.io.InputStream;

import toools.io.block.BlockReader;
import toools.io.block.SimpleBlockReader;

public class BinaryReader extends NumberReader
{
	private int posInCurrentBlock = 0;
	private final byte[] buf = new byte[8];

	public BinaryReader(InputStream is, int bufSize) 
	{
		this(new SimpleBlockReader(is, bufSize));
	}

	public BinaryReader(BlockReader br) 
	{
		super(br);
	}

	private byte[] where(int n)
	{
		// we have enough material in the block
		if (currentBlock != null && posInCurrentBlock < currentBlock.size - n)
		{
			return currentBlock.buf;
		}
		else
		{
			for (int posInBuf = 0; posInBuf < n; ++posInBuf)
			{
				if (currentBlock == null || posInCurrentBlock == currentBlock.size)
				{
					currentBlock = reader.getNextBlock();

					if (currentBlock.size == - 1)
					{
						throw new IORuntimeException("EOF");
					}

					posInCurrentBlock = 0;
				}

				buf[posInBuf] = currentBlock.buf[posInCurrentBlock++];
			}

			return buf;
		}
	}

	public long next(int nbBytes)
	{
		if (where(nbBytes) == currentBlock.buf)
		{
			long r = Bits.getLong(currentBlock.buf, posInCurrentBlock, nbBytes);
			posInCurrentBlock += nbBytes;
			return r;
		}
		else
		{
			return Bits.getLong(buf, 0, nbBytes);
		}
	}

	@Override
	public long nextLong() throws IOException
	{
		return next(8);
	}

	@Override
	public int nextInt() throws IOException
	{
		return (int) next(4);
	}

	@Override
	public boolean nextBoolean() throws IOException
	{
		return next(1) != 0;
	}
}
