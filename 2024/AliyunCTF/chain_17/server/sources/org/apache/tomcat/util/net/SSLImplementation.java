package org.apache.tomcat.util.net;

import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.jsse.JSSEImplementation;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SSLImplementation.class */
public abstract class SSLImplementation {
    private static final Log logger = LogFactory.getLog((Class<?>) SSLImplementation.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) SSLImplementation.class);

    public abstract SSLSupport getSSLSupport(SSLSession sSLSession, Map<String, List<String>> map);

    public abstract SSLUtil getSSLUtil(SSLHostConfigCertificate sSLHostConfigCertificate);

    public static SSLImplementation getInstance(String className) throws ClassNotFoundException {
        if (className == null) {
            return new JSSEImplementation();
        }
        try {
            Class<?> clazz = Class.forName(className);
            return (SSLImplementation) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            String msg = sm.getString("sslImplementation.cnfe", className);
            if (logger.isDebugEnabled()) {
                logger.debug(msg, e);
            }
            throw new ClassNotFoundException(msg, e);
        }
    }
}
