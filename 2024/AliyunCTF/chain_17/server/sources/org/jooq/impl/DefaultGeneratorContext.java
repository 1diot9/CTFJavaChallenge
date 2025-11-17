package org.jooq.impl;

import java.time.Instant;
import java.util.Map;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.GeneratorContext;
import org.jooq.GeneratorStatementType;
import org.jooq.Record;
import org.jooq.Table;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultGeneratorContext.class */
final class DefaultGeneratorContext<R extends Record, X extends Table<R>, T> extends AbstractScope implements GeneratorContext<R, X, T> {
    final Instant renderTime;
    final X table;
    final Field<T> field;
    final GeneratorStatementType statementType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultGeneratorContext(Configuration configuration) {
        this(configuration, null, null, null, null, null);
    }

    DefaultGeneratorContext(Configuration configuration, Map<Object, Object> data, Instant renderTime, X table, Field<T> field, GeneratorStatementType statementType) {
        super(configuration, data);
        this.renderTime = renderTime != null ? renderTime : creationTime();
        this.table = table;
        this.field = field;
        this.statementType = statementType;
    }

    @Override // org.jooq.GeneratorContext
    public final Instant renderTime() {
        return this.renderTime;
    }

    @Override // org.jooq.GeneratorContext
    public final X table() {
        return this.table;
    }

    @Override // org.jooq.GeneratorContext
    public final Field<T> field() {
        return this.field;
    }

    @Override // org.jooq.GeneratorContext
    public final GeneratorStatementType statementType() {
        return this.statementType;
    }
}
