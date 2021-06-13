package io.github.thiagolvlsantos.git.transactions.mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.thiagolvlsantos.git.transactions.config.GitConfiguration;
import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;

class GitConfigurationTest {

	@Test
	void read() throws Exception {
		GitConfiguration config = new GitConfiguration();
		Map<String, Object> repo = new HashMap<>();
		repo.put("all.other", "data");
		config.setRepository(repo);
		assertThatThrownBy(() -> config.get("all.nothing"))//
				.isExactlyInstanceOf(GitTransactionsException.class)//
				.hasMessage("Missing property>all.nothing");
	}
}
