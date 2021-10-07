package io.github.thiagolvlsantos.git.transactions.provider.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.config.GitConstants;
import io.github.thiagolvlsantos.git.transactions.scope.AspectScoped;

@Component
@Profile("!" + GitConstants.PROFILE_WEB)
@AspectScoped
public class GitProviderDefault extends AbstractGitProvider {
}