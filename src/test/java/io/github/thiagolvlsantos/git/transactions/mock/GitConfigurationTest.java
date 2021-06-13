package io.github.thiagolvlsantos.git.transactions.mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.thiagolvlsantos.git.transactions.config.GitConfiguration;
import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class GitConfigurationTest {

	private GitConfiguration config;

	@BeforeEach
	public void before() {
		config = new GitConfiguration();
		Map<String, Object> repo = new HashMap<>();
		repo.put("all.other", "data");
		config.setRepository(repo);
	}

	@ParameterizedTest
	@ValueSource(strings = { "notfound", "all.notfound" })
	void nullProperty(String property) {
		assertThatThrownBy(() -> config.get(property))//
				.isExactlyInstanceOf(GitTransactionsException.class)//
				.hasMessage("Missing property>" + property);
	}
}
