package io.github.thiagolvlsantos.git.transactions.id.impl;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import io.github.thiagolvlsantos.git.transactions.id.ISessionIdHolder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SessionIdHolderHelper {

	public static ISessionIdHolder holder(ApplicationContext context) {
		ISessionIdHolder sessionHolder = null;
		try {
			sessionHolder = context.getBean(ISessionIdHolder.class);
		} catch (NoSuchBeanDefinitionException e) {
			sessionHolder = AbstractSessionIdHolder.INSTANCE;
		}
		return sessionHolder;
	}
}
