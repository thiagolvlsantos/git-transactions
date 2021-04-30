package com.thiagolvlsantos.gitt.read;

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

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@Order(0)
public class GitReadAspect {

	private @Autowired ApplicationContext context;
	private @Autowired ApplicationEventPublisher publisher;

	@Around("@annotation(com.thiagolvlsantos.gitt.read.GitRead)")
	public Object read(ProceedingJoinPoint jp) throws Throwable {
		AspectScope scope = context.getBean(AspectScope.class);
		scope.openAspect();
		Signature signature = jp.getSignature();
		String name = signature.getName();
		GitRead annotation = getAnnotation(signature);
		long time = System.currentTimeMillis();
		init(jp, annotation);
		if (log.isInfoEnabled()) {
			log.info("** READ({}).init: {} ms, ({}) **", name, System.currentTimeMillis() - time, annotation);
		}
		try {
			time = System.currentTimeMillis();
			Object result = success(jp, annotation, jp.proceed());
			if (log.isInfoEnabled()) {
				log.info("** READ({}).success: {} ms **", name, System.currentTimeMillis() - time);
			}
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, annotation, e);
			if (log.isInfoEnabled()) {
				log.info("** READ({}).failure: {} ms **", name, System.currentTimeMillis() - time);
			}
			throw error;
		} finally {
			try {
				time = System.currentTimeMillis();
				IGitProvider provider = context.getBean(IGitProvider.class);
				if (!annotation.value().isEmpty()) {
					provider.cleanRead(annotation.value());
				}
				for (GitReadDir d : annotation.values()) {
					provider.cleanRead(d.value());
				}
				if (log.isInfoEnabled()) {
					log.info("** READ({}).finalyze: {} ms **", name, System.currentTimeMillis() - time);
				}
			} finally {
				scope.closeAspect();
			}
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