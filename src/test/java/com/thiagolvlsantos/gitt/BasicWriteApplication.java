package com.thiagolvlsantos.gitt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BasicWriteApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(BasicWriteApplication.class, args);
		BasicWrite s = ctx.getBean(BasicWrite.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		s.write();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
