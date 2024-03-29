package io.github.thiagolvlsantos.git.transactions.write;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;

@Component
public class GitWriteListener implements ApplicationListener<GitWriteEvent> {

	private @Autowired ApplicationContext context;

	@Override
	public void onApplicationEvent(GitWriteEvent event) {
		try {
			IGitProvider provider = context.getBean(IGitProvider.class);
			GitWriteDynamic dynamic = event.getDynamic();
			List<String> gs = new LinkedList<>();
			String group = dynamic.value();
			if (!group.isEmpty()) {
				gs.add(group);
			}
			GitWriteDirDynamic[] values = dynamic.values();
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
			throw new GitTransactionsException(e.getMessage(), e);
		}
	}
}