package com.thiagolvlsantos.gitt.id;

public class SessionIdHolderThreadLocal implements ISessionIdHolder {

	private ThreadLocal<String> ids = new ThreadLocal<>();

	@Override
	public String current() {
		String id = ids.get();
		if (id == null) {
			id = String.valueOf(Thread.currentThread().getName() + "_" + System.currentTimeMillis());
			ids.set(id);
		}
		return id;
	}

	@Override
	public void clear() {
		ids.set(null);
	}
}