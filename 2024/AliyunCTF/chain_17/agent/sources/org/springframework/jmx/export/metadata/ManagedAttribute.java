package org.springframework.jmx.export.metadata;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/metadata/ManagedAttribute.class */
public class ManagedAttribute extends AbstractJmxAttribute {
    public static final ManagedAttribute EMPTY = new ManagedAttribute();

    @Nullable
    private Object defaultValue;

    @Nullable
    private String persistPolicy;
    private int persistPeriod = -1;

    public void setDefaultValue(@Nullable Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Nullable
    public Object getDefaultValue() {
        return this.defaultValue;
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
}
