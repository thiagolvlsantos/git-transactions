package com.thiagolvlsantos.gitt;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.GitServices;
import com.thiagolvlsantos.gitt.write.GitWrite;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BasicWrite {

	private @Autowired GitServices services;

	@GitWrite("projects")
	public void write() throws Exception {
		File dirProjects = services.writeDirectory("projects");
		File newFile = new File(dirProjects, "projectA.txt");
		if (log.isInfoEnabled()) {
			log.info("BEFORE:" + Files.readString(newFile.toPath()));
		}
		String newContent = "{\"name\": \"projectA\", date: \"" + LocalDateTime.now() + "\"}";
		Files.write(newFile.toPath(), newContent.getBytes(), StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
		if (log.isInfoEnabled()) {
			log.info(" AFTER:" + Files.readString(newFile.toPath()));
		}
	}
}