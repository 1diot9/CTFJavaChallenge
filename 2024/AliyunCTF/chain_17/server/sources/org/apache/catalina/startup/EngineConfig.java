package org.apache.catalina.startup;

import org.apache.catalina.Engine;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/EngineConfig.class */
public class EngineConfig implements LifecycleListener {
    protected Engine engine = null;
    private static final Log log = LogFactory.getLog((Class<?>) EngineConfig.class);
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.engine = (Engine) event.getLifecycle();
            if (event.getType().equals(Lifecycle.START_EVENT)) {
                start();
            } else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
                stop();
            }
        } catch (ClassCastException e) {
            log.error(sm.getString("engineConfig.cce", event.getLifecycle()), e);
        }
    }

    protected void start() {
        if (this.engine.getLogger().isDebugEnabled()) {
            this.engine.getLogger().debug(sm.getString("engineConfig.start"));
        }
    }

    protected void stop() {
        if (this.engine.getLogger().isDebugEnabled()) {
            this.engine.getLogger().debug(sm.getString("engineConfig.stop"));
        }
    }
}
