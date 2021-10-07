package io.github.thiagolvlsantos.git.transactions.read;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GitCommitHelper {

	public static List<GitCommitValue> commitParameters(ProceedingJoinPoint jp, Signature signature) {
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
}
