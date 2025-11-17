package org.apache.logging.log4j.spi;

import java.util.Objects;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.SimpleMessage;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/MessageFactory2Adapter.class */
public class MessageFactory2Adapter implements MessageFactory2 {
    private final MessageFactory wrapped;

    public MessageFactory2Adapter(final MessageFactory wrapped) {
        this.wrapped = (MessageFactory) Objects.requireNonNull(wrapped);
    }

    public MessageFactory getOriginal() {
        return this.wrapped;
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final CharSequence charSequence) {
        return new SimpleMessage(charSequence);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0) {
        return this.wrapped.newMessage(message, p0);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1) {
        return this.wrapped.newMessage(message, p0, p1);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        return this.wrapped.newMessage(message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return this.wrapped.newMessage(message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return this.wrapped.newMessage(message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return this.wrapped.newMessage(message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        return this.wrapped.newMessage(message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return this.wrapped.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return this.wrapped.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return this.wrapped.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final Object message) {
        return this.wrapped.newMessage(message);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final String message) {
        return this.wrapped.newMessage(message);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final String message, final Object... params) {
        return this.wrapped.newMessage(message, params);
    }
}
