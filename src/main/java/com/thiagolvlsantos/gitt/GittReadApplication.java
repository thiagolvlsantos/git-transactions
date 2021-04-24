package com.thiagolvlsantos.gitt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GittReadApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(GittReadApplication.class, args);
		ServiceRead s = ctx.getBean(ServiceRead.class);
		s.readProjects();
		s.readProjects();
		s.readProjects();
	}
}
