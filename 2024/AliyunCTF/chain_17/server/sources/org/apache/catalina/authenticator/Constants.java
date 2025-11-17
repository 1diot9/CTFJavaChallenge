package org.apache.catalina.authenticator;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/Constants.class */
public class Constants {
    public static final String SPNEGO_METHOD = "SPNEGO";
    public static final String FORM_ACTION = "/j_security_check";
    public static final String FORM_PASSWORD = "j_password";
    public static final String FORM_USERNAME = "j_username";
    public static final String KRB5_CONF_PROPERTY = "java.security.krb5.conf";
    public static final String DEFAULT_KRB5_CONF = "conf/krb5.ini";
    public static final String JAAS_CONF_PROPERTY = "java.security.auth.login.config";
    public static final String DEFAULT_JAAS_CONF = "conf/jaas.conf";
    public static final String DEFAULT_LOGIN_MODULE_NAME = "com.sun.security.jgss.krb5.accept";
    public static final String SINGLE_SIGN_ON_COOKIE = "JSESSIONIDSSO";
    public static final String COOKIE_PARTITIONED_ATTR = "Partitioned";
    public static final String REQ_SSOID_NOTE = "org.apache.catalina.request.SSOID";
    public static final String REQ_JASPIC_SUBJECT_NOTE = "org.apache.catalina.authenticator.jaspic.SUBJECT";
    public static final String SESSION_ID_NOTE = "org.apache.catalina.authenticator.SESSION_ID";
    public static final String SESS_PASSWORD_NOTE = "org.apache.catalina.session.PASSWORD";
    public static final String SESS_USERNAME_NOTE = "org.apache.catalina.session.USERNAME";
    public static final String FORM_REQUEST_NOTE = "org.apache.catalina.authenticator.REQUEST";
}
