package io.github.thiagolvlsantos.git.transactions.read;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.config.GitConstants;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile(GitConstants.PROFILE_TEST)
@Slf4j
public class GitReadListenerPrint implements ApplicationListener<GitReadEvent> {

	@Override
	public void onApplicationEvent(GitReadEvent event) {
		if (log.isInfoEnabled()) {
			log.info("READ<<<<<" + event);
		}
	}
}
