package com.thiagolvlsantos.gitt.id;

import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.scope.AspectScoped;

@Component
@AspectScoped
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
}