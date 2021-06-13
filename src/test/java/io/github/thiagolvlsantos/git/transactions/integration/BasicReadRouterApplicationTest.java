package io.github.thiagolvlsantos.git.transactions.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import io.github.thiagolvlsantos.git.transactions.config.GitConfiguration;

@ContextConfiguration(classes = GitConfiguration.class)
@SpringBootTest
@ComponentScan("io.github.thiagolvlsantos")
@Import(BasicReadRouter.class)
class BasicReadRouterApplicationTest {

	@Test
	void testReaderRouter(@Autowired ApplicationContext ctx) throws Exception {
		BasicReadRouter s = ctx.getBean(BasicReadRouter.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertThat(s.read("proj1")).contains("odd ids");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertThat(s.read("proj2")).contains("even ids");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
