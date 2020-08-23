package com.thiagolvlsantos.gitt.read;

import org.springframework.context.ApplicationListener;

//@Component
public class GitReadListenerPrint implements ApplicationListener<GitReadEvent> {

	@Override
	public void onApplicationEvent(GitReadEvent event) {
		System.out.println("READ<<<<<" + event);
	}
}
