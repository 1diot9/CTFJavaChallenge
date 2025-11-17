package org.jooq.impl;

import java.io.Closeable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLXML;
import org.jooq.ResourceManagingScope;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ResourceManagingScopeTrait.class */
interface ResourceManagingScopeTrait extends ResourceManagingScope {
    @Override // org.jooq.ResourceManagingScope
    default java.sql.Array autoFree(java.sql.Array array) {
        DefaultExecuteContext.register(array);
        return array;
    }

    @Override // org.jooq.ResourceManagingScope
    default Blob autoFree(Blob blob) {
        DefaultExecuteContext.register(blob);
        return blob;
    }

    @Override // org.jooq.ResourceManagingScope
    default Clob autoFree(Clob clob) {
        DefaultExecuteContext.register(clob);
        return clob;
    }

    @Override // org.jooq.ResourceManagingScope
    default SQLXML autoFree(SQLXML xml) {
        DefaultExecuteContext.register(xml);
        return xml;
    }

    @Override // org.jooq.ResourceManagingScope
    default <R extends Closeable> R autoClose(R closeable) {
        DefaultExecuteContext.register(closeable);
        return closeable;
    }

    @Override // org.jooq.ResourceManagingScope
    default <R extends AutoCloseable> R autoClose(R closeable) {
        DefaultExecuteContext.register(closeable);
        return closeable;
    }
}
