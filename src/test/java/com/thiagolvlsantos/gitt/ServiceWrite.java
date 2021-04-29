package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.file.FileServices;
import com.thiagolvlsantos.gitt.write.GitWrite;
import com.thiagolvlsantos.gitt.write.GitWriteDir;

@Component
public class ServiceWrite {

	private static final String GITT_EXAMPLE_PROJECTS = "projects";
	private static final String GITT_EXAMPLE_PRODUCTS = "products";

	private @Autowired FileServices services;

	@GitWrite(GITT_EXAMPLE_PROJECTS)
	public void write() throws Exception {
		dumpWrite("Write");
	}

	@GitWrite(value = GITT_EXAMPLE_PROJECTS, //
			values = { //
					@GitWriteDir(value = GITT_EXAMPLE_PRODUCTS) //
			})
	public void writeMix() throws Exception {
		dumpWrite("Mix");
	}

	@GitWrite(values = { //
			@GitWriteDir(value = GITT_EXAMPLE_PROJECTS), //
			@GitWriteDir(value = GITT_EXAMPLE_PRODUCTS) //
	})
	public void writeDouble() throws Exception {
		dumpWrite("Double");
	}

	private void dumpWrite(String msg) throws FileNotFoundException, IOException {
		File dirProjects = services.dirWrite(GITT_EXAMPLE_PROJECTS);
		File fileProjects = new File(dirProjects, "projectA.txt");
		FileOutputStream outProjects = new FileOutputStream(fileProjects);
		outProjects.write(("{\"name\": \"projectA\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
		outProjects.close();
		System.out.println(msg + "...writeProjects..." + dirProjects + " DONE");

		if (msg.equals("Mix") || msg.equals("Double")) {
			File dirProducts = services.dirWrite(GITT_EXAMPLE_PRODUCTS);
			File fileProducts = new File(dirProducts, "productA.txt");
			FileOutputStream outProducts = new FileOutputStream(fileProducts);
			outProducts.write(("{\"name\": \"productA\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
			outProducts.close();
			System.out.println(msg + "...writeProducts..." + dirProducts + " DONE");
		}
	}

}