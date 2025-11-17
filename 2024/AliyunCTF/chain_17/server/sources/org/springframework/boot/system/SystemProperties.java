package org.springframework.boot.system;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/system/SystemProperties.class */
public final class SystemProperties {
    private SystemProperties() {
    }

    public static String get(String... properties) {
        String override;
        for (String property : properties) {
            try {
                String override2 = System.getProperty(property);
                override = override2 != null ? override2 : System.getenv(property);
            } catch (Throwable ex) {
                System.err.println("Could not resolve '" + property + "' as system property: " + ex);
            }
            if (override != null) {
                return override;
            }
        }
        return null;
    }
}
