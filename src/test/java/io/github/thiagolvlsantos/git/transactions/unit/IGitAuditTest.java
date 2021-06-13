package io.github.thiagolvlsantos.git.transactions.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import io.github.thiagolvlsantos.git.transactions.provider.GitAuditHelper;
import io.github.thiagolvlsantos.git.transactions.provider.IGitAudit;

@ContextConfiguration(classes = IGitAuditTest.Config.class)
@SpringBootTest
class IGitAuditTest {

	public static class Config {
		@Bean
		public IGitAudit audit() {
			return new IGitAudit() {
				@Override
				public String username() {
					return "Thiago";
				}
			};
		}
	}

	@Test
	void defined(@Autowired ApplicationContext ctx) throws Exception {
		IGitAudit s = GitAuditHelper.audit(ctx);
		assertThat(s.username()).isEqualTo("Thiago");
	}
}
