package io.github.thiagolvlsantos.git.transactions.watcher;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.context.ApplicationEventPublisher;

import io.github.thiagolvlsantos.git.transactions.file.EFileStatus;
import io.github.thiagolvlsantos.git.transactions.file.FileEvent;
import io.github.thiagolvlsantos.git.transactions.file.FileItem;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class Watcher {
	private static final String GIT_DIR = ".git";

	private String group;
	private FileAlterationObserver observer;
	private FileAlterationListener listener;
	private ApplicationEventPublisher publisher;
	private List<FileItem> items = new LinkedList<>();

	public Watcher(String group, Path dir, ApplicationEventPublisher publisher) {
		this.group = group;
		this.publisher = publisher;
		this.observer = new FileAlterationObserver(dir.toFile(),
				pathname -> !pathname.getAbsolutePath().contains(GIT_DIR));
		this.observer.checkAndNotify(); // initial setup
		this.listener = new FileAlterationListenerAdaptor() {
			@Override
			public void onFileCreate(File file) {
				items.add(new FileItem(file, EFileStatus.CREATE));
			}

			@Override
			public void onFileDelete(File file) {
				items.add(new FileItem(file, EFileStatus.DELETE));
			}

			@Override
			public void onFileChange(File file) {
				items.add(new FileItem(file, EFileStatus.MODIFY));
			}
		};
		observer.addListener(listener);
	}

	@SneakyThrows
	public void finish() {
		observer.checkAndNotify();
		publisher.publishEvent(new FileEvent(this, group, items));
		observer.destroy();
	}
}