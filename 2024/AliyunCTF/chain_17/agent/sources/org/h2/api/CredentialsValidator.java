package org.h2.api;

import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.Configurable;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/CredentialsValidator.class */
public interface CredentialsValidator extends Configurable {
    boolean validateCredentials(AuthenticationInfo authenticationInfo) throws Exception;
}
