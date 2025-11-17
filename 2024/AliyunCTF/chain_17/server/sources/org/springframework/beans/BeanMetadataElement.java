package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanMetadataElement.class */
public interface BeanMetadataElement {
    @Nullable
    default Object getSource() {
        return null;
    }
}
