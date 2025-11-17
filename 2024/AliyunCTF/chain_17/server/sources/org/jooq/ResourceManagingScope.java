package org.jooq;

import java.io.Closeable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLXML;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ResourceManagingScope.class */
public interface ResourceManagingScope extends Scope {
    Array autoFree(Array array);

    Blob autoFree(Blob blob);

    Clob autoFree(Clob clob);

    SQLXML autoFree(SQLXML sqlxml);

    <R extends Closeable> R autoClose(R r);

    <R extends AutoCloseable> R autoClose(R r);
}
