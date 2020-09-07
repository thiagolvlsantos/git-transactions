package com.thiagolvlsantos.gitt.file;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class FileServices {

	private @Autowired ApplicationEventPublisher publisher;

	public void notify(Object source, String group, EFileStatus state, File... files) {
		if (group == null) {
			throw new IllegalArgumentException("Group should be not null.");
		}
		if (state == null) {
			throw new IllegalArgumentException("State should be not null.");
		}
		if (files == null) {
			throw new IllegalArgumentException("Files should be not null.");
		}
		List<FileItem> items = Arrays.asList(files).stream().map(f -> new FileItem(f, state))
				.collect(Collectors.toList());
		publisher.publishEvent(new FileEvent(source, group, items));
	}
}
