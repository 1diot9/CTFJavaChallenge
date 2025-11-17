package org.apache.el.util;

import jakarta.el.ELException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/util/ReflectionUtil.class */
public class ReflectionUtil {
    protected static final String[] PRIMITIVE_NAMES = {"boolean", "byte", "char", "double", "float", "int", "long", "short", "void"};
    protected static final Class<?>[] PRIMITIVES = {Boolean.TYPE, Byte.TYPE, Character.TYPE, Double.TYPE, Float.TYPE, Integer.TYPE, Long.TYPE, Short.TYPE, Void.TYPE};

    private ReflectionUtil() {
    }

    public static Class<?> forName(String name) throws ClassNotFoundException {
        if (null == name || name.isEmpty()) {
            return null;
        }
        Class<?> c = forNamePrimitive(name);
        if (c == null) {
            if (name.endsWith(ClassUtils.ARRAY_SUFFIX)) {
                String nc = name.substring(0, name.length() - 2);
                c = Array.newInstance(Class.forName(nc, true, getContextClassLoader()), 0).getClass();
            } else {
                c = Class.forName(name, true, getContextClassLoader());
            }
        }
        return c;
    }

    protected static Class<?> forNamePrimitive(String name) {
        int p;
        if (name.length() <= 8 && (p = Arrays.binarySearch(PRIMITIVE_NAMES, name)) >= 0) {
            return PRIMITIVES[p];
        }
        return null;
    }

    public static Class<?>[] toTypeArray(String[] s) throws ClassNotFoundException {
        if (s == null) {
            return null;
        }
        Class<?>[] c = new Class[s.length];
        for (int i = 0; i < s.length; i++) {
            c[i] = forName(s[i]);
        }
        return c;
    }

    public static String[] toTypeNameArray(Class<?>[] c) {
        if (c == null) {
            return null;
        }
        String[] s = new String[c.length];
        for (int i = 0; i < c.length; i++) {
            s[i] = c[i].getName();
        }
        return s;
    }

    /* JADX WARN: Code restructure failed: missing block: B:68:0x0158, code lost:            r29 = Integer.MAX_VALUE;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.reflect.Method getMethod(org.apache.el.lang.EvaluationContext r11, java.lang.Object r12, java.lang.Object r13, java.lang.Class<?>[] r14, java.lang.Object[] r15) throws jakarta.el.MethodNotFoundException {
        /*
            Method dump skipped, instructions count: 937
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.el.util.ReflectionUtil.getMethod(org.apache.el.lang.EvaluationContext, java.lang.Object, java.lang.Object, java.lang.Class[], java.lang.Object[]):java.lang.reflect.Method");
    }

    private static Method resolveAmbiguousMethod(Set<Method> candidates, Class<?>[] paramTypes) {
        Method m = candidates.iterator().next();
        int nonMatchIndex = 0;
        Class<?> nonMatchClass = null;
        int i = 0;
        while (true) {
            if (i >= paramTypes.length) {
                break;
            }
            if (m.getParameterTypes()[i] == paramTypes[i]) {
                i++;
            } else {
                nonMatchIndex = i;
                nonMatchClass = paramTypes[i];
                break;
            }
        }
        if (nonMatchClass == null) {
            return null;
        }
        Iterator<Method> it = candidates.iterator();
        while (it.hasNext()) {
            if (it.next().getParameterTypes()[nonMatchIndex] == paramTypes[nonMatchIndex]) {
                return null;
            }
        }
        Class<?> superclass = nonMatchClass.getSuperclass();
        while (true) {
            Class<?> superClass = superclass;
            if (superClass != null) {
                for (Method c : candidates) {
                    if (c.getParameterTypes()[nonMatchIndex].equals(superClass)) {
                        return c;
                    }
                }
                superclass = superClass.getSuperclass();
            } else {
                Method match = null;
                if (Number.class.isAssignableFrom(nonMatchClass)) {
                    Iterator<Method> it2 = candidates.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        Method c2 = it2.next();
                        Class<?> candidateType = c2.getParameterTypes()[nonMatchIndex];
                        if (Number.class.isAssignableFrom(candidateType) || candidateType.isPrimitive()) {
                            if (match == null) {
                                match = c2;
                            } else {
                                match = null;
                                break;
                            }
                        }
                    }
                }
                return match;
            }
        }
    }

    private static boolean isAssignableFrom(Class<?> src, Class<?> target) {
        Class<?> targetClass;
        if (src == null) {
            return true;
        }
        if (target.isPrimitive()) {
            if (target == Boolean.TYPE) {
                targetClass = Boolean.class;
            } else if (target == Character.TYPE) {
                targetClass = Character.class;
            } else if (target == Byte.TYPE) {
                targetClass = Byte.class;
            } else if (target == Short.TYPE) {
                targetClass = Short.class;
            } else if (target == Integer.TYPE) {
                targetClass = Integer.class;
            } else if (target == Long.TYPE) {
                targetClass = Long.class;
            } else if (target == Float.TYPE) {
                targetClass = Float.class;
            } else {
                targetClass = Double.class;
            }
        } else {
            targetClass = target;
        }
        return targetClass.isAssignableFrom(src);
    }

    private static boolean isCoercibleFrom(EvaluationContext ctx, Object src, Class<?> target) {
        try {
            ELSupport.coerceToType(ctx, src, target);
            return true;
        } catch (ELException e) {
            return false;
        }
    }

    private static Method getMethod(Class<?> type, Object base, Method m) {
        Method mp;
        if (m == null || (Modifier.isPublic(type.getModifiers()) && ((Modifier.isStatic(m.getModifiers()) && m.canAccess(null)) || m.canAccess(base)))) {
            return m;
        }
        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> iface : interfaces) {
            try {
                Method mp2 = iface.getMethod(m.getName(), m.getParameterTypes());
                mp = getMethod(mp2.getDeclaringClass(), base, mp2);
            } catch (NoSuchMethodException e) {
            }
            if (mp != null) {
                return mp;
            }
        }
        Class<?> sup = type.getSuperclass();
        if (sup != null) {
            try {
                Method mp3 = sup.getMethod(m.getName(), m.getParameterTypes());
                Method mp4 = getMethod(mp3.getDeclaringClass(), base, mp3);
                if (mp4 != null) {
                    return mp4;
                }
                return null;
            } catch (NoSuchMethodException e2) {
                return null;
            }
        }
        return null;
    }

    private static String paramString(Class<?>[] types) {
        if (types != null) {
            StringBuilder sb = new StringBuilder();
            for (Class<?> type : types) {
                if (type == null) {
                    sb.append("null, ");
                } else {
                    sb.append(type.getName()).append(", ");
                }
            }
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        }
        return null;
    }

    private static ClassLoader getContextClassLoader() {
        ClassLoader tccl;
        if (System.getSecurityManager() != null) {
            PrivilegedAction<ClassLoader> pa = new PrivilegedGetTccl();
            tccl = (ClassLoader) AccessController.doPrivileged(pa);
        } else {
            tccl = Thread.currentThread().getContextClassLoader();
        }
        return tccl;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/util/ReflectionUtil$PrivilegedGetTccl.class */
    public static class PrivilegedGetTccl implements PrivilegedAction<ClassLoader> {
        private PrivilegedGetTccl() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/util/ReflectionUtil$MatchResult.class */
    private static class MatchResult implements Comparable<MatchResult> {
        private final boolean varArgs;
        private final int exactCount;
        private final int assignableCount;
        private final int coercibleCount;
        private final int varArgsCount;
        private final boolean bridge;

