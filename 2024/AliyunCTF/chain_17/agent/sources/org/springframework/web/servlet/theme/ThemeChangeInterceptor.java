package org.springframework.web.servlet.theme;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/theme/ThemeChangeInterceptor.class */
public class ThemeChangeInterceptor implements HandlerInterceptor {
    public static final String DEFAULT_PARAM_NAME = "theme";
    private String paramName = "theme";

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String newTheme = request.getParameter(this.paramName);
        if (newTheme != null) {
            ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
            if (themeResolver == null) {
                throw new IllegalStateException("No ThemeResolver found: not in a DispatcherServlet request?");
            }
            themeResolver.setThemeName(request, response, newTheme);
            return true;
        }
        return true;
    }
}
