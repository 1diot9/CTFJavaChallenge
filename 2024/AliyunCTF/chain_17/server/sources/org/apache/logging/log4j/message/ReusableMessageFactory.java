package org.apache.logging.log4j.message;

import java.io.Serializable;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ReusableMessageFactory.class */
public final class ReusableMessageFactory implements MessageFactory2, Serializable {
    public static final ReusableMessageFactory INSTANCE = new ReusableMessageFactory();
    private static final long serialVersionUID = -8970940216592525651L;
    private final ThreadLocal<ReusableParameterizedMessage> threadLocalParameterized = new ThreadLocal<>();
    private final ThreadLocal<ReusableSimpleMessage> threadLocalSimpleMessage = new ThreadLocal<>();
    private final ThreadLocal<ReusableObjectMessage> threadLocalObjectMessage = new ThreadLocal<>();

    private ReusableParameterizedMessage getParameterized() {
        ReusableParameterizedMessage result = this.threadLocalParameterized.get();
        if (result == null) {
            result = new ReusableParameterizedMessage();
            this.threadLocalParameterized.set(result);
        }
        return result.reserved ? new ReusableParameterizedMessage().reserve() : result.reserve();
    }

    private ReusableSimpleMessage getSimple() {
        ReusableSimpleMessage result = this.threadLocalSimpleMessage.get();
        if (result == null) {
            result = new ReusableSimpleMessage();
            this.threadLocalSimpleMessage.set(result);
        }
        return result;
    }

    private ReusableObjectMessage getObject() {
        ReusableObjectMessage result = this.threadLocalObjectMessage.get();
        if (result == null) {
            result = new ReusableObjectMessage();
            this.threadLocalObjectMessage.set(result);
        }
        return result;
    }

    public static void release(final Message message) {
        if (message instanceof Clearable) {
            ((Clearable) message).clear();
        }
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final CharSequence charSequence) {
        ReusableSimpleMessage result = getSimple();
        result.set(charSequence);
        return result;
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final String message, final Object... params) {
        return getParameterized().set(message, params);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0) {
        return getParameterized().set(message, p0);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1) {
        return getParameterized().set(message, p0, p1);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        return getParameterized().set(message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return getParameterized().set(message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return getParameterized().set(message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final String message) {
        ReusableSimpleMessage result = getSimple();
        result.set(message);
        return result;
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final Object message) {
        ReusableObjectMessage result = getObject();
        result.set(message);
        return result;
    }
}
