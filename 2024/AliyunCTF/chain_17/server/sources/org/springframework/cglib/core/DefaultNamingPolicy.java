package org.springframework.cglib.core;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/DefaultNamingPolicy.class */
public class DefaultNamingPolicy implements NamingPolicy {
    public static final DefaultNamingPolicy INSTANCE = new DefaultNamingPolicy();
    private static final boolean STRESS_HASH_CODE = Boolean.getBoolean("org.springframework.cglib.test.stressHashCodes");

    @Override // org.springframework.cglib.core.NamingPolicy
    public String getClassName(String prefix, String source, Object key, Predicate names) {
        if (prefix == null) {
            prefix = "org.springframework.cglib.empty.Object";
        } else if (prefix.startsWith("java")) {
            prefix = "$" + prefix;
        }
        String base = prefix + "$$" + source.substring(source.lastIndexOf(46) + 1) + getTag() + "$$" + Integer.toHexString(STRESS_HASH_CODE ? 0 : key.hashCode());
        String attempt = base;
        int index = 2;
        while (names.evaluate(attempt)) {
            int i = index;
            index++;
            attempt = base + "_" + i;
        }
        return attempt;
    }

    protected String getTag() {
        return "ByCGLIB";
    }

    public int hashCode() {
        return getTag().hashCode();
    }

    @Override // org.springframework.cglib.core.NamingPolicy
    public boolean equals(Object o) {
        if (o instanceof DefaultNamingPolicy) {
            DefaultNamingPolicy defaultNamingPolicy = (DefaultNamingPolicy) o;
            if (defaultNamingPolicy.getTag().equals(getTag())) {
                return true;
            }
        }
        return false;
    }
}
