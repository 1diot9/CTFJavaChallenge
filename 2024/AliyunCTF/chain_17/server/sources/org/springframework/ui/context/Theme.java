package org.springframework.ui.context;

import org.springframework.context.MessageSource;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/context/Theme.class */
public interface Theme {
    String getName();

    MessageSource getMessageSource();
}
