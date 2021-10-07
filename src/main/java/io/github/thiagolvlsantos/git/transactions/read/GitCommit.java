package io.github.thiagolvlsantos.git.transactions.read;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface GitCommit {

	/**
	 * The group.
	 * 
	 * @return The reference group.
	 */
	String value() default "";
}