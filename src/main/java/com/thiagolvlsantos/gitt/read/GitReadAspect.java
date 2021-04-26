package com.thiagolvlsantos.gitt.read;

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
public class GitReadAspect {

	private @Autowired ApplicationContext context;
	private @Autowired ApplicationEventPublisher publisher;
	private @Autowired IGitProvider provider;

	@Around("@annotation(com.thiagolvlsantos.gitt.read.GitRead)")
	public Object read(ProceedingJoinPoint jp) throws Throwable {
		Signature signature = jp.getSignature();
		GitRead annotation = getAnnotation(signature);
		String group = annotation.value();
		Path path = provider.directoryRead(group).toPath();
		long time = System.currentTimeMillis();
		init(jp, annotation);
		if (annotation.watcher()) {
			publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.START, group, path));
		}
		if (log.isInfoEnabled()) {
			log.info("** READ({}).init: {}, ({}) **", group, signature.getName(), System.currentTimeMillis() - time);
		}
		try {
			time = System.currentTimeMillis();
			Object result = success(jp, annotation, jp.proceed());
			if (log.isInfoEnabled()) {
				log.info("** READ({}).success: {} ({}) **", group, signature.getName(),
						System.currentTimeMillis() - time);
			}
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, annotation, e);
			if (log.isInfoEnabled()) {
				log.info("** READ({}).failure: {} ({}) **", group, signature.getName(),
						System.currentTimeMillis() - time);
			}
			throw error;
		} finally {
			time = System.currentTimeMillis();
			if (annotation.watcher()) {
				publisher.publishEvent(new FileWatcherEvent(this, EWatcherAction.STOP, group, path));
			}
			context.getBean(IGitProvider.class).cleanRead(group);
			SessionIdHolderHelper.holder(context).clear();
			log.info("** READ({}).finalyze: {} ({}) **", group, signature.getName(), System.currentTimeMillis() - time);
		}
	}

	private GitRead getAnnotation(Signature signature) {
		if (signature instanceof MethodSignature) {
			return AnnotationUtils.findAnnotation(((MethodSignature) signature).getMethod(), GitRead.class);
		}
		return null;
	}

	private void init(ProceedingJoinPoint jp, GitRead annotation) {
		publisher.publishEvent(new GitReadEvent(jp, annotation, EGitRead.INIT));
	}

	private Object success(ProceedingJoinPoint jp, GitRead annotation, Object result) {
		GitReadEvent event = new GitReadEvent(jp, annotation, EGitRead.SUCCESS, result);
		publisher.publishEvent(event);
		return event.getResult();
	}

	private Throwable error(ProceedingJoinPoint jp, GitRead annotation, Throwable e) {
		GitReadEvent event = new GitReadEvent(jp, annotation, EGitRead.FAILURE, e);
		publisher.publishEvent(event);
		return event.getError();
	}
}