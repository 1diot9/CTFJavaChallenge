package org.springframework.boot.autoconfigure.web.servlet;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/JerseyApplicationPath.class */
public interface JerseyApplicationPath {
    String getPath();

    default String getRelativePath(String path) {
        String prefix = getPrefix();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return prefix + path;
    }

    default String getPrefix() {
        String result = getPath();
        int index = result.indexOf(42);
        if (index != -1) {
            result = result.substring(0, index);
        }
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    default String getUrlMapping() {
        String path = getPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.equals("/")) {
            return "/*";
        }
        if (path.contains("*")) {
            return path;
        }
        if (path.endsWith("/")) {
            return path + "*";
        }
        return path + "/*";
    }
}
