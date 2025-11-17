package org.springframework.boot.loader.net.protocol.jar;

/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/Optimizations.class */
final class Optimizations {
    private static final ThreadLocal<Boolean> status = new ThreadLocal<>();

    private Optimizations() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void enable(boolean readContents) {
        status.set(Boolean.valueOf(readContents));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void disable() {
        status.remove();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isEnabled() {
        return status.get() != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isEnabled(boolean readContents) {
        return Boolean.valueOf(readContents).equals(status.get());
    }
}
