package org.springframework.web.servlet.theme;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/theme/FixedThemeResolver.class */
public class FixedThemeResolver extends AbstractThemeResolver {
    @Override // org.springframework.web.servlet.ThemeResolver
    public String resolveThemeName(HttpServletRequest request) {
        return getDefaultThemeName();
    }

    @Override // org.springframework.web.servlet.ThemeResolver
    public void setThemeName(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String themeName) {
        throw new UnsupportedOperationException("Cannot change theme - use a different theme resolution strategy");
    }
}
