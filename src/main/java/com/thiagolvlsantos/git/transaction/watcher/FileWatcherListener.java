package com.thiagolvlsantos.git.transaction.watcher;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.git.transaction.file.EFileStatus;
import com.thiagolvlsantos.git.transaction.file.FileEvent;
import com.thiagolvlsantos.git.transaction.file.FileItem;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileWatcherListener implements ApplicationListener<FileWatcherEvent> {

	private Map<String, Watcher> watchers = new HashMap<>();
	private @Autowired ApplicationEventPublisher publisher;
	private Thread shutdownHook;

	@Override
	public void onApplicationEvent(FileWatcherEvent event) {
		String group = event.getGroup();
		Path dir = event.getDir();
		switch (event.getType()) {
		case START:
			start(group, dir);
			break;
		case STOP:
			stop(group, dir);
			break;
		}
	}

	public Watcher start(String group, Path dir) {
		if (shutdownHook == null) {
			shutdownHook = new Thread() {
				@Override
				public void run() {
					for (Watcher t : watchers.values()) {
						t.finish();
						if (log.isInfoEnabled()) {
							log.info("Shutdown:" + t.getGroup());
						}
					}
				}
			};
			Runtime.getRuntime().addShutdownHook(shutdownHook);
		}
		Watcher tmp = new Watcher(group, dir);
		String key = key(group, dir);
		watchers.compute(key, (k, v) -> {
			if (v != null) {
				stop(group, dir);
			}
			return tmp;
		});
		if (log.isDebugEnabled()) {
			log.debug("FileWatcher.start({}) size={}, keys={}", key, watchers.size(), watchers.keySet());
		}
		return tmp;
	}

	private String key(String group, Path dir) {
		return group + ":" + dir.toString();
	}

	public Watcher stop(String group, Path dir) {
		String key = key(group, dir);
		Watcher tmp = watchers.remove(key);
		if (tmp != null) {
			tmp.finish();
		}
		if (log.isDebugEnabled()) {
			log.debug("FileWatcher.stop({}) size={}, keys={}", key, watchers.size(), watchers.keySet());
		}
		return tmp;
	}

	@Getter
	public class Watcher {
		private String group;
		private FileAlterationObserver observer;
		private FileAlterationListener listener;

		public Watcher(String group, Path dir) {
			this.group = group;
			this.observer = new FileAlterationObserver(dir.toFile(), new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return !pathname.getAbsolutePath().contains(".git");
				}
			});
			this.observer.checkAndNotify(); // initial setup
			this.listener = new FileAlterationListenerAdaptor() {
				@Override
				public void onFileCreate(File file) {
					publisher.publishEvent(new FileEvent(this, group, new FileItem(file, EFileStatus.CREATE)));
				}

				@Override
				public void onFileDelete(File file) {
					publisher.publishEvent(new FileEvent(this, group, new FileItem(file, EFileStatus.DELETE)));
				}

				@Override
				public void onFileChange(File file) {
					publisher.publishEvent(new FileEvent(this, group, new FileItem(file, EFileStatus.MODIFY)));
				}
			};
			observer.addListener(listener);
		}

		@SneakyThrows
		public void finish() {
			observer.checkAndNotify();
			observer.destroy();
		}
	}
}