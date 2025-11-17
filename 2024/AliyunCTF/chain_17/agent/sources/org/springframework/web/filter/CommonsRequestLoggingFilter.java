package org.springframework.web.filter;

import jakarta.servlet.http.HttpServletRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/CommonsRequestLoggingFilter.class */
public class CommonsRequestLoggingFilter extends AbstractRequestLoggingFilter {
    @Override // org.springframework.web.filter.AbstractRequestLoggingFilter
    protected boolean shouldLog(HttpServletRequest request) {
        return this.logger.isDebugEnabled();
    }

    @Override // org.springframework.web.filter.AbstractRequestLoggingFilter
    protected void beforeRequest(HttpServletRequest request, String message) {
        this.logger.debug(message);
    }

    @Override // org.springframework.web.filter.AbstractRequestLoggingFilter
    protected void afterRequest(HttpServletRequest request, String message) {
        this.logger.debug(message);
    }
}
