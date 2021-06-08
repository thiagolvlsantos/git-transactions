package io.github.thiagolvlsantos.git.transactions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import io.github.thiagolvlsantos.git.transactions.config.GittConfig;

@ContextConfiguration(classes = GittConfig.class)
@SpringBootTest
@ComponentScan("io.github.thiagolvlsantos")
@Import(BasicRead.class)
class BasicReadApplicationTest {

	@Test
	void read(@Autowired ApplicationContext ctx) throws Exception {
		BasicRead s = ctx.getBean(BasicRead.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertTrue(s.read());
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
