package org.springframework.jmx.export.metadata;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/metadata/ManagedResource.class */
public class ManagedResource extends AbstractJmxAttribute {

    @Nullable
    private String objectName;

    @Nullable
    private String logFile;

    @Nullable
    private String persistPolicy;

    @Nullable
    private String persistName;

    @Nullable
    private String persistLocation;
    private boolean log = false;
    private int persistPeriod = -1;

    public void setObjectName(@Nullable String objectName) {
        this.objectName = objectName;
    }

    @Nullable
    public String getObjectName() {
        return this.objectName;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isLog() {
        return this.log;
    }

    public void setLogFile(@Nullable String logFile) {
        this.logFile = logFile;
    }

    @Nullable
    public String getLogFile() {
        return this.logFile;
    }

    public void setPersistPolicy(@Nullable String persistPolicy) {
        this.persistPolicy = persistPolicy;
    }

    @Nullable
    public String getPersistPolicy() {
        return this.persistPolicy;
    }

    public void setPersistPeriod(int persistPeriod) {
        this.persistPeriod = persistPeriod;
    }

    public int getPersistPeriod() {
        return this.persistPeriod;
    }

    public void setPersistName(@Nullable String persistName) {
        this.persistName = persistName;
    }

    @Nullable
    public String getPersistName() {
        return this.persistName;
    }

    public void setPersistLocation(@Nullable String persistLocation) {
        this.persistLocation = persistLocation;
    }

    @Nullable
    public String getPersistLocation() {
        return this.persistLocation;
    }
}
