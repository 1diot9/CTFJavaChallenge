package org.springframework.web.context.support;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/HttpRequestHandlerServlet.class */
public class HttpRequestHandlerServlet extends HttpServlet {

    @Nullable
    private HttpRequestHandler target;

    @Override // jakarta.servlet.GenericServlet
    public void init() throws ServletException {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.target = (HttpRequestHandler) wac.getBean(getServletName(), HttpRequestHandler.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // jakarta.servlet.http.HttpServlet
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Assert.state(this.target != null, "No HttpRequestHandler available");
        LocaleContextHolder.setLocale(request.getLocale());
        try {
            this.target.handleRequest(request, response);
        } catch (HttpRequestMethodNotSupportedException ex) {
            String[] supportedMethods = ex.getSupportedMethods();
            if (supportedMethods != null) {
                response.setHeader(HttpHeaders.ALLOW, StringUtils.arrayToDelimitedString(supportedMethods, ", "));
            }
            response.sendError(405, ex.getMessage());
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}
