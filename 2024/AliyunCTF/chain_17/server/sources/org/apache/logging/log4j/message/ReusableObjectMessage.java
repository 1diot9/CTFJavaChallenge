package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

@PerformanceSensitive({"allocation"})
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ReusableObjectMessage.class */
public class ReusableObjectMessage implements ReusableMessage, ParameterVisitable, Clearable {
    private static final long serialVersionUID = 6922476812535519960L;
    private transient Object obj;

    public void set(final Object object) {
        this.obj = object;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        return String.valueOf(this.obj);
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        StringBuilders.appendValue(buffer, this.obj);
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        if (this.obj instanceof String) {
            return (String) this.obj;
        }
        return null;
    }

    public Object getParameter() {
        return this.obj;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return new Object[]{this.obj};
    }

    public String toString() {
        return getFormattedMessage();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        if (this.obj instanceof Throwable) {
            return (Throwable) this.obj;
        }
        return null;
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public Object[] swapParameters(final Object[] emptyReplacement) {
        if (emptyReplacement.length == 0) {
            Object[] params = new Object[10];
            params[0] = this.obj;
            return params;
        }
        emptyReplacement[0] = this.obj;
        return emptyReplacement;
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public short getParameterCount() {
        return (short) 1;
    }

    @Override // org.apache.logging.log4j.message.ParameterVisitable
    public <S> void forEachParameter(final ParameterConsumer<S> action, final S state) {
        action.accept(this.obj, 0, state);
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public Message memento() {
        return new ObjectMessage(this.obj);
    }

    @Override // org.apache.logging.log4j.message.Clearable
    public void clear() {
        this.obj = null;
    }
}
