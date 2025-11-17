package org.h2.api;

import java.util.Collection;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.Configurable;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/UserToRolesMapper.class */
public interface UserToRolesMapper extends Configurable {
    Collection<String> mapUserToRoles(AuthenticationInfo authenticationInfo) throws AuthenticationException;
}
