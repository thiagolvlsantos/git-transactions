package com.thiagolvlsantos.gitt.write;

import java.util.LinkedList;
import java.util.List;

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
			GitWriteDynamic annotation = event.getAnnotation();
			List<String> gs = new LinkedList<>();
			String group = annotation.value();
			if (!group.isEmpty()) {
				gs.add(group);
			}
			GitWriteDirDynamic[] values = annotation.values();
			for (GitWriteDirDynamic v : values) {
				gs.add(v.value());
			}
			for (String g : gs) {
				switch (event.getType()) {
				case INIT:
					provider.pullWrite(g);
					break;
				case SUCCESS:
					provider.pushWrite(g);
					break;
				case FAILURE:
					provider.cleanWrite(g);
					break;
				}
			}
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}
}