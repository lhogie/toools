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
 
 package toools.math;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import toools.exceptions.CodeShouldNotHaveBeenReachedException;
import toools.text.TextUtilities;
import toools.util.assertion.Assertions;



public class BitMatrix implements Cloneable, Serializable
{
    private static final long serialVersionUID = -5968830269646057153L;

    public static final class Area
    {
        public int fromI, fromJ, rowCount, columnCount;

        public Area(int a, int b, int c, int d)
        {
            this.fromI = a;
            this.fromJ = b;
            this.rowCount = c;
            this.columnCount = d;
        }
    }

    public enum BIT_OPERATION {OR, AND, XOR};

    
    
    private byte[][] bytes;
    private int rowCount, columnCount;

    public BitMatrix(int rowCount, int columnCount)
    {
        this.bytes = new byte[rowCount][columnCount / 8 + 1];
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }

    public void clear()
    {
        for (byte[] row : bytes)
        {
            Arrays.fill(row, (byte) 0);
        }
    }

    public boolean get(int i, int j)
    {
        if (!(0 <= i && i < this.rowCount))
            throw new ArrayIndexOutOfBoundsException(i);

        if (!(0 <= j && j < this.columnCount))
            throw new ArrayIndexOutOfBoundsException(j);

        byte b = this.bytes[i][j >> 3];
        return (b & (1 << (j & 7))) > 0;
    }

    public void set(int i, int j, boolean value)
    {
        if (!(0 <= i && i < this.rowCount))
            throw new ArrayIndexOutOfBoundsException(i);

        if (!(0 <= j && j < this.columnCount))
            throw new ArrayIndexOutOfBoundsException(j);

        byte b = this.bytes[i][j >> 3];

        byte mask = (byte) (1 << (j & 7));

        if (value)
        {
            // switch on the cell
            b |= mask;
        }
        else
        {
            // switch off the cell
            b &= ~mask;
        }

        this.bytes[i][j >> 3] = b;
    }

    public int getRowCount()
    {
        return rowCount;
    }

    public int getColumnCount()
    {
        return this.columnCount;
    }

    public void set(Area area, boolean value)
    {
        for (int i = area.fromI; i < area.fromI + area.rowCount; ++i)
        {
            for (int j = area.fromJ; j < area.fromJ + area.columnCount; ++j)
            {
                set(i, j, value);
            }
        }
    }

    public boolean sameSize(BitMatrix otherMatrix)
    {
        return getRowCount() == otherMatrix.getRowCount() && getColumnCount() == otherMatrix.getColumnCount();
    }

    public int findNextONCellOnLine(int line, int fromColumn)
    {
        Assertions.ensure(fromColumn >= 0);

        for (int thisColumn = fromColumn; thisColumn < getColumnCount(); ++thisColumn)
        {
            if (get(line, thisColumn))
            {
                return thisColumn;
            }
        }

        return -1;
    }

    public int findNextONCellOnColumn(int column, int fromLine)
    {
        for (int thisLine = fromLine; thisLine < getColumnCount(); ++thisLine)
        {
            if (get(column, thisLine))
            {
                return thisLine;
            }
        }

        return -1;
    }

