package io.github.thiagolvlsantos.git.transactions.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BasicReadApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(BasicReadApplication.class, args);
		BasicRead s = ctx.getBean(BasicRead.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		s.read();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
