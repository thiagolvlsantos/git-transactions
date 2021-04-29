package com.thiagolvlsantos.gitt.read;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("test")
@Slf4j
public class GitReadListenerPrint implements ApplicationListener<GitReadEvent> {

	@Override
	public void onApplicationEvent(GitReadEvent event) {
		if (log.isInfoEnabled()) {
			log.info("READ<<<<<" + event);
		}
	}
}
