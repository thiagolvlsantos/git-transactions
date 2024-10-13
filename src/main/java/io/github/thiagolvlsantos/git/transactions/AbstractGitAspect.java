package io.github.thiagolvlsantos.git.transactions;

import java.lang.annotation.Annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGitAspect<A extends Annotation, D extends IGitAnnotation> {

	private static long serial = 0;
	protected @Autowired ApplicationContext context;

	public Object perform(ProceedingJoinPoint jp, Class<A> type) throws Throwable {
		IGitTransaction manager = context.getBean(IGitTransaction.class);
		Signature signature = jp.getSignature();
		A annotation = getAnnotation(signature, type);
		D dynamic = toDynamic(jp, annotation);
		manager.beginTransaction(context, dynamic);
		long time = System.currentTimeMillis();
		long total = time;
		init(jp, dynamic);
		String simpleName = type.getSimpleName();
		String group = dynamic.value();
		String method = signature.getName();
		final long seq = serial++;
		log.info("** #{} @{}({}[{}]).init: {} ms, @{} **", seq, simpleName, group, method,
				System.currentTimeMillis() - time, dynamic);
		time = System.currentTimeMillis();
		try {
			Object result = success(jp, dynamic, jp.proceed());
			log.info("** #{} @{}({}[{}]).success: {} ms **", seq, simpleName, group, method,
					System.currentTimeMillis() - time);
			return result;
		} catch (Throwable e) {
			Throwable error = error(jp, dynamic, e);
			log.error("** #{} @{}({}[{}]).failure: {} ms **", seq, simpleName, group, method,
					System.currentTimeMillis() - time);
			throw error;
		} finally {
			try {
				time = System.currentTimeMillis();
				finish(dynamic);
				long current = System.currentTimeMillis();
				log.info("** #{} @{}({}[{}]).finalyze: {} ms, TOTAL: {} ms **", seq, simpleName, group, method,
						current - time, current - total);
			} finally {
				manager.endTransaction(context, dynamic);
			}
		}
	}

	protected <T extends Annotation> T getAnnotation(Signature signature, Class<T> type) {
		if (signature instanceof MethodSignature) {
			return AnnotationUtils.findAnnotation(((MethodSignature) signature).getMethod(), type);
		}
		throw new GitTransactionsException("Annotation @" + type.getSimpleName() + " allowed only for methods.", null);
	}

	protected abstract D toDynamic(ProceedingJoinPoint jp, A annotation);

	protected abstract void init(ProceedingJoinPoint jp, D dynamic);

	protected abstract Object success(ProceedingJoinPoint jp, D dynamic, Object result);

	protected abstract Throwable error(ProceedingJoinPoint jp, D dynamic, Throwable e);

	protected abstract void finish(D dynamic);
}