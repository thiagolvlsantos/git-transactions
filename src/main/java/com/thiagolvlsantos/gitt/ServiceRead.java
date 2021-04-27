package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.IGitProvider;
import com.thiagolvlsantos.gitt.read.GitRead;
import com.thiagolvlsantos.gitt.read.GitReadDir;

@Component
public class ServiceRead {

	private static final String GITT_EXAMPLE_PROJECTS = "projects";
	private static final String GITT_EXAMPLE_PRODUCTS = "products";

	private @Autowired IGitProvider provider;

	@GitRead(GITT_EXAMPLE_PROJECTS)
	public void read() throws IOException {
		dumpRead("Read");
	}

	@GitRead(value = GITT_EXAMPLE_PROJECTS, //
			values = { //
					@GitReadDir(GITT_EXAMPLE_PRODUCTS) //
			})
	public void readMix() throws IOException {
		dumpRead("Mix");
	}

	@GitRead(values = { //
			@GitReadDir(GITT_EXAMPLE_PROJECTS), //
			@GitReadDir(GITT_EXAMPLE_PRODUCTS)//
	})
	public void readDouble() throws IOException {
		dumpRead("Double");
	}

	private void dumpRead(String msg) {
		File dir = provider.directoryRead(GITT_EXAMPLE_PROJECTS);
		System.out.println(msg + "...readProjects..." + dir);
		for (File f : dir.listFiles()) {
			System.out.println(f.getName());
		}

		if (msg.equals("Mix") || msg.equals("Double")) {
			dir = provider.directoryRead(GITT_EXAMPLE_PRODUCTS);
			System.out.println(msg + "...readProducts..." + dir);
			for (File f : dir.listFiles()) {
				System.out.println(f.getName());
			}
		}
	}
}