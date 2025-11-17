package org.apache.catalina.core;

import java.net.URLConnection;
import java.security.SecureRandom;
import java.sql.DriverManager;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/JreMemoryLeakPreventionListener.class */
public class JreMemoryLeakPreventionListener implements LifecycleListener {
    private static final Log log = LogFactory.getLog((Class<?>) JreMemoryLeakPreventionListener.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) JreMemoryLeakPreventionListener.class);
    private boolean appContextProtection = false;
    private boolean urlCacheProtection = true;
    private boolean driverManagerProtection = true;
    private String classesToInitialize = null;
    private boolean initSeedGenerator = false;

    public boolean isAppContextProtection() {
        return this.appContextProtection;
    }

    public void setAppContextProtection(boolean appContextProtection) {
        this.appContextProtection = appContextProtection;
    }

    public boolean isUrlCacheProtection() {
        return this.urlCacheProtection;
    }

    public void setUrlCacheProtection(boolean urlCacheProtection) {
        this.urlCacheProtection = urlCacheProtection;
    }

    public boolean isDriverManagerProtection() {
        return this.driverManagerProtection;
    }

    public void setDriverManagerProtection(boolean driverManagerProtection) {
        this.driverManagerProtection = driverManagerProtection;
    }

    public String getClassesToInitialize() {
        return this.classesToInitialize;
    }

    public void setClassesToInitialize(String classesToInitialize) {
        this.classesToInitialize = classesToInitialize;
    }

    public boolean getInitSeedGenerator() {
        return this.initSeedGenerator;
    }

    public void setInitSeedGenerator(boolean initSeedGenerator) {
        this.initSeedGenerator = initSeedGenerator;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.BEFORE_INIT_EVENT.equals(event.getType())) {
            if (!(event.getLifecycle() instanceof Server)) {
                log.warn(sm.getString("listener.notServer", event.getLifecycle().getClass().getSimpleName()));
            }
            if (this.driverManagerProtection) {
                DriverManager.getDrivers();
            }
            Thread currentThread = Thread.currentThread();
            ClassLoader loader = currentThread.getContextClassLoader();
            try {
                currentThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
                if (this.appContextProtection) {
                    ImageIO.getCacheDirectory();
                }
                if (this.urlCacheProtection) {
                    URLConnection.setDefaultUseCaches("JAR", false);
                }
                if (this.initSeedGenerator) {
                    SecureRandom.getSeed(1);
                }
                if (this.classesToInitialize != null) {
                    StringTokenizer strTok = new StringTokenizer(this.classesToInitialize, ", \r\n\t");
                    while (strTok.hasMoreTokens()) {
                        String classNameToLoad = strTok.nextToken();
                        try {
                            Class.forName(classNameToLoad);
                        } catch (ClassNotFoundException e) {
                            log.error(sm.getString("jreLeakListener.classToInitializeFail", classNameToLoad), e);
                        }
                    }
                }
            } finally {
                currentThread.setContextClassLoader(loader);
            }
        }
    }
}
