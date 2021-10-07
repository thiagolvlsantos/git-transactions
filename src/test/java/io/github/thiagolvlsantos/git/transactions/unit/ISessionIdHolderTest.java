package io.github.thiagolvlsantos.git.transactions.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import io.github.thiagolvlsantos.git.transactions.id.ISessionIdHolder;
import io.github.thiagolvlsantos.git.transactions.id.impl.SessionIdHolderHelper;

@ContextConfiguration(classes = ISessionIdHolderTest.Config.class)
@SpringBootTest
class ISessionIdHolderTest {

	private static final String CURRENT = "fixed";

	public static class Config {
		@Bean
		public ISessionIdHolder holder() {
			return new ISessionIdHolder() {

				@Override
				public String current() {
					return CURRENT;
				}
			};
		}
	}

	@Test
	void defined(@Autowired ApplicationContext ctx) throws Exception {
		ISessionIdHolder s = SessionIdHolderHelper.holder(ctx);
		assertThat(s.current()).isEqualTo(CURRENT);
	}
}
