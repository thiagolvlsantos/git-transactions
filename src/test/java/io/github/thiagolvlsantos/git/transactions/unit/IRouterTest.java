package io.github.thiagolvlsantos.git.transactions.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.thiagolvlsantos.git.transactions.provider.IGitRouter;

class IRouterTest {

	@Test
	void defaultValue() {
		IGitRouter r = new IGitRouter() {
		};
		assertThat(r.route(null, null)).isBlank();
	}
}
