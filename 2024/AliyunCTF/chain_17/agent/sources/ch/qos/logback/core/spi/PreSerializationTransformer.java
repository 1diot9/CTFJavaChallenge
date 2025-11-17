package ch.qos.logback.core.spi;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/PreSerializationTransformer.class */
public interface PreSerializationTransformer<E> {
    Serializable transform(E e);
}
