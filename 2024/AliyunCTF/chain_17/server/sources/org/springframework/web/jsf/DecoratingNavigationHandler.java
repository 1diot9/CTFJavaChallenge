package org.springframework.web.jsf;

import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/jsf/DecoratingNavigationHandler.class */
public abstract class DecoratingNavigationHandler extends NavigationHandler {

    @Nullable
    private NavigationHandler decoratedNavigationHandler;

    public abstract void handleNavigation(FacesContext facesContext, @Nullable String fromAction, @Nullable String outcome, @Nullable NavigationHandler originalNavigationHandler);

    protected DecoratingNavigationHandler() {
    }

    protected DecoratingNavigationHandler(NavigationHandler originalNavigationHandler) {
        this.decoratedNavigationHandler = originalNavigationHandler;
    }

    @Nullable
    public final NavigationHandler getDecoratedNavigationHandler() {
        return this.decoratedNavigationHandler;
    }

    public final void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
        handleNavigation(facesContext, fromAction, outcome, this.decoratedNavigationHandler);
    }

    protected final void callNextHandlerInChain(FacesContext facesContext, @Nullable String fromAction, @Nullable String outcome, @Nullable NavigationHandler originalNavigationHandler) {
        NavigationHandler decoratedNavigationHandler = getDecoratedNavigationHandler();
        if (decoratedNavigationHandler instanceof DecoratingNavigationHandler) {
            DecoratingNavigationHandler decHandler = (DecoratingNavigationHandler) decoratedNavigationHandler;
            decHandler.handleNavigation(facesContext, fromAction, outcome, originalNavigationHandler);
        } else if (decoratedNavigationHandler != null) {
            decoratedNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
        } else if (originalNavigationHandler != null) {
            originalNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
        }
    }
}
