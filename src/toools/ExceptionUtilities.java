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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ExceptionUtilities
{
	public static String toString(Throwable e)
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public static void main(String[] args)
	{
		try
		{
			new ArrayList().get(1);

		}
		catch (Exception e)
		{
			System.out.println("*** " + toString(e));
			throw toRuntimeException(e);
		}
	}

	public static String extractMsgFromStrackTrace(Throwable e)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		e.printStackTrace(ps);
		String s = new String(bos.toByteArray());
		s = s.split("\n")[0];
		s = s.replaceFirst("[^:]+: ", "").trim();
		return s.isEmpty() ? null : s;
	}

	public static RuntimeException toRuntimeException(Exception e)
	{
		RuntimeException r = new RuntimeException(
				e.getClass().getName() + ": " + e.getMessage());
		r.setStackTrace(e.getStackTrace());
		return r;
	}

	public static Throwable getDeepestCause(Throwable e)
	{
		while (true)
		{
			Throwable cause = e.getCause();

			if (cause == null)
				return e;
			
			e = cause;
		}
	}
}
