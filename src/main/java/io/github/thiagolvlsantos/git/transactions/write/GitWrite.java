package io.github.thiagolvlsantos.git.transactions.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.thiagolvlsantos.git.transactions.provider.IGitRouter;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GitWrite {

	String value() default "";

	boolean watcher() default true;

	Class<? extends IGitRouter> router() default IGitRouter.class;

	GitWriteDir[] values() default {};
}
