package org.springframework.boot.web.servlet.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/Encoding.class */
public class Encoding {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private Charset charset = DEFAULT_CHARSET;
    private Boolean force;
    private Boolean forceRequest;
    private Boolean forceResponse;
    private Map<Locale, Charset> mapping;

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/Encoding$Type.class */
    public enum Type {
        REQUEST,
        RESPONSE
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public boolean isForce() {
        return Boolean.TRUE.equals(this.force);
    }

    public void setForce(boolean force) {
        this.force = Boolean.valueOf(force);
    }

    public boolean isForceRequest() {
        return Boolean.TRUE.equals(this.forceRequest);
    }

    public void setForceRequest(boolean forceRequest) {
        this.forceRequest = Boolean.valueOf(forceRequest);
    }

    public boolean isForceResponse() {
        return Boolean.TRUE.equals(this.forceResponse);
    }

    public void setForceResponse(boolean forceResponse) {
        this.forceResponse = Boolean.valueOf(forceResponse);
    }

    public Map<Locale, Charset> getMapping() {
        return this.mapping;
    }

    public void setMapping(Map<Locale, Charset> mapping) {
        this.mapping = mapping;
    }

    public boolean shouldForce(Type type) {
        Boolean force = type != Type.REQUEST ? this.forceResponse : this.forceRequest;
        if (force == null) {
            force = this.force;
        }
        if (force == null) {
            force = Boolean.valueOf(type == Type.REQUEST);
        }
        return force.booleanValue();
    }
}
