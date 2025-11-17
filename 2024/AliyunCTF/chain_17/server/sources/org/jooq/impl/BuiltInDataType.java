package org.jooq.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jooq.DataType;
import org.jooq.SQLDialect;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BuiltInDataType.class */
public class BuiltInDataType<T> extends DefaultDataType<T> {
    public BuiltInDataType(Class<T> type, String typeName) {
        super((SQLDialect) null, type, typeName);
    }

    public BuiltInDataType(SQLDialect dialect, DataType<T> sqlDataType, String typeName) {
        super(dialect, sqlDataType, typeName);
    }

    public BuiltInDataType(SQLDialect dialect, DataType<T> sqlDataType, String typeName, String castTypeName) {
        super(dialect, sqlDataType, typeName, castTypeName);
    }
}
