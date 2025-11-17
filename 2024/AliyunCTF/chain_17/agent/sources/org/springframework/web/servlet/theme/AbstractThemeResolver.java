package org.springframework.web.servlet.theme;

import org.springframework.web.servlet.ThemeResolver;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/theme/AbstractThemeResolver.class */
public abstract class AbstractThemeResolver implements ThemeResolver {
    public static final String ORIGINAL_DEFAULT_THEME_NAME = "theme";
    private String defaultThemeName = "theme";

    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

    public String getDefaultThemeName() {
        return this.defaultThemeName;
    }
}
