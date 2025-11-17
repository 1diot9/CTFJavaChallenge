package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AggregateFilterStep.class */
public interface AggregateFilterStep<T> extends WindowBeforeOverStep<T> {
    @Support
    @NotNull
    WindowBeforeOverStep<T> filterWhere(Condition condition);

    @Support
    @NotNull
    WindowBeforeOverStep<T> filterWhere(Condition... conditionArr);

    @Support
    @NotNull
    WindowBeforeOverStep<T> filterWhere(Collection<? extends Condition> collection);

    @Support
    @NotNull
    WindowBeforeOverStep<T> filterWhere(Field<Boolean> field);

    @PlainSQL
    @Support
    @NotNull
    WindowBeforeOverStep<T> filterWhere(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    WindowBeforeOverStep<T> filterWhere(String str);

    @PlainSQL
    @Support
    @NotNull
    WindowBeforeOverStep<T> filterWhere(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    WindowBeforeOverStep<T> filterWhere(String str, QueryPart... queryPartArr);
}
