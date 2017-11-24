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
 
 package toools.util.assertion;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Assertions
{

    public static void ensureArgumentIsNotNull(Object o)
    {
        ensureArgument(o != null, "object was expected to be non-null");
    }

    public static void ensureArgument(boolean b, String msg)
    {
        if (!b)
        {
            throwException(new IllegalArgumentException(msg));
        }
    }

    
    public static void ensureThisCodeIsNeverExecuted()
    {
        ensure(false, "This code should never execute");
    }

    public static void ensure(boolean b)
    {
        ensure(b, "assertion failed!");
    }

    public static void ensure(boolean b, String msg)
    {
        if (!b)
        {
            throwException(new AssertionFailedException(msg));
        }
    }

    private static void throwException(RuntimeException e)
    {
        StackTraceElement[] trace = e.getStackTrace();
        List<StackTraceElement> list = new LinkedList<StackTraceElement>(Arrays.asList(trace));
        
        while (list.get(0).getClassName().endsWith("Assertions"))
        {
            list.remove(0);
        }

        e.setStackTrace(list.toArray(new StackTraceElement[0]));
        throw e;
    }
}
