package org.springframework.boot.system;

import java.io.Console;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Future;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/system/JavaVersion.class */
public enum JavaVersion {
    SEVENTEEN("17", Console.class, BasicAuthenticator.charsetparam),
    EIGHTEEN("18", Duration.class, "isPositive"),
    NINETEEN("19", Future.class, "state"),
    TWENTY("20", Class.class, "accessFlags"),
    TWENTY_ONE("21", SortedSet.class, "getFirst");

    private final String name;
    private final boolean available;

    JavaVersion(String name, Class clazz, String methodName) {
        this.name = name;
        this.available = ClassUtils.hasMethod(clazz, methodName, new Class[0]);
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.name;
    }

    public static JavaVersion getJavaVersion() {
        List<JavaVersion> candidates = Arrays.asList(values());
        Collections.reverse(candidates);
        for (JavaVersion candidate : candidates) {
            if (candidate.available) {
                return candidate;
            }
        }
        return SEVENTEEN;
    }

    public boolean isEqualOrNewerThan(JavaVersion version) {
        return compareTo(version) >= 0;
    }

    public boolean isOlderThan(JavaVersion version) {
        return compareTo(version) < 0;
    }
}
