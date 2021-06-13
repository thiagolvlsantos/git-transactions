package io.github.thiagolvlsantos.git.transactions.exceptions;

@SuppressWarnings("serial")
public class GitTransactionsException extends RuntimeException {

	public GitTransactionsException(String message, Throwable cause) {
		super(message, cause);
	}
}
