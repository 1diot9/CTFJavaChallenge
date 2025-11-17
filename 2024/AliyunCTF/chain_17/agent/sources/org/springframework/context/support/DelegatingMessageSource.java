package org.springframework.context.support;

import java.util.Locale;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/DelegatingMessageSource.class */
public class DelegatingMessageSource extends MessageSourceSupport implements HierarchicalMessageSource {

    @Nullable
    private MessageSource parentMessageSource;

    @Override // org.springframework.context.HierarchicalMessageSource
    public void setParentMessageSource(@Nullable MessageSource parent) {
        this.parentMessageSource = parent;
    }

    @Override // org.springframework.context.HierarchicalMessageSource
    @Nullable
    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }

    @Override // org.springframework.context.MessageSource
    @Nullable
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(code, args, defaultMessage, locale);
        }
        if (defaultMessage != null) {
            return renderDefaultMessage(defaultMessage, args, locale);
        }
        return null;
    }

    @Override // org.springframework.context.MessageSource
    public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(code, args, locale);
        }
        throw new NoSuchMessageException(code, locale);
    }

    @Override // org.springframework.context.MessageSource
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(resolvable, locale);
        }
        if (resolvable.getDefaultMessage() != null) {
            return renderDefaultMessage(resolvable.getDefaultMessage(), resolvable.getArguments(), locale);
        }
        String[] codes = resolvable.getCodes();
        String code = (codes == null || codes.length <= 0) ? "" : codes[0];
        throw new NoSuchMessageException(code, locale);
    }

    public String toString() {
        return this.parentMessageSource != null ? this.parentMessageSource.toString() : "Empty MessageSource";
    }
}
