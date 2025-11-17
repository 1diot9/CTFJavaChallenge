package org.springframework.web.servlet.tags;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/ThemeTag.class */
public class ThemeTag extends MessageTag {
    @Override // org.springframework.web.servlet.tags.MessageTag
    protected MessageSource getMessageSource() {
        return getRequestContext().getTheme().getMessageSource();
    }

    @Override // org.springframework.web.servlet.tags.MessageTag
    protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
        return "Theme '" + getRequestContext().getTheme().getName() + "': " + ex.getMessage();
    }
}
