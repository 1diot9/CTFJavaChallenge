package org.springframework.web.context.request;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/FacesWebRequest.class */
public class FacesWebRequest extends FacesRequestAttributes implements NativeWebRequest {
    public FacesWebRequest(FacesContext facesContext) {
        super(facesContext);
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public Object getNativeRequest() {
        return getExternalContext().getRequest();
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public Object getNativeResponse() {
        return getExternalContext().getResponse();
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public <T> T getNativeRequest(@Nullable Class<T> cls) {
        if (cls != null) {
            T t = (T) getExternalContext().getRequest();
            if (cls.isInstance(t)) {
                return t;
            }
            return null;
        }
        return null;
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public <T> T getNativeResponse(@Nullable Class<T> cls) {
        if (cls != null) {
            T t = (T) getExternalContext().getResponse();
            if (cls.isInstance(t)) {
                return t;
            }
            return null;
        }
        return null;
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getHeader(String headerName) {
        return (String) getExternalContext().getRequestHeaderMap().get(headerName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String[] getHeaderValues(String headerName) {
        return (String[]) getExternalContext().getRequestHeaderValuesMap().get(headerName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Iterator<String> getHeaderNames() {
        return getExternalContext().getRequestHeaderMap().keySet().iterator();
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getParameter(String paramName) {
        return (String) getExternalContext().getRequestParameterMap().get(paramName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Iterator<String> getParameterNames() {
        return getExternalContext().getRequestParameterNames();
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String[] getParameterValues(String paramName) {
        return (String[]) getExternalContext().getRequestParameterValuesMap().get(paramName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Map<String, String[]> getParameterMap() {
        return getExternalContext().getRequestParameterValuesMap();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Locale getLocale() {
        return getFacesContext().getExternalContext().getRequestLocale();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public String getContextPath() {
        return getFacesContext().getExternalContext().getRequestContextPath();
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getRemoteUser() {
        return getFacesContext().getExternalContext().getRemoteUser();
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public Principal getUserPrincipal() {
        return getFacesContext().getExternalContext().getUserPrincipal();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean isUserInRole(String role) {
        return getFacesContext().getExternalContext().isUserInRole(role);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean isSecure() {
        return false;
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(long lastModifiedTimestamp) {
        return false;
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(@Nullable String eTag) {
        return false;
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(@Nullable String etag, long lastModifiedTimestamp) {
        return false;
    }

    @Override // org.springframework.web.context.request.WebRequest
    public String getDescription(boolean includeClientInfo) {
        ExternalContext externalContext = getExternalContext();
        StringBuilder sb = new StringBuilder();
        sb.append("context=").append(externalContext.getRequestContextPath());
        if (includeClientInfo) {
            Object session = externalContext.getSession(false);
            if (session != null) {
                sb.append(";session=").append(getSessionId());
            }
            String user = externalContext.getRemoteUser();
            if (StringUtils.hasLength(user)) {
                sb.append(";user=").append(user);
            }
        }
        return sb.toString();
    }

    public String toString() {
        return "FacesWebRequest: " + getDescription(true);
    }
}
