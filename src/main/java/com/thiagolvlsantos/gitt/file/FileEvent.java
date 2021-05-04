package com.thiagolvlsantos.gitt.file;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@SuppressWarnings("serial")
public class FileEvent extends ApplicationEvent {
	private String group;
	private List<FileItem> items;

	public FileEvent(Object source, String group, List<FileItem> items) {
		super(source);
		this.group = group;
		this.items = items;
	}

	public FileEvent(Object source, String group, FileItem... items) {
		super(source);
		this.group = group;
		this.items = Arrays.asList(items);
	}
}