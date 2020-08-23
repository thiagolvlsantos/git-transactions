package com.thiagolvlsantos.gitt.write;

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
public class GitWriteAspect {

	private @Autowired ApplicationEventPublisher publisher;

	@Around("@annotation(com.thiagolvlsantos.gitt.write.GitWrite)")
	public Object write(ProceedingJoinPoint jp) throws Throwable {
		Signature signature = jp.getSignature();
		GitWrite annotation = getAnnotation(signature);
		if (log.isDebugEnabled()) {
			log.info("** WRITE.init: {}, {} **", signature.getName(), annotation);
		}
		long time = System.currentTimeMillis();
		init(jp, annotation);
		try {
			Object result = success(jp, annotation, jp.proceed());
			if (log.isDebugEnabled()) {
				log.info("** WRITE.success: {} ({}) **", signature.getName(), System.currentTimeMillis() - time);
			}
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, annotation, e);
			if (log.isDebugEnabled()) {
				log.info("** WRITE.failure: {} ({}) **", signature.getName(), System.currentTimeMillis() - time);
			}
			throw error;
		}
	}

	private GitWrite getAnnotation(Signature signature) {
		if (signature instanceof MethodSignature) {
			return AnnotationUtils.findAnnotation(((MethodSignature) signature).getMethod(), GitWrite.class);
		}
		return null;
	}

	private void init(ProceedingJoinPoint jp, GitWrite annotation) {
		publisher.publishEvent(new GitWriteEvent(jp, annotation, EGitWrite.INIT));
	}

	private Object success(ProceedingJoinPoint jp, GitWrite annotation, Object result) {
		GitWriteEvent event = new GitWriteEvent(jp, annotation, EGitWrite.SUCCESS, result);
		publisher.publishEvent(event);
		return event.getResult();
	}

	private Throwable error(ProceedingJoinPoint jp, GitWrite annotation, Throwable e) {
		GitWriteEvent event = new GitWriteEvent(jp, annotation, EGitWrite.FAILURE, e);
		publisher.publishEvent(event);
		return event.getError();
	}
}