package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.file.FileWatcher;
import com.thiagolvlsantos.gitt.provider.IGitProvider;
import com.thiagolvlsantos.gitt.read.GitRead;
import com.thiagolvlsantos.gitt.write.GitWrite;

@Component
public class Service {

	private static final String GITT_EXAMPLE_PRODUCTS = "gitt-example-products";
	private static final String GITT_EXAMPLE_PROJECTS = "gitt-example-projects";
	private static final String GITT_EXAMPLE_DEPLOYMENTS = "gitt-example-deployments";
	private @Autowired IGitProvider provider;
	private @Autowired FileWatcher watcher;

	@PostConstruct
	public void bind() {
		watcher.get(GITT_EXAMPLE_PROJECTS, provider.directory(GITT_EXAMPLE_PROJECTS).toPath());
		watcher.get(GITT_EXAMPLE_PRODUCTS, provider.directory(GITT_EXAMPLE_PRODUCTS).toPath());
		watcher.get(GITT_EXAMPLE_DEPLOYMENTS, provider.directory(GITT_EXAMPLE_DEPLOYMENTS).toPath());
	}

	@GitRead(GITT_EXAMPLE_PROJECTS)
	public void readProjects() throws IOException {
		File directory = provider.directory(GITT_EXAMPLE_PROJECTS);
		System.out.println("...readProjects..." + directory);

		File file = new File(directory, "projectA.txt");
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(("{\"name\": \"projectA\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
		out.close();
	}

	@GitWrite(GITT_EXAMPLE_PRODUCTS)
	public void writeProducts() throws IOException {
		File directory = provider.directory(GITT_EXAMPLE_PRODUCTS);
		System.out.println("...writeProducts..." + directory);

		File file = new File(directory, "product1.txt");
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(("{\"name\": \"product1\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
		out.close();
	}

	@GitWrite(GITT_EXAMPLE_DEPLOYMENTS)
	public void writeDeploymentsError() {
		System.out.println("...write.error..." + provider.directory(GITT_EXAMPLE_DEPLOYMENTS));
		throw new RuntimeException("Falhei!");
	}

	public void stop() {
		watcher.del(GITT_EXAMPLE_PROJECTS, provider.directory(GITT_EXAMPLE_PROJECTS).toPath());
		watcher.del(GITT_EXAMPLE_PRODUCTS, provider.directory(GITT_EXAMPLE_PRODUCTS).toPath());
		watcher.del(GITT_EXAMPLE_DEPLOYMENTS, provider.directory(GITT_EXAMPLE_DEPLOYMENTS).toPath());
	}
}
