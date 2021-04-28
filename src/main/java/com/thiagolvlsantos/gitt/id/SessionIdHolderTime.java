package com.thiagolvlsantos.gitt.id;

public class SessionIdHolderTime implements ISessionIdHolder {

	public static ISessionIdHolder INSTANCE = new SessionIdHolderTime();

	private String time;

	@Override
	public String current() {
		if (time == null) {
			time = String.valueOf(System.currentTimeMillis());
		}
		return time;
	}

	@Override
	public void clear() {
		time = null;
	}
}