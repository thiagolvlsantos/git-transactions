package com.thiagolvlsantos.gitt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.GitServices;
import com.thiagolvlsantos.gitt.provider.IGitRouter;
import com.thiagolvlsantos.gitt.read.GitRead;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BasicReadRouter {

	private @Autowired GitServices services;

	@GitRead(value = "projects", router = RouterMath.class)
	public void read(String name) throws IOException {
		File dir = services.readDirectory("projects", RouterMath.class, name);
		for (File f : dir.listFiles()) {
			System.out.println(f.getName());
		}
		if (log.isInfoEnabled()) {
			log.info("DIR:" + dir.getAbsolutePath());
			log.info("CONTENT(README.md):" + Files.readString(new File(dir, "README.md").toPath()));
		}
	}

	public static class RouterMath implements IGitRouter {
		@Override
		public String qualifier(String group, Object[] args) {
			String name = String.valueOf(args[0]);
			char last = name.charAt(name.length() - 1);
			if (last == '0' || last == '2' || last == '4' || last == '6' || last == '8') {
				return "even";
			}
			return "odd";
		}
	}
}