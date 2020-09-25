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
 
 package toools.log;

import toools.text.TextUtilities;
import toools.util.Date;

public abstract class Logger
{
	private static final Logger DEFAULT_LOGGER = new StdOutLogger(true);

	private double verbosityLevel = 1;
	private boolean printDate = true;

	public static Logger getDefaultLogger()
	{
		return DEFAULT_LOGGER;
	}

	public Logger(boolean printDate)
	{
		this.printDate = printDate;
	}

	public final void log(Object o)
	{
		log(o, 1);
	}

	public synchronized void log(Object o, double importance)
	{
		if (importance > (1 - getVerbosityLevel()))
		{
			if (printDate)
			{
				String date = Date.now();
				String[] lines = o.toString().split("\n");

				logImpl(date + "\t" + lines[0]);

				for (int i = 1; i < lines.length; ++i)
				{
					logImpl(TextUtilities.repeat(' ', date.length()) + "\t" + lines[i]);
				}
			}
			else
			{
				logImpl(o);
			}
		}
	}

	protected abstract void logImpl(Object o);

	public void logWithDate(Object o)
	{
		log(Date.now() + " --- " + o);
	}

	public double getVerbosityLevel()
	{
		return verbosityLevel;
	}

	public void setVerbosityLevel(double verbosityLevel)
	{
		if (verbosityLevel < 0)
			throw new IllegalArgumentException("< 0");

		if (verbosityLevel > 1)
			throw new IllegalArgumentException("> 1");

		this.verbosityLevel = verbosityLevel;
	}


	public void logWarning(Object o)
	{
		log(o);

	}

	public void logError(Object o)
	{
		log(o);
	}

}
