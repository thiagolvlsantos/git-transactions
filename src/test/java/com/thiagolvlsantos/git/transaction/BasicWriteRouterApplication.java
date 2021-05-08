package com.thiagolvlsantos.git.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BasicWriteRouterApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(BasicWriteRouterApplication.class, args);
		BasicWriteRouter s = ctx.getBean(BasicWriteRouter.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		s.write("proj1");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		s.write("proj2");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
