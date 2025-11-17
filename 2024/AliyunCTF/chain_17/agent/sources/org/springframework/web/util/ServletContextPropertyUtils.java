package org.springframework.web.util;

import jakarta.servlet.ServletContext;
import org.springframework.lang.Nullable;
import org.springframework.util.PropertyPlaceholderHelper;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ServletContextPropertyUtils.class */
public abstract class ServletContextPropertyUtils {
    private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper("${", "}", ":", false);
    private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper("${", "}", ":", true);

    public static String resolvePlaceholders(String text, ServletContext servletContext) {
        return resolvePlaceholders(text, servletContext, false);
    }

    public static String resolvePlaceholders(String text, ServletContext servletContext, boolean ignoreUnresolvablePlaceholders) {
        if (text.isEmpty()) {
            return text;
        }
        PropertyPlaceholderHelper helper = ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper;
        return helper.replacePlaceholders(text, new ServletContextPlaceholderResolver(text, servletContext));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ServletContextPropertyUtils$ServletContextPlaceholderResolver.class */
    public static class ServletContextPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
        private final String text;
        private final ServletContext servletContext;

        public ServletContextPlaceholderResolver(String text, ServletContext servletContext) {
            this.text = text;
            this.servletContext = servletContext;
        }

        @Override // org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver
        @Nullable
        public String resolvePlaceholder(String placeholderName) {
            try {
                String propVal = this.servletContext.getInitParameter(placeholderName);
                if (propVal == null) {
                    propVal = System.getProperty(placeholderName);
                    if (propVal == null) {
                        propVal = System.getenv(placeholderName);
                    }
                }
                return propVal;
            } catch (Throwable ex) {
                System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "] as ServletContext init-parameter or system property: " + ex);
                return null;
            }
        }
    }
}
