package org.springframework.web.servlet.view;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.servlet.support.RequestContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/AbstractTemplateView.class */
public abstract class AbstractTemplateView extends AbstractUrlBasedView {
    public static final String SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE = "springMacroRequestContext";
    private boolean exposeRequestAttributes = false;
    private boolean allowRequestOverride = false;
    private boolean exposeSessionAttributes = false;
    private boolean allowSessionOverride = false;
    private boolean exposeSpringMacroHelpers = true;

    protected abstract void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

    public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
        this.exposeRequestAttributes = exposeRequestAttributes;
    }

    public void setAllowRequestOverride(boolean allowRequestOverride) {
        this.allowRequestOverride = allowRequestOverride;
    }

    public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
        this.exposeSessionAttributes = exposeSessionAttributes;
    }

    public void setAllowSessionOverride(boolean allowSessionOverride) {
        this.allowSessionOverride = allowSessionOverride;
    }

    public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
        this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session;
        if (this.exposeRequestAttributes) {
            Map<String, Object> exposed = null;
            Enumeration<String> en = request.getAttributeNames();
            while (en.hasMoreElements()) {
                String attribute = en.nextElement();
                if (model.containsKey(attribute) && !this.allowRequestOverride) {
                    throw new ServletException("Cannot expose request attribute '" + attribute + "' because of an existing model object of the same name");
                }
                Object attributeValue = request.getAttribute(attribute);
                if (this.logger.isDebugEnabled()) {
                    exposed = exposed != null ? exposed : new LinkedHashMap<>();
                    exposed.put(attribute, attributeValue);
                }
                model.put(attribute, attributeValue);
            }
            if (this.logger.isTraceEnabled() && exposed != null) {
                this.logger.trace("Exposed request attributes to model: " + exposed);
            }
        }
        if (this.exposeSessionAttributes && (session = request.getSession(false)) != null) {
            Map<String, Object> exposed2 = null;
            Enumeration<String> en2 = session.getAttributeNames();
            while (en2.hasMoreElements()) {
                String attribute2 = en2.nextElement();
                if (model.containsKey(attribute2) && !this.allowSessionOverride) {
                    throw new ServletException("Cannot expose session attribute '" + attribute2 + "' because of an existing model object of the same name");
                }
                Object attributeValue2 = session.getAttribute(attribute2);
                if (this.logger.isDebugEnabled()) {
                    exposed2 = exposed2 != null ? exposed2 : new LinkedHashMap<>();
                    exposed2.put(attribute2, attributeValue2);
                }
                model.put(attribute2, attributeValue2);
            }
            if (this.logger.isTraceEnabled() && exposed2 != null) {
                this.logger.trace("Exposed session attributes to model: " + exposed2);
            }
        }
        if (this.exposeSpringMacroHelpers) {
            if (model.containsKey(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE)) {
                throw new ServletException("Cannot expose bind macro helper 'springMacroRequestContext' because of an existing model object of the same name");
            }
            model.put(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, new RequestContext(request, response, getServletContext(), model));
        }
        applyContentType(response);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Rendering [" + getUrl() + "]");
        }
        renderMergedTemplateModel(model, request, response);
    }

    protected void applyContentType(HttpServletResponse response) {
        if (response.getContentType() == null) {
            response.setContentType(getContentType());
        }
    }
}
