package org.apache.catalina.filters;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/Constants.class */
public final class Constants {
    public static final String CSRF_NONCE_SESSION_ATTR_NAME = "org.apache.catalina.filters.CSRF_NONCE";
    public static final String CSRF_NONCE_REQUEST_ATTR_NAME = "org.apache.catalina.filters.CSRF_REQUEST_NONCE";
    public static final String CSRF_NONCE_REQUEST_PARAM = "org.apache.catalina.filters.CSRF_NONCE";
    public static final String CSRF_NONCE_REQUEST_PARAM_NAME_KEY = "org.apache.catalina.filters.CSRF_NONCE_PARAM_NAME";
    public static final String METHOD_GET = "GET";
    public static final String CSRF_REST_NONCE_HEADER_NAME = "X-CSRF-Token";
    public static final String CSRF_REST_NONCE_HEADER_FETCH_VALUE = "Fetch";
    public static final String CSRF_REST_NONCE_HEADER_REQUIRED_VALUE = "Required";
    public static final String CSRF_REST_NONCE_SESSION_ATTR_NAME = "org.apache.catalina.filters.CSRF_REST_NONCE";
    public static final String CSRF_REST_NONCE_HEADER_NAME_KEY = "org.apache.catalina.filters.CSRF_REST_NONCE_HEADER_NAME";
}
