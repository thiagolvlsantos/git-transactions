package io.github.thiagolvlsantos.git.transactions.read;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.AbstractGitAspect;
import io.github.thiagolvlsantos.git.transactions.GitRepo;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
import io.github.thiagolvlsantos.git.transactions.provider.IGitRouter;
import lombok.SneakyThrows;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class GitReadAspect extends AbstractGitAspect<GitRead, GitReadDynamic> {

	private @Autowired ApplicationEventPublisher publisher;

	@Around("@annotation(io.github.thiagolvlsantos.git.transactions.read.GitRead)")
	public Object read(ProceedingJoinPoint jp) throws Throwable {
		return perform(jp, GitRead.class);
	}

	@Override
	@SneakyThrows
	protected GitReadDynamic toDynamic(ProceedingJoinPoint jp, GitRead annotation) {
		String value = null;
		List<GitReadDirDynamic> list = null;
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
			list = Stream.of(annotation.values()).map(v -> GitReadDirDynamic.builder().value(v.value()).build())
					.collect(Collectors.toList());
		}
		GitReadDirDynamic[] values = list != null ? list.toArray(new GitReadDirDynamic[0]) : new GitReadDirDynamic[0];
		return GitReadDynamic.builder().value(value).values(values).build();
	}

	@Override
	protected void init(ProceedingJoinPoint jp, GitReadDynamic dynamic) {
		List<GitCommitValue> commitParameters = commitParameters(jp, jp.getSignature());
		publisher.publishEvent(new GitReadEvent(jp, dynamic, EGitRead.INIT, commitParameters));
	}

	protected List<GitCommitValue> commitParameters(ProceedingJoinPoint jp, Signature signature) {
		List<GitCommitValue> commits = new LinkedList<>();
		if (signature instanceof MethodSignature) {
			Object[] args = jp.getArgs();
			MethodSignature methodSig = (MethodSignature) signature;
			Method method = methodSig.getMethod();
			int index = 0;
			for (Parameter p : method.getParameters()) {
				GitCommit annotation = p.getAnnotation(GitCommit.class);
				Object value = args[index];
				if (annotation != null && value != null) {
					commits.add(GitCommitValue.builder().annotation(annotation).value(value).build());
				}
				index++;
			}
		}
		return commits;
	}

	@Override
	protected Object success(ProceedingJoinPoint jp, GitReadDynamic dynamic, Object result) {
		GitReadEvent event = new GitReadEvent(jp, dynamic, EGitRead.SUCCESS, result);
		publisher.publishEvent(event);
		return event.getResult();
	}

	@Override
	protected Throwable error(ProceedingJoinPoint jp, GitReadDynamic dynamic, Throwable e) {
		GitReadEvent event = new GitReadEvent(jp, dynamic, EGitRead.FAILURE, e);
		publisher.publishEvent(event);
		return event.getError();
	}

	@Override
	@SneakyThrows
	protected void finish(GitReadDynamic dynamic) {
		IGitProvider provider = context.getBean(IGitProvider.class);
		if (!dynamic.value().isEmpty()) {
			provider.cleanRead(dynamic.value());
		}
		for (GitReadDirDynamic d : dynamic.values()) {
			provider.cleanRead(d.value());
		}
	}
}