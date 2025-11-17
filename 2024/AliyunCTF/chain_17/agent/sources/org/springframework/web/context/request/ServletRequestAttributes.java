package org.springframework.web.context.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/ServletRequestAttributes.class */
public class ServletRequestAttributes extends AbstractRequestAttributes {
    public static final String DESTRUCTION_CALLBACK_NAME_PREFIX = ServletRequestAttributes.class.getName() + ".DESTRUCTION_CALLBACK.";
    protected static final Set<Class<?>> immutableValueTypes = new HashSet(16);
    private final HttpServletRequest request;

    @Nullable
    private HttpServletResponse response;

    @Nullable
    private volatile HttpSession session;
    private final Map<String, Object> sessionAttributesToUpdate;

    static {
        immutableValueTypes.addAll(NumberUtils.STANDARD_NUMBER_TYPES);
        immutableValueTypes.add(Boolean.class);
        immutableValueTypes.add(Character.class);
        immutableValueTypes.add(String.class);
    }

    public ServletRequestAttributes(HttpServletRequest request) {
        this.sessionAttributesToUpdate = new ConcurrentHashMap(1);
        Assert.notNull(request, "Request must not be null");
        this.request = request;
    }

    public ServletRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response) {
        this(request);
        this.response = response;
    }

    public final HttpServletRequest getRequest() {
        return this.request;
    }

    @Nullable
    public final HttpServletResponse getResponse() {
        return this.response;
    }

    @Nullable
    protected final HttpSession getSession(boolean allowCreate) {
        if (isRequestActive()) {
            HttpSession session = this.request.getSession(allowCreate);
            this.session = session;
            return session;
        }
        HttpSession session2 = this.session;
        if (session2 == null) {
            if (allowCreate) {
                throw new IllegalStateException("No session found and request already completed - cannot create new session!");
            }
            session2 = this.request.getSession(false);
            this.session = session2;
        }
        return session2;
    }

    private HttpSession obtainSession() {
        HttpSession session = getSession(true);
        Assert.state(session != null, "No HttpSession");
        return session;
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object getAttribute(String name, int scope) {
        if (scope == 0) {
            if (!isRequestActive()) {
                throw new IllegalStateException("Cannot ask for request attribute - request is not active anymore!");
            }
            return this.request.getAttribute(name);
        }
        HttpSession session = getSession(false);
        if (session != null) {
            try {
                Object value = session.getAttribute(name);
                if (value != null) {
                    this.sessionAttributesToUpdate.put(name, value);
                }
                return value;
            } catch (IllegalStateException e) {
                return null;
            }
        }
        return null;
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void setAttribute(String name, Object value, int scope) {
        if (scope == 0) {
            if (!isRequestActive()) {
                throw new IllegalStateException("Cannot set request attribute - request is not active anymore!");
            }
            this.request.setAttribute(name, value);
        } else {
            HttpSession session = obtainSession();
            this.sessionAttributesToUpdate.remove(name);
            session.setAttribute(name, value);
        }
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void removeAttribute(String name, int scope) {
        if (scope == 0) {
            if (isRequestActive()) {
                removeRequestDestructionCallback(name);
                this.request.removeAttribute(name);
                return;
            }
            return;
        }
        HttpSession session = getSession(false);
        if (session != null) {
            this.sessionAttributesToUpdate.remove(name);
            try {
                session.removeAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name);
                session.removeAttribute(name);
            } catch (IllegalStateException e) {
            }
        }
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public String[] getAttributeNames(int scope) {
        if (scope == 0) {
            if (!isRequestActive()) {
                throw new IllegalStateException("Cannot ask for request attributes - request is not active anymore!");
            }
            return StringUtils.toStringArray(this.request.getAttributeNames());
        }
        HttpSession session = getSession(false);
        if (session != null) {
            try {
                return StringUtils.toStringArray(session.getAttributeNames());
            } catch (IllegalStateException e) {
            }
        }
        return new String[0];
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        if (scope == 0) {
            registerRequestDestructionCallback(name, callback);
        } else {
            registerSessionDestructionCallback(name, callback);
        }
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object resolveReference(String key) {
        if ("request".equals(key)) {
            return this.request;
        }
        if ("session".equals(key)) {
            return getSession(true);
        }
        return null;
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public String getSessionId() {
        return obtainSession().getId();
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object getSessionMutex() {
        return WebUtils.getSessionMutex(obtainSession());
    }

    @Override // org.springframework.web.context.request.AbstractRequestAttributes
    protected void updateAccessedSessionAttributes() {
        if (!this.sessionAttributesToUpdate.isEmpty()) {
            HttpSession session = getSession(false);
            if (session != null) {
                try {
                    for (Map.Entry<String, Object> entry : this.sessionAttributesToUpdate.entrySet()) {
                        String name = entry.getKey();
                        Object newValue = entry.getValue();
                        Object oldValue = session.getAttribute(name);
                        if (oldValue == newValue && !isImmutableSessionAttribute(name, newValue)) {
                            session.setAttribute(name, newValue);
                        }
                    }
                } catch (IllegalStateException e) {
                }
            }
            this.sessionAttributesToUpdate.clear();
        }
    }

    protected boolean isImmutableSessionAttribute(String name, @Nullable Object value) {
        return value == null || immutableValueTypes.contains(value.getClass());
    }

    protected void registerSessionDestructionCallback(String name, Runnable callback) {
        HttpSession session = obtainSession();
        session.setAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name, new DestructionCallbackBindingListener(callback));
    }

    public String toString() {
        return this.request.toString();
    }
}
