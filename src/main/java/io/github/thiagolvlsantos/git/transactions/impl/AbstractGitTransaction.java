package io.github.thiagolvlsantos.git.transactions.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import io.github.thiagolvlsantos.git.transactions.IGitTransaction;
import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGitTransaction implements IGitTransaction {

	private static final String TRANSACTION_LEVEL = "gtlevel";
	private AtomicInteger counter = new AtomicInteger(0);
	private Map<Integer, String> gaps = new ConcurrentHashMap<>();

	@Override
	public void beginTransaction(ApplicationContext context) throws GitTransactionsException {
		try {
			synchronized (counter) {
				setGitTransactionLevel();
				log.debug("push({})", counter.intValue());
				counter.incrementAndGet();
				context.getBean(IGitProvider.class).init();
			}
		} catch (BeansException | GitAPIException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new GitTransactionsException(e.getMessage(), e);
		}
	}

	protected void setGitTransactionLevel() {
		MDC.put(TRANSACTION_LEVEL, gaps.computeIfAbsent(counter.intValue(), n -> {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < n; i++) {
				sb.append("    ");
			}
			return sb.toString();
		}));
	}

	@Override
	public void endTransaction(ApplicationContext context) throws GitTransactionsException {
		try {
			synchronized (counter) {
				int current = counter.decrementAndGet();
				if (current == 0) {
					context.getBean(IGitProvider.class).clean();
				}
				log.debug("pop({})", current);
				setGitTransactionLevel();
			}
		} catch (BeansException | GitAPIException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new GitTransactionsException(e.getMessage(), e);
		} finally {
			MDC.remove(TRANSACTION_LEVEL);
		}
	}
}