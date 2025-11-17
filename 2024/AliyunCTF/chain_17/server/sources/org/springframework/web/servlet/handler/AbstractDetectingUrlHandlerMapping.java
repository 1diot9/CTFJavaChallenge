package org.springframework.web.servlet.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractDetectingUrlHandlerMapping.class */
public abstract class AbstractDetectingUrlHandlerMapping extends AbstractUrlHandlerMapping {
    private boolean detectHandlersInAncestorContexts = false;

    protected abstract String[] determineUrlsForHandler(String beanName);

    public void setDetectHandlersInAncestorContexts(boolean detectHandlersInAncestorContexts) {
        this.detectHandlersInAncestorContexts = detectHandlersInAncestorContexts;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping, org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext() throws ApplicationContextException {
        super.initApplicationContext();
        detectHandlers();
    }

    protected void detectHandlers() throws BeansException {
        String[] beanNamesForType;
        ApplicationContext applicationContext = obtainApplicationContext();
        if (this.detectHandlersInAncestorContexts) {
            beanNamesForType = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, (Class<?>) Object.class);
        } else {
            beanNamesForType = applicationContext.getBeanNamesForType(Object.class);
        }
        String[] beanNames = beanNamesForType;
        for (String beanName : beanNames) {
            String[] urls = determineUrlsForHandler(beanName);
            if (!ObjectUtils.isEmpty((Object[]) urls)) {
                registerHandler(urls, beanName);
            }
        }
        if (this.mappingsLogger.isDebugEnabled()) {
            this.mappingsLogger.debug(formatMappingName() + " " + getHandlerMap());
        } else if ((this.logger.isDebugEnabled() && !getHandlerMap().isEmpty()) || this.logger.isTraceEnabled()) {
            this.logger.debug("Detected " + getHandlerMap().size() + " mappings in " + formatMappingName());
        }
    }
}
