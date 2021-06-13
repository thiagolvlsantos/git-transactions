package io.github.thiagolvlsantos.git.transactions.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
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
		repo.put("base", "data1");

		Map<String, Object> all = new HashMap<>();
		all.put("other", "data2");
		repo.put("all", all);

		Map<String, Object> tmp = new HashMap<>();
		tmp.put("url", "http://$route$");
		repo.put("project_template", tmp);

		config.setRepository(repo);
	}

	@Test
	void templateValue() {
		assertThat(config.get("project_template.url")).isEqualTo("http://template");
	}

	@ParameterizedTest
	@NullSource
	void nullValue(String property) {
		assertThat(config.get(property)).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = { "base", "all.other" })
	void foundValues(String property) {
		assertThat(config.get(property)).isNotBlank();
	}

	@ParameterizedTest
	@ValueSource(strings = { "notfound", "all.notfound", "any.notfound" })
	void notFoundValues(String property) {
		assertThatThrownBy(() -> config.get(property))//
				.isExactlyInstanceOf(GitTransactionsException.class)//
				.hasMessage("Missing property>" + property);
	}
}
