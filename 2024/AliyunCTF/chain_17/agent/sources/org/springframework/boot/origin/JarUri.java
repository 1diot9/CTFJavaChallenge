package org.springframework.boot.origin;

import java.net.URI;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/origin/JarUri.class */
final class JarUri {
    private static final String JAR_SCHEME = "jar:";
    private static final String JAR_EXTENSION = ".jar";
    private final String uri;
    private final String description;

    private JarUri(String uri) {
        this.uri = uri;
        this.description = extractDescription(uri);
    }

    private String extractDescription(String uri) {
        String uri2 = uri.substring("jar:".length());
        int firstDotJar = uri2.indexOf(".jar");
        String firstJar = getFilename(uri2.substring(0, firstDotJar + ".jar".length()));
        String uri3 = uri2.substring(firstDotJar + ".jar".length());
        int lastDotJar = uri3.lastIndexOf(".jar");
        if (lastDotJar == -1) {
            return firstJar;
        }
        return firstJar + uri3.substring(0, lastDotJar + ".jar".length());
    }

    private String getFilename(String string) {
        int lastSlash = string.lastIndexOf(47);
        return lastSlash == -1 ? string : string.substring(lastSlash + 1);
    }

    String getDescription() {
        return this.description;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getDescription(String existing) {
        return existing + " from " + this.description;
    }

    public String toString() {
        return this.uri;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JarUri from(URI uri) {
        return from(uri.toString());
    }

    static JarUri from(String uri) {
        if (uri.startsWith("jar:") && uri.contains(".jar")) {
            return new JarUri(uri);
        }
        return null;
    }
}
