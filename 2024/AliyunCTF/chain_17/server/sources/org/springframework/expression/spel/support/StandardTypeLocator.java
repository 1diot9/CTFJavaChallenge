package org.springframework.expression.spel.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.expression.TypeLocator;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/support/StandardTypeLocator.class */
public class StandardTypeLocator implements TypeLocator {

    @Nullable
    private final ClassLoader classLoader;
    private final List<String> importPrefixes;
    private final Map<String, Class<?>> typeCache;

    public StandardTypeLocator() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public StandardTypeLocator(@Nullable ClassLoader classLoader) {
        this.importPrefixes = new ArrayList(1);
        this.typeCache = new ConcurrentHashMap();
        this.classLoader = classLoader;
        registerImport("java.lang");
    }

    public void registerImport(String prefix) {
        this.importPrefixes.add(prefix);
    }

    public void removeImport(String prefix) {
        this.importPrefixes.remove(prefix);
    }

    public List<String> getImportPrefixes() {
        return Collections.unmodifiableList(this.importPrefixes);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x003b, code lost:            if (r0.isClassReloadable(r0) == false) goto L12;     */
    @Override // org.springframework.expression.TypeLocator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Class<?> findType(java.lang.String r9) throws org.springframework.expression.EvaluationException {
        /*
            r8 = this;
            r0 = r8
            java.util.Map<java.lang.String, java.lang.Class<?>> r0 = r0.typeCache
            r1 = r9
            java.lang.Object r0 = r0.get(r1)
            java.lang.Class r0 = (java.lang.Class) r0
            r10 = r0
            r0 = r10
            if (r0 == 0) goto L14
            r0 = r10
            return r0
        L14:
            r0 = r8
            r1 = r9
            java.lang.Class r0 = r0.loadType(r1)
            r11 = r0
            r0 = r11
            if (r0 == 0) goto L4c
            r0 = r8
            java.lang.ClassLoader r0 = r0.classLoader
            r13 = r0
            r0 = r13
            boolean r0 = r0 instanceof org.springframework.core.SmartClassLoader
            if (r0 == 0) goto L3e
            r0 = r13
            org.springframework.core.SmartClassLoader r0 = (org.springframework.core.SmartClassLoader) r0
            r12 = r0
            r0 = r12
            r1 = r11
            boolean r0 = r0.isClassReloadable(r1)
            if (r0 != 0) goto L4a
        L3e:
            r0 = r8
            java.util.Map<java.lang.String, java.lang.Class<?>> r0 = r0.typeCache
            r1 = r9
            r2 = r11
            java.lang.Object r0 = r0.put(r1, r2)
        L4a:
            r0 = r11
            return r0
        L4c:
            org.springframework.expression.spel.SpelEvaluationException r0 = new org.springframework.expression.spel.SpelEvaluationException
            r1 = r0
            org.springframework.expression.spel.SpelMessage r2 = org.springframework.expression.spel.SpelMessage.TYPE_NOT_FOUND
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = r3
            r5 = 0
            r6 = r9
            r4[r5] = r6
            r1.<init>(r2, r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.expression.spel.support.StandardTypeLocator.findType(java.lang.String):java.lang.Class");
    }

    @Nullable
    private Class<?> loadType(String typeName) {
        try {
            return ClassUtils.forName(typeName, this.classLoader);
        } catch (ClassNotFoundException e) {
            for (String prefix : this.importPrefixes) {
                try {
                    String nameToLookup = prefix + "." + typeName;
                    return ClassUtils.forName(nameToLookup, this.classLoader);
                } catch (ClassNotFoundException e2) {
                }
            }
            return null;
        }
    }
}
