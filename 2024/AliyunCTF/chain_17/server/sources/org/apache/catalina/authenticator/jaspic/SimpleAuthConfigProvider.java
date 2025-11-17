package org.apache.catalina.authenticator.jaspic;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.ClientAuthConfig;
import jakarta.security.auth.message.config.ServerAuthConfig;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/SimpleAuthConfigProvider.class */
public class SimpleAuthConfigProvider implements AuthConfigProvider {
    private final Map<String, Object> properties;
    private volatile ServerAuthConfig serverAuthConfig;

    public SimpleAuthConfigProvider(Map<String, Object> properties, AuthConfigFactory factory) {
        this.properties = properties;
        if (factory != null) {
            factory.registerConfigProvider(this, null, null, "Automatic registration");
        }
    }

    @Override // jakarta.security.auth.message.config.AuthConfigProvider
    public ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
        return null;
    }

    @Override // jakarta.security.auth.message.config.AuthConfigProvider
    public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
        ServerAuthConfig serverAuthConfig = this.serverAuthConfig;
        if (serverAuthConfig == null) {
            synchronized (this) {
                if (this.serverAuthConfig == null) {
                    this.serverAuthConfig = createServerAuthConfig(layer, appContext, handler, this.properties);
                }
                serverAuthConfig = this.serverAuthConfig;
            }
        }
        return serverAuthConfig;
    }

    protected ServerAuthConfig createServerAuthConfig(String layer, String appContext, CallbackHandler handler, Map<String, Object> properties) {
        return new SimpleServerAuthConfig(layer, appContext, handler, properties);
    }

    @Override // jakarta.security.auth.message.config.AuthConfigProvider
    public void refresh() {
        ServerAuthConfig serverAuthConfig = this.serverAuthConfig;
        if (serverAuthConfig != null) {
            serverAuthConfig.refresh();
        }
    }
}
