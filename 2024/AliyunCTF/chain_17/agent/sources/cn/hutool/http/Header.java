package cn.hutool.http;

import org.springframework.http.HttpHeaders;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/Header.class */
public enum Header {
    AUTHORIZATION("Authorization"),
    PROXY_AUTHORIZATION("Proxy-Authorization"),
    DATE(HttpHeaders.DATE),
    CONNECTION("Connection"),
    MIME_VERSION("MIME-Version"),
    TRAILER(HttpHeaders.TRAILER),
    TRANSFER_ENCODING("Transfer-Encoding"),
    UPGRADE("Upgrade"),
    VIA(HttpHeaders.VIA),
    CACHE_CONTROL(HttpHeaders.CACHE_CONTROL),
    PRAGMA(HttpHeaders.PRAGMA),
    CONTENT_TYPE(HttpHeaders.CONTENT_TYPE),
    HOST("Host"),
    REFERER(HttpHeaders.REFERER),
    ORIGIN("Origin"),
    USER_AGENT(HttpHeaders.USER_AGENT),
    ACCEPT(HttpHeaders.ACCEPT),
    ACCEPT_LANGUAGE(HttpHeaders.ACCEPT_LANGUAGE),
    ACCEPT_ENCODING(HttpHeaders.ACCEPT_ENCODING),
    ACCEPT_CHARSET(HttpHeaders.ACCEPT_CHARSET),
    COOKIE(HttpHeaders.COOKIE),
    CONTENT_LENGTH(HttpHeaders.CONTENT_LENGTH),
    WWW_AUTHENTICATE("WWW-Authenticate"),
    SET_COOKIE(HttpHeaders.SET_COOKIE),
    CONTENT_ENCODING(HttpHeaders.CONTENT_ENCODING),
    CONTENT_DISPOSITION(HttpHeaders.CONTENT_DISPOSITION),
    ETAG(HttpHeaders.ETAG),
    LOCATION("Location");

    private final String value;

    Header(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override // java.lang.Enum
    public String toString() {
        return getValue();
    }
}
