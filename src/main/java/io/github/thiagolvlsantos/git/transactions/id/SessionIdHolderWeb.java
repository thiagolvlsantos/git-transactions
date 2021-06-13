package io.github.thiagolvlsantos.git.transactions.id;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.config.GitConstants;

@Component
@Profile(GitConstants.PROFILE_WEB)
@Scope(GitConstants.SCOPE_REQUEST)
public class SessionIdHolderWeb extends AbstractSessionIdHolder {
}