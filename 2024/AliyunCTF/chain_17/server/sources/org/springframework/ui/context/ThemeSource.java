package org.springframework.ui.context;

import org.springframework.lang.Nullable;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/context/ThemeSource.class */
public interface ThemeSource {
    @Nullable
    Theme getTheme(String themeName);
}
