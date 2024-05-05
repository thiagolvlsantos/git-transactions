package io.github.thiagolvlsantos.git.transactions;

import org.springframework.context.ApplicationContext;

import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;

public interface IGitTransaction {

	void beginTransaction(ApplicationContext context) throws GitTransactionsException;

	void endTransaction(ApplicationContext context) throws GitTransactionsException;
}
