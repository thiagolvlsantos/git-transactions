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

class GitConfigurationTest {

	private GitConfiguration config;

	@BeforeEach
	public void before() {
		// Emulates:
		//
		// gitt.repository.base=data1
		// gitt.repository.all.other=data2
		//
		// gitt.repository.project_template.url=http://$route$
		// gitt.repository.project_template.other=#{null}

		config = new GitConfiguration();

		Map<String, Object> repo = new HashMap<>();
		repo.put("base", "data1");

		Map<String, Object> all = new HashMap<>();
		repo.put("all", all);
		all.put("other", "data2");

		Map<String, Object> template = new HashMap<>();
		template.put("url", "http://$route$");
		template.put("other", null);
		repo.put("project_template", template);

		config.setRepository(repo);
	}

	@Test
	void templateValues() {
		assertThat(config.get("project_template.url")).isEqualTo("http://template");
		assertThat(config.get("project_template.other")).isNull();
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
	@ValueSource(strings = { "all.not.found" })
	void notFoundValues(String property) {
		assertThatThrownBy(() -> config.get(property))//
				.isExactlyInstanceOf(GitTransactionsException.class)//
				.hasMessage("Missing property>" + property);
	}
}
