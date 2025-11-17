package org.jooq;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Parameter.class */
public interface Parameter<T> extends Named, Typed<T> {
    boolean isDefaulted();

    boolean isUnnamed();

    ParamMode getParamMode();
}
