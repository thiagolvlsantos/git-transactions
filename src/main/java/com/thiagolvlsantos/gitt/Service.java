package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.file.EFileStatus;
import com.thiagolvlsantos.gitt.file.FileServices;
import com.thiagolvlsantos.gitt.provider.IGitProvider;
import com.thiagolvlsantos.gitt.read.GitRead;
import com.thiagolvlsantos.gitt.write.GitWrite;

@Component
public class Service {

	private static final String GITT_EXAMPLE_PRODUCTS = "products";
	private static final String GITT_EXAMPLE_PROJECTS = "projects";
	private static final String GITT_EXAMPLE_DEPLOYMENTS = "deployments";

	private @Autowired IGitProvider provider;
	private @Autowired FileServices services;

	@GitRead(GITT_EXAMPLE_PROJECTS)
	public void readProjects() throws IOException {
		File directory = provider.directoryRead(GITT_EXAMPLE_PROJECTS);
		System.out.println("...readProjects..." + directory);
		File file = projects(directory);
		services.notify(this, GITT_EXAMPLE_PROJECTS, EFileStatus.CREATE, file);
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

	@GitWrite(GITT_EXAMPLE_PRODUCTS)
	public void writeProducts() throws IOException {
		File directory = provider.directoryRead(GITT_EXAMPLE_PRODUCTS);
		System.out.println("...writeProducts..." + directory);
		File file = products(directory);
		services.notify(this, GITT_EXAMPLE_PRODUCTS, EFileStatus.CREATE, file);
	}

	private File products(File directory) throws FileNotFoundException, IOException {
		File file = new File(directory, "product1.txt");
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(("{\"name\": \"product1\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
		out.close();
		return file;
	}

	@GitWrite(GITT_EXAMPLE_DEPLOYMENTS)
	public void writeDeploymentsError() {
		System.out.println("...write.error..." + provider.directoryRead(GITT_EXAMPLE_DEPLOYMENTS));
		throw new RuntimeException("Falhei!");
	}
}