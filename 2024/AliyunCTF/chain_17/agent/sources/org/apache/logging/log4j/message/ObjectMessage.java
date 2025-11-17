package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ObjectMessage.class */
public class ObjectMessage implements Message, StringBuilderFormattable {
    private static final long serialVersionUID = -5903272448334166185L;
    private transient Object obj;
    private transient String objectString;

    public ObjectMessage(final Object obj) {
        this.obj = obj == null ? "null" : obj;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        if (this.objectString == null) {
            this.objectString = String.valueOf(this.obj);
        }
        return this.objectString;
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        if (this.objectString != null) {
            buffer.append(this.objectString);
        } else {
            StringBuilders.appendValue(buffer, this.obj);
        }
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return getFormattedMessage();
    }

    public Object getParameter() {
        return this.obj;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return new Object[]{this.obj};
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectMessage that = (ObjectMessage) o;
        return this.obj == null ? that.obj == null : equalObjectsOrStrings(this.obj, that.obj);
    }

    private boolean equalObjectsOrStrings(final Object left, final Object right) {
        return left.equals(right) || String.valueOf(left).equals(String.valueOf(right));
    }

    public int hashCode() {
        if (this.obj != null) {
            return this.obj.hashCode();
        }
        return 0;
    }

    public String toString() {
        return getFormattedMessage();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.obj instanceof Serializable) {
            out.writeObject(this.obj);
        } else {
            out.writeObject(String.valueOf(this.obj));
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.obj = in.readObject();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        if (this.obj instanceof Throwable) {
            return (Throwable) this.obj;
        }
        return null;
    }
}
