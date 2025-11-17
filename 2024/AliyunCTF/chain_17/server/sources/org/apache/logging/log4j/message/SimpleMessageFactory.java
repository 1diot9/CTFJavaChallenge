package org.apache.logging.log4j.message;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/SimpleMessageFactory.class */
public final class SimpleMessageFactory extends AbstractMessageFactory {
    public static final SimpleMessageFactory INSTANCE = new SimpleMessageFactory();
    private static final long serialVersionUID = 4418995198790088516L;

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final String message, final Object... params) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return new SimpleMessage(message);
    }
}
