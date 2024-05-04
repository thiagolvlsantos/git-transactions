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

		String tool = System.getenv("TOOL");
		if (tool == null) {
			tool = System.getProperty("TOOL", "local");
		}
		System.out.println("@@@@@@@ TOOL = " + tool);
		// # FIRST WRITE

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		String writeBefore = s.write(tool);
		assertThat(writeBefore).contains("projectA");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		long base = System.currentTimeMillis();
		String commit = r.getCommit();

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		String readBefore = r.readCurrent(tool);
		assertThat(readBefore).isEqualTo(writeBefore);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		// # SECOND WRITE

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		String writeAfter = s.write(tool);
		assertThat(writeAfter).contains("projectA");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		String readAfter = r.readCurrent(tool);
		assertThat(readAfter).isEqualTo(writeAfter);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		// # Read at date before should be equals to read after
		String readAt = r.readAt(tool, base);
		assertThat(readAt).isEqualTo(readBefore);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		// # Read at date before should be equals to read after
		String readAtCommit = r.readAtCommit(tool, commit);
		assertThat(readAtCommit).isEqualTo(readBefore);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		// # Read at date before should be equals to read after
		readAt = r.readAtServices(tool, base);
		assertThat(readAt).isEqualTo(readBefore);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		// # Read at date before should be equals to read after
		readAtCommit = r.readAtCommitServices(tool, commit);
		assertThat(readAtCommit).isEqualTo(readBefore);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
