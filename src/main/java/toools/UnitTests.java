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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toools.exceptions.ExceptionUtilities;
import toools.reflect.ClassContainer;
import toools.reflect.ClassPath;
import toools.thread.Generator;

public class UnitTests
{
	public static void main(String[] args)
	{
		for (String re : args)
		{
			testAndPrint(re);
		}
	}

	public static void testAndPrint(String re)
	{
		List<Throwable> errors = test(re, false);

		System.out.println(errors.size() + " error(s) found");
	}

	public static List<Throwable> test(String re, boolean stopAtFirstError)
	{
		//Proces.TRACE_CALLS = true;

		System.out.println("Starting testing...");
		ClassPath cp = ClassPath.retrieveSystemClassPath();
		System.out.println("This is the current classpath: " + cp.toString());
		List<Throwable> errors = new ArrayList<>();

		for (ClassContainer cc : cp.getContainersMatching(re))
		{
			System.out.println("Found container: " + cc.getFile().getName());
			test(errors, stopAtFirstError, cc);
		}

		return errors;
	}

	public static void test(List<Throwable> errors, boolean stopAtFirstError,
			ClassContainer... ccc) throws IllegalArgumentException
	{
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

		for (ClassContainer cc : ccc)
		{
			System.out.println("Exploring " + cc.getFile().getPath());
			Generator<Class<?>> allClasses = cc.listAllClasses();
			// Collections.shuffle(allClasses);

			for (Class<?> c : allClasses)
			{
				// System.out.println("Scanning class " + c.getName());

				for (Method m : getMethods(c))
				{
					if (m.getName().startsWith("test"))
					{
						if (m.getParameterTypes().length == 0)
						{
							if (((m.getModifiers() & Modifier.PRIVATE) != 0)
									&& ((m.getModifiers() & Modifier.STATIC) != 0))
							{
								System.out.println("Running test method " + c + "."
										+ m.getName() + "()");
								m.setAccessible(true);

								try
								{
									m.invoke(c);
								}
								catch (Throwable e)
								{
									System.err.println("Error found");
									System.err.println("*******");
									e = ExceptionUtilities.getDeepestCause(e);
									System.err.println("Cause:");
									printStackTrace(e);


									System.err.println("*******");

									if (stopAtFirstError)
									{
										System.err.println(
												"An error was found. Stopping tests.");
										return;
									}
									else
									{
										errors.add(e.getCause());
									}
								}
							}
							else
							{
								System.err.println(
										"Warning! You may want the following method to be private static: "
												+ c + "." + m.getName());
							}
						}
						else
						{
							System.err.println(
									"Warning! You may want the following method to have no parameter: "
											+ c + "." + m.getName());
						}
					}
				}
			}
		}
	}

	private static void printStackTrace(Throwable t)
	{
		// t.printStackTrace();
		List<StackTraceElement> s = Arrays.asList(t.getStackTrace());

		System.err.println(t.getClass().getName() + ": " + t.getMessage());
		for (StackTraceElement e : s)
		{
			String c = e.getClassName();
			if (c.startsWith("toools.UnitTests") || c.startsWith("sun.reflect")
					|| c.startsWith("java.lang.reflect"))
			{

			}
			else
			{
				System.err.println("\tat " + e);
			}
		}
	}

	private static Method[] getMethods(Class<?> c)
	{
		try
		{
			return c.getDeclaredMethods();
		}
		catch (Throwable t)
		{
			System.err.println("Warning: cannot scan class " + c.getName());
			return new Method[0];
		}
	}

	public static void ensureEqual(Object v, Object expectedValue)
	{
		if (v.getClass() == Integer.class)
		{
			v = new Long((Integer) v);
		}

		if (expectedValue.getClass() == Integer.class)
		{
			expectedValue = new Long((Integer) expectedValue);
		}

		ensure(v.equals(expectedValue),
				"Test failed!!!" + "# expected " + expectedValue + " ("
						+ expectedValue.getClass() + "), but got " + v + " ("
						+ v.getClass() + ")");
	}

	public static void ensure(boolean b)
	{
		ensure(b, "Test failed!!!");
	}

	public static void ensure(boolean b, String msg)
	{
		if ( ! b)
		{
			throw new IllegalStateException(msg);
		}
	}

}
