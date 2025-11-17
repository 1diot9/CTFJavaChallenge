package org.springframework.web.servlet.i18n;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/i18n/LocaleChangeInterceptor.class */
public class LocaleChangeInterceptor implements HandlerInterceptor {
    public static final String DEFAULT_PARAM_NAME = "locale";

    @Nullable
    private String[] httpMethods;
    protected final Log logger = LogFactory.getLog(getClass());
    private String paramName = DEFAULT_PARAM_NAME;
    private boolean ignoreInvalidLocale = false;

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setHttpMethods(@Nullable String... httpMethods) {
        this.httpMethods = httpMethods;
    }

    @Nullable
    public String[] getHttpMethods() {
        return this.httpMethods;
    }

    public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
        this.ignoreInvalidLocale = ignoreInvalidLocale;
    }

    public boolean isIgnoreInvalidLocale() {
        return this.ignoreInvalidLocale;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String newLocale = request.getParameter(getParamName());
        if (newLocale != null && checkHttpMethod(request.getMethod())) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            try {
                localeResolver.setLocale(request, response, parseLocaleValue(newLocale));
                return true;
            } catch (IllegalArgumentException ex) {
                if (isIgnoreInvalidLocale()) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + ex.getMessage());
                        return true;
                    }
                    return true;
                }
                throw ex;
            }
        }
        return true;
    }

    private boolean checkHttpMethod(String currentMethod) {
        String[] configuredMethods = getHttpMethods();
        if (ObjectUtils.isEmpty((Object[]) configuredMethods)) {
            return true;
        }
        for (String configuredMethod : configuredMethods) {
            if (configuredMethod.equalsIgnoreCase(currentMethod)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }
}
