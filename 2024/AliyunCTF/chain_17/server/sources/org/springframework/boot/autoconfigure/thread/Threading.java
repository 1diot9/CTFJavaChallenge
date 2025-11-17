package org.springframework.boot.autoconfigure.thread;

import org.springframework.boot.system.JavaVersion;
import org.springframework.core.env.Environment;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thread/Threading.class */
public enum Threading {
    PLATFORM { // from class: org.springframework.boot.autoconfigure.thread.Threading.1
        @Override // org.springframework.boot.autoconfigure.thread.Threading
        public boolean isActive(Environment environment) {
            return !VIRTUAL.isActive(environment);
        }
    },
    VIRTUAL { // from class: org.springframework.boot.autoconfigure.thread.Threading.2
        @Override // org.springframework.boot.autoconfigure.thread.Threading
        public boolean isActive(Environment environment) {
            return ((Boolean) environment.getProperty("spring.threads.virtual.enabled", Boolean.TYPE, false)).booleanValue() && JavaVersion.getJavaVersion().isEqualOrNewerThan(JavaVersion.TWENTY_ONE);
        }
    };

    public abstract boolean isActive(Environment environment);
}
