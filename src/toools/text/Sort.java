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
 
 package toools.text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class Sort
{
    public static List<List> extractNumbers(List<List<String>> lines)
    {
        List<List> newLines = new Vector<List>();

        for (List<String> line : lines)
        {
            List newLine = new Vector();

            for (String token : line)
            {
                if (token.matches("[+-][0-9]+\\.[0-9]+"))
                {
                    newLine.add(Double.valueOf(token));
                }
                else
                {
                    newLine.add(token);
                }
            }

            newLines.add(newLine);
        }

        return newLines;

    }

    public static void sort(List<String> l)
    {
        Collections.sort(l, new Comparator<String>()
        {

            public int compare(String o1, String o2)
            {
                return o1.compareTo(o2);
            }
        });
    }

    public static void sort(List<List<String>> l, final int n, final boolean numericOrdering)
    {
        Collections.sort(l, new Comparator<List<String>>()
        {

            public int compare(List<String> o1, List<String> o2)
            {
                if (numericOrdering)
                {
                    return Double.valueOf(o1.get(n)).compareTo(Double.valueOf(o2.get(n)));
                }
                else
                {
                    return o1.get(n).compareTo(o2.get(n));
                }
            }
        });
    }
}
