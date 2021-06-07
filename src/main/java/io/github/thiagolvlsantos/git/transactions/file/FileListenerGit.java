package io.github.thiagolvlsantos.git.transactions.file;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileListenerGit implements ApplicationListener<FileEvent> {

	private @Autowired ApplicationContext context;

	@Override
	public void onApplicationEvent(FileEvent event) {
		try {
			IGitProvider provider = context.getBean(IGitProvider.class);
			String group = event.getGroup();
			Git git = provider.gitWrite(group);
			StringBuilder msg = new StringBuilder();
			for (FileItem item : event.getItems()) {
				String pattern = provider.normalizeWrite(group, item.getFile().toString());
				switch (item.getStatus()) {
				case CREATE:
					create(group, git, msg, pattern);
					break;
				case MODIFY:
					modify(group, git, msg, pattern);
					break;
				case DELETE:
					delete(group, git, msg, pattern);
					break;
				}
			}
			if (msg.length() > 0) {
				msg.setLength(msg.length() - 1);
				commitWrite(group, msg, provider);
			}
		} catch (GitAPIException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new RuntimeException(e);
		}
	}

	private void create(String group, Git git, StringBuilder msg, String pattern)
			throws GitAPIException, NoFilepatternException {
		AddCommand add = git.add().addFilepattern(pattern);
		msg.append("Added: " + pattern + "\n");
		DirCache addResult = add.call();
		if (log.isDebugEnabled()) {
			log.debug(group + ".ADDED: " + addResult);
		}
	}

	private void modify(String group, Git git, StringBuilder msg, String pattern)
			throws GitAPIException, NoFilepatternException {
		AddCommand update = git.add().addFilepattern(pattern);
		msg.append("Updated: " + pattern + "\n");
		DirCache addResultUpdate = update.call();
		if (log.isDebugEnabled()) {
			log.debug(group + ".UPDATED: " + addResultUpdate);
		}
	}

	private void delete(String group, Git git, StringBuilder msg, String pattern)
			throws GitAPIException, NoFilepatternException {
		RmCommand delete = git.rm().addFilepattern(pattern);
		msg.append("Deleted: " + pattern + "\n");
		DirCache deleteResult = delete.call();
		if (log.isDebugEnabled()) {
			log.debug(group + ".DELETED: " + deleteResult);
		}
	}

	private void commitWrite(String group, StringBuilder msg, IGitProvider provider) throws GitAPIException {
		String tmp = msg.toString();
		if (log.isInfoEnabled()) {
			log.info(group + ".MESSAGE:\n" + tmp);
		}
		RevCommit commit = provider.commitWrite(group, tmp);
		if (log.isInfoEnabled()) {
			log.info(group + ".COMMIT: " + commit);
		}
	}
}