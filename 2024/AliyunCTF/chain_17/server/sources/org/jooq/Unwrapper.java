package org.jooq;

import java.sql.Wrapper;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Unwrapper.class */
public interface Unwrapper {
    <T> T unwrap(Wrapper wrapper, Class<T> cls);
}
