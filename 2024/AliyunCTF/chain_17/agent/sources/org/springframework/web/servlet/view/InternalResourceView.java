package org.springframework.web.servlet.view;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/InternalResourceView.class */
public class InternalResourceView extends AbstractUrlBasedView {
    private boolean alwaysInclude;
    private boolean preventDispatchLoop;

    public InternalResourceView() {
        this.alwaysInclude = false;
        this.preventDispatchLoop = false;
    }

    public InternalResourceView(String url) {
        super(url);
        this.alwaysInclude = false;
        this.preventDispatchLoop = false;
    }

    public InternalResourceView(String url, boolean alwaysInclude) {
        super(url);
        this.alwaysInclude = false;
        this.preventDispatchLoop = false;
        this.alwaysInclude = alwaysInclude;
    }

    public void setAlwaysInclude(boolean alwaysInclude) {
        this.alwaysInclude = alwaysInclude;
    }

    public void setPreventDispatchLoop(boolean preventDispatchLoop) {
        this.preventDispatchLoop = preventDispatchLoop;
    }

    @Override // org.springframework.web.context.support.WebApplicationObjectSupport, org.springframework.context.support.ApplicationObjectSupport
    protected boolean isContextRequired() {
        return false;
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        exposeModelAsRequestAttributes(model, request);
        exposeHelpers(request);
        String dispatcherPath = prepareForRendering(request, response);
        RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
        if (rd == null) {
            throw new ServletException("Could not get RequestDispatcher for [" + getUrl() + "]: Check that the corresponding file exists within your web application archive!");
        }
        if (useInclude(request, response)) {
            response.setContentType(getContentType());
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Including [" + getUrl() + "]");
            }
            rd.include(request, response);
            return;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Forwarding to [" + getUrl() + "]");
        }
        rd.forward(request, response);
    }

    protected void exposeHelpers(HttpServletRequest request) throws Exception {
    }

    protected String prepareForRendering(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = getUrl();
        Assert.state(path != null, "'url' not set");
        if (this.preventDispatchLoop) {
            String uri = request.getRequestURI();
            if (!path.startsWith("/") ? uri.equals(StringUtils.applyRelativePath(uri, path)) : uri.equals(path)) {
                throw new ServletException("Circular view path [" + path + "]: would dispatch back to the current handler URL [" + uri + "] again. Check your ViewResolver setup! (Hint: This may be the result of an unspecified view, due to default view name generation.)");
            }
        }
        return path;
    }

    @Nullable
    protected RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
        return request.getRequestDispatcher(path);
    }

    protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
        return this.alwaysInclude || WebUtils.isIncludeRequest(request) || response.isCommitted();
    }
}
