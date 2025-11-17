package org.springframework.boot.autoconfigure.sendgrid;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.sendgrid")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sendgrid/SendGridProperties.class */
public class SendGridProperties {
    private String apiKey;
    private Proxy proxy;

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public boolean isProxyConfigured() {
        return (this.proxy == null || this.proxy.getHost() == null || this.proxy.getPort() == null) ? false : true;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sendgrid/SendGridProperties$Proxy.class */
    public static class Proxy {
        private String host;
        private Integer port;

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return this.port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }
}
