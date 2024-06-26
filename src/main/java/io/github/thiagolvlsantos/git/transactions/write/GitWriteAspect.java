package io.github.thiagolvlsantos.git.transactions.write;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.AbstractGitAspect;
import io.github.thiagolvlsantos.git.transactions.GitRepo;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
import io.github.thiagolvlsantos.git.transactions.provider.IGitRouter;
import io.github.thiagolvlsantos.git.transactions.watcher.EWatcherAction;
import io.github.thiagolvlsantos.git.transactions.watcher.FileWatcherEvent;
import lombok.SneakyThrows;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class GitWriteAspect extends AbstractGitAspect<GitWrite, GitWriteDynamic> {

	private @Autowired ApplicationEventPublisher publisher;

	@Around("@annotation(io.github.thiagolvlsantos.git.transactions.write.GitWrite)")
	public Object write(ProceedingJoinPoint jp) throws Throwable {
		return perform(jp, GitWrite.class);
	}

	@Override
	@SneakyThrows
	protected GitWriteDynamic toDynamic(ProceedingJoinPoint jp, GitWrite annotation) {
		String value = null;
		List<GitWriteDirDynamic> list = null;
		if (annotation != null) {
			value = annotation.value();
			Class<? extends IGitRouter> router = annotation.router();
			if (router != IGitRouter.class) {
				value = value + IGitRouter.SEPARATOR
						+ router.getDeclaredConstructor().newInstance().route(value, jp.getArgs());
			}
			if (value == null || value.isEmpty()) {
				GitRepo repo = AnnotationUtils.findAnnotation(jp.getThis().getClass(), GitRepo.class);
				if (repo != null) {
					value = repo.value();
				}
			}
			list = Stream.of(annotation.values())
					.map(v -> GitWriteDirDynamic.builder().value(v.value()).watcher(v.watcher()).build())
					.collect(Collectors.toList());
		}
		GitWriteDirDynamic[] values = list != null ? list.toArray(new GitWriteDirDynamic[0])
				: new GitWriteDirDynamic[0];
		return GitWriteDynamic.builder().value(value).values(values).build();
	}

	@Override
	protected void init(ProceedingJoinPoint jp, GitWriteDynamic dynamic) {
		publisher.publishEvent(new GitWriteEvent(jp, dynamic, EGitWrite.INIT));
		startWatcher(dynamic);
	}

	protected void startWatcher(GitWriteDynamic dynamic) {
		IGitProvider provider = context.getBean(IGitProvider.class);
		if (dynamic.watcher() && !dynamic.value().isEmpty()) {
			String group = dynamic.value();
			Path path = provider.directoryWrite(group).toPath();
			publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.START, group, path));
		}
		for (GitWriteDirDynamic d : dynamic.values()) {
			if (d.watcher()) {
				String g = d.value();
				Path p = provider.directoryWrite(g).toPath();
				publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.START, g, p));
			}
		}
	}

	@Override
	protected Object success(ProceedingJoinPoint jp, GitWriteDynamic dynamic, Object result) {
		stopWatcher(dynamic, EWatcherAction.STOP);
		GitWriteEvent event = new GitWriteEvent(jp, dynamic, EGitWrite.SUCCESS, result);
		publisher.publishEvent(event);
		return event.getResult();
	}

	@Override
	protected Throwable error(ProceedingJoinPoint jp, GitWriteDynamic dynamic, Throwable e) {
		stopWatcher(dynamic, EWatcherAction.IGNORE);
		GitWriteEvent event = new GitWriteEvent(jp, dynamic, EGitWrite.FAILURE, e);
		publisher.publishEvent(event);
		return event.getError();
	}

	protected void stopWatcher(GitWriteDynamic dynamic, EWatcherAction action) {
		IGitProvider provider = context.getBean(IGitProvider.class);
		if (dynamic.watcher() && !dynamic.value().isEmpty()) {
			String group = dynamic.value();
			Path path = provider.directoryWrite(group).toPath();
			publisher.publishEvent(new FileWatcherEvent(this, action, group, path));
		}
		for (GitWriteDirDynamic d : dynamic.values()) {
			if (d.watcher()) {
				String g = d.value();
				Path p = provider.directoryWrite(g).toPath();
				publisher.publishEvent(new FileWatcherEvent(this, action, g, p));
			}
		}
	}

	@Override
	@SneakyThrows
	protected void finish(GitWriteDynamic dynamic) {
		IGitProvider provider = context.getBean(IGitProvider.class);
		if (!dynamic.value().isEmpty()) {
			provider.cleanWrite(dynamic.value());
		}
		for (GitWriteDirDynamic d : dynamic.values()) {
			provider.cleanWrite(d.value());
		}
	}
}