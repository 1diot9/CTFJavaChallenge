package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/CatalinaProperties.class */
public class CatalinaProperties {
    private static final Log log = LogFactory.getLog((Class<?>) CatalinaProperties.class);
    private static Properties properties = null;

    static {
        loadProperties();
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }

    private static void loadProperties() {
        InputStream is = null;
        String fileName = "catalina.properties";
        try {
            String configUrl = System.getProperty("catalina.config");
            if (configUrl != null) {
                if (configUrl.indexOf(47) == -1) {
                    fileName = configUrl;
                } else {
                    is = new URI(configUrl).toURL().openStream();
                }
            }
        } catch (Throwable t) {
            handleThrowable(t);
        }
        if (is == null) {
            try {
                File home = new File(Bootstrap.getCatalinaBase());
                File conf = new File(home, "conf");
                File propsFile = new File(conf, fileName);
                is = new FileInputStream(propsFile);
            } catch (Throwable t2) {
                handleThrowable(t2);
            }
        }
        if (is == null) {
            try {
                is = CatalinaProperties.class.getResourceAsStream("/org/apache/catalina/startup/catalina.properties");
            } catch (Throwable t3) {
                handleThrowable(t3);
            }
        }
        if (is != null) {
            try {
                try {
                    properties = new Properties();
                    properties.load(is);
                    try {
                        is.close();
                    } catch (IOException ioe) {
                        log.warn("Could not close catalina properties file", ioe);
                    }
                } catch (Throwable th) {
                    try {
                        is.close();
                    } catch (IOException ioe2) {
                        log.warn("Could not close catalina properties file", ioe2);
                    }
                    throw th;
                }
            } catch (Throwable t4) {
                handleThrowable(t4);
                log.warn(t4);
                try {
                    is.close();
                } catch (IOException ioe3) {
                    log.warn("Could not close catalina properties file", ioe3);
                }
            }
        }
        if (is == null) {
            log.warn("Failed to load catalina properties file");
            properties = new Properties();
        }
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String value = properties.getProperty(name);
            if (value != null) {
                System.setProperty(name, value);
            }
        }
    }

    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        }
        if (t instanceof VirtualMachineError) {
            throw ((VirtualMachineError) t);
        }
    }
}
