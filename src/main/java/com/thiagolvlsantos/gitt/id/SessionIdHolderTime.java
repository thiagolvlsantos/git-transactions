package com.thiagolvlsantos.gitt.id;

public class SessionIdHolderTime implements ISessionIdHolder {

	private Long time;

	@Override
	public String current() {
		if (time == null) {
			time = System.currentTimeMillis();
		}
		return String.valueOf(time);
	}

	@Override
	public void clear() {
		time = null;
	}
}