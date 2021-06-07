package io.github.thiagolvlsantos.git.transactions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.provider.GitServices;
import io.github.thiagolvlsantos.git.transactions.read.GitRead;
import io.github.thiagolvlsantos.git.transactions.read.GitReadDir;
import io.github.thiagolvlsantos.git.transactions.write.GitWrite;
import io.github.thiagolvlsantos.git.transactions.write.GitWriteDir;

@Component
public class ServiceReadWrite {

	private static final String GITT_EXAMPLE_PROJECTS = "projects";
	private static final String GITT_EXAMPLE_PRODUCTS = "products";

	private @Autowired GitServices services;

	@GitWrite(value = GITT_EXAMPLE_PROJECTS, values = { //
			@GitWriteDir(value = GITT_EXAMPLE_PRODUCTS) //
	})
	@GitRead(value = GITT_EXAMPLE_PROJECTS, values = { //
			@GitReadDir(GITT_EXAMPLE_PRODUCTS) //
	})
	public void mix() throws Exception {
		dumpRead("Mix");
		dumpWrite("Mix");
	}

	private void dumpRead(String msg) {
		File dir = services.readDirectory(GITT_EXAMPLE_PROJECTS);
		System.out.println(msg + "...readProjects..." + dir);
		for (File f : dir.listFiles()) {
			System.out.println(f.getName());
		}

		if (msg.equals("Mix") || msg.equals("Double")) {
			dir = services.readDirectory(GITT_EXAMPLE_PRODUCTS);
			System.out.println(msg + "...readProducts..." + dir);
			for (File f : dir.listFiles()) {
				System.out.println(f.getName());
			}
		}
	}

	private void dumpWrite(String msg) throws FileNotFoundException, IOException {
		File dirProjects = services.writeDirectory(GITT_EXAMPLE_PROJECTS);
		File fileProjects = new File(dirProjects, "projectA.txt");
		FileOutputStream outProjects = new FileOutputStream(fileProjects);
		outProjects.write(("{\"name\": \"projectA\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
		outProjects.close();
		System.out.println(msg + "...writeProjects..." + dirProjects + " DONE");

		if (msg.equals("Mix") || msg.equals("Double")) {
			File dirProducts = services.writeDirectory(GITT_EXAMPLE_PRODUCTS);
			File fileProducts = new File(dirProducts, "productA.txt");
			FileOutputStream outProducts = new FileOutputStream(fileProducts);
			outProducts.write(("{\"name\": \"productA\", date: \"" + LocalDateTime.now() + "\"}").getBytes());
			outProducts.close();
			System.out.println(msg + "...writeProducts..." + dirProducts + " DONE");
		}
	}

}