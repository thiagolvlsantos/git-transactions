package io.github.thiagolvlsantos.git.transactions.impl.web;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.config.GitConstants;
import io.github.thiagolvlsantos.git.transactions.impl.AbstractGitTransaction;

@Component
@Profile(GitConstants.PROFILE_WEB)
@Scope(GitConstants.SCOPE_REQUEST)
public class GitTransactionWeb extends AbstractGitTransaction {
}