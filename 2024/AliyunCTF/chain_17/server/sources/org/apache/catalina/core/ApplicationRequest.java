package org.apache.catalina.core;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationRequest.class */
class ApplicationRequest extends ServletRequestWrapper {

    @Deprecated
    protected static final String[] specials = {"jakarta.servlet.include.request_uri", "jakarta.servlet.include.context_path", "jakarta.servlet.include.servlet_path", "jakarta.servlet.include.path_info", "jakarta.servlet.include.query_string", RequestDispatcher.INCLUDE_MAPPING, "jakarta.servlet.forward.request_uri", "jakarta.servlet.forward.context_path", "jakarta.servlet.forward.servlet_path", "jakarta.servlet.forward.path_info", "jakarta.servlet.forward.query_string", RequestDispatcher.FORWARD_MAPPING};
    private static final Set<String> specialsSet = new HashSet(Arrays.asList(specials));
    protected final HashMap<String, Object> attributes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationRequest(ServletRequest request) {
        super(request);
        this.attributes = new HashMap<>();
        setRequest(request);
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Object getAttribute(String name) {
        Object obj;
        synchronized (this.attributes) {
            obj = this.attributes.get(name);
        }
        return obj;
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Enumeration<String> getAttributeNames() {
        Enumeration<String> enumeration;
        synchronized (this.attributes) {
            enumeration = Collections.enumeration(this.attributes.keySet());
        }
        return enumeration;
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public void removeAttribute(String name) {
        synchronized (this.attributes) {
            this.attributes.remove(name);
            if (!isSpecial(name)) {
                getRequest().removeAttribute(name);
            }
        }
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public void setAttribute(String name, Object value) {
        synchronized (this.attributes) {
            this.attributes.put(name, value);
            if (!isSpecial(name)) {
                getRequest().setAttribute(name, value);
            }
        }
    }

    @Override // jakarta.servlet.ServletRequestWrapper
    public void setRequest(ServletRequest request) {
        super.setRequest(request);
        synchronized (this.attributes) {
            this.attributes.clear();
            Enumeration<String> names = request.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                Object value = request.getAttribute(name);
                this.attributes.put(name, value);
            }
        }
    }

    @Deprecated
    protected boolean isSpecial(String name) {
        return specialsSet.contains(name);
    }
}
