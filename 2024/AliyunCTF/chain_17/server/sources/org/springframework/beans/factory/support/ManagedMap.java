package org.springframework.beans.factory.support;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ManagedMap.class */
public class ManagedMap<K, V> extends LinkedHashMap<K, V> implements Mergeable, BeanMetadataElement {

    @Nullable
    private Object source;

    @Nullable
    private String keyTypeName;

    @Nullable
    private String valueTypeName;
    private boolean mergeEnabled;

    public ManagedMap() {
    }

    public ManagedMap(int initialCapacity) {
        super(initialCapacity);
    }

    @SafeVarargs
    public static <K, V> ManagedMap<K, V> ofEntries(Map.Entry<? extends K, ? extends V>... entries) {
        ManagedMap<K, V> map = new ManagedMap<>();
        for (Map.Entry<? extends K, ? extends V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public void setKeyTypeName(@Nullable String keyTypeName) {
        this.keyTypeName = keyTypeName;
    }

    @Nullable
    public String getKeyTypeName() {
        return this.keyTypeName;
    }

    public void setValueTypeName(@Nullable String valueTypeName) {
        this.valueTypeName = valueTypeName;
    }

    @Nullable
    public String getValueTypeName() {
        return this.valueTypeName;
    }

    public void setMergeEnabled(boolean mergeEnabled) {
        this.mergeEnabled = mergeEnabled;
    }

    @Override // org.springframework.beans.Mergeable
    public boolean isMergeEnabled() {
        return this.mergeEnabled;
    }

    @Override // org.springframework.beans.Mergeable
    public Object merge(@Nullable Object parent) {
        if (!this.mergeEnabled) {
            throw new IllegalStateException("Not allowed to merge when the 'mergeEnabled' property is set to 'false'");
        }
        if (parent == null) {
            return this;
        }
        if (!(parent instanceof Map)) {
            throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
        }
        Map<K, V> merged = new ManagedMap<>();
        merged.putAll((Map) parent);
        merged.putAll(this);
        return merged;
    }
}
