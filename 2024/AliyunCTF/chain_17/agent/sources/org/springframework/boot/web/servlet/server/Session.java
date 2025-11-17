package org.springframework.boot.web.servlet.server;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/Session.class */
public class Session {
    private Set<SessionTrackingMode> trackingModes;
    private boolean persistent;
    private File storeDir;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration timeout = Duration.ofMinutes(30);

    @NestedConfigurationProperty
    private final Cookie cookie = new Cookie();
    private final SessionStoreDirectory sessionStoreDirectory = new SessionStoreDirectory();

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/Session$Cookie.class */
    public static class Cookie extends org.springframework.boot.web.server.Cookie {
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/Session$SessionTrackingMode.class */
    public enum SessionTrackingMode {
        COOKIE,
        URL,
        SSL
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Set<SessionTrackingMode> getTrackingModes() {
        return this.trackingModes;
    }

    public void setTrackingModes(Set<SessionTrackingMode> trackingModes) {
        this.trackingModes = trackingModes;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public File getStoreDir() {
        return this.storeDir;
    }

    public void setStoreDir(File storeDir) {
        this.sessionStoreDirectory.setDirectory(storeDir);
        this.storeDir = storeDir;
    }

    public Cookie getCookie() {
        return this.cookie;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SessionStoreDirectory getSessionStoreDirectory() {
        return this.sessionStoreDirectory;
    }
}