    public boolean equals(BitMatrix otherMatrix)
    {
        if (sameSize(otherMatrix))
        {
            for (int i = 0; i < getRowCount(); ++i)
            {
                for (int j = 0; j < getColumnCount(); ++j)
                {
                    if (get(i, j) != otherMatrix.get(i, j))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public BitMatrix transpose()
    {
        BitMatrix result = new BitMatrix(getColumnCount(), getRowCount());

        for (int i = 0; i < getRowCount(); ++i)
        {
            for (int j = 0; j < getColumnCount(); ++j)
            {
                result.set(j, i, get(i, j));
            }
        }

        return result;
    }

    public void not()
    {
        not(new Area(0, 0, getRowCount(), getColumnCount()));
    }

    public void not(Area area)
    {
        for (int i = area.fromI; i < area.fromI + area.rowCount; ++i)
        {
            for (int j = area.fromJ; j < area.fromJ + area.columnCount; ++j)
            {
                set(i, j, !get(i, j));
            }
        }
    }

    public void and(BitMatrix otherMatrix)
    {
        performBitOperation(otherMatrix, BIT_OPERATION.AND, new Area(0, 0, getRowCount(), getColumnCount()));
    }

    public void or(BitMatrix otherMatrix)
    {
        performBitOperation(otherMatrix, BIT_OPERATION.OR, new Area(0, 0, getRowCount(), getColumnCount()));
    }

    public void xor(BitMatrix otherMatrix)
    {
        performBitOperation(otherMatrix, BIT_OPERATION.XOR, new Area(0, 0, getRowCount(), getColumnCount()));
    }

    public void performBitOperation(BitMatrix otherMatrix, BIT_OPERATION operation, Area area)
    {
        if (sameSize(otherMatrix))
        {
            for (int i = area.fromI; i < area.fromI + area.rowCount; ++i)
            {
                for (int j = area.fromJ; j < area.fromJ + area.columnCount; ++j)
                {
                    if (operation == BIT_OPERATION.AND)
                    {
                        set(i, j, get(i, j) && otherMatrix.get(i, j));
                    }
                    else if (operation == BIT_OPERATION.OR)
                    {
                        set(i, j, get(i, j) || otherMatrix.get(i, j));
                    }
                    else if (operation == BIT_OPERATION.XOR)
                    {
                        set(i, j, get(i, j) ^ otherMatrix.get(i, j));
                    }
                    else
                    {
                        throw new CodeShouldNotHaveBeenReachedException();
                    }
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("matrices do not have the same size");
        }
    }

    public BitMatrix get(Area area)
    {
        BitMatrix result = new BitMatrix(area.rowCount, area.columnCount);

        for (int i = area.fromI; i < area.fromI + area.rowCount; ++i)
        {
            for (int j = area.fromJ; j < area.fromJ + area.columnCount; ++j)
            {
                result.set(i - area.fromI, j - area.fromJ, get(i, j));
            }
        }

        return result;
    }

    public Object clone()
    {
        try
        {
            Constructor constr = getClass().getConstructor(Integer.class, Integer.class);
            BitMatrix clone = (BitMatrix) constr.newInstance(getRowCount(), getColumnCount());

            for (int i = 0; i < getRowCount(); ++i)
            {
                for (int j = 0; j < getColumnCount(); ++j)
                {
                    clone.set(i, j, get(i, j));
                }
            }

            return clone;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public void randomize()
    {
        randomize(new Area(0, 0, getRowCount(), getColumnCount()), 0.5);
    }

    public void randomize(Area area, double probabilityThatOneGivenCellIsOn)
    {
        for (int i = area.fromI; i < area.fromI + area.rowCount; ++i)
        {
            for (int j = area.fromJ; j < area.fromJ + area.columnCount; ++j)
            {
                set(i, j, Math.random() < probabilityThatOneGivenCellIsOn);
            }
        }
    }

    public int computeCardinality()
    {
        int cardinality = 0;

        for (int i = 0; i < getRowCount(); ++i)
        {
            for (int j = 0; j < getColumnCount(); ++j)
            {
                if (get(i, j))
                {
                    ++cardinality;
                }
            }
        }

        return cardinality;
    }

    public void set(int i, int j, BitMatrix otherMatrix)
    {
        for (int a = 0; a < otherMatrix.getRowCount(); ++a)
        {
            for (int b = 0; b < otherMatrix.getColumnCount(); ++b)
            {
                set(a + i, b + j, otherMatrix.get(a, b));
            }
        }
    }

    public static BitMatrix load(InputStream is) throws IOException
    {
        try
        {
            return (BitMatrix) new ObjectInputStream(is).readObject();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new CodeShouldNotHaveBeenReachedException();
        }
    }

    public void save(OutputStream os) throws IOException
    {
        ObjectOutputStream o = new ObjectOutputStream(os);
        o.writeObject(this);
        o.flush();
    }

    public String toString()
    {
        return toString('*', ' ', "", true);
    }

    public String toString(char truechar, char falsechar, String cellSeparator, boolean showBrackets)
    {
        String s = "";
        // s += getRowCount() + "/" + getColumnCount() + " bit matrix\n";

        for (int i = 0; i < getRowCount(); ++i)
        {
            if (showBrackets)
            {
                s += "[";
            }

            for (int j = 0; j < getColumnCount(); ++j)
            {
                s += (get(i, j) ? truechar : falsechar);

                if (j < getColumnCount() - 1)
                {
                    s += cellSeparator;
                }
            }

            if (showBrackets)
            {
                s += "]";
            }

            s += "\n";
        }

        return s;
    }

    public String toStringOfBytes()
    {
        String s = "";

        for (int i = 0; i < this.bytes.length; ++i)
        {
            byte[] b = this.bytes[i];

            for (int j = 0; j < b.length; ++j)
            {
                s += TextUtilities.flushRight(String.valueOf(b[j]), 5, ' ');

                s += "\n";
            }
        }

        return s;
    }

    public int computeCardinalityOfLine(int lineIndex)
    {
        int column = -1;
        int count = 0;

        while (column < getColumnCount())
        {
            column = findNextONCellOnLine(lineIndex, column + 1);

            if (column == -1)
            {
                return count;
            }
            else
            {
                ++count;
            }
        }

        throw new CodeShouldNotHaveBeenReachedException();
    }

    public void setLine(int lineNumber, String s)
    {
        for (int j = 0; j < getColumnCount(); ++j)
        {
            set(lineNumber, j, s.charAt(j) != ' ');
        }
    }

    public int hashCode()
    {
        int[] lineHashCodes = new int[getColumnCount()];

        for (int i = 0; i < getRowCount(); ++i)
        {
            byte[] line = this.bytes[i];
            lineHashCodes[i] = Arrays.hashCode(line);
        }

        return Arrays.hashCode(lineHashCodes);
    }

    public String toLaTeX()
    {
        String s = "";
        s += "\\begin{tabular}{|" + TextUtilities.repeat("c|", getColumnCount()) + "}\n\t\\hline\n\t";

        for (int i = 0; i < getRowCount(); ++i)
        {
            for (int j = 0; j < getColumnCount(); ++j)
            {
                String symbol = get(i, j) ? " " : "$\\circ$";
                s += symbol;

                if (j < getColumnCount() - 1)
                {
                    s += " & ";
                }
            }

            if (i < getRowCount() - 1)
            {
                s += " \\hline\n";
            }
        }

        s += "\\\\ \n\t\\hline\n\\end{tabular}";
        return s;
    }

}
