package org.apache.tomcat.util.descriptor.web;

import jakarta.servlet.SessionTrackingMode;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/SessionConfig.class */
public class SessionConfig {
    private Integer sessionTimeout;
    private String cookieName;
    private final Map<String, String> cookieAttributes = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private final EnumSet<SessionTrackingMode> sessionTrackingModes = EnumSet.noneOf(SessionTrackingMode.class);

    public Integer getSessionTimeout() {
        return this.sessionTimeout;
    }

    public void setSessionTimeout(String sessionTimeout) {
        this.sessionTimeout = Integer.valueOf(sessionTimeout);
    }

    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieDomain() {
        return getCookieAttribute(Constants.COOKIE_DOMAIN_ATTR);
    }

    public void setCookieDomain(String cookieDomain) {
        setCookieAttribute(Constants.COOKIE_DOMAIN_ATTR, cookieDomain);
    }

    public String getCookiePath() {
        return getCookieAttribute(Constants.COOKIE_PATH_ATTR);
    }

    public void setCookiePath(String cookiePath) {
        setCookieAttribute(Constants.COOKIE_PATH_ATTR, cookiePath);
    }

    public String getCookieComment() {
        return getCookieAttribute(Constants.COOKIE_COMMENT_ATTR);
    }

    public void setCookieComment(String cookieComment) {
        setCookieAttribute(Constants.COOKIE_COMMENT_ATTR, cookieComment);
    }

    public Boolean getCookieHttpOnly() {
        String httpOnly = getCookieAttribute(Constants.COOKIE_HTTP_ONLY_ATTR);
        if (httpOnly == null) {
            return null;
        }
        return Boolean.valueOf(httpOnly);
    }

    public void setCookieHttpOnly(String cookieHttpOnly) {
        setCookieAttribute(Constants.COOKIE_HTTP_ONLY_ATTR, cookieHttpOnly);
    }

    public Boolean getCookieSecure() {
        String secure = getCookieAttribute(Constants.COOKIE_SECURE_ATTR);
        if (secure == null) {
            return null;
        }
        return Boolean.valueOf(secure);
    }

    public void setCookieSecure(String cookieSecure) {
        setCookieAttribute(Constants.COOKIE_SECURE_ATTR, cookieSecure);
    }

    public Integer getCookieMaxAge() {
        String maxAge = getCookieAttribute(Constants.COOKIE_MAX_AGE_ATTR);
        if (maxAge == null) {
            return null;
        }
        return Integer.valueOf(maxAge);
    }

    public void setCookieMaxAge(String cookieMaxAge) {
        setCookieAttribute(Constants.COOKIE_MAX_AGE_ATTR, cookieMaxAge);
    }

    public Map<String, String> getCookieAttributes() {
        return this.cookieAttributes;
    }

    public void setCookieAttribute(String name, String value) {
        this.cookieAttributes.put(name, value);
    }

    public String getCookieAttribute(String name) {
        return this.cookieAttributes.get(name);
    }

    public EnumSet<SessionTrackingMode> getSessionTrackingModes() {
        return this.sessionTrackingModes;
    }

    public void addSessionTrackingMode(String sessionTrackingMode) {
        this.sessionTrackingModes.add(SessionTrackingMode.valueOf(sessionTrackingMode));
    }
}
