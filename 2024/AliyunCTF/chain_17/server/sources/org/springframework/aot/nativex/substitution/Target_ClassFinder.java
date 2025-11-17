package org.springframework.aot.nativex.substitution;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "com.sun.beans.finder.ClassFinder")
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/substitution/Target_ClassFinder.class */
final class Target_ClassFinder {
    Target_ClassFinder() {
    }

    @Alias
    public static Class<?> findClass(String name, ClassLoader loader) throws ClassNotFoundException {
        return null;
    }
}
