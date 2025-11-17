package org.jooq.impl;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;

@org.jooq.Internal
@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractKeys.class */
public abstract class AbstractKeys {
    @Deprecated
    public static Index createIndex(String name, Table<?> table, OrderField<?>[] sortFields, boolean unique) {
        return Internal.createIndex(name, table, sortFields, unique);
    }

    @Deprecated
    public static <R extends Record, T> Identity<R, T> createIdentity(Table<R> table, TableField<R, T> field) {
        return Internal.createIdentity(table, field);
    }

    @SafeVarargs
    @Deprecated
    public static <R extends Record> UniqueKey<R> createUniqueKey(Table<R> table, TableField<R, ?>... fields) {
        return Internal.createUniqueKey(table, fields);
    }

    @SafeVarargs
    @Deprecated
    public static <R extends Record> UniqueKey<R> createUniqueKey(Table<R> table, String name, TableField<R, ?>... fields) {
        return Internal.createUniqueKey(table, name, fields);
    }

    @SafeVarargs
    @Deprecated
    public static <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key, Table<R> table, TableField<R, ?>... fields) {
        return Internal.createForeignKey(key, table, fields);
    }

    @SafeVarargs
    @Deprecated
    public static <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key, Table<R> table, String name, TableField<R, ?>... fields) {
        return Internal.createForeignKey(key, table, name, fields);
    }
}
