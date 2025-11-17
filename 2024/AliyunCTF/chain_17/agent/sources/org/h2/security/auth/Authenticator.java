package org.h2.security.auth;

import org.h2.engine.Database;
import org.h2.engine.User;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/Authenticator.class */
public interface Authenticator {
    User authenticate(AuthenticationInfo authenticationInfo, Database database) throws AuthenticationException;

    void init(Database database) throws AuthConfigException;
}
