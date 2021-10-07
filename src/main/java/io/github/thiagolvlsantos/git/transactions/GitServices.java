package io.github.thiagolvlsantos.git.transactions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.file.EFileStatus;
import io.github.thiagolvlsantos.git.transactions.file.FileEvent;
import io.github.thiagolvlsantos.git.transactions.file.FileItem;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
import io.github.thiagolvlsantos.git.transactions.provider.IGitRouter;
import lombok.SneakyThrows;

@Component
public class GitServices {

	private @Autowired ApplicationContext context;
	private @Autowired ApplicationEventPublisher publisher;

	@SneakyThrows
	public void setTimestamp(String group, Long timestamp) {
		context.getBean(IGitProvider.class).setTimestamp(group, timestamp);
	}

	@SneakyThrows
	public void setCommit(String group, String commit) {
		context.getBean(IGitProvider.class).setCommit(group, commit);
	}

	public File readDirectory(String group) {
		return context.getBean(IGitProvider.class).directoryRead(group);
	}

	@SneakyThrows
	public File readDirectory(String group, Class<? extends IGitRouter> router, Object... args) {
		return readDirectory(renamedGroup(group, router, args));
	}

	public File writeDirectory(String group) {
		return context.getBean(IGitProvider.class).directoryWrite(group);
	}

	@SneakyThrows
	public File writeDirectory(String group, Class<? extends IGitRouter> router, Object... args) {
		return writeDirectory(renamedGroup(group, router, args));
	}

	public String renamedGroup(String group, Class<? extends IGitRouter> router, Object... args)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String value = group;
		if (router != IGitRouter.class) {
			value = value + IGitRouter.SEPARATOR + router.getDeclaredConstructor().newInstance().route(group, args);
		}
		return value;
	}

	public void created(Object source, String group, File... files) {
		notify(source, group, EFileStatus.CREATE, files);
	}

	public void modified(Object source, String group, File... files) {
		notify(source, group, EFileStatus.MODIFY, files);
	}

	public void deleted(Object source, String group, File... files) {
		notify(source, group, EFileStatus.DELETE, files);
	}

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

	@SneakyThrows
	public Iterable<RevCommit> history(String group, File path, Integer skip, Integer max) {
		return context.getBean(IGitProvider.class).logRead(group, path != null ? String.valueOf(path) : null, skip,
				max);
	}
}