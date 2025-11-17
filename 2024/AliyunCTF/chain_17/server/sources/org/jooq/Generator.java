package org.jooq;

import java.io.Serializable;
import java.util.function.Function;
import org.jooq.Record;
import org.jooq.Table;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Generator.class */
public interface Generator<R extends Record, X extends Table<R>, T> extends Function<GeneratorContext<R, X, T>, Field<T>>, Serializable {
    default boolean supports(GeneratorStatementType statementType) {
        return true;
    }
}
