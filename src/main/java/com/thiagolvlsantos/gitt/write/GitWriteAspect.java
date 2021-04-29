package com.thiagolvlsantos.gitt.write;

import java.nio.file.Path;

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

import com.thiagolvlsantos.gitt.provider.IGitProvider;
import com.thiagolvlsantos.gitt.scope.AspectScope;
import com.thiagolvlsantos.gitt.watcher.EWatcherAction;
import com.thiagolvlsantos.gitt.watcher.FileWatcherEvent;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@Order(10)
public class GitWriteAspect {

	private @Autowired ApplicationContext context;
	private @Autowired ApplicationEventPublisher publisher;

	@Around("@annotation(com.thiagolvlsantos.gitt.write.GitWrite)")
	public Object write(ProceedingJoinPoint jp) throws Throwable {
		AspectScope scope = context.getBean(AspectScope.class);
		scope.openAspect();
		Signature signature = jp.getSignature();
		String name = signature.getName();
		GitWrite annotation = getAnnotation(signature);
		long time = System.currentTimeMillis();
		init(jp, annotation);
		startWatcher(annotation);
		if (log.isInfoEnabled()) {
			log.info("** WRITE({}).init: {}, {} **", name, System.currentTimeMillis() - time, annotation);
		}
		try {
			time = System.currentTimeMillis();
			Object result = success(jp, annotation, jp.proceed());
			if (log.isInfoEnabled()) {
				log.info("** WRITE({}).success: {} ({}) **", name, System.currentTimeMillis() - time, annotation);
			}
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, annotation, e);
			if (log.isInfoEnabled()) {
				log.info("** WRITE({}).failure: {} ({}) **", name, System.currentTimeMillis() - time, annotation);
			}
			throw error;
		} finally {
			try {
				time = System.currentTimeMillis();
				IGitProvider provider = context.getBean(IGitProvider.class);
				if (!annotation.value().isEmpty()) {
					provider.cleanWrite(annotation.value());
				}
				for (GitWriteDir d : annotation.values()) {
					provider.cleanWrite(d.value());
				}
				if (log.isInfoEnabled()) {
					log.info("** WRITE({}).finalyze: {} ({}) **", name, System.currentTimeMillis() - time, annotation);
				}
			} finally {
				scope.closeAspect();
			}
		}
	}

	private GitWrite getAnnotation(Signature signature) {
		if (signature instanceof MethodSignature) {
			return AnnotationUtils.findAnnotation(((MethodSignature) signature).getMethod(), GitWrite.class);
		}
		return null;
	}

	private void startWatcher(GitWrite annotation) {
		IGitProvider provider = context.getBean(IGitProvider.class);
		if (annotation.watcher() && !annotation.value().isEmpty()) {
			String group = annotation.value();
			Path path = provider.directoryWrite(group).toPath();
			publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.START, group, path));
		}
		for (GitWriteDir d : annotation.values()) {
			if (d.watcher()) {
				String g = d.value();
				Path p = provider.directoryWrite(g).toPath();
				publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.START, g, p));
			}
		}
	}

	private void init(ProceedingJoinPoint jp, GitWrite annotation) {
		publisher.publishEvent(new GitWriteEvent(jp, annotation, EGitWrite.INIT));
	}

	private Object success(ProceedingJoinPoint jp, GitWrite annotation, Object result) {
		stopWatcher(annotation);
		GitWriteEvent event = new GitWriteEvent(jp, annotation, EGitWrite.SUCCESS, result);
		publisher.publishEvent(event);
		return event.getResult();
	}

	private Throwable error(ProceedingJoinPoint jp, GitWrite annotation, Throwable e) {
		stopWatcher(annotation);
		GitWriteEvent event = new GitWriteEvent(jp, annotation, EGitWrite.FAILURE, e);
		publisher.publishEvent(event);
		return event.getError();
	}

	private void stopWatcher(GitWrite annotation) {
		IGitProvider provider = context.getBean(IGitProvider.class);
		if (annotation.watcher() && !annotation.value().isEmpty()) {
			String group = annotation.value();
			Path path = provider.directoryWrite(group).toPath();
			publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.STOP, group, path));
		}
		for (GitWriteDir d : annotation.values()) {
			if (d.watcher()) {
				String g = d.value();
				Path p = provider.directoryWrite(g).toPath();
				publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.STOP, g, p));
			}
		}
	}
}