        MatchResult(boolean varArgs, int exactCount, int assignableCount, int coercibleCount, int varArgsCount, boolean bridge) {
            this.varArgs = varArgs;
            this.exactCount = exactCount;
            this.assignableCount = assignableCount;
            this.coercibleCount = coercibleCount;
            this.varArgsCount = varArgsCount;
            this.bridge = bridge;
        }

        public boolean isVarArgs() {
            return this.varArgs;
        }

        public int getExactCount() {
            return this.exactCount;
        }

        public int getAssignableCount() {
            return this.assignableCount;
        }

        public int getCoercible() {
            return this.coercibleCount;
        }

        public int getVarArgsCount() {
            return this.varArgsCount;
        }

        public boolean isBridge() {
            return this.bridge;
        }

        @Override // java.lang.Comparable
        public int compareTo(MatchResult o) {
            int cmp = Boolean.compare(o.isVarArgs(), isVarArgs());
            if (cmp == 0) {
                cmp = Integer.compare(getExactCount(), o.getExactCount());
                if (cmp == 0) {
                    cmp = Integer.compare(getAssignableCount(), o.getAssignableCount());
                    if (cmp == 0) {
                        cmp = Integer.compare(getCoercible(), o.getCoercible());
                        if (cmp == 0) {
                            cmp = Integer.compare(o.getVarArgsCount(), getVarArgsCount());
                            if (cmp == 0) {
                                cmp = Boolean.compare(o.isBridge(), isBridge());
                            }
                        }
                    }
                }
            }
            return cmp;
        }

        public boolean equals(Object o) {
            return o == this || (null != o && getClass().equals(o.getClass()) && ((MatchResult) o).getExactCount() == getExactCount() && ((MatchResult) o).getAssignableCount() == getAssignableCount() && ((MatchResult) o).getCoercible() == getCoercible() && ((MatchResult) o).getVarArgsCount() == getVarArgsCount() && ((MatchResult) o).isVarArgs() == isVarArgs() && ((MatchResult) o).isBridge() == isBridge());
        }

        public int hashCode() {
            int result = (31 * 1) + this.assignableCount;
            return (31 * ((31 * ((31 * ((31 * ((31 * result) + (this.bridge ? 1231 : 1237))) + this.coercibleCount)) + this.exactCount)) + (this.varArgs ? 1231 : 1237))) + this.varArgsCount;
        }
    }
}
