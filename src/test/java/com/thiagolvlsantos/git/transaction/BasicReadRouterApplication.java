package com.thiagolvlsantos.git.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BasicReadRouterApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(BasicReadRouterApplication.class, args);
		BasicReadRouter s = ctx.getBean(BasicReadRouter.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		s.read("proj1");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		s.read("proj2");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
