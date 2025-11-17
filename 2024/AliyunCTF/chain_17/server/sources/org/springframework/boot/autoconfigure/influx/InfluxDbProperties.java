package org.springframework.boot.autoconfigure.influx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.influx")
@Deprecated(since = "3.2.0", forRemoval = true)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/influx/InfluxDbProperties.class */
public class InfluxDbProperties {
    private String url;
    private String user;
    private String password;

    @DeprecatedConfigurationProperty(reason = "the new InfluxDb Java client provides Spring Boot integration", since = "3.2.0")
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @DeprecatedConfigurationProperty(reason = "the new InfluxDb Java client provides Spring Boot integration", since = "3.2.0")
    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @DeprecatedConfigurationProperty(reason = "the new InfluxDb Java client provides Spring Boot integration", since = "3.2.0")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
