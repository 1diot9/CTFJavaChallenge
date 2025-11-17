package org.springframework.boot.autoconfigure.web.servlet;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/DispatcherServletPath.class */
public interface DispatcherServletPath {
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

    default String getServletUrlMapping() {
        if (getPath().equals("") || getPath().equals("/")) {
            return "/";
        }
        if (getPath().contains("*")) {
            return getPath();
        }
        if (getPath().endsWith("/")) {
            return getPath() + "*";
        }
        return getPath() + "/*";
    }
}
