package org.springframework.core.log;

import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogMessage.class */
public abstract class LogMessage implements CharSequence {

    @Nullable
    private String result;

    abstract String buildString();

    @Override // java.lang.CharSequence
    public int length() {
        return toString().length();
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override // java.lang.CharSequence
    public String toString() {
        if (this.result == null) {
            this.result = buildString();
        }
        return this.result;
    }

    public static LogMessage of(Supplier<? extends CharSequence> supplier) {
        return new SupplierMessage(supplier);
    }

    public static LogMessage format(String format, @Nullable Object arg1) {
        return new FormatMessage1(format, arg1);
    }

    public static LogMessage format(String format, @Nullable Object arg1, @Nullable Object arg2) {
        return new FormatMessage2(format, arg1, arg2);
    }

    public static LogMessage format(String format, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3) {
        return new FormatMessage3(format, arg1, arg2, arg3);
    }

    public static LogMessage format(String format, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4) {
        return new FormatMessage4(format, arg1, arg2, arg3, arg4);
    }

    public static LogMessage format(String format, @Nullable Object... args) {
        return new FormatMessageX(format, args);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogMessage$SupplierMessage.class */
    private static final class SupplierMessage extends LogMessage {
        private final Supplier<? extends CharSequence> supplier;

        SupplierMessage(Supplier<? extends CharSequence> supplier) {
            Assert.notNull(supplier, "Supplier must not be null");
            this.supplier = supplier;
        }

        @Override // org.springframework.core.log.LogMessage
        String buildString() {
            return this.supplier.get().toString();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogMessage$FormatMessage.class */
    private static abstract class FormatMessage extends LogMessage {
        protected final String format;

        FormatMessage(String format) {
            Assert.notNull(format, "Format must not be null");
            this.format = format;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogMessage$FormatMessage1.class */
    private static final class FormatMessage1 extends FormatMessage {

        @Nullable
        private final Object arg1;

        FormatMessage1(String format, @Nullable Object arg1) {
            super(format);
            this.arg1 = arg1;
        }

        @Override // org.springframework.core.log.LogMessage
        protected String buildString() {
            return String.format(this.format, this.arg1);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogMessage$FormatMessage2.class */
    private static final class FormatMessage2 extends FormatMessage {

        @Nullable
        private final Object arg1;

        @Nullable
        private final Object arg2;

        FormatMessage2(String format, @Nullable Object arg1, @Nullable Object arg2) {
            super(format);
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        @Override // org.springframework.core.log.LogMessage
        String buildString() {
            return String.format(this.format, this.arg1, this.arg2);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogMessage$FormatMessage3.class */
    private static final class FormatMessage3 extends FormatMessage {

        @Nullable
        private final Object arg1;

        @Nullable
        private final Object arg2;

        @Nullable
        private final Object arg3;

        FormatMessage3(String format, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3) {
            super(format);
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
        }

        @Override // org.springframework.core.log.LogMessage
        String buildString() {
            return String.format(this.format, this.arg1, this.arg2, this.arg3);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogMessage$FormatMessage4.class */
    private static final class FormatMessage4 extends FormatMessage {

        @Nullable
        private final Object arg1;

        @Nullable
        private final Object arg2;

        @Nullable
        private final Object arg3;

        @Nullable
        private final Object arg4;

        FormatMessage4(String format, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4) {
            super(format);
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
            this.arg4 = arg4;
        }

        @Override // org.springframework.core.log.LogMessage
        String buildString() {
            return String.format(this.format, this.arg1, this.arg2, this.arg3, this.arg4);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogMessage$FormatMessageX.class */
    private static final class FormatMessageX extends FormatMessage {

        @Nullable
        private final Object[] args;

        FormatMessageX(String format, @Nullable Object... args) {
            super(format);
            this.args = args;
        }

        @Override // org.springframework.core.log.LogMessage
        String buildString() {
            return String.format(this.format, this.args);
        }
    }
}
