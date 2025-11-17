package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectCorrelatedSubqueryStep.class */
public interface SelectCorrelatedSubqueryStep<R extends Record> extends SelectFinalStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    Condition compare(Comparator comparator, R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition compare(Comparator comparator, Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition eq(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition eq(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition eq(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition equal(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition equal(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition equal(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition ne(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition ne(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition ne(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notEqual(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notEqual(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lt(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lt(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lt(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lessThan(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lessThan(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition le(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition le(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition le(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lessOrEqual(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lessOrEqual(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition gt(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition gt(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition gt(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition greaterThan(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition greaterThan(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition ge(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition ge(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition ge(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition greaterOrEqual(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition greaterOrEqual(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition in(R... rArr);

    @Support
    @CheckReturnValue
    @NotNull
    Condition in(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notIn(R... rArr);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notIn(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition isDistinctFrom(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition isDistinctFrom(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition isDistinctFrom(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    Condition isNotDistinctFrom(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition isNotDistinctFrom(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition isNotDistinctFrom(QuantifiedSelect<? extends R> quantifiedSelect);

    @Support
    @CheckReturnValue
    @NotNull
    BetweenAndStep<R> between(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition between(R r, R r2);

    @Support
    @CheckReturnValue
    @NotNull
    BetweenAndStep<R> between(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition between(Select<? extends R> select, Select<? extends R> select2);

    @Support
    @CheckReturnValue
    @NotNull
    BetweenAndStepR<R> betweenSymmetric(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition betweenSymmetric(R r, R r2);

    @Support
    @CheckReturnValue
    @NotNull
    BetweenAndStepR<R> betweenSymmetric(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition betweenSymmetric(Select<? extends R> select, Select<? extends R> select2);

    @Support
    @CheckReturnValue
    @NotNull
    BetweenAndStepR<R> notBetween(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notBetween(R r, R r2);

    @Support
    @CheckReturnValue
    @NotNull
    BetweenAndStepR<R> notBetween(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notBetween(Select<? extends R> select, Select<? extends R> select2);

    @Support
    @CheckReturnValue
    @NotNull
    BetweenAndStepR<R> notBetweenSymmetric(R r);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notBetweenSymmetric(R r, R r2);

    @Support
    @CheckReturnValue
    @NotNull
    BetweenAndStepR<R> notBetweenSymmetric(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Condition notBetweenSymmetric(Select<? extends R> select, Select<? extends R> select2);

    @Support
    @CheckReturnValue
    @NotNull
    Condition isNull();

    @Support
    @CheckReturnValue
    @NotNull
    Condition isNotNull();
}
