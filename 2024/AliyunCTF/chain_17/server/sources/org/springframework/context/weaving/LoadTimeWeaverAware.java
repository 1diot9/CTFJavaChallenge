package org.springframework.context.weaving;

import org.springframework.beans.factory.Aware;
import org.springframework.instrument.classloading.LoadTimeWeaver;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/weaving/LoadTimeWeaverAware.class */
public interface LoadTimeWeaverAware extends Aware {
    void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver);
}
