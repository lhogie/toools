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
import toools.io.block.DataBlock;
import toools.io.block.SimpleBlockReader;

public abstract class NumberReader
{
	protected final BlockReader reader;
	protected DataBlock currentBlock;

	public NumberReader(InputStream is, int bufSize) throws IOException
	{
		this(new SimpleBlockReader(is, bufSize));
	}

	public NumberReader(BlockReader br) 
	{
		this.reader = br;
	}

	public long getNbByteRead()
	{
		return reader.getNbBytesRead();
	}
	
	public abstract long nextLong() throws IOException;

	public abstract int nextInt() throws IOException;

	public abstract boolean nextBoolean() throws IOException;
}
