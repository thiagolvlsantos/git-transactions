package com.thiagolvlsantos.gitt.read;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.IGitProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GitReadListener implements ApplicationListener<GitReadEvent> {

	private @Autowired ApplicationContext context;

	@Override
	public void onApplicationEvent(GitReadEvent event) {
		try {
			IGitProvider provider = context.getBean(IGitProvider.class);
			String group = event.getAnnotation().value();
			switch (event.getType()) {
			case INIT:
				provider.pull(group);
				break;
			case SUCCESS:
				provider.push(group);
				break;
			case FAILURE:
				provider.clean(group);
				break;
			}
		} catch (GitAPIException e) {
			if(log.isDebugEnabled()) {
				log.debug(e.getMessage(),e);
			}
			throw new RuntimeException(e);
		}
	}
}