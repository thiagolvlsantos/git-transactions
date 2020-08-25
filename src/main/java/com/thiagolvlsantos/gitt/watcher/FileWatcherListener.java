package com.thiagolvlsantos.gitt.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.file.EFileStatus;
import com.thiagolvlsantos.gitt.file.FileEvent;
import com.thiagolvlsantos.gitt.file.FileItem;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileWatcherListener implements ApplicationListener<FileWatcherEvent> {

	@Value("${gitt.filesystem.check.interval:50}")
	private long fileSystemCheckInterval;
	private Map<String, Watcher> watchers = new HashMap<>();
	private @Autowired ApplicationEventPublisher publisher;

	@Override
	public void onApplicationEvent(FileWatcherEvent event) {
		switch (event.getType()) {
		case START:
			start(event.getGroup(), event.getDir());
			break;
		case STOP:
			stop(event.getGroup(), event.getDir());
			break;
		}
	}

	public Watcher start(String group, Path dir) {
		Watcher tmp = new Watcher(group, dir, true);
		String key = key(group, dir);
		watchers.compute(key, (p, w) -> {
			if (w != null) {
				w.setActive(false);
			}
			return tmp;
		});
		tmp.start();
		while (!tmp.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (log.isInfoEnabled()) {
			log.info("FileWatcher.start({}) size={}, keys={}", key, watchers.size(), watchers.keySet());
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
			tmp.setActive(false);
		}
		try {
			tmp.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (log.isInfoEnabled()) {
			log.info("FileWatcher.stop({}) size={}, keys={}", key, watchers.size(), watchers.keySet());
		}
		return tmp;
	}

	@Setter
	@AllArgsConstructor
	public class Watcher extends Thread {
		private String group;
		private Path dir;
		private boolean active;

		@SuppressWarnings("unchecked")
		public void run() {
			try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
				WatchKey register = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				List<FileItem> items = new LinkedList<>();
				while (active) {
					WatchKey key;
					try {
						key = watcher.poll(fileSystemCheckInterval, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						if (log.isDebugEnabled()) {
							log.debug("POLL error", e);
						}
						break;
					}
					if (key != null && active) {
						items.clear();
						for (WatchEvent<?> event : key.pollEvents()) {
							WatchEvent.Kind<?> kind = event.kind();
							if (kind == OVERFLOW) {
								continue;
							}
							EFileStatus status = null;
							if (kind == ENTRY_CREATE) {
								status = EFileStatus.CREATE;
							}
							if (kind == ENTRY_MODIFY) {
								status = EFileStatus.MODIFY;
							}
							if (kind == ENTRY_DELETE) {
								status = EFileStatus.DELETE;
							}
							WatchEvent<Path> ev = (WatchEvent<Path>) event;
							Path filename = ev.context();
							Path child = dir.resolve(filename);
							File file = child.toFile();
							if (!file.toString().contains(".git")) {
								items.add(new FileItem(file, status));
							} else {
								log.info("Ignore:" + file);
							}
						}
						publisher.publishEvent(new FileEvent(this, group, items));
						boolean valid = key.reset();
						if (!valid) {
							break;
						}
					}
				}
				register.cancel();
			} catch (Exception e) {
				if (log.isInfoEnabled()) {
					log.info(e.getMessage(), e);
				}
				throw new RuntimeException(e);
			}
		}
	}
}
