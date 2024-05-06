package io.github.thiagolvlsantos.git.transactions.provider.impl.web;

import javax.annotation.PreDestroy;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.config.GitConstants;
import io.github.thiagolvlsantos.git.transactions.provider.impl.AbstractGitProvider;

@Component
@Profile(GitConstants.PROFILE_WEB)
@Scope(GitConstants.SCOPE_REQUEST)
public class GitProviderWeb extends AbstractGitProvider {

	@PreDestroy
	public void doClean() throws GitAPIException {
		super.clean(null); // remove transaction objects on destroy
	}
}