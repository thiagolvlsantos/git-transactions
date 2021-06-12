package io.github.thiagolvlsantos.git.transactions.watcher;

import java.io.File;
import java.nio.file.Path;

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
	private String group;
	private FileAlterationObserver observer;
	private FileAlterationListener listener;

	public Watcher(String group, Path dir, ApplicationEventPublisher publisher) {
		this.group = group;
		this.observer = new FileAlterationObserver(dir.toFile(),
				pathname -> !pathname.getAbsolutePath().contains(".git"));
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