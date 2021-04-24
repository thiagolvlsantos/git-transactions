package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.IGitProvider;
import com.thiagolvlsantos.gitt.read.GitRead;

@Component
public class ServiceRead {

	private static final String GITT_EXAMPLE_PROJECTS = "projects";

	private @Autowired IGitProvider provider;

	@GitRead(GITT_EXAMPLE_PROJECTS)
	public void readProjects() throws IOException {
		File directory = provider.directoryRead(GITT_EXAMPLE_PROJECTS);
		System.out.println("...readProjects..." + directory);
		for (File f : directory.listFiles()) {
			System.out.println(f.getName());
		}
	}
}