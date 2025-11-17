package org.springframework.expression.spel.ast;

import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/FormatHelper.class */
abstract class FormatHelper {
    FormatHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String formatMethodForMessage(String name, List<TypeDescriptor> argumentTypes) {
        StringJoiner sj = new StringJoiner(",", "(", ")");
        Iterator<TypeDescriptor> it = argumentTypes.iterator();
        while (it.hasNext()) {
            TypeDescriptor typeDescriptor = it.next();
            String className = typeDescriptor != null ? formatClassNameForMessage(typeDescriptor.getType()) : "null";
            sj.add(className);
        }
        return name + sj;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String formatClassNameForMessage(@Nullable Class<?> clazz) {
        return clazz != null ? ClassUtils.getQualifiedName(clazz) : "null";
    }
}
