package org.springframework.web.servlet.theme;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/theme/SessionThemeResolver.class */
public class SessionThemeResolver extends AbstractThemeResolver {
    public static final String THEME_SESSION_ATTRIBUTE_NAME = SessionThemeResolver.class.getName() + ".THEME";

    @Override // org.springframework.web.servlet.ThemeResolver
    public String resolveThemeName(HttpServletRequest request) {
        String themeName = (String) WebUtils.getSessionAttribute(request, THEME_SESSION_ATTRIBUTE_NAME);
        return themeName != null ? themeName : getDefaultThemeName();
    }

    @Override // org.springframework.web.servlet.ThemeResolver
    public void setThemeName(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String themeName) {
        WebUtils.setSessionAttribute(request, THEME_SESSION_ATTRIBUTE_NAME, StringUtils.hasText(themeName) ? themeName : null);
    }
}
