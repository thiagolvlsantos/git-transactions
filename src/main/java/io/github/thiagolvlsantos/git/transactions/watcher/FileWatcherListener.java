package io.github.thiagolvlsantos.git.transactions.watcher;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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
		case IGNORE:
			ignore(group, dir);
			break;
		default:
			log.info("File watcher received: {}", event);
		}
	}

	public Watcher start(String group, Path dir) {
		if (shutdownHook == null) {
			shutdownHook = new Thread() {
				@Override
				public void run() {
					for (Watcher t : watchers.values()) {
						t.finish();
						log.info("Shutdown:{}", t.getGroup());
					}
				}
			};
			Runtime.getRuntime().addShutdownHook(shutdownHook);
		}
		Watcher tmp = new Watcher(group, dir, publisher);
		String key = key(group, dir);
		watchers.compute(key, (k, v) -> {
			if (v != null) {
				stop(group, dir);
			}
			return tmp;
		});
		log.debug("FileWatcher.start({}) size={}, keys={}", key, watchers.size(), watchers.keySet());
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
		log.debug("FileWatcher.stop({}) size={}, keys={}", key, watchers.size(), watchers.keySet());
		return tmp;
	}

	public Watcher ignore(String group, Path dir) {
		String key = key(group, dir);
		Watcher tmp = watchers.remove(key);
		if (tmp != null) {
			tmp.ignore();
		}
		log.debug("FileWatcher.ignore({}) size={}, keys={}", key, watchers.size(), watchers.keySet());
		return tmp;
	}
}