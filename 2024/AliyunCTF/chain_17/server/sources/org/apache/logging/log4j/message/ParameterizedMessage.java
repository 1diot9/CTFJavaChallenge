package org.apache.logging.log4j.message;

import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.message.ParameterFormatter;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ParameterizedMessage.class */
public class ParameterizedMessage implements Message, StringBuilderFormattable {
    public static final String RECURSION_PREFIX = "[...";
    public static final String RECURSION_SUFFIX = "...]";
    public static final String ERROR_PREFIX = "[!!!";
    public static final String ERROR_SEPARATOR = "=>";
    public static final String ERROR_MSG_SEPARATOR = ":";
    public static final String ERROR_SUFFIX = "!!!]";
    private static final long serialVersionUID = -665975803997290697L;
    private static final ThreadLocal<StringBuilder> STRING_BUILDER_HOLDER;
    private final String pattern;
    private final transient Object[] args;
    private final transient Throwable throwable;
    private final ParameterFormatter.MessagePatternAnalysis patternAnalysis;
    private String formattedMessage;

    static {
        ThreadLocal<StringBuilder> threadLocal;
        if (Constants.ENABLE_THREADLOCALS) {
            threadLocal = ThreadLocal.withInitial(() -> {
                return new StringBuilder(Constants.MAX_REUSABLE_MESSAGE_SIZE);
            });
        } else {
            threadLocal = null;
        }
        STRING_BUILDER_HOLDER = threadLocal;
    }

    @Deprecated
    public ParameterizedMessage(final String pattern, final String[] args, final Throwable throwable) {
        this(pattern, Arrays.stream(args).toArray(x$0 -> {
            return new Object[x$0];
        }), throwable);
    }

    public ParameterizedMessage(final String pattern, final Object[] args, final Throwable throwable) {
        this.args = args;
        this.pattern = pattern;
        this.patternAnalysis = ParameterFormatter.analyzePattern(pattern, args != null ? args.length : 0);
        this.throwable = determineThrowable(throwable, this.args, this.patternAnalysis);
    }

    private static Throwable determineThrowable(final Throwable throwable, final Object[] args, final ParameterFormatter.MessagePatternAnalysis analysis) {
        if (throwable != null) {
            return throwable;
        }
        if (args != null && args.length > analysis.placeholderCount) {
            Object lastArg = args[args.length - 1];
            if (lastArg instanceof Throwable) {
                return (Throwable) lastArg;
            }
            return null;
        }
        return null;
    }

    public ParameterizedMessage(final String pattern, final Object... args) {
        this(pattern, args, (Throwable) null);
    }

    public ParameterizedMessage(final String pattern, final Object arg) {
        this(pattern, arg);
    }

    public ParameterizedMessage(final String pattern, final Object arg0, final Object arg1) {
        this(pattern, arg0, arg1);
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return this.pattern;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return this.args;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        if (this.formattedMessage == null) {
            if (STRING_BUILDER_HOLDER != null) {
                StringBuilder buffer = STRING_BUILDER_HOLDER.get();
                buffer.setLength(0);
                formatTo(buffer);
                this.formattedMessage = buffer.toString();
                StringBuilders.trimToMaxSize(buffer, Constants.MAX_REUSABLE_MESSAGE_SIZE);
            } else {
                StringBuilder buffer2 = new StringBuilder();
                formatTo(buffer2);
                this.formattedMessage = buffer2.toString();
            }
        }
        return this.formattedMessage;
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        if (this.formattedMessage != null) {
            buffer.append(this.formattedMessage);
        } else {
            int argCount = this.args != null ? this.args.length : 0;
            ParameterFormatter.formatMessage(buffer, this.pattern, this.args, argCount, this.patternAnalysis);
        }
    }

    public static String format(final String pattern, final Object[] args) {
        int argCount = args != null ? args.length : 0;
        return ParameterFormatter.format(pattern, args, argCount);
    }

    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ParameterizedMessage that = (ParameterizedMessage) object;
        return Objects.equals(this.pattern, that.pattern) && Arrays.equals(this.args, that.args);
    }

    public int hashCode() {
        int result = this.pattern != null ? this.pattern.hashCode() : 0;
        return (31 * result) + (this.args != null ? Arrays.hashCode(this.args) : 0);
    }

    public static int countArgumentPlaceholders(final String pattern) {
        if (pattern == null) {
            return 0;
        }
        return ParameterFormatter.analyzePattern(pattern, -1).placeholderCount;
    }

    public static String deepToString(final Object o) {
        return ParameterFormatter.deepToString(o);
    }

    public static String identityToString(final Object obj) {
        return ParameterFormatter.identityToString(obj);
    }

    public String toString() {
        return "ParameterizedMessage[messagePattern=" + this.pattern + ", stringArgs=" + Arrays.toString(this.args) + ", throwable=" + this.throwable + ']';
    }
}
