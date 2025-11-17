package org.springframework.aot.nativex.substitution;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import java.beans.Customizer;

@TargetClass(className = "java.beans.Introspector")
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/substitution/Target_Introspector.class */
final class Target_Introspector {
    Target_Introspector() {
    }

    @Substitute
    private static Class<?> findCustomizerClass(Class<?> type) {
        String name = type.getName() + "Customizer";
        try {
            Class<?> type2 = Target_ClassFinder.findClass(name, type.getClassLoader());
            if (Customizer.class.isAssignableFrom(type2)) {
                Class<?> c = type2;
                do {
                    c = c.getSuperclass();
                    if (c.getName().equals("java.awt.Component")) {
                        return type2;
                    }
                } while (!c.getName().equals("java.lang.Object"));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
