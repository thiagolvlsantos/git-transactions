package com.thiagolvlsantos.gitt.file;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.provider.IGitProvider;

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
			Git git = provider.git(group);
			StringBuilder msg = new StringBuilder();
			for (FileItem item : event.getItems()) {
				String pattern = provider.normalize(group, item.getFile().toString());
				switch (item.getStatus()) {
				case CREATE:
					AddCommand add = git.add().addFilepattern(pattern);
					msg.append("New: " + pattern + "\n");
					DirCache addResult = add.call();
					if (log.isInfoEnabled()) {
						log.info(group + ".NEW:" + addResult);
					}
					break;
				case MODIFY:
					AddCommand update = git.add().addFilepattern(pattern);
					msg.append("Updated: " + pattern + "\n");
					DirCache addResultUpdate = update.call();
					if (log.isInfoEnabled()) {
						log.info(group + ".UPDATED:" + addResultUpdate);
					}
					break;
				case DELETE:
					RmCommand delete = git.rm().addFilepattern(pattern);
					msg.append("Deleted: " + pattern + "\n");
					DirCache deleteResult = delete.call();
					if (log.isInfoEnabled()) {
						log.info(group + ".DELETED:" + deleteResult);
					}
					break;
				}
			}
			String tmp = msg.toString();
			if (log.isInfoEnabled()) {
				log.info(group + ".COMMIT MESSAGE:" + tmp);
			}
			provider.commit(group, tmp);
		} catch (GitAPIException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}