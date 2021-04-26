package com.thiagolvlsantos.gitt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GittWriteWatcherApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(GittWriteWatcherApplication.class, args);
		ServiceWriteWatcher s = ctx.getBean(ServiceWriteWatcher.class);
		s.writeProjects();
	}
}
