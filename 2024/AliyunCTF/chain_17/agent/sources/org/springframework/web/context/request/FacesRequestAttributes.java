package org.springframework.web.context.request;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/FacesRequestAttributes.class */
public class FacesRequestAttributes implements RequestAttributes {
    private static final Log logger = LogFactory.getLog((Class<?>) FacesRequestAttributes.class);
    private final FacesContext facesContext;

    public FacesRequestAttributes(FacesContext facesContext) {
        Assert.notNull(facesContext, "FacesContext must not be null");
        this.facesContext = facesContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final FacesContext getFacesContext() {
        return this.facesContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ExternalContext getExternalContext() {
        return getFacesContext().getExternalContext();
    }

    protected Map<String, Object> getAttributeMap(int scope) {
        if (scope == 0) {
            return getExternalContext().getRequestMap();
        }
        return getExternalContext().getSessionMap();
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object getAttribute(String name, int scope) {
        return getAttributeMap(scope).get(name);
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void setAttribute(String name, Object value, int scope) {
        getAttributeMap(scope).put(name, value);
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void removeAttribute(String name, int scope) {
        getAttributeMap(scope).remove(name);
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public String[] getAttributeNames(int scope) {
        return StringUtils.toStringArray(getAttributeMap(scope).keySet());
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        if (logger.isWarnEnabled()) {
            logger.warn("Could not register destruction callback [" + callback + "] for attribute '" + name + "' because FacesRequestAttributes does not support such callbacks");
        }
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object resolveReference(String key) {
        boolean z = -1;
        switch (key.hashCode()) {
            case -1584598353:
                if (key.equals("viewScope")) {
                    z = 14;
                    break;
                }
                break;
            case -1354757532:
                if (key.equals("cookie")) {
                    z = 7;
                    break;
                }
                break;
            case -1221270899:
                if (key.equals("header")) {
                    z = 8;
                    break;
                }
                break;
            case -1192419559:
                if (key.equals("facesContext")) {
                    z = 6;
                    break;
                }
                break;
            case -697829026:
                if (key.equals("sessionScope")) {
                    z = 4;
                    break;
                }
                break;
            case -341064690:
                if (key.equals(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE)) {
                    z = 16;
                    break;
                }
                break;
            case -272077475:
                if (key.equals("initParam")) {
                    z = 12;
                    break;
                }
                break;
            case -10806556:
                if (key.equals("applicationScope")) {
                    z = 5;
                    break;
                }
                break;
            case 3619493:
                if (key.equals("view")) {
                    z = 13;
                    break;
                }
                break;
            case 97513456:
                if (key.equals("flash")) {
                    z = 15;
                    break;
                }
                break;
            case 106436749:
                if (key.equals("param")) {
                    z = 10;
                    break;
                }
                break;
            case 889144335:
                if (key.equals("paramValues")) {
                    z = 11;
                    break;
                }
                break;
            case 1095692943:
                if (key.equals("request")) {
                    z = false;
                    break;
                }
                break;
            case 1291689221:
                if (key.equals("requestScope")) {
                    z = 3;
                    break;
                }
                break;
            case 1554253136:
                if (key.equals("application")) {
                    z = 2;
                    break;
                }
                break;
            case 1984987798:
                if (key.equals("session")) {
                    z = true;
                    break;
                }
                break;
            case 2073857551:
                if (key.equals("headerValues")) {
                    z = 9;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return getExternalContext().getRequest();
            case true:
                return getExternalContext().getSession(true);
            case true:
                return getExternalContext().getContext();
            case true:
                return getExternalContext().getRequestMap();
            case true:
                return getExternalContext().getSessionMap();
            case true:
                return getExternalContext().getApplicationMap();
            case true:
                return getFacesContext();
            case true:
                return getExternalContext().getRequestCookieMap();
            case true:
                return getExternalContext().getRequestHeaderMap();
            case true:
                return getExternalContext().getRequestHeaderValuesMap();
            case true:
                return getExternalContext().getRequestParameterMap();
            case true:
                return getExternalContext().getRequestParameterValuesMap();
            case true:
                return getExternalContext().getInitParameterMap();
            case true:
                return getFacesContext().getViewRoot();
            case true:
                return getFacesContext().getViewRoot().getViewMap();
            case true:
                return getExternalContext().getFlash();
            case true:
                return getFacesContext().getApplication().getResourceHandler();
            default:
                return null;
        }
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public String getSessionId() {
        Object session = getExternalContext().getSession(true);
        try {
            Method getIdMethod = session.getClass().getMethod("getId", new Class[0]);
            return String.valueOf(ReflectionUtils.invokeMethod(getIdMethod, session));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Session object [" + session + "] does not have a getId() method");
        }
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object getSessionMutex() {
        ExternalContext externalContext = getExternalContext();
        Object session = externalContext.getSession(true);
        Object mutex = externalContext.getSessionMap().get(WebUtils.SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = session != null ? session : externalContext;
        }
        return mutex;
    }
}
