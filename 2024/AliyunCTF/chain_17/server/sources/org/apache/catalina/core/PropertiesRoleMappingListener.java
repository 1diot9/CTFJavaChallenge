package org.apache.catalina.core;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/PropertiesRoleMappingListener.class */
public class PropertiesRoleMappingListener implements LifecycleListener {
    private static final String WEBAPP_PROTOCOL = "webapp:";
    private static final Log log = LogFactory.getLog((Class<?>) PropertiesRoleMappingListener.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) ContextNamingInfoListener.class);
    private String roleMappingFile = "webapp:/WEB-INF/role-mapping.properties";
    private String keyPrefix;

    public void setRoleMappingFile(String roleMappingFile) {
        Objects.requireNonNull(roleMappingFile, sm.getString("propertiesRoleMappingListener.roleMappingFileNull"));
        if (roleMappingFile.isEmpty()) {
            throw new IllegalArgumentException(sm.getString("propertiesRoleMappingListener.roleMappingFileEmpty"));
        }
        this.roleMappingFile = roleMappingFile;
    }

    public String getRoleMappingFile() {
        return this.roleMappingFile;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
            if (!(event.getLifecycle() instanceof Context)) {
                log.warn(sm.getString("listener.notContext", event.getLifecycle().getClass().getSimpleName()));
                return;
            }
            Properties props = new Properties();
            Context context = (Context) event.getLifecycle();
            try {
                ConfigurationSource.Resource resource = context.findConfigFileResource(this.roleMappingFile);
                try {
                    props.load(resource.getInputStream());
                    if (resource != null) {
                        resource.close();
                    }
                    int linkCount = 0;
                    for (Map.Entry<Object, Object> prop : props.entrySet()) {
                        String role = (String) prop.getKey();
                        if (this.keyPrefix != null) {
                            if (role.startsWith(this.keyPrefix)) {
                                role = role.substring(this.keyPrefix.length());
                            }
                        }
                        String link = (String) prop.getValue();
                        if (log.isTraceEnabled()) {
                            log.trace(sm.getString("propertiesRoleMappingListener.linkedRole", role, link));
                        }
                        context.addRoleMapping(role, link);
                        linkCount++;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("propertiesRoleMappingListener.linkedRoleCount", Integer.valueOf(linkCount)));
                    }
                } finally {
                }
            } catch (IOException e) {
                throw new IllegalStateException(sm.getString("propertiesRoleMappingListener.roleMappingFileFail", this.roleMappingFile), e);
            }
        }
    }
}
