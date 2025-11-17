package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Assert.class */
abstract class Assert {
    private Assert() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String requireNonEmpty(String s, String message) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return s;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> T requireNonNull(@Nullable T t, String message) {
        if (t == null) {
            throw new IllegalArgumentException(message);
        }
        return t;
    }
}
