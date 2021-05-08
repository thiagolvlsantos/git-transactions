package com.thiagolvlsantos.git.transaction.id;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.git.transaction.scope.AspectScoped;

@Component
@Profile("!web")
@AspectScoped
public class SessionIdHolderDefault extends AbstractSessionIdHolder {
}