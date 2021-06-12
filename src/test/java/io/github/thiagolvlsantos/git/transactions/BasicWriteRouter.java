package io.github.thiagolvlsantos.git.transactions;

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
public class BasicWriteRouter {

	private @Autowired GitServices services;

	@GitWrite(value = "projects", router = RouterName.class)
	public String write(String name) throws Exception {
		File dirProjects = services.writeDirectory("projects", RouterName.class, name);
		File newFile = new File(dirProjects, name + ".txt");
		if (newFile.exists() && log.isInfoEnabled()) {
			log.info("BEFORE:" + Files.readString(newFile.toPath()));
		}
		String newContent = "{\"name\": \"" + name + "\", date: \"" + LocalDateTime.now() + "\"}";
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