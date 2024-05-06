package io.github.thiagolvlsantos.git.transactions.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import io.github.thiagolvlsantos.git.transactions.IGitAnnotation;
import io.github.thiagolvlsantos.git.transactions.IGitTransaction;
import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGitTransaction implements IGitTransaction {

	protected static final String TRANSACTION_LEVEL = "gtlevel";
	protected AtomicInteger counter = new AtomicInteger(0);
	protected Map<Integer, String> levels = new ConcurrentHashMap<>();

	@Override
	public void beginTransaction(ApplicationContext context, IGitAnnotation annotation)
			throws GitTransactionsException {
		try {
			synchronized (counter) {
				setGitTransactionLevel();
				log.debug("");
				log.debug("** @@@@@@@@@@@@@@ START: {} >>>>>>>>>>>>>>>>>>> ", annotation.value());
				log.debug("push({}): {}", counter.intValue(), annotation.value());
				counter.incrementAndGet();
				context.getBean(IGitProvider.class).init(annotation);
			}
		} catch (BeansException | GitAPIException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new GitTransactionsException(e.getMessage(), e);
		}
	}

	protected void setGitTransactionLevel() {
		MDC.put(TRANSACTION_LEVEL, levels.computeIfAbsent(counter.intValue(), n -> {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < n; i++) {
				sb.append("    ");
			}
			return sb.toString();
		}));
	}

	@Override
	public void endTransaction(ApplicationContext context, IGitAnnotation annotation) throws GitTransactionsException {
		try {
			synchronized (counter) {
				int current = counter.decrementAndGet();
				endOnCounter(context, annotation, current);
				log.debug("pop({}): {}", current, annotation);
				log.debug("** <<<<<<<<<<<<<<<<<< END: {} @@@@@@@@@@@@@@", annotation.value());
				log.debug("");
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

	protected void endOnCounter(ApplicationContext context, IGitAnnotation annotation, int current)
			throws GitAPIException {
		if (current == 0) {
			context.getBean(IGitProvider.class).clean(annotation);
		}
	}
}