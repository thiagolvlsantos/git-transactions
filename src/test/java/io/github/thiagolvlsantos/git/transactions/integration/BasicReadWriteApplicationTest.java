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
@Import({ BasicWrite.class, BasicRead.class })
class BasicReadWriteApplicationTest {

	@Test
	void testWriteReadAt(@Autowired ApplicationContext ctx) throws Exception {
		BasicWrite s = ctx.getBean(BasicWrite.class);
		BasicRead r = ctx.getBean(BasicRead.class);

		// # FIRST WRITE

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		String writeBefore = s.write();
		assertThat(writeBefore).contains("projectA");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		long base = System.currentTimeMillis();

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		String readBefore = r.readCurrent();
		assertThat(readBefore).isEqualTo(writeBefore);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		// # SECOND WRITE

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		String writeAfter = s.write();
		assertThat(writeAfter).contains("projectA");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		String readAfter = r.readCurrent();
		assertThat(readAfter).isEqualTo(writeAfter);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		// # Read at date before should be equals to read after
		String readAt = r.readAt(base);
		assertThat(readAt).isEqualTo(readBefore);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
