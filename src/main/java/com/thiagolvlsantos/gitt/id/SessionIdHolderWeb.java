package com.thiagolvlsantos.gitt.id;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Profile("web")
@Scope("request")
public class SessionIdHolderWeb extends AbstractSessionIdHolder {
}