package org.h2.security.auth.impl;

import java.util.Arrays;
import java.util.Collection;
import org.h2.api.UserToRolesMapper;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/impl/AssignRealmNameRole.class */
public class AssignRealmNameRole implements UserToRolesMapper {
    private String roleNameFormat;

    public AssignRealmNameRole() {
        this("@%s");
    }

    public AssignRealmNameRole(String str) {
        this.roleNameFormat = str;
    }

    @Override // org.h2.security.auth.Configurable
    public void configure(ConfigProperties configProperties) {
        this.roleNameFormat = configProperties.getStringValue("roleNameFormat", this.roleNameFormat);
    }

    @Override // org.h2.api.UserToRolesMapper
    public Collection<String> mapUserToRoles(AuthenticationInfo authenticationInfo) throws AuthenticationException {
        return Arrays.asList(String.format(this.roleNameFormat, authenticationInfo.getRealm()));
    }
}
