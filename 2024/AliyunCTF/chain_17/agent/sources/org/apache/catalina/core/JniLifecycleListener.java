package org.apache.catalina.core;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/JniLifecycleListener.class */
public class JniLifecycleListener implements LifecycleListener {
    private static final Log log = LogFactory.getLog((Class<?>) JniLifecycleListener.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) JniLifecycleListener.class);
    private String libraryName = "";
    private String libraryPath = "";

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.BEFORE_START_EVENT.equals(event.getType())) {
            if (!this.libraryName.isEmpty()) {
                System.loadLibrary(this.libraryName);
                log.info(sm.getString("jniLifecycleListener.load.name", this.libraryName));
            } else {
                if (!this.libraryPath.isEmpty()) {
                    System.load(this.libraryPath);
                    log.info(sm.getString("jniLifecycleListener.load.path", this.libraryPath));
                    return;
                }
                throw new IllegalArgumentException(sm.getString("jniLifecycleListener.missingPathOrName"));
            }
        }
    }

    public void setLibraryName(String libraryName) {
        if (!this.libraryPath.isEmpty()) {
            throw new IllegalArgumentException(sm.getString("jniLifecycleListener.bothPathAndName"));
        }
        this.libraryName = libraryName;
    }

    public String getLibraryName() {
        return this.libraryName;
    }

    public void setLibraryPath(String libraryPath) {
        if (!this.libraryName.isEmpty()) {
            throw new IllegalArgumentException(sm.getString("jniLifecycleListener.bothPathAndName"));
        }
        this.libraryPath = libraryPath;
    }

    public String getLibraryPath() {
        return this.libraryPath;
    }
}
