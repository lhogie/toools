package toools.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import toools.collections.Collections;
import toools.io.file.AbstractFile;
import toools.io.file.Directory;
import toools.net.SSHParms;
import toools.net.SSHUtils;

public class RSync {
	public static void rsyncTo(List<AbstractFile> files, Directory dest, Consumer<String> stdout,
			Consumer<String> stderr) throws IOException {
		dest.mkdirs();
		List<String> args = new ArrayList<>();
		args.add("rsync");

		args.add("-a");
		args.add("--delete");
		args.add("--copy-links");
		args.add("-v");

		for (AbstractFile e : files) {
			if (e instanceof Directory) {
				args.add(e.getPath() + "/");
			} else {
				args.add(e.getPath());
			}
		}

		args.add(dest.getPath());
		
		try {
//			System.out.println(args);
			Process rsync = Runtime.getRuntime().exec(args.toArray(new String[0]));
			Utilities.grabLines(rsync.getInputStream(), stdout, err -> {
			});
			Utilities.grabLines(rsync.getErrorStream(), stderr, err -> {
			});
			rsync.waitFor();
			var exitCode = rsync.exitValue();

			if (exitCode != 0) {
				throw new Error("Error: rsync to " + dest + " exited with value " + exitCode);
			}
		} catch (InterruptedException e1) {
			throw new IllegalStateException(e1);
		}
	}

	
	public static void rsyncTo(SSHParms sshParameters, List<AbstractFile> files, String destPath, Consumer<String> stdout,
			Consumer<String> stderr) throws IOException {
		List<String> args = new ArrayList<>();
		args.add("rsync");

		if (sshParameters != null) {
			args.add("-e");
			List<String> sshArgs = new ArrayList<>();
			sshArgs.add(SSHUtils.sshCmd());
			SSHUtils.addSSHOptions(sshArgs, sshParameters);
			args.add(Collections.toString(sshArgs, " "));
		}

		args.add("-a");
		args.add("--delete");
		args.add("--copy-links");
		args.add("-v");

		for (AbstractFile e : files) {
			if (e instanceof Directory) {
				args.add(e.getPath() + "/");
			} else {
				args.add(e.getPath());
			}
		}

		args.add(sshParameters.host + ":" + destPath);

		try {
//			System.out.println(args);
			Process rsync = Runtime.getRuntime().exec(args.toArray(new String[0]));
			Utilities.grabLines(rsync.getInputStream(), stdout, err -> {
			});
			Utilities.grabLines(rsync.getErrorStream(), stderr, err -> {
			});
			rsync.waitFor();
			var exitCode = rsync.exitValue();

			if (exitCode != 0) {
				throw new Error("Error: rsync to " + sshParameters + " exited with value " + exitCode);
			}
		} catch (InterruptedException e1) {
			throw new IllegalStateException(e1);
		}
	}
}
