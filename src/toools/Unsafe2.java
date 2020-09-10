
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
 
 package toools;

import sun.misc.Unsafe;
import toools.util.assertion.Assertions;

public class Unsafe2
{

	public static final Unsafe unsafe = Unsafe.getUnsafe();


	private final static Object[] array = new Object[1];
	private final static long baseOffset = unsafe.arrayBaseOffset(Object[].class);

	public static long getAddress(Object obj)
	{
		array[0] = obj;
		return unsafe.getLong(array, baseOffset);
	}

	public static Object getObjectAtAddress(long address)
	{
		unsafe.getAndSetLong(array, baseOffset, address);
		return array[0];
	}

	private static void testAddr()
	{
		Object o = "coucou";
		long a = getAddress(o);
		Assertions.ensure(getObjectAtAddress(a).equals(o));
	}
	
	public static void main(String[] args)
	{
		System.out.println(getAddress("salut"));
		long r = unsafe.allocateMemory(2500000000L);
		unsafe.getByte(r);
	}
}
