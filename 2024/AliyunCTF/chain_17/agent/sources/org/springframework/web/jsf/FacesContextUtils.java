package org.springframework.web.jsf;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/jsf/FacesContextUtils.class */
public abstract class FacesContextUtils {
    @Nullable
    public static WebApplicationContext getWebApplicationContext(FacesContext fc) {
        Assert.notNull(fc, "FacesContext must not be null");
        Object attr = fc.getExternalContext().getApplicationMap().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException) attr;
            throw runtimeException;
        }
        if (attr instanceof Error) {
            Error error = (Error) attr;
            throw error;
        }
        if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Root context attribute is not of type WebApplicationContext: " + attr);
        }
        WebApplicationContext wac = (WebApplicationContext) attr;
        return wac;
    }

    public static WebApplicationContext getRequiredWebApplicationContext(FacesContext fc) throws IllegalStateException {
        WebApplicationContext wac = getWebApplicationContext(fc);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return wac;
    }

    @Nullable
    public static Object getSessionMutex(FacesContext fc) {
        Assert.notNull(fc, "FacesContext must not be null");
        ExternalContext ec = fc.getExternalContext();
        Object mutex = ec.getSessionMap().get(WebUtils.SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = ec.getSession(true);
        }
        return mutex;
    }
}
