package io.github.thiagolvlsantos.git.transactions.integration;

import static org.assertj.core.api.Assertions.assertThat;

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
@Import(BasicWriteRouter.class)
class BasicWriteRouterApplicationTest {

	@Test
	void testWriteRouter(@Autowired ApplicationContext ctx) throws Exception {
		BasicWriteRouter s = ctx.getBean(BasicWriteRouter.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertThat(s.write("proj1")).contains("proj1");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertThat(s.write("proj2")).contains("proj2");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
