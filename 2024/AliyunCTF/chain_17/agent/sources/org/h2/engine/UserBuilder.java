package org.h2.engine;

import org.h2.security.auth.AuthenticationInfo;
import org.h2.util.MathUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/UserBuilder.class */
public class UserBuilder {
    public static User buildUser(AuthenticationInfo authenticationInfo, Database database, boolean z) {
        User user = new User(database, z ? database.allocateObjectId() : -1, authenticationInfo.getFullyQualifiedName(), false);
        user.setUserPasswordHash(authenticationInfo.getRealm() == null ? authenticationInfo.getConnectionInfo().getUserPasswordHash() : MathUtils.secureRandomBytes(64));
        user.setTemporary(!z);
        return user;
    }
}
