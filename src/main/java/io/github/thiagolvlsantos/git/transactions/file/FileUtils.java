package io.github.thiagolvlsantos.git.transactions.file;

import java.io.File;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

	public static boolean mkdirs(File dir) {
		return dir.mkdirs();
	}

	public static boolean delete(File f) {
		boolean ok = true;
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				ok = ok & delete(c);
			}
		}
		return ok & f.delete();
	}
}
