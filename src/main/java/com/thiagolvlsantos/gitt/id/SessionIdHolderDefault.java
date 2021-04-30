package com.thiagolvlsantos.gitt.id;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.scope.AspectScoped;

@Component
@Profile("!web")
@AspectScoped
public class SessionIdHolderDefault extends AbstractSessionIdHolder {
}