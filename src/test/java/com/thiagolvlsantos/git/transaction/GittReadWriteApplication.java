package com.thiagolvlsantos.git.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GittReadWriteApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(GittReadWriteApplication.class, args);
		ServiceReadWrite s = ctx.getBean(ServiceReadWrite.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		s.mix();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
