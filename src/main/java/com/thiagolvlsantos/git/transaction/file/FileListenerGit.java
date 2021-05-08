package com.thiagolvlsantos.git.transaction.file;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.git.transaction.provider.IGitProvider;

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
					AddCommand add = git.add().addFilepattern(pattern);
					msg.append("Added: " + pattern + "\n");
					DirCache addResult = add.call();
					if (log.isDebugEnabled()) {
						log.debug(group + ".ADDED: " + addResult);
					}
					break;
				case MODIFY:
					AddCommand update = git.add().addFilepattern(pattern);
					msg.append("Updated: " + pattern + "\n");
					DirCache addResultUpdate = update.call();
					if (log.isDebugEnabled()) {
						log.debug(group + ".UPDATED: " + addResultUpdate);
					}
					break;
				case DELETE:
					RmCommand delete = git.rm().addFilepattern(pattern);
					msg.append("Deleted: " + pattern + "\n");
					DirCache deleteResult = delete.call();
					if (log.isDebugEnabled()) {
						log.debug(group + ".DELETED: " + deleteResult);
					}
					break;
				}
			}
			if (msg.length() > 0) {
				msg.setLength(msg.length() - 1);
				String tmp = msg.toString();
				if (log.isInfoEnabled()) {
					log.info(group + ".MESSAGE:\n" + tmp);
				}
				RevCommit commit = provider.commitWrite(group, tmp);
				if (log.isInfoEnabled()) {
					log.info(group + ".COMMIT: " + commit);
				}
			}
		} catch (GitAPIException e) {
			if(log.isDebugEnabled()) {
				log.debug(e.getMessage(),e);
			}
			throw new RuntimeException(e);
		}
	}
}