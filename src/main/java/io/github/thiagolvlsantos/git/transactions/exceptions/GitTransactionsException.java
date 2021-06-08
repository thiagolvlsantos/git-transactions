package io.github.thiagolvlsantos.git.transactions.exceptions;

@SuppressWarnings("serial")
public class GitTransactionsException extends RuntimeException {

	public GitTransactionsException() {
		super();
	}

	public GitTransactionsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GitTransactionsException(String message, Throwable cause) {
		super(message, cause);
	}

	public GitTransactionsException(String message) {
		super(message);
	}

	public GitTransactionsException(Throwable cause) {
		super(cause);
	}
}
