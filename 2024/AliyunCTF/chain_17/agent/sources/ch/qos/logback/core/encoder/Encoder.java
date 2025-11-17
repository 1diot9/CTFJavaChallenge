package ch.qos.logback.core.encoder;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/encoder/Encoder.class */
public interface Encoder<E> extends ContextAware, LifeCycle {
    byte[] headerBytes();

    byte[] encode(E e);

    byte[] footerBytes();
}
