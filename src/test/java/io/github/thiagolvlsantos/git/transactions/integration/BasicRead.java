package io.github.thiagolvlsantos.git.transactions.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.GitServices;
import io.github.thiagolvlsantos.git.transactions.read.GitCommit;
import io.github.thiagolvlsantos.git.transactions.read.GitRead;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BasicRead {

	private @Autowired GitServices services;

	private String readContent(String tool) throws IOException {
		File dir = services.readDirectory("projects");
		for (File f : dir.listFiles()) {
			System.out.println(f.getName());
		}
		String content = Files.readString(new File(dir, tool + "projectA.txt").toPath());
		if (log.isInfoEnabled()) {
			log.info("CONTENT(" + tool + "projectA.txt):" + content);
		}
		return content;
	}

	@GitRead("projects")
	public boolean read() throws IOException {
		return readContent("").contains("\"name\": \"projectA\"");
	}

	@GitRead("projects")
	public String readCurrent(String tool) throws IOException {
		return readContent(tool);
	}

	@GitRead("projects")
	public String readAt(String tool, @GitCommit("projects") Long timestamp) throws IOException {
		return readContent(tool);
	}

	@GitRead("projects")
	public String readAtServices(String tool, Long timestamp) throws IOException {
		services.setTimestamp("projects", timestamp);
		return readContent(tool);
	}

	@GitRead("projects")
	public String readAtCommit(String tool, @GitCommit("projects") String commit) throws IOException {
		return readContent(tool);
	}

	@GitRead("projects")
	public String readAtCommitServices(String tool, String commit) throws IOException {
		services.setCommit("projects", commit);
		return readContent(tool);
	}

	@GitRead("projects")
	public String getCommit() {
		return services.history("projects", null, 0, 1).iterator().next().getName();
	}
}