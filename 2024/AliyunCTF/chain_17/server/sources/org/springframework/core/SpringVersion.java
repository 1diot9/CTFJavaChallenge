package org.springframework.core;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/SpringVersion.class */
public final class SpringVersion {
    private SpringVersion() {
    }

    @Nullable
    public static String getVersion() {
        Package pkg = SpringVersion.class.getPackage();
        if (pkg != null) {
            return pkg.getImplementationVersion();
        }
        return null;
    }
}
