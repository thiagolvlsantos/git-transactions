package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.file.FileServices;
import com.thiagolvlsantos.gitt.read.GitRead;

@Component
public class BasicRead {

	private @Autowired FileServices services;

	@GitRead("projects")
	public void read() throws IOException {
		File dir = services.dirRead("projects");
		for (File f : dir.listFiles()) {
			System.out.println(f.getName());
		}
	}
}