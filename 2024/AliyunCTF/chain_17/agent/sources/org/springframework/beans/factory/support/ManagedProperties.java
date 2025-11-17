package org.springframework.beans.factory.support;

import java.util.Properties;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ManagedProperties.class */
public class ManagedProperties extends Properties implements Mergeable, BeanMetadataElement {

    @Nullable
    private Object source;
    private boolean mergeEnabled;

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
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
        if (!(parent instanceof Properties)) {
            throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
        }
        Properties properties = (Properties) parent;
        Properties merged = new ManagedProperties();
        merged.putAll(properties);
        merged.putAll(this);
        return merged;
    }
}
