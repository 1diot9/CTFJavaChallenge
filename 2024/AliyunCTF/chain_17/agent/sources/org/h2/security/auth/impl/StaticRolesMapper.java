package org.h2.security.auth.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.h2.api.UserToRolesMapper;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/impl/StaticRolesMapper.class */
public class StaticRolesMapper implements UserToRolesMapper {
    private Collection<String> roles;

    public StaticRolesMapper() {
    }

    public StaticRolesMapper(String... strArr) {
        this.roles = Arrays.asList(strArr);
    }

    @Override // org.h2.security.auth.Configurable
    public void configure(ConfigProperties configProperties) {
        String stringValue = configProperties.getStringValue("roles", "");
        if (stringValue != null) {
            this.roles = new HashSet(Arrays.asList(stringValue.split(",")));
        }
    }

    @Override // org.h2.api.UserToRolesMapper
    public Collection<String> mapUserToRoles(AuthenticationInfo authenticationInfo) throws AuthenticationException {
        return this.roles;
    }
}
