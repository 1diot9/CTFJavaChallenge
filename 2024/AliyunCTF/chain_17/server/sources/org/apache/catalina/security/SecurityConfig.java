package org.apache.catalina.security;

import java.security.Security;
import org.apache.catalina.startup.CatalinaProperties;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/security/SecurityConfig.class */
public final class SecurityConfig {
    private static final Object singletonLock = new Object();
    private static volatile SecurityConfig singleton = null;
    private static final Log log = LogFactory.getLog((Class<?>) SecurityConfig.class);
    private static final String PACKAGE_ACCESS = "sun.,org.apache.catalina.,org.apache.jasper.,org.apache.coyote.,org.apache.tomcat.";
    private static final String PACKAGE_DEFINITION = "java.,sun.,org.apache.catalina.,org.apache.coyote.,org.apache.tomcat.,org.apache.jasper.";
    private final String packageDefinition;
    private final String packageAccess;

    private SecurityConfig() {
        String definition = null;
        String access = null;
        try {
            try {
                definition = CatalinaProperties.getProperty("package.definition");
                access = CatalinaProperties.getProperty("package.access");
                this.packageDefinition = definition;
                this.packageAccess = access;
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to load properties using CatalinaProperties", ex);
                }
                this.packageDefinition = definition;
                this.packageAccess = access;
            }
        } catch (Throwable th) {
            this.packageDefinition = definition;
            this.packageAccess = access;
            throw th;
        }
    }

    public static SecurityConfig newInstance() {
        if (singleton == null) {
            synchronized (singletonLock) {
                if (singleton == null) {
                    singleton = new SecurityConfig();
                }
            }
        }
        return singleton;
    }

    public void setPackageAccess() {
        if (this.packageAccess == null) {
            setSecurityProperty("package.access", PACKAGE_ACCESS);
        } else {
            setSecurityProperty("package.access", this.packageAccess);
        }
    }

    public void setPackageDefinition() {
        if (this.packageDefinition == null) {
            setSecurityProperty("package.definition", PACKAGE_DEFINITION);
        } else {
            setSecurityProperty("package.definition", this.packageDefinition);
        }
    }

    private void setSecurityProperty(String properties, String packageList) {
        if (System.getSecurityManager() != null) {
            String definition = Security.getProperty(properties);
            if (definition != null && definition.length() > 0) {
                if (packageList.length() > 0) {
                    definition = definition + "," + packageList;
                }
            } else {
                definition = packageList;
            }
            Security.setProperty(properties, definition);
        }
    }
}
