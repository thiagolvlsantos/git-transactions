package io.github.thiagolvlsantos.git.transactions.write;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.GitRepo;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
import io.github.thiagolvlsantos.git.transactions.provider.IGitRouter;
import io.github.thiagolvlsantos.git.transactions.scope.AspectScope;
import io.github.thiagolvlsantos.git.transactions.watcher.EWatcherAction;
import io.github.thiagolvlsantos.git.transactions.watcher.FileWatcherEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@Order(10)
public class GitWriteAspect {

	private @Autowired ApplicationContext context;
	private @Autowired ApplicationEventPublisher publisher;

	@Around("@annotation(io.github.thiagolvlsantos.git.transactions.write.GitWrite)")
	public Object write(ProceedingJoinPoint jp) throws Throwable {
		AspectScope scope = context.getBean(AspectScope.class);
		scope.openAspect();
		Signature signature = jp.getSignature();
		String name = signature.getName();
		GitWriteDynamic dynamic = getDynamic(getAnnotation(signature, GitWrite.class), jp);
		long time = System.currentTimeMillis();
		init(jp, dynamic);
		log.info("** WRITE({}).init: {} ms, {} **", name, System.currentTimeMillis() - time, dynamic);
		try {
			time = System.currentTimeMillis();
			Object result = success(jp, dynamic, jp.proceed());
			log.info("** WRITE({}).success: {} ms **", name, System.currentTimeMillis() - time);
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, dynamic, e);
			log.error("** WRITE({}).failure: {} ms **", name, System.currentTimeMillis() - time);
			throw error;
		} finally {
			try {
				time = System.currentTimeMillis();
				IGitProvider provider = context.getBean(IGitProvider.class);
				if (!dynamic.value().isEmpty()) {
					provider.cleanWrite(dynamic.value());
				}
				for (GitWriteDirDynamic d : dynamic.values()) {
					provider.cleanWrite(d.value());
				}
				log.info("** WRITE({}).finalyze: {} ms **", name, System.currentTimeMillis() - time);
			} finally {
				scope.closeAspect();
			}
		}
	}

	protected <T extends Annotation> T getAnnotation(Signature signature, Class<T> type) {
		if (signature instanceof MethodSignature) {
			return AnnotationUtils.findAnnotation(((MethodSignature) signature).getMethod(), type);
		}
		return null;
	}

	@SneakyThrows
	protected GitWriteDynamic getDynamic(GitWrite annotation, ProceedingJoinPoint jp) {
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

	protected void init(ProceedingJoinPoint jp, GitWriteDynamic annotation) {
		publisher.publishEvent(new GitWriteEvent(jp, annotation, EGitWrite.INIT));
		startWatcher(annotation);
	}

	protected void startWatcher(GitWriteDynamic annotation) {
		IGitProvider provider = context.getBean(IGitProvider.class);
		if (annotation.watcher() && !annotation.value().isEmpty()) {
			String group = annotation.value();
			Path path = provider.directoryWrite(group).toPath();
			publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.START, group, path));
		}
		for (GitWriteDirDynamic d : annotation.values()) {
			if (d.watcher()) {
				String g = d.value();
				Path p = provider.directoryWrite(g).toPath();
				publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.START, g, p));
			}
		}
	}

	protected Object success(ProceedingJoinPoint jp, GitWriteDynamic annotation, Object result) {
		stopWatcher(annotation, EWatcherAction.STOP);
		GitWriteEvent event = new GitWriteEvent(jp, annotation, EGitWrite.SUCCESS, result);
		publisher.publishEvent(event);
		return event.getResult();
	}

	protected Throwable error(ProceedingJoinPoint jp, GitWriteDynamic annotation, Throwable e) {
		stopWatcher(annotation, EWatcherAction.IGNORE);
		GitWriteEvent event = new GitWriteEvent(jp, annotation, EGitWrite.FAILURE, e);
		publisher.publishEvent(event);
		return event.getError();
	}

	protected void stopWatcher(GitWriteDynamic annotation, EWatcherAction action) {
		IGitProvider provider = context.getBean(IGitProvider.class);
		if (annotation.watcher() && !annotation.value().isEmpty()) {
			String group = annotation.value();
			Path path = provider.directoryWrite(group).toPath();
			publisher.publishEvent(new FileWatcherEvent(this, action, group, path));
		}
		for (GitWriteDirDynamic d : annotation.values()) {
			if (d.watcher()) {
				String g = d.value();
				Path p = provider.directoryWrite(g).toPath();
				publisher.publishEvent(new FileWatcherEvent(this, action, g, p));
			}
		}
	}
}