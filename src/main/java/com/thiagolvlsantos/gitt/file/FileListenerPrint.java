package com.thiagolvlsantos.gitt.file;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class FileListenerPrint implements ApplicationListener<FileEvent> {

	@Override
	public void onApplicationEvent(FileEvent event) {
		System.out.println("ITEMS: " + event.getItems());
	}
}
