package com.thiagolvlsantos.gitt.read;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.IGitProvider;
import com.thiagolvlsantos.gitt.watcher.EWatcherAction;
import com.thiagolvlsantos.gitt.watcher.FileWatcherEvent;

@Component
public class GitReadListener implements ApplicationListener<GitReadEvent> {

	private @Autowired ApplicationContext context;
	private @Autowired ApplicationEventPublisher publisher;

	@Override
	public void onApplicationEvent(GitReadEvent event) {
		try {
			IGitProvider provider = context.getBean(IGitProvider.class);
			String group = event.getAnnotation().value();
			switch (event.getType()) {
			case INIT:
				provider.pull(group);
				publisher.publishEvent(
						new FileWatcherEvent(this, EWatcherAction.START, group, provider.directory(group).toPath()));
				break;
			case SUCCESS:
				publisher.publishEvent(
						new FileWatcherEvent(this, EWatcherAction.STOP, group, provider.directory(group).toPath()));
				provider.push(group);
				break;
			case FAILURE:
				publisher.publishEvent(
						new FileWatcherEvent(this, EWatcherAction.STOP, group, provider.directory(group).toPath()));
				provider.clean(group);
				break;
			}
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}
}