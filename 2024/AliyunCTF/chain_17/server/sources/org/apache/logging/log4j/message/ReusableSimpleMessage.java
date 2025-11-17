package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ReusableSimpleMessage.class */
public class ReusableSimpleMessage implements ReusableMessage, CharSequence, ParameterVisitable, Clearable {
    private static final long serialVersionUID = -9199974506498249809L;
    private CharSequence charSequence;

    public void set(final String message) {
        this.charSequence = message;
    }

    public void set(final CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        return String.valueOf(this.charSequence);
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        if (this.charSequence instanceof String) {
            return (String) this.charSequence;
        }
        return null;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return Constants.EMPTY_OBJECT_ARRAY;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return null;
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        buffer.append(this.charSequence);
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public Object[] swapParameters(final Object[] emptyReplacement) {
        return emptyReplacement;
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public short getParameterCount() {
        return (short) 0;
    }

    @Override // org.apache.logging.log4j.message.ParameterVisitable
    public <S> void forEachParameter(final ParameterConsumer<S> action, final S state) {
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public Message memento() {
        return new SimpleMessage(this.charSequence);
    }

    @Override // java.lang.CharSequence
    public int length() {
        if (this.charSequence == null) {
            return 0;
        }
        return this.charSequence.length();
    }

    @Override // java.lang.CharSequence
    public char charAt(final int index) {
        return this.charSequence.charAt(index);
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(final int start, final int end) {
        return this.charSequence.subSequence(start, end);
    }

    @Override // org.apache.logging.log4j.message.Clearable
    public void clear() {
        this.charSequence = null;
    }
}
