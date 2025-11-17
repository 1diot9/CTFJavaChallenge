package org.springframework.boot.web.server;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.convert.DurationUnit;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/Cookie.class */
public class Cookie {
    private String name;
    private String domain;
    private String path;
    private Boolean httpOnly;
    private Boolean secure;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration maxAge;
    private SameSite sameSite;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getHttpOnly() {
        return this.httpOnly;
    }

    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public Boolean getSecure() {
        return this.secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Duration getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
    }

    public SameSite getSameSite() {
        return this.sameSite;
    }

    public void setSameSite(SameSite sameSite) {
        this.sameSite = sameSite;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/Cookie$SameSite.class */
    public enum SameSite {
        NONE("None"),
        LAX("Lax"),
        STRICT("Strict");

        private final String attributeValue;

        SameSite(String attributeValue) {
            this.attributeValue = attributeValue;
        }

        public String attributeValue() {
            return this.attributeValue;
        }
    }
}
