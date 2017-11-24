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


/**
 * A non-ordered pair.
 * 
 * @author lhogie
 * 
 * @param <E>
 * @param <B>
 */

public class Couple<E>
{
    private E e1_;
    private E e2_;

    public Couple(E a, E b)
    {
    	if (a == null)
    		throw new NullPointerException();

    	if (b == null)
    		throw new NullPointerException();

    	e1_ = a;
    	e2_ = b;
    }

    @Override
    public int hashCode()
    {
        int ah = e1_.hashCode();
        int bh = e2_.hashCode();
        return (Math.min(ah, bh) + ":" + Math.max(ah, bh)).hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return o.getClass() == Couple.class && o.hashCode() == hashCode();
    }

    public E getA()
    {
        return e1_;
    }

//    public void setA(E a)
//    {
//        if (a == null)
//            throw new IllegalArgumentException();
//
//        this.e1_ = a;
//    }

    public E getB()
    {
        return e2_;
    }

//    public void setB(E b)
//    {
//        if (b == null)
//            throw new IllegalArgumentException();
//
//        this.e2_ = b;
//    }
    
    public E getOtherElement(E e)
    {
        if (e == this.e1_)
        {
            return e2_;
        }
        else if (e == this.e2_)
        {
            return e1_;
        }
        else
        {
            throw new IllegalStateException("no such element");
        }
    }
}
