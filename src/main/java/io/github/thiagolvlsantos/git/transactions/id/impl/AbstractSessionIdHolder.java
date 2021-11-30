package io.github.thiagolvlsantos.git.transactions.id.impl;

import io.github.thiagolvlsantos.git.transactions.id.ISessionIdHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSessionIdHolder implements ISessionIdHolder {

	public static final ISessionIdHolder INSTANCE = new AbstractSessionIdHolder() {
	};

	private String time;

	@Override
	public String current() {
		if (time == null) {
			time = String.valueOf(System.currentTimeMillis());
			log.info("{}.ID={}", getClass().getName(), time);
		}
		return time;
	}
}