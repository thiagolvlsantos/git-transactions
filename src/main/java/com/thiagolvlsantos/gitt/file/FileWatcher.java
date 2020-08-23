package com.thiagolvlsantos.gitt.file;

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
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileWatcher {

	@Value("${gitt.filesystem.check.interval:1000}")
	private long fileSystemCheckInterval;
	private Map<String, Watcher> watchers = new HashMap<>();
	private @Autowired ApplicationEventPublisher publisher;

	public Watcher get(String group, Path dir) {
		File system = dir.toFile();
		if (!system.exists()) {
			system.mkdirs();
		}
		Watcher tmp = new Watcher(group, dir, true);
		watchers.compute(group + dir.toString(), (p, w) -> {
			if (w != null) {
				w.setActive(false);
			}
			return tmp;
		});
		tmp.start();
		return tmp;
	}

	public Watcher del(String group, Path dir) {
		Watcher tmp = watchers.remove(group + dir.toString());
		if (tmp != null) {
			tmp.setActive(false);
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
					if (key != null) {
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
