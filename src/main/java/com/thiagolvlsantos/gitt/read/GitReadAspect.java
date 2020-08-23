package com.thiagolvlsantos.gitt.read;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class GitReadAspect {

	private @Autowired ApplicationEventPublisher publisher;

	@Around("@annotation(com.thiagolvlsantos.gitt.read.GitRead)")
	public Object read(ProceedingJoinPoint jp) throws Throwable {
		Signature signature = jp.getSignature();
		GitRead annotation = getAnnotation(signature);
		if (log.isDebugEnabled()) {
			log.info("** READ.init: {}, {} **", signature.getName(), annotation);
		}
		long time = System.currentTimeMillis();
		init(jp, annotation);
		try {
			Object result = success(jp, annotation, jp.proceed());
			if (log.isDebugEnabled()) {
				log.info("** READ.success: {} ({}) **", signature.getName(), System.currentTimeMillis() - time);
			}
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, annotation, e);
			if (log.isDebugEnabled()) {
				log.info("** READ.failure: {} ({}) **", signature.getName(), System.currentTimeMillis() - time);
			}
			throw error;
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