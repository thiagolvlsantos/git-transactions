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
@Import(ServiceWrite.class)
class GittWriteApplicationTest {

	@Test
	void testWrite(@Autowired ApplicationContext ctx) throws Exception {
		ServiceWrite s = ctx.getBean(ServiceWrite.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertThat(s.write()).isTrue();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertThat(s.writeMix()).isTrue();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertThat(s.writeDouble()).isTrue();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
