package org.springframework.web.servlet.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/UserRoleAuthorizationInterceptor.class */
public class UserRoleAuthorizationInterceptor implements HandlerInterceptor {

    @Nullable
    private String[] authorizedRoles;

    public final void setAuthorizedRoles(String... authorizedRoles) {
        this.authorizedRoles = authorizedRoles;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        if (this.authorizedRoles != null) {
            for (String role : this.authorizedRoles) {
                if (request.isUserInRole(role)) {
                    return true;
                }
            }
        }
        handleNotAuthorized(request, response, handler);
        return false;
    }

    protected void handleNotAuthorized(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        response.sendError(403);
    }
}
