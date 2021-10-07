package io.github.thiagolvlsantos.git.transactions.read;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
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
			List<GitCommitValue> commits = event.getCommits();
			for (String g : gs) {
				switch (event.getType()) {
				case INIT:
					provider.pullRead(g);
					setCommit(provider, g, findCommit(g, commits));
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
			throw new GitTransactionsException(e.getMessage(), e);
		}
	}

	protected GitCommitValue findCommit(String group, List<GitCommitValue> commits) {
		for (GitCommitValue tmp : commits) {
			String annotationGroup = tmp.getAnnotation().value();
			if (group.equalsIgnoreCase(annotationGroup)) {
				return tmp;
			}
		}
		return null;
	}

	protected void setCommit(IGitProvider provider, String group, GitCommitValue commitValue) throws GitAPIException {
		if (commitValue != null) {
			Object value = commitValue.getValue();
			if (value instanceof String) {
				provider.setCommit(group, String.valueOf(value));
			} else if (value instanceof Number) {
				provider.setTimestamp(group, (long) value);
			} else {
				throw new GitTransactionsException(
						"Only 'String' (commit id) or 'Long' (timestamp) are allowed with @GitCommit annotation.",
						null);
			}
		}
	}

}