package com.thiagolvlsantos.git.transaction.id;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSessionIdHolder implements ISessionIdHolder {

	public static ISessionIdHolder INSTANCE = new AbstractSessionIdHolder() {
	};

	private String time;

	@Override
	public String current() {
		if (time == null) {
			time = String.valueOf(System.currentTimeMillis());
			if (log.isInfoEnabled()) {
				log.info("{}.ID={}", getClass().getName(), time);
			}
		}
		return time;
	}
}