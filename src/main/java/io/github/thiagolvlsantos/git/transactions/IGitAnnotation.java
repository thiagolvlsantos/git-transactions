package io.github.thiagolvlsantos.git.transactions;

public interface IGitAnnotation {

	String value();

	@SuppressWarnings("unchecked")
	default <T extends IGitAnnotation> T[] values() {
		return (T[]) new Object[0];
	}
}
