package ch.qos.logback.core.sift;

import ch.qos.logback.core.spi.LifeCycle;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/sift/Discriminator.class */
public interface Discriminator<E> extends LifeCycle {
    String getDiscriminatingValue(E e);

    String getKey();
}
