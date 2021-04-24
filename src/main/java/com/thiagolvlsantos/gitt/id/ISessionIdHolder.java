package com.thiagolvlsantos.gitt.id;

public interface ISessionIdHolder {

	String current();

	void clear();

	ISessionIdHolder INSTANCE = new SessionIdHolderThreadLocal();
}
