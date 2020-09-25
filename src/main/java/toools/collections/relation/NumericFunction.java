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


public class NumericFunction<X> extends Function<X, Double>
{
    public String getGNUPlotText(Collection<X> keys)
    {
        StringBuffer buf = new StringBuffer();
        
        for (X x : keys)
        {
            buf.append(x.toString());
            buf.append('\t');
            buf.append(getValue(x));
            buf.append('\n');
        }
        
        return buf.toString();
    }


//    public static <A> NumericFunction<A> getAverageFunction(Collection<NumericFunction<A>> functions)
//    {
//        if (!RelationUtilities.haveSameKeys((Collection) functions))
//            throw new IllegalArgumentException("functions do not have the same key sets");
//
//        Relation<A, Double> mergeRelation = RelationUtilities.merge((Collection) functions);
//        NumericFunction<A> averageFunction = new NumericFunction<A>();
//        
//        for (A key : mergeRelation.getKeys())
//        {
//            Collection<Double> values = mergeRelation.getValues(key);
//            double average = MathsUtilities.computeAverage(values);
//            averageFunction.add(key, average);
//        }
//        
//        return averageFunction;
//    }
//
//
//    public static  <X> NumericFunction<X> getStandardDeviationFunction(Collection<NumericFunction<X>> functions)
//    {
//    	NumericFunction<X> stdDevFunction = new NumericFunction<X>();
//        Relation<X, Double> mergeRelation = RelationUtilities.merge(functions);
//        Iterator keyIterator = mergeRelation.getKeys().iterator();
//        
//        while (keyIterator.hasNext())
//        {
//            Object key = keyIterator.next();
//            Collection values = mergeRelation.getValues(key);
//            double stdDev = MathsUtilities.computeStandardDeviation(values);
//            stdDevFunction.add(key, new Double(stdDev));
//        }
//        
//        return stdDevFunction;
//    }
}
