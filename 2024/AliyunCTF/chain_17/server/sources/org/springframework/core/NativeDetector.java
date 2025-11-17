package org.springframework.core;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/NativeDetector.class */
public abstract class NativeDetector {

    @Nullable
    private static final String imageCode = System.getProperty("org.graalvm.nativeimage.imagecode");
    private static final boolean inNativeImage;

    static {
        inNativeImage = imageCode != null;
    }

    public static boolean inNativeImage() {
        return inNativeImage;
    }

    public static boolean inNativeImage(Context... contexts) {
        for (Context context : contexts) {
            if (context.key.equals(imageCode)) {
                return true;
            }
        }
        return false;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/NativeDetector$Context.class */
    public enum Context {
        BUILD("buildtime"),
        RUN("runtime");

        private final String key;

        Context(final String key) {
            this.key = key;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.key;
        }
    }
}
