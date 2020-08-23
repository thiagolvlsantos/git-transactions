package com.thiagolvlsantos.gitt.file;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileListenerPrint implements ApplicationListener<FileEvent> {

	@Override
	public void onApplicationEvent(FileEvent event) {
		log.info("ITEMS:{}", event.getItems());
	}
}
