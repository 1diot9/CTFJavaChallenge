package org.springframework.boot.autoconfigure.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WelcomePageHandlerMapping.class */
final class WelcomePageHandlerMapping extends AbstractUrlHandlerMapping {
    private static final Log logger = LogFactory.getLog((Class<?>) WelcomePageHandlerMapping.class);
    private static final List<MediaType> MEDIA_TYPES_ALL = Collections.singletonList(MediaType.ALL);

    /* JADX INFO: Access modifiers changed from: package-private */
    public WelcomePageHandlerMapping(TemplateAvailabilityProviders templateAvailabilityProviders, ApplicationContext applicationContext, Resource indexHtmlResource, String staticPathPattern) {
        setOrder(2);
        WelcomePage welcomePage = WelcomePage.resolve(templateAvailabilityProviders, applicationContext, indexHtmlResource, staticPathPattern);
        if (welcomePage != WelcomePage.UNRESOLVED) {
            logger.info(LogMessage.of(() -> {
                return !welcomePage.isTemplated() ? "Adding welcome page: " + indexHtmlResource : "Adding welcome page template: index";
            }));
            ParameterizableViewController controller = new ParameterizableViewController();
            controller.setViewName(welcomePage.getViewName());
            setRootHandler(controller);
        }
    }

    @Override // org.springframework.web.servlet.handler.AbstractUrlHandlerMapping, org.springframework.web.servlet.handler.AbstractHandlerMapping
    public Object getHandlerInternal(HttpServletRequest request) throws Exception {
        if (isHtmlTextAccepted(request)) {
            return super.getHandlerInternal(request);
        }
        return null;
    }

    private boolean isHtmlTextAccepted(HttpServletRequest request) {
        for (MediaType mediaType : getAcceptedMediaTypes(request)) {
            if (mediaType.includes(MediaType.TEXT_HTML)) {
                return true;
            }
        }
        return false;
    }

    private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        if (StringUtils.hasText(acceptHeader)) {
            try {
                return MediaType.parseMediaTypes(acceptHeader);
            } catch (InvalidMediaTypeException ex) {
                logger.warn("Received invalid Accept header. Assuming all media types are accepted", logger.isDebugEnabled() ? ex : null);
            }
        }
        return MEDIA_TYPES_ALL;
    }
}
