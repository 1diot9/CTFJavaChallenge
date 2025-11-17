package org.springframework.boot.autoconfigure.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WelcomePageNotAcceptableHandlerMapping.class */
class WelcomePageNotAcceptableHandlerMapping extends AbstractUrlHandlerMapping {
    /* JADX INFO: Access modifiers changed from: package-private */
    public WelcomePageNotAcceptableHandlerMapping(TemplateAvailabilityProviders templateAvailabilityProviders, ApplicationContext applicationContext, Resource indexHtmlResource, String staticPathPattern) {
        setOrder(2147483637);
        WelcomePage welcomePage = WelcomePage.resolve(templateAvailabilityProviders, applicationContext, indexHtmlResource, staticPathPattern);
        if (welcomePage != WelcomePage.UNRESOLVED) {
            setRootHandler(this::handleRequest);
        }
    }

    private ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractUrlHandlerMapping, org.springframework.web.servlet.handler.AbstractHandlerMapping
    public Object getHandlerInternal(HttpServletRequest request) throws Exception {
        return super.getHandlerInternal(request);
    }
}
