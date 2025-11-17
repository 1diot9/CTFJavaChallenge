package org.springframework.boot.loader.log;

/* loaded from: server.jar:org/springframework/boot/loader/log/DebugLogger.class */
public abstract class DebugLogger {
    private static final String ENABLED_PROPERTY = "loader.debug";
    private static final DebugLogger disabled;

    public abstract void log(String message);

    public abstract void log(String message, Object arg1);

    public abstract void log(String message, Object arg1, Object arg2);

    public abstract void log(String message, Object arg1, Object arg2, Object arg3);

    public abstract void log(String message, Object arg1, Object arg2, Object arg3, Object arg4);

    static {
        disabled = Boolean.getBoolean(ENABLED_PROPERTY) ? null : new DisabledDebugLogger();
    }

    public static DebugLogger get(Class<?> sourceClass) {
        return disabled != null ? disabled : new SystemErrDebugLogger(sourceClass);
    }

    /* loaded from: server.jar:org/springframework/boot/loader/log/DebugLogger$DisabledDebugLogger.class */
    private static final class DisabledDebugLogger extends DebugLogger {
        private DisabledDebugLogger() {
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message) {
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message, Object arg1) {
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message, Object arg1, Object arg2) {
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message, Object arg1, Object arg2, Object arg3) {
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message, Object arg1, Object arg2, Object arg3, Object arg4) {
        }
    }

    /* loaded from: server.jar:org/springframework/boot/loader/log/DebugLogger$SystemErrDebugLogger.class */
    private static final class SystemErrDebugLogger extends DebugLogger {
        private final String prefix;

        SystemErrDebugLogger(Class<?> sourceClass) {
            this.prefix = "LOADER: " + sourceClass + " : ";
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message) {
            print(message);
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message, Object arg1) {
            print(message.formatted(arg1));
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message, Object arg1, Object arg2) {
            print(message.formatted(arg1, arg2));
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message, Object arg1, Object arg2, Object arg3) {
            print(message.formatted(arg1, arg2, arg3));
        }

        @Override // org.springframework.boot.loader.log.DebugLogger
        public void log(String message, Object arg1, Object arg2, Object arg3, Object arg4) {
            print(message.formatted(arg1, arg2, arg3, arg4));
        }

        private void print(String message) {
            System.err.println(this.prefix + message);
        }
    }
}
