package io.github.thiagolvlsantos.git.transactions.file;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.config.GitConstants;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile(GitConstants.PROFILE_TEST)
@Slf4j
public class FileListenerPrint implements ApplicationListener<FileEvent> {

	@Override
	public void onApplicationEvent(FileEvent event) {
		log.info("ITEMS: {}", event.getItems());
	}
}
