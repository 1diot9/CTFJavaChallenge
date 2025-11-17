package org.jooq.impl;

import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.GeneratorContext;
import org.jooq.GeneratorStatementType;
import org.jooq.Record;
import org.jooq.Table;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultGenerator.class */
final class DefaultGenerator<R extends Record, X extends Table<R>, T> implements Generator<R, X, T> {
    final Generator<R, X, T> delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultGenerator(Generator<R, X, T> delegate) {
        this.delegate = delegate;
    }

    @Override // java.util.function.Function
    public final Field<T> apply(GeneratorContext<R, X, T> t) {
        return this.delegate.apply(t);
    }

    @Override // org.jooq.Generator
    public final boolean supports(GeneratorStatementType statementType) {
        return this.delegate.supports(statementType);
    }
}
