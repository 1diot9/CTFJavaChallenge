package io.micrometer.common.util.internal.logging;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/util/internal/logging/FormattingTuple.class */
final class FormattingTuple {
    private final String message;
    private final Throwable throwable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FormattingTuple(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}
