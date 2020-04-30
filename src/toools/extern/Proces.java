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

package toools.extern;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toools.collections.Collections;
import toools.exceptions.NotYetImplementedException;
import toools.io.Utilities;
import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class Proces
{

	public static boolean isTerminated(Process p)
	{
		try
		{
			p.exitValue();
			return true;
		}
		catch (IllegalThreadStateException e)
		{
			return false;
		}
	}

	public static boolean TRACE_CALLS = false;

	public static void execAsFilter(String name, Directory cwd, byte[] stdin,
			String... args)
	{
		throw new NotYetImplementedException();
	}

	public static byte[] exec(String name, Directory d, String... args)
	{
		return exec(name, d, null, args);
	}

	public static byte[] exec(String name, byte[] sdtin, String... args)
	{
		return exec(name, Directory.getCurrentDirectory(), sdtin, args);
	}

	public static byte[] exec(String name, String... args)
	{
		return exec(name, Directory.getCurrentDirectory(), null, args);
	}

	public static byte[] exec(String name, Directory directory, byte[] stdin,
			String... args)
	{
		try
		{
			ProcessOutput output = rawExec(name, directory, stdin, args);

			// the execution went ok
			if (output.getReturnCode() == 0)
			{
				return output.getStdout();
			}
			else
			{
				byte[] out = output.getStdout();
				byte[] err = output.getStderr();
				String error = new String(err.length == 0 ? out : err);
				throw new ProcesException("Command " + name + " " + Arrays.asList(args)
						+ " has failed (exit code=" + output.getReturnCode() + ")\n"
						+ error + "stdin was: "
						+ (stdin != null ? new String(stdin) : ""));
			}
		}
		catch (IOException e)
		{
			throw new ProcesException(e);
		}
	}

	public static ProcessOutput rawExec(String name, Directory d, String... args)
			throws IOException
	{
		return rawExec(name, d, null, args);
	}

	public static ProcessOutput rawExec(String name, byte[] in, String... args)
			throws IOException
	{
		return rawExec(name, Directory.getCurrentDirectory(), in, args);
	}

	public static ProcessOutput rawExec(String name, String... args) throws IOException
	{
		return rawExec(name, Directory.getCurrentDirectory(), null, args);
	}

	public static ProcessOutput rawExec(String name, Directory directory, byte[] stdin,
			String... args) throws IOException
	{
		try
		{
			List<String> tokens = new ArrayList<String>();
			tokens.add(name);

			if (args != null)
			{
				tokens.addAll(Arrays.asList(args));
			}

			if (TRACE_CALLS)
				System.out.println("exec: " + Collections.toString(tokens, " "));

			Process process = Runtime.getRuntime().exec(tokens.toArray(new String[0]),
					null, directory.toFile());

			ReadThread stdoutThread = new ReadThread(process.getInputStream());
			ReadThread stderrthread = new ReadThread(process.getErrorStream());

			if (stdin != null)
			{
				process.getOutputStream().write(stdin);
				process.getOutputStream().flush();
				process.getOutputStream().close();
			}

			// streams MUST be read in parallel of the process in order
			// to prevent buffer overflows (which freeze the process)
			// System.out.println(1);

			process.waitFor();

			for (ReadThread t : new ReadThread[] { stdoutThread, stderrthread })
			{
				synchronized (t)
				{
					if ( ! t.hasCompleted)
					{
						t.wait();
					}
				}
			}

			return new ProcessOutput(process.exitValue(),
					stdoutThread.targetBuffer.toByteArray(),
					stderrthread.targetBuffer.toByteArray());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			throw new IllegalStateException("command " + name + " has been interrupted");
		}
		finally
		{

		}
	}

	public final static List<Directory> path = retrieveSystemPath();

	public static RegularFile locate(String cmd)
	{
		return locate(path, cmd);
	}

	public static RegularFile locate(List<Directory> directories, String cmd)
	{
		for (Directory dir : directories)
		{
			RegularFile f = locate(dir, cmd);

			if (f != null)
			{
				return f;
			}
		}

		return null;
	}

	public static RegularFile locate(Directory d, String cmd)
	{
		RegularFile f = d.getChildRegularFile(cmd);
		return f.exists() ? f : null;
	}

	public static List<Directory> retrieveSystemPath()
	{
		List<Directory> files = new ArrayList<Directory>();

		for (String filename : System.getenv("PATH")
				.split(System.getProperty("path.separator")))
		{
			if (filename.trim().length() > 0)
			{
				files.add(new Directory(filename));
			}
		}

		return files;
	}

	public static boolean commandIsAvailable(String name)
	{
		return locate(name) != null;
	}

	public static void ensureCommandsAreAvailable(String... commands)
	{
		for (String cmd : commands)
		{
			if ( ! commandIsAvailable(cmd))
			{
				throw new IllegalStateException("Command \"" + cmd
						+ "\" is not available on this computer. If there are not in the system PATH, you can add the directory to "
						+ Proces.class + ".path static attribute");
			}
		}
	}

	public static void main(String[] args)
	{
		path.add(new Directory("/opt/local/bin/"));
		System.out.println(locate("dot"));
	}

	private static class ReadThread extends Thread
	{
		boolean hasCompleted = false;
		private BufferedInputStream is;
		ByteArrayOutputStream targetBuffer = new ByteArrayOutputStream();

		public ReadThread(InputStream is)
		{
			this.is = new BufferedInputStream(is);
			start();
		}

		@Override
		public void run()
		{
			synchronized (this)
			{

				// get all the content from the I/O stream and put
				// it into a byte array
				Utilities.copy(is, targetBuffer);

				// it's finished, flag it!
				hasCompleted = true;

				// resume the thread that was waiting for retrieving the
				// data
				notify();

			}
		}
	}

}
