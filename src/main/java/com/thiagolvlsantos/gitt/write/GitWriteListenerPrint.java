package com.thiagolvlsantos.gitt.write;

import org.springframework.context.ApplicationListener;

//@Component
public class GitWriteListenerPrint implements ApplicationListener<GitWriteEvent> {

	@Override
	public void onApplicationEvent(GitWriteEvent event) {
		System.out.println("WRITE>>>>>" + event);
	}
}
