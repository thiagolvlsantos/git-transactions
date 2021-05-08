package com.thiagolvlsantos.git.transaction.read;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.git.transaction.provider.IGitProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GitReadListener implements ApplicationListener<GitReadEvent> {

	private @Autowired ApplicationContext context;

	@Override
	public void onApplicationEvent(GitReadEvent event) {
		try {
			IGitProvider provider = context.getBean(IGitProvider.class);
			GitReadDynamic annotation = event.getAnnotation();
			List<String> gs = new LinkedList<>();
			String group = annotation.value();
			if (!group.isEmpty()) {
				gs.add(group);
			}
			GitReadDirDynamic[] values = annotation.values();
			for (GitReadDirDynamic v : values) {
				gs.add(v.value());
			}
			for (String g : gs) {
				switch (event.getType()) {
				case INIT:
					provider.pullRead(g);
					break;
				case SUCCESS:
					provider.pushRead(g);
					break;
				case FAILURE:
					provider.cleanRead(g);
					break;
				}
			}
		} catch (GitAPIException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new RuntimeException(e);
		}
	}
}