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
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.id.SessionIdHolderHelper;
import com.thiagolvlsantos.gitt.provider.IGitProvider;
import com.thiagolvlsantos.gitt.watcher.EWatcherAction;
import com.thiagolvlsantos.gitt.watcher.FileWatcherEvent;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class GitWriteAspect {

	private @Autowired ApplicationContext context;
	private @Autowired ApplicationEventPublisher publisher;
	private @Autowired IGitProvider provider;

	@Around("@annotation(com.thiagolvlsantos.gitt.write.GitWrite)")
	public Object write(ProceedingJoinPoint jp) throws Throwable {
		Signature signature = jp.getSignature();
		String name = signature.getName();
		GitWrite annotation = getAnnotation(signature);
		String group = annotation.value();
		long time = System.currentTimeMillis();
		init(jp, annotation);
		startWatcher(annotation);
		if (log.isInfoEnabled()) {
			log.info("** WRITE({}).init: {}, {} **", group, name, System.currentTimeMillis() - time);
		}
		try {
			time = System.currentTimeMillis();
			Object result = success(jp, annotation, jp.proceed());
			if (log.isInfoEnabled()) {
				log.info("** WRITE({}).success: {} ({}) **", group, name, System.currentTimeMillis() - time);
			}
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, annotation, e);
			if (log.isInfoEnabled()) {
				log.info("** WRITE({}).failure: {} ({}) **", group, name, System.currentTimeMillis() - time);
			}
			throw error;
		} finally {
			time = System.currentTimeMillis();
			context.getBean(IGitProvider.class).cleanWrite(group);
			SessionIdHolderHelper.holder(context).clear();
			log.info("** WRITE({}).finalyze: {} ({}) **", group, name, System.currentTimeMillis() - time);
		}
	}

	private GitWrite getAnnotation(Signature signature) {
		if (signature instanceof MethodSignature) {
			return AnnotationUtils.findAnnotation(((MethodSignature) signature).getMethod(), GitWrite.class);
		}
		return null;
	}

	private void startWatcher(GitWrite annotation) {
		if (annotation.watcher()) {
			String group = annotation.value();
			Path path = provider.directoryWrite(group).toPath();
			publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.START, group, path));
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
		if (annotation.watcher()) {
			String group = annotation.value();
			Path path = provider.directoryWrite(group).toPath();
			publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.STOP, group, path));
		}
	}
}