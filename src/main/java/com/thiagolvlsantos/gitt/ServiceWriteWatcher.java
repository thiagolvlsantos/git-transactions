package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.FileOutputStream;
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
	public void writeProjects() throws Exception {
		File directory = provider.directoryWrite(GITT_EXAMPLE_PROJECTS);
		File file = new File(directory, "projectA.txt");
		FileOutputStream out = new FileOutputStream(file);
		out.write(("{\"name\": \"projectA\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
		out.close();
		System.out.println("...writeProjects..." + directory + " DONE");
	}

}