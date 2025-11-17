package org.springframework.boot.ssl;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/NoSuchSslBundleException.class */
public class NoSuchSslBundleException extends RuntimeException {
    private final String bundleName;

    public NoSuchSslBundleException(String bundleName, String message) {
        this(bundleName, message, null);
    }

    public NoSuchSslBundleException(String bundleName, String message, Throwable cause) {
        super(message, cause);
        this.bundleName = bundleName;
    }

    public String getBundleName() {
        return this.bundleName;
    }
}
