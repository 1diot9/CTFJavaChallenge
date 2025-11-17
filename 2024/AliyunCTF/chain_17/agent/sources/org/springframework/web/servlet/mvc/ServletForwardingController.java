package org.springframework.web.servlet.mvc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/ServletForwardingController.class */
public class ServletForwardingController extends AbstractController implements BeanNameAware {

    @Nullable
    private String servletName;

    @Nullable
    private String beanName;

    public ServletForwardingController() {
        super(false);
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
        if (this.servletName == null) {
            this.servletName = name;
        }
    }

    @Override // org.springframework.web.servlet.mvc.AbstractController
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ServletContext servletContext = getServletContext();
        Assert.state(servletContext != null, "No ServletContext");
        RequestDispatcher rd = servletContext.getNamedDispatcher(this.servletName);
        if (rd == null) {
            throw new ServletException("No servlet with name '" + this.servletName + "' defined in web.xml");
        }
        if (useInclude(request, response)) {
            rd.include(request, response);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Included servlet [" + this.servletName + "] in ServletForwardingController '" + this.beanName + "'");
                return null;
            }
            return null;
        }
        rd.forward(request, response);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Forwarded to servlet [" + this.servletName + "] in ServletForwardingController '" + this.beanName + "'");
            return null;
        }
        return null;
    }

    protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
        return WebUtils.isIncludeRequest(request) || response.isCommitted();
    }
}
