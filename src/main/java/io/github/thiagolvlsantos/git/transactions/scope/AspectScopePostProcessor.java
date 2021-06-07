package io.github.thiagolvlsantos.git.transactions.scope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class AspectScopePostProcessor implements BeanFactoryPostProcessor {

	public static final String ASPECT_SCOPE_NAME = "aspect";
	private static AspectScope instance = new AspectScope();

	public AspectScope instance() {
		return instance;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
		factory.registerScope(ASPECT_SCOPE_NAME, instance);
	}
}