package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.logging.log4j.util.StringBuilderFormattable;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/SimpleMessage.class */
public class SimpleMessage implements Message, StringBuilderFormattable, CharSequence {
    private static final long serialVersionUID = -8398002534962715992L;
    private String message;
    private transient CharSequence charSequence;

    public SimpleMessage() {
        this((String) null);
    }

    public SimpleMessage(final String message) {
        this.message = message;
        this.charSequence = message;
    }

    public SimpleMessage(final CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        String valueOf = this.message == null ? String.valueOf(this.charSequence) : this.message;
        this.message = valueOf;
        return valueOf;
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        buffer.append(this.message != null ? this.message : this.charSequence);
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return this.message;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return null;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimpleMessage that = (SimpleMessage) o;
        return this.charSequence == null ? that.charSequence == null : this.charSequence.equals(that.charSequence);
    }

    public int hashCode() {
        if (this.charSequence != null) {
            return this.charSequence.hashCode();
        }
        return 0;
    }

    @Override // java.lang.CharSequence
    public String toString() {
        return getFormattedMessage();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return null;
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

    private void writeObject(final ObjectOutputStream out) throws IOException {
        getFormattedMessage();
        out.defaultWriteObject();
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.charSequence = this.message;
    }
}
