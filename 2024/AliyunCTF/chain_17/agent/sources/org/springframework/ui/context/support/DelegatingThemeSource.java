package org.springframework.ui.context.support;

import org.springframework.lang.Nullable;
import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/context/support/DelegatingThemeSource.class */
public class DelegatingThemeSource implements HierarchicalThemeSource {

    @Nullable
    private ThemeSource parentThemeSource;

    @Override // org.springframework.ui.context.HierarchicalThemeSource
    public void setParentThemeSource(@Nullable ThemeSource parentThemeSource) {
        this.parentThemeSource = parentThemeSource;
    }

    @Override // org.springframework.ui.context.HierarchicalThemeSource
    @Nullable
    public ThemeSource getParentThemeSource() {
        return this.parentThemeSource;
    }

    @Override // org.springframework.ui.context.ThemeSource
    @Nullable
    public Theme getTheme(String themeName) {
        if (this.parentThemeSource != null) {
            return this.parentThemeSource.getTheme(themeName);
        }
        return null;
    }
}
