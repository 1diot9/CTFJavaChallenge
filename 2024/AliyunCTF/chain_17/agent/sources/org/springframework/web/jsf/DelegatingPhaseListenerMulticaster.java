package org.springframework.web.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import java.util.Collection;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/jsf/DelegatingPhaseListenerMulticaster.class */
public class DelegatingPhaseListenerMulticaster implements PhaseListener {
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    public void beforePhase(PhaseEvent event) {
        for (PhaseListener listener : getDelegates(event.getFacesContext())) {
            listener.beforePhase(event);
        }
    }

    public void afterPhase(PhaseEvent event) {
        for (PhaseListener listener : getDelegates(event.getFacesContext())) {
            listener.afterPhase(event);
        }
    }

    protected Collection<PhaseListener> getDelegates(FacesContext facesContext) {
        ListableBeanFactory bf = getBeanFactory(facesContext);
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(bf, PhaseListener.class, true, false).values();
    }

    protected ListableBeanFactory getBeanFactory(FacesContext facesContext) {
        return getWebApplicationContext(facesContext);
    }

    protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}
