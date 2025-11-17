package org.apache.catalina.core;

import jakarta.servlet.SessionCookieConfig;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.apache.catalina.LifecycleState;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationSessionCookieConfig.class */
public class ApplicationSessionCookieConfig implements SessionCookieConfig {
    private static final StringManager sm = StringManager.getManager((Class<?>) ApplicationSessionCookieConfig.class);
    private static final int DEFAULT_MAX_AGE = -1;
    private static final boolean DEFAULT_HTTP_ONLY = false;
    private static final boolean DEFAULT_SECURE = false;
    private final Map<String, String> attributes = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private String name;
    private StandardContext context;

    public ApplicationSessionCookieConfig(StandardContext context) {
        this.context = context;
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public String getComment() {
        return null;
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public String getDomain() {
        return getAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_DOMAIN_ATTR);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public int getMaxAge() {
        String maxAge = getAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_MAX_AGE_ATTR);
        if (maxAge == null) {
            return -1;
        }
        return Integer.parseInt(maxAge);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public String getName() {
        return this.name;
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public String getPath() {
        return getAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_PATH_ATTR);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public boolean isHttpOnly() {
        String httpOnly = getAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_HTTP_ONLY_ATTR);
        if (httpOnly == null) {
            return false;
        }
        return Boolean.parseBoolean(httpOnly);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public boolean isSecure() {
        String secure = getAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_SECURE_ATTR);
        if (secure == null) {
            return false;
        }
        return Boolean.parseBoolean(secure);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public void setComment(String comment) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationSessionCookieConfig.ise", "comment", this.context.getPath()));
        }
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public void setDomain(String domain) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationSessionCookieConfig.ise", "domain name", this.context.getPath()));
        }
        setAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_DOMAIN_ATTR, domain);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public void setHttpOnly(boolean httpOnly) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationSessionCookieConfig.ise", org.apache.tomcat.util.descriptor.web.Constants.COOKIE_HTTP_ONLY_ATTR, this.context.getPath()));
        }
        setAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_HTTP_ONLY_ATTR, Boolean.toString(httpOnly));
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public void setMaxAge(int maxAge) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationSessionCookieConfig.ise", "max age", this.context.getPath()));
        }
        setAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_MAX_AGE_ATTR, Integer.toString(maxAge));
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public void setName(String name) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationSessionCookieConfig.ise", "name", this.context.getPath()));
        }
        this.name = name;
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public void setPath(String path) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationSessionCookieConfig.ise", "path", this.context.getPath()));
        }
        setAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_PATH_ATTR, path);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public void setSecure(boolean secure) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationSessionCookieConfig.ise", "secure", this.context.getPath()));
        }
        setAttribute(org.apache.tomcat.util.descriptor.web.Constants.COOKIE_SECURE_ATTR, Boolean.toString(secure));
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public void setAttribute(String name, String value) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationSessionCookieConfig.ise", name, this.context.getPath()));
        }
        this.attributes.put(name, value);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override // jakarta.servlet.SessionCookieConfig
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0193 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00a6 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static jakarta.servlet.http.Cookie createSessionCookie(org.apache.catalina.Context r5, java.lang.String r6, boolean r7) {
        /*
            Method dump skipped, instructions count: 434
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.core.ApplicationSessionCookieConfig.createSessionCookie(org.apache.catalina.Context, java.lang.String, boolean):jakarta.servlet.http.Cookie");
    }
}
