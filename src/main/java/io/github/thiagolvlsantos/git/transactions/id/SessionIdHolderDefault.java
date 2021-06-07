package io.github.thiagolvlsantos.git.transactions.id;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.transactions.scope.AspectScoped;

@Component
@Profile("!web")
@AspectScoped
public class SessionIdHolderDefault extends AbstractSessionIdHolder {
}