package io.github.thiagolvlsantos.git.transactions.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import io.github.thiagolvlsantos.git.transactions.id.ISessionIdHolder;
import io.github.thiagolvlsantos.git.transactions.id.impl.SessionIdHolderHelper;

@ContextConfiguration(classes = ISessionIdHolderDefaultTest.Config.class)
@SpringBootTest
class ISessionIdHolderDefaultTest {

	public static class Config {
	}

	@Test
	void defined(@Autowired ApplicationContext ctx) throws Exception {
		ISessionIdHolder s = SessionIdHolderHelper.holder(ctx);
		assertThat(s.current()).isNotBlank();
	}
}