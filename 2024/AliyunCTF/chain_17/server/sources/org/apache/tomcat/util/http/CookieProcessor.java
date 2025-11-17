package org.apache.tomcat.util.http;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/CookieProcessor.class */
public interface CookieProcessor {
    void parseCookieHeader(MimeHeaders mimeHeaders, ServerCookies serverCookies);

    String generateHeader(Cookie cookie, HttpServletRequest httpServletRequest);

    Charset getCharset();
}
