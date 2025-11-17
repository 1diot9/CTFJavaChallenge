package org.springframework.web.context.request;

import jakarta.faces.context.FacesContext;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/RequestContextHolder.class */
public abstract class RequestContextHolder {
    private static final boolean jsfPresent = ClassUtils.isPresent("jakarta.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());
    private static final ThreadLocal<RequestAttributes> requestAttributesHolder = new NamedThreadLocal("Request attributes");
    private static final ThreadLocal<RequestAttributes> inheritableRequestAttributesHolder = new NamedInheritableThreadLocal("Request context");

    public static void resetRequestAttributes() {
        requestAttributesHolder.remove();
        inheritableRequestAttributesHolder.remove();
    }

    public static void setRequestAttributes(@Nullable RequestAttributes attributes) {
        setRequestAttributes(attributes, false);
    }

    public static void setRequestAttributes(@Nullable RequestAttributes attributes, boolean inheritable) {
        if (attributes == null) {
            resetRequestAttributes();
        } else if (inheritable) {
            inheritableRequestAttributesHolder.set(attributes);
            requestAttributesHolder.remove();
        } else {
            requestAttributesHolder.set(attributes);
            inheritableRequestAttributesHolder.remove();
        }
    }

    @Nullable
    public static RequestAttributes getRequestAttributes() {
        RequestAttributes attributes = requestAttributesHolder.get();
        if (attributes == null) {
            attributes = inheritableRequestAttributesHolder.get();
        }
        return attributes;
    }

    public static RequestAttributes currentRequestAttributes() throws IllegalStateException {
        RequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            if (jsfPresent) {
                attributes = FacesRequestAttributesFactory.getFacesRequestAttributes();
            }
            if (attributes == null) {
                throw new IllegalStateException("No thread-bound request found: Are you referring to request attributes outside of an actual web request, or processing a request outside of the originally receiving thread? If you are actually operating within a web request and still receive this message, your code is probably running outside of DispatcherServlet: In this case, use RequestContextListener or RequestContextFilter to expose the current request.");
            }
        }
        return attributes;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/RequestContextHolder$FacesRequestAttributesFactory.class */
    private static class FacesRequestAttributesFactory {
        private FacesRequestAttributesFactory() {
        }

        @Nullable
        public static RequestAttributes getFacesRequestAttributes() {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                if (facesContext != null) {
                    return new FacesRequestAttributes(facesContext);
                }
                return null;
            } catch (NoClassDefFoundError e) {
                return null;
            }
        }
    }
}
