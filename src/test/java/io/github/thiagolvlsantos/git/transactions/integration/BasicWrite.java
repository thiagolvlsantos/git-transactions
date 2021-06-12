package io.github.thiagolvlsantos.git.transactions.integration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.provider.GitServices;
import io.github.thiagolvlsantos.git.transactions.write.GitWrite;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BasicWrite {

	private @Autowired GitServices services;

	@GitWrite("projects")
	public String write() throws Exception {
		String projectName = "projectA";
		File dirProjects = services.writeDirectory("projects");
		File newFile = new File(dirProjects, projectName + ".txt");
		if (newFile.exists() && log.isInfoEnabled()) {
			log.info("BEFORE:" + Files.readString(newFile.toPath()));
		}
		String newContent = "{\"name\": \"" + projectName + "\", date: \"" + LocalDateTime.now() + "\"}";
		newFile.getParentFile().mkdirs();
		Files.write(newFile.toPath(), newContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
		String content = Files.readString(newFile.toPath());
		if (log.isInfoEnabled()) {
			log.info(" AFTER:" + content);
		}
		return content;
	}
}