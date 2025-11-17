package org.springframework.boot.web.servlet.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/filter/ApplicationContextHeaderFilter.class */
public class ApplicationContextHeaderFilter extends OncePerRequestFilter {
    public static final String HEADER_NAME = "X-Application-Context";
    private final ApplicationContext applicationContext;

    public ApplicationContextHeaderFilter(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader(HEADER_NAME, this.applicationContext.getId());
        filterChain.doFilter(request, response);
    }
}
