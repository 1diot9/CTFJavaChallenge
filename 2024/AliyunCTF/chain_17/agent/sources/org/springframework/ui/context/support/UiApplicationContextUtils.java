package org.springframework.ui.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.ThemeSource;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/context/support/UiApplicationContextUtils.class */
public abstract class UiApplicationContextUtils {
    public static final String THEME_SOURCE_BEAN_NAME = "themeSource";
    private static final Log logger = LogFactory.getLog((Class<?>) UiApplicationContextUtils.class);

    public static ThemeSource initThemeSource(ApplicationContext context) {
        HierarchicalThemeSource themeSource;
        if (context.containsLocalBean(THEME_SOURCE_BEAN_NAME)) {
            ThemeSource themeSource2 = (ThemeSource) context.getBean(THEME_SOURCE_BEAN_NAME, ThemeSource.class);
            ApplicationContext parent = context.getParent();
            if (parent instanceof ThemeSource) {
                ThemeSource pts = (ThemeSource) parent;
                if (themeSource2 instanceof HierarchicalThemeSource) {
                    HierarchicalThemeSource hts = (HierarchicalThemeSource) themeSource2;
                    if (hts.getParentThemeSource() == null) {
                        hts.setParentThemeSource(pts);
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Using ThemeSource [" + themeSource2 + "]");
            }
            return themeSource2;
        }
        ApplicationContext parent2 = context.getParent();
        if (parent2 instanceof ThemeSource) {
            ThemeSource pts2 = (ThemeSource) parent2;
            themeSource = new DelegatingThemeSource();
            themeSource.setParentThemeSource(pts2);
        } else {
            themeSource = new ResourceBundleThemeSource();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Unable to locate ThemeSource with name 'themeSource': using default [" + themeSource + "]");
        }
        return themeSource;
    }
}
