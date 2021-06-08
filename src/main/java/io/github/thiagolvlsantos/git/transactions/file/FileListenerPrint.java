package io.github.thiagolvlsantos.git.transactions.file;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("test")
@Slf4j
public class FileListenerPrint implements ApplicationListener<FileEvent> {

	@Override
	public void onApplicationEvent(FileEvent event) {
		if (log.isInfoEnabled()) {
			log.info("ITEMS: " + event.getItems());
		}
	}
}
