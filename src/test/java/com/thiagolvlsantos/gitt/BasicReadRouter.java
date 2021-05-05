package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.GitServices;
import com.thiagolvlsantos.gitt.read.GitRead;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BasicReadRouter {

	private @Autowired GitServices services;

	@GitRead(value = "projects", router = RouterName.class)
	public void read(String name) throws IOException {
		File dir = services.readDirectory("projects", RouterName.class, name);
		for (File f : dir.listFiles()) {
			System.out.println(f.getName());
		}
		if (log.isInfoEnabled()) {
			log.info("DIR:" + dir.getAbsolutePath());
			log.info("CONTENT(README.md):" + Files.readString(new File(dir, "README.md").toPath()));
		}
	}
}