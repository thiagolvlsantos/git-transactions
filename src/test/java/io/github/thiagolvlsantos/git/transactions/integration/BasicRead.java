package io.github.thiagolvlsantos.git.transactions.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.provider.GitServices;
import io.github.thiagolvlsantos.git.transactions.read.GitRead;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BasicRead {

	private @Autowired GitServices services;

	private String readContent() throws IOException {
		File dir = services.readDirectory("projects");
		for (File f : dir.listFiles()) {
			System.out.println(f.getName());
		}
		String content = Files.readString(new File(dir, "projectA.txt").toPath());
		if (log.isInfoEnabled()) {
			log.info("CONTENT(projectA.txt):" + content);
		}
		return content;
	}

	@GitRead("projects")
	public boolean read() throws IOException {
		return readContent().contains("\"name\": \"projectA\"");
	}

	@GitRead("projects")
	public String readCurrent() throws IOException {
		return readContent();
	}

	@GitRead("projects")
	public String readAt(Long timestamp) throws IOException {
		services.setTimestamp("projects", timestamp);
		return readContent();
	}
}