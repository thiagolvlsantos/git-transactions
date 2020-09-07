package com.thiagolvlsantos.gitt.write;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.IGitProvider;

@Component
public class GitWriteListener implements ApplicationListener<GitWriteEvent> {

	private @Autowired ApplicationContext context;

	@Override
	public void onApplicationEvent(GitWriteEvent event) {
		try {
			IGitProvider provider = context.getBean(IGitProvider.class);
			String group = event.getAnnotation().value();
			switch (event.getType()) {
			case INIT:
				provider.pullWrite(group);
				break;
			case SUCCESS:
				provider.pushWrite(group);
				break;
			case FAILURE:
				provider.cleanWrite(group);
				break;
			}
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}
}