package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.IGitProvider;
import com.thiagolvlsantos.gitt.write.GitWrite;

@Component
public class ServiceWriteWatcher {

	private static final String GITT_EXAMPLE_PROJECTS = "projects";

	private @Autowired IGitProvider provider;

	@GitWrite(value = GITT_EXAMPLE_PROJECTS, watcher = true)
	public void writeProjects() throws IOException {
		File directory = provider.directoryWrite(GITT_EXAMPLE_PROJECTS);
		System.out.println("...writeProjects..." + directory);
		projects(directory);
		for (File f : directory.listFiles()) {
			System.out.println(f.getName());
		}
	}

	private File projects(File directory) throws FileNotFoundException, IOException {
		File file = new File(directory, "projectA.txt");
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(("{\"name\": \"projectA\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
		out.close();
		return file;
	}
}