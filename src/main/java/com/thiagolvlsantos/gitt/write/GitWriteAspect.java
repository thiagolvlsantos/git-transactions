package com.thiagolvlsantos.gitt.write;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.id.ISessionIdHolder;
import com.thiagolvlsantos.gitt.provider.IGitProvider;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class GitWriteAspect {

	private @Autowired ApplicationContext context;
	private @Autowired ApplicationEventPublisher publisher;

	@Around("@annotation(com.thiagolvlsantos.gitt.write.GitWrite)")
	public Object write(ProceedingJoinPoint jp) throws Throwable {
		Signature signature = jp.getSignature();
		GitWrite annotation = getAnnotation(signature);
		if (log.isInfoEnabled()) {
			log.info("** WRITE.init: {}, {} **", signature.getName(), annotation);
		}
		long time = System.currentTimeMillis();
		init(jp, annotation);
		try {
			Object result = success(jp, annotation, jp.proceed());
			if (log.isInfoEnabled()) {
				log.info("** WRITE.success: {} ({}) **", signature.getName(), System.currentTimeMillis() - time);
			}
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, annotation, e);
			if (log.isInfoEnabled()) {
				log.info("** WRITE.failure: {} ({}) **", signature.getName(), System.currentTimeMillis() - time);
			}
			throw error;
		} finally {
			context.getBean(IGitProvider.class).cleanWrite(annotation.value());
			sessionHolder().clear();
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

	private ISessionIdHolder sessionHolder() {
		ISessionIdHolder sessionHolder = null;
		try {
			sessionHolder = context.getBean(ISessionIdHolder.class);
		} catch (NoSuchBeanDefinitionException e) {
			sessionHolder = ISessionIdHolder.INSTANCE;
		}
		return sessionHolder;
	}
}