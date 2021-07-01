package io.github.thiagolvlsantos.git.transactions.config;

public class GitConstants {

	private GitConstants() {
	}

	// profiles of some classes in packages.
	public static final String PROFILE_TEST = "gitt_test";
	public static final String PROFILE_WEB = "gitt_web";

	// added to avoid explicit dependency of web scope in spring
	public static final String SCOPE_REQUEST = "request";
}
