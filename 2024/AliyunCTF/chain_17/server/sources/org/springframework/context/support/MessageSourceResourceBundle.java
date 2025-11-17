package org.springframework.context.support;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/MessageSourceResourceBundle.class */
public class MessageSourceResourceBundle extends ResourceBundle {
    private final MessageSource messageSource;
    private final Locale locale;

    public MessageSourceResourceBundle(MessageSource source, Locale locale) {
        Assert.notNull(source, "MessageSource must not be null");
        this.messageSource = source;
        this.locale = locale;
    }

    public MessageSourceResourceBundle(MessageSource source, Locale locale, ResourceBundle parent) {
        this(source, locale);
        setParent(parent);
    }

    @Override // java.util.ResourceBundle
    @Nullable
    protected Object handleGetObject(String key) {
        try {
            return this.messageSource.getMessage(key, null, this.locale);
        } catch (NoSuchMessageException e) {
            return null;
        }
    }

    @Override // java.util.ResourceBundle
    public boolean containsKey(String key) {
        try {
            this.messageSource.getMessage(key, null, this.locale);
            return true;
        } catch (NoSuchMessageException e) {
            return false;
        }
    }

    @Override // java.util.ResourceBundle
    public Enumeration<String> getKeys() {
        throw new UnsupportedOperationException("MessageSourceResourceBundle does not support enumerating its keys");
    }

    @Override // java.util.ResourceBundle
    public Locale getLocale() {
        return this.locale;
    }
}
