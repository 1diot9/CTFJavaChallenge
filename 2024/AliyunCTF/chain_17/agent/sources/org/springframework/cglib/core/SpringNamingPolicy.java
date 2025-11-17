package org.springframework.cglib.core;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/SpringNamingPolicy.class */
public final class SpringNamingPolicy implements NamingPolicy {
    public static final SpringNamingPolicy INSTANCE = new SpringNamingPolicy();
    private static final String SPRING_LABEL = "$$SpringCGLIB$$";
    private static final String FAST_CLASS_SUFFIX = "FastClass$$";

    private SpringNamingPolicy() {
    }

    @Override // org.springframework.cglib.core.NamingPolicy
    public String getClassName(String prefix, String source, Object key, Predicate names) {
        String base;
        if (prefix == null) {
            prefix = "org.springframework.cglib.empty.Object";
        } else if (prefix.startsWith("java.") || prefix.startsWith("javax.")) {
            prefix = "_" + prefix;
        }
        int existingLabel = prefix.indexOf(SPRING_LABEL);
        if (existingLabel >= 0) {
            base = prefix.substring(0, existingLabel + SPRING_LABEL.length());
        } else {
            base = prefix + "$$SpringCGLIB$$";
        }
        boolean isFastClass = source != null && source.endsWith(".FastClass");
        if (isFastClass && !prefix.contains(FAST_CLASS_SUFFIX)) {
            base = base + "FastClass$$";
        }
        int index = 0;
        String str = base;
        int i = 0;
        while (true) {
            String attempt = str + i;
            if (names.evaluate(attempt)) {
                str = base;
                i = index;
                index++;
            } else {
                return attempt;
            }
        }
    }
}
