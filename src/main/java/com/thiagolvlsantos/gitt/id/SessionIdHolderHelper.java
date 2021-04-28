package com.thiagolvlsantos.gitt.id;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SessionIdHolderHelper {

	public static ISessionIdHolder holder(ApplicationContext context) {
		ISessionIdHolder sessionHolder = null;
		try {
			sessionHolder = context.getBean(ISessionIdHolder.class);
		} catch (NoSuchBeanDefinitionException e) {
			sessionHolder = SessionIdHolderTime.INSTANCE;
		}
		return sessionHolder;
	}
}
