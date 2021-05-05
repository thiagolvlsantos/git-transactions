package com.thiagolvlsantos.gitt.read;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.thiagolvlsantos.gitt.provider.IGitRouter;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GitRead {

	String value() default "";

	Class<? extends IGitRouter> router() default IGitRouter.class;

	GitReadDir[] values() default {};
}