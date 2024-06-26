package io.github.thiagolvlsantos.git.transactions.id.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.config.GitConstants;

@Component
@Profile("!" + GitConstants.PROFILE_WEB)
public class SessionIdHolderDefault extends AbstractSessionIdHolder {
}