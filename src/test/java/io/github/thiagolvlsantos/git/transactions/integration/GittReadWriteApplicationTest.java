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
@Import(ServiceReadWrite.class)
class GittReadWriteApplicationTest {

	@Test
	void testReadWrite(@Autowired ApplicationContext ctx) throws Exception {
		ServiceReadWrite s = ctx.getBean(ServiceReadWrite.class);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		assertThat(s.mix()).isTrue();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
