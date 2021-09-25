package io.github.thiagolvlsantos.git.transactions.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import io.github.thiagolvlsantos.git.transactions.provider.GitAuditHelper;
import io.github.thiagolvlsantos.git.transactions.provider.IGitAudit;

@ContextConfiguration(classes = IGitAuditDefaultTest.Config.class)
@SpringBootTest
class IGitAuditDefaultTest {

	public static class Config {
	}

	@Test
	void defined(@Autowired ApplicationContext ctx) throws Exception {
		IGitAudit s = GitAuditHelper.audit(ctx);
		assertThat(s.author().getUser()).isEmpty();
		assertThat(s.author().getEmail()).isEmpty();
	}
}