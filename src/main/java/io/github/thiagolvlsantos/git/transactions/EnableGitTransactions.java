package io.github.thiagolvlsantos.git.transactions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.github.thiagolvlsantos.git.transactions.EnableGitTransactions.GitTransactions;
import io.github.thiagolvlsantos.git.transactions.config.GitConfiguration;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ GitConfiguration.class, GitTransactions.class })
public @interface EnableGitTransactions {

	@Configuration
	@ComponentScan("io.github.thiagolvlsantos.git.transactions")
	public static class GitTransactions {
	}
}
