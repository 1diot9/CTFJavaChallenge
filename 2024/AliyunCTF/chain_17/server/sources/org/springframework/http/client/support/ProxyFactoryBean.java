package org.springframework.http.client.support;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/support/ProxyFactoryBean.class */
public class ProxyFactoryBean implements FactoryBean<Proxy>, InitializingBean {

    @Nullable
    private String hostname;

    @Nullable
    private Proxy proxy;
    private Proxy.Type type = Proxy.Type.HTTP;
    private int port = -1;

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws IllegalArgumentException {
        Assert.notNull(this.type, "Property 'type' is required");
        Assert.notNull(this.hostname, "Property 'hostname' is required");
        if (this.port < 0 || this.port > 65535) {
            throw new IllegalArgumentException("Property 'port' value out of range: " + this.port);
        }
        SocketAddress socketAddress = new InetSocketAddress(this.hostname, this.port);
        this.proxy = new Proxy(this.type, socketAddress);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Proxy getObject() {
        return this.proxy;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return Proxy.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
