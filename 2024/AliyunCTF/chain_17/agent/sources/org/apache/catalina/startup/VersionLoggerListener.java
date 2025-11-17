package org.apache.catalina.startup;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/VersionLoggerListener.class */
public class VersionLoggerListener implements LifecycleListener {
    private static final Log log = LogFactory.getLog((Class<?>) VersionLoggerListener.class);
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    private boolean logArgs = true;
    private boolean logEnv = false;
    private boolean logProps = false;

    public boolean getLogArgs() {
        return this.logArgs;
    }

    public void setLogArgs(boolean logArgs) {
        this.logArgs = logArgs;
    }

    public boolean getLogEnv() {
        return this.logEnv;
    }

    public void setLogEnv(boolean logEnv) {
        this.logEnv = logEnv;
    }

    public boolean getLogProps() {
        return this.logProps;
    }

    public void setLogProps(boolean logProps) {
        this.logProps = logProps;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.BEFORE_INIT_EVENT.equals(event.getType())) {
            if (!(event.getLifecycle() instanceof Server)) {
                log.warn(sm.getString("listener.notServer", event.getLifecycle().getClass().getSimpleName()));
            }
            log();
        }
    }

    private void log() {
        log.info(sm.getString("versionLoggerListener.serverInfo.server.version", ServerInfo.getServerInfo()));
        log.info(sm.getString("versionLoggerListener.serverInfo.server.built", ServerInfo.getServerBuilt()));
        log.info(sm.getString("versionLoggerListener.serverInfo.server.number", ServerInfo.getServerNumber()));
        log.info(sm.getString("versionLoggerListener.os.name", System.getProperty("os.name")));
        log.info(sm.getString("versionLoggerListener.os.version", System.getProperty("os.version")));
        log.info(sm.getString("versionLoggerListener.os.arch", System.getProperty("os.arch")));
        log.info(sm.getString("versionLoggerListener.java.home", System.getProperty("java.home")));
        log.info(sm.getString("versionLoggerListener.vm.version", System.getProperty("java.runtime.version")));
        log.info(sm.getString("versionLoggerListener.vm.vendor", System.getProperty("java.vm.vendor")));
        log.info(sm.getString("versionLoggerListener.catalina.base", System.getProperty("catalina.base")));
        log.info(sm.getString("versionLoggerListener.catalina.home", System.getProperty("catalina.home")));
        if (this.logArgs) {
            List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
            for (String arg : args) {
                log.info(sm.getString("versionLoggerListener.arg", arg));
            }
        }
        if (this.logEnv) {
            for (Map.Entry<String, String> e : new TreeMap<>(System.getenv()).entrySet()) {
                log.info(sm.getString("versionLoggerListener.env", e.getKey(), e.getValue()));
            }
        }
        if (this.logProps) {
            SortedMap<String, String> sortedMap = new TreeMap<>();
            for (Map.Entry<Object, Object> e2 : System.getProperties().entrySet()) {
                sortedMap.put(String.valueOf(e2.getKey()), String.valueOf(e2.getValue()));
            }
            for (Map.Entry<String, String> e3 : sortedMap.entrySet()) {
                log.info(sm.getString("versionLoggerListener.prop", e3.getKey(), e3.getValue()));
            }
        }
    }
}
