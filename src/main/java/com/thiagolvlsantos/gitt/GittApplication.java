package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.thiagolvlsantos.gitt.watcher.FileWatcherListener;

@SpringBootApplication
public class GittApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(GittApplication.class, args);
		Service s = ctx.getBean(Service.class);
		try {
			s.readProjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			s.writeProducts();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			s.writeProducts();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			s.writeDeploymentsError();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// watch(ctx);
	}

	private static void watch(ApplicationContext ctx) throws Exception {
		FileWatcherListener watcher = ctx.getBean(FileWatcherListener.class);
		Path path = Paths.get("data");
		File dir = path.toFile();
		dir.mkdir();
		String group = "aqui";
		watcher.start(group, path);
		System.out.println("Watcher on");

		Thread.sleep(1000);
		File file = new File(dir, "aqui.txt");
		if (file.exists()) {
			System.out.println("REMOVER");
			file.delete();
		}
		Thread.sleep(1000);
		System.out.println("CRIAR");
		FileOutputStream out = new FileOutputStream(file);
		out.write("teste".getBytes());
		out.close();

		Thread.sleep(1000);
		System.out.println("MUDAR");
		out = new FileOutputStream(file);
		out.write("mudou".getBytes());
		out.close();

		Thread.sleep(5000);
		System.out.println("");
		watcher.stop(group, path);
		System.out.println("END");
	}

}
