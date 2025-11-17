package org.apache.tomcat.util.security;

import java.security.Permission;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/security/PermissionCheck.class */
public interface PermissionCheck {
    boolean check(Permission permission);
}
