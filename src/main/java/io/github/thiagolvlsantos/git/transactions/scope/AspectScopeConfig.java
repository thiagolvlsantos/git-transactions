package io.github.thiagolvlsantos.git.transactions.scope;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AspectScopeConfig {

	private static AspectScopePostProcessor processor = new AspectScopePostProcessor();

	@Bean
	public AspectScope scope() {
		return processor.instance();
	}

	@Bean
	public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
		return processor;
	}
}
