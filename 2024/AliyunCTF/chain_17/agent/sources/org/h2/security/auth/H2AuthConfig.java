package org.h2.security.auth;

import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/H2AuthConfig.class */
public class H2AuthConfig {
    private boolean allowUserRegistration = true;
    private boolean createMissingRoles = true;
    private List<RealmConfig> realms;
    private List<UserToRolesMapperConfig> userToRolesMappers;

    public boolean isAllowUserRegistration() {
        return this.allowUserRegistration;
    }

    public void setAllowUserRegistration(boolean z) {
        this.allowUserRegistration = z;
    }

    public boolean isCreateMissingRoles() {
        return this.createMissingRoles;
    }

    public void setCreateMissingRoles(boolean z) {
        this.createMissingRoles = z;
    }

    public List<RealmConfig> getRealms() {
        if (this.realms == null) {
            this.realms = new ArrayList();
        }
        return this.realms;
    }

    public void setRealms(List<RealmConfig> list) {
        this.realms = list;
    }

    public List<UserToRolesMapperConfig> getUserToRolesMappers() {
        if (this.userToRolesMappers == null) {
            this.userToRolesMappers = new ArrayList();
        }
        return this.userToRolesMappers;
    }

    public void setUserToRolesMappers(List<UserToRolesMapperConfig> list) {
        this.userToRolesMappers = list;
    }
}
