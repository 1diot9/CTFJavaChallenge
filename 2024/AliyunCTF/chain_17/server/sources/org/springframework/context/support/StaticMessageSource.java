package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/StaticMessageSource.class */
public class StaticMessageSource extends AbstractMessageSource {
    private final Map<String, Map<Locale, MessageHolder>> messageMap = new HashMap();

    @Override // org.springframework.context.support.AbstractMessageSource
    @Nullable
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        MessageHolder holder;
        Map<Locale, MessageHolder> localeMap = this.messageMap.get(code);
        if (localeMap == null || (holder = localeMap.get(locale)) == null) {
            return null;
        }
        return holder.getMessage();
    }

    @Override // org.springframework.context.support.AbstractMessageSource
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        MessageHolder holder;
        Map<Locale, MessageHolder> localeMap = this.messageMap.get(code);
        if (localeMap == null || (holder = localeMap.get(locale)) == null) {
            return null;
        }
        return holder.getMessageFormat();
    }

    public void addMessage(String code, Locale locale, String msg) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(locale, "Locale must not be null");
        Assert.notNull(msg, "Message must not be null");
        this.messageMap.computeIfAbsent(code, key -> {
            return new HashMap(4);
        }).put(locale, new MessageHolder(msg, locale));
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Added message [" + msg + "] for code [" + code + "] and Locale [" + locale + "]");
        }
    }

    public void addMessages(Map<String, String> messages, Locale locale) {
        Assert.notNull(messages, "Messages Map must not be null");
        messages.forEach((code, msg) -> {
            addMessage(code, locale, msg);
        });
    }

    public String toString() {
        return getClass().getName() + ": " + this.messageMap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/StaticMessageSource$MessageHolder.class */
    public class MessageHolder {
        private final String message;
        private final Locale locale;

        @Nullable
        private volatile MessageFormat cachedFormat;

        public MessageHolder(String message, Locale locale) {
            this.message = message;
            this.locale = locale;
        }

        public String getMessage() {
            return this.message;
        }

        public MessageFormat getMessageFormat() {
            MessageFormat messageFormat = this.cachedFormat;
            if (messageFormat == null) {
                messageFormat = StaticMessageSource.this.createMessageFormat(this.message, this.locale);
                this.cachedFormat = messageFormat;
            }
            return messageFormat;
        }

        public String toString() {
            return this.message;
        }
    }
}
