package org.apache.catalina.core;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ContextNamingInfoListener.class */
public class ContextNamingInfoListener implements LifecycleListener {
    private static final String PATH_ENTRY_NAME = "context/path";
    private static final String ENCODED_PATH_ENTRY_NAME = "context/encodedPath";
    private static final String WEBAPP_VERSION_ENTRY_NAME = "context/webappVersion";
    private static final String NAME_ENTRY_NAME = "context/name";
    private static final String BASE_NAME_ENTRY_NAME = "context/baseName";
    private static final String DISPLAY_NAME_ENTRY_NAME = "context/displayName";
    private static final Log log = LogFactory.getLog((Class<?>) ContextNamingInfoListener.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) ContextNamingInfoListener.class);
    private boolean emptyOnRoot = true;

    public void setEmptyOnRoot(boolean emptyOnRoot) {
        this.emptyOnRoot = emptyOnRoot;
    }

    public boolean isEmptyOnRoot() {
        return this.emptyOnRoot;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
            if (!(event.getLifecycle() instanceof Context)) {
                log.warn(sm.getString("listener.notContext", event.getLifecycle().getClass().getSimpleName()));
                return;
            }
            Context context = (Context) event.getLifecycle();
            String path = context.getPath();
            String encodedPath = context.getEncodedPath();
            String name = context.getName();
            if (!this.emptyOnRoot && path.isEmpty()) {
                encodedPath = "/";
                path = "/";
                name = "ROOT" + name;
            }
            addEnvEntry(context, PATH_ENTRY_NAME, path);
            addEnvEntry(context, ENCODED_PATH_ENTRY_NAME, encodedPath);
            addEnvEntry(context, WEBAPP_VERSION_ENTRY_NAME, context.getWebappVersion());
            addEnvEntry(context, NAME_ENTRY_NAME, name);
            addEnvEntry(context, BASE_NAME_ENTRY_NAME, context.getBaseName());
            addEnvEntry(context, DISPLAY_NAME_ENTRY_NAME, context.getDisplayName());
        }
    }

    private void addEnvEntry(Context context, String name, String value) {
        ContextEnvironment ce = new ContextEnvironment();
        ce.setName(name);
        ce.setOverride(true);
        ce.setType("java.lang.String");
        ce.setValue(value);
        if (log.isDebugEnabled()) {
            log.info(sm.getString("contextNamingInfoListener.envEntry", name, value));
        }
        context.getNamingResources().addEnvironment(ce);
    }
}
