package org.jooq;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Explain.class */
public interface Explain {
    double rows();

    double cost();

    String plan();
}
