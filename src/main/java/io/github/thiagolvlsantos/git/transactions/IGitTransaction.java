package io.github.thiagolvlsantos.git.transactions;

import org.springframework.context.ApplicationContext;

import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;

public interface IGitTransaction {

	void begin(ApplicationContext context) throws GitTransactionsException;

	int depth(ApplicationContext context) throws GitTransactionsException;

	void finish(ApplicationContext context) throws GitTransactionsException;
}
