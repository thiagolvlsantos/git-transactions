package com.thiagolvlsantos.git.transaction.watcher;

import java.nio.file.Path;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class FileWatcherEvent extends ApplicationEvent {

	private EWatcherAction type;
	private String group;
	private Path dir;

	public FileWatcherEvent(Object source, EWatcherAction type, String group, Path dir) {
		super(source);
		this.type = type;
		this.group = group;
		this.dir = dir;
	}
}
