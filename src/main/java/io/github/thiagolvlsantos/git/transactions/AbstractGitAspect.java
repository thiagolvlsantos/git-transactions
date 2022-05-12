package io.github.thiagolvlsantos.git.transactions;

import java.lang.annotation.Annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.scope.AspectScope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGitAspect<A extends Annotation, P> {

	protected @Autowired ApplicationContext context;

	public Object perform(ProceedingJoinPoint jp, Class<A> type) throws Throwable {
		AspectScope scope = context.getBean(AspectScope.class);
		scope.openAspect();
		Signature signature = jp.getSignature();
		A annotation = getAnnotation(signature, type);
		P dynamic = toDynamic(jp, annotation);
		long time = System.currentTimeMillis();
		init(jp, dynamic);
		String simpleName = type.getSimpleName();
		String name = signature.getName();
		log.info("** @{}({}).init: {} ms, ({}) **", simpleName, name, System.currentTimeMillis() - time, dynamic);
		time = System.currentTimeMillis();
		try {
			Object result = success(jp, dynamic, jp.proceed());
			log.info("** @{}({}).success: {} ms **", simpleName, name, System.currentTimeMillis() - time);
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, dynamic, e);
			log.error("** @{}({}).failure: {} ms **", simpleName, name, System.currentTimeMillis() - time);
			throw error;
		} finally {
			try {
				time = System.currentTimeMillis();
				finish(dynamic);
				log.info("** @{}({}).finalyze: {} ms **", simpleName, name, System.currentTimeMillis() - time);
			} finally {
				scope.closeAspect();
			}
		}
	}

	protected <T extends Annotation> T getAnnotation(Signature signature, Class<T> type) {
		if (signature instanceof MethodSignature) {
			return AnnotationUtils.findAnnotation(((MethodSignature) signature).getMethod(), type);
		}
		throw new GitTransactionsException("Annotation @" + type.getSimpleName() + "  allowed only for methods.", null);
	}

	protected abstract P toDynamic(ProceedingJoinPoint jp, A annotation);

	protected abstract void init(ProceedingJoinPoint jp, P annotation);

	protected abstract Object success(ProceedingJoinPoint jp, P annotation, Object result);

	protected abstract Throwable error(ProceedingJoinPoint jp, P annotation, Throwable e);

	protected abstract void finish(P dynamic);
}