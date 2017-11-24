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
 
 package toools.math.geometry;

import java.util.Random;

import toools.math.MathsUtilities;



public class Position
{
    private double x, y;

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setXY(double newX, double newY)
    {
        x = newX;
        y = newY;
    }

    @Override
	public String toString()
    {
        return '(' + x + ", " + y + ')';
    }

    @Override
	public boolean equals(Object obj)
	{
		return obj instanceof Position && super.equals((Position) obj);
	}

	public boolean equals(Position l)
    {
        return x == l.x && y == l.y;
    }

    public double getDistanceTo(Position l)
    {
        double dx = x - l.getX();
        double dy = y - l.getY();
        double d = dx * dx + dy * dy;
        return Math.sqrt(d);
    }
 
    @Override
	public int hashCode()
    {
        return (x + ":" + y).hashCode();
    }

    
    public static Position createRandomLocation(double minX, double maxX, double minY, double maxY, Random pnrg)
    {
        Position l = new Position();
        l.setXY(MathsUtilities.pickRandomBetween(minX, maxX, pnrg), MathsUtilities.pickRandomBetween(minY, maxY, pnrg));
        return l;
    }
    
}
