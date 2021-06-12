package io.github.thiagolvlsantos.git.transactions.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.file.EFileStatus;
import io.github.thiagolvlsantos.git.transactions.provider.GitServices;
import io.github.thiagolvlsantos.git.transactions.write.GitWrite;

@Component
public class ServiceWriteNotify {

	private static final String GITT_EXAMPLE_PROJECTS = "projects";

	private @Autowired GitServices services;

	@GitWrite(value = GITT_EXAMPLE_PROJECTS, watcher = false)
	public void write() throws IOException {
		File directory = services.writeDirectory(GITT_EXAMPLE_PROJECTS);
		System.out.println("...writeProjects..." + directory);
		File file = new File(directory, "projectA.txt");
		FileOutputStream out = new FileOutputStream(file);
		out.write(("{\"name\": \"projectA\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
		out.close();
		for (File f : directory.listFiles()) {
			System.out.println(f.getName());
		}
		services.notify(this, GITT_EXAMPLE_PROJECTS, EFileStatus.CREATE, file);
	}
}