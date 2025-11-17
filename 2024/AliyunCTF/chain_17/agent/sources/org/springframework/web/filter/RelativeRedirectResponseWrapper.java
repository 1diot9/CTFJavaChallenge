package org.springframework.web.filter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/RelativeRedirectResponseWrapper.class */
final class RelativeRedirectResponseWrapper extends HttpServletResponseWrapper {
    private final HttpStatusCode redirectStatus;

    private RelativeRedirectResponseWrapper(HttpServletResponse response, HttpStatusCode redirectStatus) {
        super(response);
        Assert.notNull(redirectStatus, "'redirectStatus' is required");
        this.redirectStatus = redirectStatus;
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        resetBuffer();
        setStatus(this.redirectStatus.value());
        setHeader("Location", location);
        flushBuffer();
    }

    public static HttpServletResponse wrapIfNecessary(HttpServletResponse response, HttpStatusCode redirectStatus) {
        RelativeRedirectResponseWrapper wrapper = (RelativeRedirectResponseWrapper) WebUtils.getNativeResponse(response, RelativeRedirectResponseWrapper.class);
        return wrapper != null ? response : new RelativeRedirectResponseWrapper(response, redirectStatus);
    }
}
