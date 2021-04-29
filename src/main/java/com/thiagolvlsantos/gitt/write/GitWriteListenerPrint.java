package com.thiagolvlsantos.gitt.write;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("test")
@Slf4j
public class GitWriteListenerPrint implements ApplicationListener<GitWriteEvent> {

	@Override
	public void onApplicationEvent(GitWriteEvent event) {
		if (log.isInfoEnabled()) {
			log.info("READ>>>>>" + event);
		}
	}
}
