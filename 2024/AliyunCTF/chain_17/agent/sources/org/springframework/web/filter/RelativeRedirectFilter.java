package org.springframework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/RelativeRedirectFilter.class */
public class RelativeRedirectFilter extends OncePerRequestFilter {
    private HttpStatusCode redirectStatus = HttpStatus.SEE_OTHER;

    public void setRedirectStatus(HttpStatusCode status) {
        Assert.notNull(status, "Property 'redirectStatus' is required");
        Assert.isTrue(status.is3xxRedirection(), (Supplier<String>) () -> {
            return "Not a redirect status code: " + status;
        });
        this.redirectStatus = status;
    }

    public HttpStatusCode getRedirectStatus() {
        return this.redirectStatus;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, RelativeRedirectResponseWrapper.wrapIfNecessary(response, this.redirectStatus));
    }
}
