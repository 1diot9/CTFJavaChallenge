package org.springframework.beans.factory.support;

import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/NullBean.class */
public final class NullBean {
    public boolean equals(@Nullable Object other) {
        return this == other || other == null;
    }

    public int hashCode() {
        return NullBean.class.hashCode();
    }

    public String toString() {
        return "null";
    }
}
