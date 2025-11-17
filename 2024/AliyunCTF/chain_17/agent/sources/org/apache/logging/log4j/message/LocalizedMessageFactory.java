package org.apache.logging.log4j.message;

import java.util.ResourceBundle;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/LocalizedMessageFactory.class */
public class LocalizedMessageFactory extends AbstractMessageFactory {
    private static final long serialVersionUID = -1996295808703146741L;
    private final transient ResourceBundle resourceBundle;
    private final String baseName;

    public LocalizedMessageFactory(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.baseName = null;
    }

    public LocalizedMessageFactory(final String baseName) {
        this.resourceBundle = null;
        this.baseName = baseName;
    }

    public String getBaseName() {
        return this.baseName;
    }

    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final String key) {
        if (this.resourceBundle == null) {
            return new LocalizedMessage(this.baseName, key);
        }
        return new LocalizedMessage(this.resourceBundle, key);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(final String key, final Object... params) {
        if (this.resourceBundle == null) {
            return new LocalizedMessage(this.baseName, key, params);
        }
        return new LocalizedMessage(this.resourceBundle, key, params);
    }
}
