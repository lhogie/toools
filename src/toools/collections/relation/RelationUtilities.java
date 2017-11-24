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
 
 package toools.collections.relation;

import java.util.Collection;
import java.util.Iterator;

/*
 * Created on Aug 18, 2004
 */

/**
 * @author luc.hogie
 */
public class RelationUtilities
{
	
	public static <A, B> Relation<A, B> merge(Collection<Relation<A, B>> relations)
	{
		if (relations.isEmpty())
			throw new IllegalArgumentException("no functions to compute");
		
        Relation<A, B> mergeRelation = new HashRelation<A, B>();
        
		for (Relation<A, B> relation : relations)
		{
			for (A key : relation.getKeys())
            {
				mergeRelation.addAll(key, relation.getValues(key));
			}
		}
		
		return mergeRelation;
	}
    
    public static <A, B> boolean haveSameKeys(Collection<Relation<A, B>> relations)
    {
        if (relations.isEmpty())
            throw new IllegalArgumentException("no relation to compute");

        Iterator<Relation<A, B> > i = relations.iterator();
        Collection<A> refKeys = i.next().getKeys();
        
        while (i.hasNext())
        {
            if (!i.next().getKeys().equals(refKeys))
            {
                return false;
            }
        }
        
        return true;
    }
}
