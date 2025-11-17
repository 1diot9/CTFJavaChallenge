package org.jooq.impl;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.BiFunction;
import org.jooq.BetweenAndStep;
import org.jooq.BetweenAndStepR;
import org.jooq.CloseableResultQuery;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Cursor;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.GroupField;
import org.jooq.JoinType;
import org.jooq.Name;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.Path;
import org.jooq.QuantifiedSelect;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Results;
import org.jooq.Row;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectFinalStep;
import org.jooq.SelectForUpdateOfStep;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectForUpdateWaitStep;
import org.jooq.SelectFromStep;
import org.jooq.SelectHavingConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectIntoStep;
import org.jooq.SelectJoinPartitionByStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitAfterOffsetStep;
import org.jooq.SelectLimitPercentAfterOffsetStep;
import org.jooq.SelectLimitPercentStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectOnStep;
import org.jooq.SelectOptionalOnStep;
import org.jooq.SelectQualifyConditionStep;
import org.jooq.SelectQualifyStep;
import org.jooq.SelectQuery;
import org.jooq.SelectSeekLimitStep;
import org.jooq.SelectSeekStep1;
import org.jooq.SelectSeekStep10;
import org.jooq.SelectSeekStep11;
import org.jooq.SelectSeekStep12;
import org.jooq.SelectSeekStep13;
import org.jooq.SelectSeekStep14;
import org.jooq.SelectSeekStep15;
import org.jooq.SelectSeekStep16;
import org.jooq.SelectSeekStep17;
import org.jooq.SelectSeekStep18;
import org.jooq.SelectSeekStep19;
import org.jooq.SelectSeekStep2;
import org.jooq.SelectSeekStep20;
import org.jooq.SelectSeekStep21;
import org.jooq.SelectSeekStep22;
import org.jooq.SelectSeekStep3;
import org.jooq.SelectSeekStep4;
import org.jooq.SelectSeekStep5;
import org.jooq.SelectSeekStep6;
import org.jooq.SelectSeekStep7;
import org.jooq.SelectSeekStep8;
import org.jooq.SelectSeekStep9;
import org.jooq.SelectSeekStepN;
import org.jooq.SelectSelectStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.WindowDefinition;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.QOM;
import org.reactivestreams.Subscriber;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SelectImpl.class */
public final class SelectImpl<R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> extends AbstractDelegatingQuery<R, SelectQueryImpl<R>> implements ResultQueryTrait<R>, SelectSelectStep<R>, SelectOptionalOnStep<R>, SelectOnConditionStep<R>, SelectConditionStep<R>, SelectHavingConditionStep<R>, SelectQualifyConditionStep<R>, SelectSeekStep1<R, T1>, SelectSeekStep2<R, T1, T2>, SelectSeekStep3<R, T1, T2, T3>, SelectSeekStep4<R, T1, T2, T3, T4>, SelectSeekStep5<R, T1, T2, T3, T4, T5>, SelectSeekStep6<R, T1, T2, T3, T4, T5, T6>, SelectSeekStep7<R, T1, T2, T3, T4, T5, T6, T7>, SelectSeekStep8<R, T1, T2, T3, T4, T5, T6, T7, T8>, SelectSeekStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9>, SelectSeekStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, SelectSeekStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, SelectSeekStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, SelectSeekStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, SelectSeekStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, SelectSeekStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, SelectSeekStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, SelectSeekStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, SelectSeekStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, SelectSeekStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, SelectSeekStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, SelectSeekStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>, SelectSeekStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>, SelectSeekStepN<R>, SelectSeekLimitStep<R>, SelectLimitPercentStep<R>, SelectLimitAfterOffsetStep<R>, SelectLimitPercentAfterOffsetStep<R>, SelectForUpdateOfStep<R> {
    private transient TableLike<?> joinTable;
    private transient Field<?>[] joinPartitionBy;
    private transient JoinType joinType;
    private transient QOM.JoinHint joinHint;
    private transient ConditionProviderImpl joinConditions;
    private transient ConditionStep conditionStep;
    private transient Number limit;
    private transient Field<? extends Number> limitParam;
    private transient Number offset;
    private transient Field<? extends Number> offsetParam;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SelectImpl$ConditionStep.class */
    public enum ConditionStep {
        ON,
        WHERE,
        HAVING,
        QUALIFY
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ CloseableResultQuery keepStatement(boolean z) {
        return (CloseableResultQuery) super.keepStatement(z);
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ CloseableResultQuery queryTimeout(int i) {
        return (CloseableResultQuery) super.queryTimeout(i);
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ CloseableResultQuery poolable(boolean z) {
        return (CloseableResultQuery) super.poolable(z);
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ CloseableResultQuery bind(int i, Object obj) throws IllegalArgumentException, DataTypeException {
        return (CloseableResultQuery) super.bind(i, obj);
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ CloseableResultQuery bind(String str, Object obj) throws IllegalArgumentException, DataTypeException {
        return (CloseableResultQuery) super.bind(str, obj);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public /* bridge */ /* synthetic */ ResultQuery coerce(Collection collection) {
        return coerce((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public /* bridge */ /* synthetic */ ResultQuery intern(Field[] fieldArr) {
        return intern((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ ResultQuery queryTimeout(int i) {
        return (ResultQuery) super.queryTimeout(i);
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ ResultQuery poolable(boolean z) {
        return (ResultQuery) super.poolable(z);
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ ResultQuery bind(int i, Object obj) throws IllegalArgumentException, DataTypeException {
        return (ResultQuery) super.bind(i, obj);
    }

    @Override // org.jooq.impl.AbstractDelegatingQuery, org.jooq.CloseableQuery, org.jooq.Query
    public /* bridge */ /* synthetic */ ResultQuery bind(String str, Object obj) throws IllegalArgumentException, DataTypeException {
        return (ResultQuery) super.bind(str, obj);
    }

    @Override // org.jooq.SelectSelectStep
    public /* bridge */ /* synthetic */ SelectSelectStep select(Collection collection) {
        return select((Collection<? extends SelectFieldOrAsterisk>) collection);
    }

    @Override // org.jooq.SelectIntoStep
    public /* bridge */ /* synthetic */ SelectFromStep into(Table table) {
        return into((Table<?>) table);
    }

    @Override // org.jooq.SelectFromStep
    public /* bridge */ /* synthetic */ SelectJoinStep from(Collection collection) {
        return from((Collection<? extends TableLike<?>>) collection);
    }

    @Override // org.jooq.SelectFromStep
    public /* bridge */ /* synthetic */ SelectJoinStep from(TableLike[] tableLikeArr) {
        return from((TableLike<?>[]) tableLikeArr);
    }

    @Override // org.jooq.SelectFromStep
    public /* bridge */ /* synthetic */ SelectJoinStep from(TableLike tableLike) {
        return from((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectWhereStep
    public /* bridge */ /* synthetic */ SelectConditionStep whereNotExists(Select select) {
        return whereNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectWhereStep
    public /* bridge */ /* synthetic */ SelectConditionStep whereExists(Select select) {
        return whereExists((Select<?>) select);
    }

    @Override // org.jooq.SelectWhereStep
    public /* bridge */ /* synthetic */ SelectConditionStep where(Field field) {
        return where((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectWhereStep
    public /* bridge */ /* synthetic */ SelectConditionStep where(Collection collection) {
        return where((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.SelectGroupByStep
    public /* bridge */ /* synthetic */ SelectHavingStep groupByDistinct(Collection collection) {
        return groupByDistinct((Collection<? extends GroupField>) collection);
    }

    @Override // org.jooq.SelectGroupByStep
    public /* bridge */ /* synthetic */ SelectHavingStep groupBy(Collection collection) {
        return groupBy((Collection<? extends GroupField>) collection);
    }

    @Override // org.jooq.SelectHavingStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep having(Field field) {
        return having((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectHavingStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep having(Collection collection) {
        return having((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.SelectWindowStep
    public /* bridge */ /* synthetic */ SelectQualifyStep window(Collection collection) {
        return window((Collection<? extends WindowDefinition>) collection);
    }

    @Override // org.jooq.SelectQualifyStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep qualify(Field field) {
        return qualify((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectQualifyStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep qualify(Collection collection) {
        return qualify((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.SelectOrderByStep
    public /* bridge */ /* synthetic */ SelectSeekStepN orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.SelectOrderByStep
    public /* bridge */ /* synthetic */ SelectSeekStepN orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.SelectLimitStep, org.jooq.SelectOffsetStep
    public /* bridge */ /* synthetic */ SelectLimitAfterOffsetStep offset(Field field) {
        return offset((Field<? extends Number>) field);
    }

    @Override // org.jooq.SelectLimitStep
    public /* bridge */ /* synthetic */ SelectLimitPercentAfterOffsetStep limit(Field field, Field field2) {
        return limit((Field<? extends Number>) field, (Field<? extends Number>) field2);
    }

    @Override // org.jooq.SelectLimitStep
    public /* bridge */ /* synthetic */ SelectLimitPercentAfterOffsetStep limit(Field field, Number number) {
        return limit((Field<? extends Number>) field, number);
    }

    @Override // org.jooq.SelectLimitStep
    public /* bridge */ /* synthetic */ SelectLimitPercentAfterOffsetStep limit(Number number, Field field) {
        return limit(number, (Field<? extends Number>) field);
    }

    @Override // org.jooq.SelectLimitStep, org.jooq.SelectSeekLimitStep, org.jooq.SelectLimitAfterOffsetStep
    public /* bridge */ /* synthetic */ SelectLimitPercentStep limit(Field field) {
        return limit((Field<? extends Number>) field);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOnStep straightJoin(Path path) {
        return straightJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOnStep straightJoin(TableLike tableLike) {
        return straightJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinStep outerApply(TableLike tableLike) {
        return outerApply((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinStep crossApply(TableLike tableLike) {
        return crossApply((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep leftAntiJoin(Path path) {
        return leftAntiJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOnStep leftAntiJoin(TableLike tableLike) {
        return leftAntiJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep leftSemiJoin(Path path) {
        return leftSemiJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOnStep leftSemiJoin(TableLike tableLike) {
        return leftSemiJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinStep naturalFullOuterJoin(TableLike tableLike) {
        return naturalFullOuterJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinStep naturalRightOuterJoin(TableLike tableLike) {
        return naturalRightOuterJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinStep naturalLeftOuterJoin(TableLike tableLike) {
        return naturalLeftOuterJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinStep naturalJoin(TableLike tableLike) {
        return naturalJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep fullOuterJoin(Path path) {
        return fullOuterJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOnStep fullOuterJoin(TableLike tableLike) {
        return fullOuterJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep fullJoin(Path path) {
        return fullJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOnStep fullJoin(TableLike tableLike) {
        return fullJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep rightOuterJoin(Path path) {
        return rightOuterJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinPartitionByStep rightOuterJoin(TableLike tableLike) {
        return rightOuterJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep rightJoin(Path path) {
        return rightJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinPartitionByStep rightJoin(TableLike tableLike) {
        return rightJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep leftOuterJoin(Path path) {
        return leftOuterJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinPartitionByStep leftOuterJoin(TableLike tableLike) {
        return leftOuterJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep leftJoin(Path path) {
        return leftJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinPartitionByStep leftJoin(TableLike tableLike) {
        return leftJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectJoinStep crossJoin(TableLike tableLike) {
        return crossJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep innerJoin(Path path) {
        return innerJoin((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOnStep innerJoin(TableLike tableLike) {
        return innerJoin((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep join(Path path) {
        return join((Path<?>) path);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOnStep join(TableLike tableLike) {
        return join((TableLike<?>) tableLike);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep join(TableLike tableLike, JoinType joinType, QOM.JoinHint joinHint) {
        return join((TableLike<?>) tableLike, joinType, joinHint);
    }

    @Override // org.jooq.SelectJoinStep
    public /* bridge */ /* synthetic */ SelectOptionalOnStep join(TableLike tableLike, JoinType joinType) {
        return join((TableLike<?>) tableLike, joinType);
    }

    @Override // org.jooq.SelectOnStep
    public /* bridge */ /* synthetic */ SelectJoinStep using(Collection collection) {
        return using((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.SelectOnStep
    public /* bridge */ /* synthetic */ SelectJoinStep using(Field[] fieldArr) {
        return using((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.SelectOnStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep onKey(ForeignKey foreignKey) {
        return onKey((ForeignKey<?, ?>) foreignKey);
    }

    @Override // org.jooq.SelectOnStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep onKey(TableField[] tableFieldArr) throws DataAccessException {
        return onKey((TableField<?, ?>[]) tableFieldArr);
    }

    @Override // org.jooq.SelectOnStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep on(Field field) {
        return on((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep orNotExists(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep orExists(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep orNot(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep or(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep andNotExists(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep andExists(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep andNot(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectOnConditionStep and(Field field) {
        return and((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectConditionStep orNotExists(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectConditionStep orExists(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectConditionStep orNot(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectConditionStep or(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectConditionStep andNotExists(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectConditionStep andExists(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectConditionStep andNot(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectConditionStep and(Field field) {
        return and((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep orNotExists(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep orExists(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep orNot(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep or(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep andNotExists(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep andExists(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep andNot(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectHavingConditionStep and(Field field) {
        return and((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep orNotExists(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep orExists(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep orNot(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep or(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep andNotExists(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep andExists(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep andNot(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public /* bridge */ /* synthetic */ SelectQualifyConditionStep and(Field field) {
        return and((Field<Boolean>) field);
    }

    @Override // org.jooq.SelectSeekLimitStep, org.jooq.SelectLimitAfterOffsetStep
    public /* bridge */ /* synthetic */ SelectForUpdateStep limit(Field field) {
        return limit((Field<? extends Number>) field);
    }

    @Override // org.jooq.SelectOffsetStep
    public /* bridge */ /* synthetic */ SelectForUpdateStep offset(Field field) {
        return offset((Field<? extends Number>) field);
    }

    @Override // org.jooq.SelectSeekLimitStep, org.jooq.SelectLimitAfterOffsetStep
    public /* bridge */ /* synthetic */ SelectLimitPercentAfterOffsetStep limit(Field field) {
        return limit((Field<? extends Number>) field);
    }

    @Override // org.jooq.SelectForUpdateOfStep
    public /* bridge */ /* synthetic */ SelectForUpdateWaitStep of(Table[] tableArr) {
        return of((Table<?>[]) tableArr);
    }

    @Override // org.jooq.SelectForUpdateOfStep
    public /* bridge */ /* synthetic */ SelectForUpdateWaitStep of(Collection collection) {
        return of((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.SelectForUpdateOfStep
    public /* bridge */ /* synthetic */ SelectForUpdateWaitStep of(Field[] fieldArr) {
        return of((Field<?>[]) fieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectImpl(Configuration configuration, WithImpl with) {
        this(configuration, with, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectImpl(Configuration configuration, WithImpl with, boolean distinct) {
        this(new SelectQueryImpl(configuration, with, distinct));
    }

    SelectImpl(SelectQueryImpl<R> query) {
        super(query);
    }

    @Override // org.jooq.SelectFinalStep
    public final SelectQuery<R> getQuery() {
        return getDelegate();
    }

    @Override // org.jooq.SelectSelectStep
    public final SelectImpl select(SelectFieldOrAsterisk... fields) {
        getQuery().addSelect(fields);
        return this;
    }

    @Override // org.jooq.SelectSelectStep
    public final SelectImpl select(Collection<? extends SelectFieldOrAsterisk> fields) {
        getQuery().addSelect(fields);
        return this;
    }

    @Override // org.jooq.SelectDistinctOnStep
    public final SelectIntoStep<R> on(SelectFieldOrAsterisk... fields) {
        return distinctOn(Arrays.asList(fields));
    }

    @Override // org.jooq.SelectDistinctOnStep
    public final SelectIntoStep<R> on(Collection<? extends SelectFieldOrAsterisk> fields) {
        return distinctOn(fields);
    }

    @Override // org.jooq.SelectDistinctOnStep
    public final SelectIntoStep<R> distinctOn(SelectFieldOrAsterisk... fields) {
        getQuery().addDistinctOn(fields);
        return this;
    }

    @Override // org.jooq.SelectDistinctOnStep
    public final SelectIntoStep<R> distinctOn(Collection<? extends SelectFieldOrAsterisk> fields) {
        getQuery().addDistinctOn(fields);
        return this;
    }

    @Override // org.jooq.SelectIntoStep
    public final SelectImpl into(Table<?> table) {
        getQuery().setInto(table);
        return this;
    }

    @Override // org.jooq.SelectFromStep
    public final SelectImpl hint(String hint) {
        getQuery().addHint(hint);
        return this;
    }

    @Override // org.jooq.SelectOptionStep
    public final SelectImpl option(String hint) {
        getQuery().addOption(hint);
        return this;
    }

    @Override // org.jooq.SelectFromStep
    public final SelectImpl from(TableLike<?> table) {
        getQuery().addFrom(table);
        return this;
    }

    @Override // org.jooq.SelectFromStep
    public final SelectImpl from(TableLike<?>... tables) {
        getQuery().addFrom(tables);
        return this;
    }

    @Override // org.jooq.SelectFromStep
    public final SelectImpl from(Collection<? extends TableLike<?>> tables) {
        getQuery().addFrom(tables);
        return this;
    }

    @Override // org.jooq.SelectFromStep
    public final SelectImpl from(SQL sql) {
        return from((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectFromStep
    public final SelectImpl from(String sql) {
        return from((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectFromStep
    public final SelectImpl from(String sql, Object... bindings) {
        return from((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectFromStep
    public final SelectImpl from(String sql, QueryPart... parts) {
        return from((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectFromStep
    public final SelectJoinStep<R> from(Name name) {
        return from((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl where(Condition conditions) {
        this.conditionStep = ConditionStep.WHERE;
        getQuery().addConditions(conditions);
        return this;
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl where(Condition... conditions) {
        this.conditionStep = ConditionStep.WHERE;
        getQuery().addConditions(conditions);
        return this;
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl where(Collection<? extends Condition> conditions) {
        this.conditionStep = ConditionStep.WHERE;
        getQuery().addConditions(conditions);
        return this;
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl where(SQL sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl where(String sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl where(String sql, Object... bindings) {
        return where(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl where(String sql, QueryPart... parts) {
        return where(DSL.condition(sql, parts));
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl whereExists(Select<?> select) {
        this.conditionStep = ConditionStep.WHERE;
        return andExists(select);
    }

    @Override // org.jooq.SelectWhereStep
    public final SelectImpl whereNotExists(Select<?> select) {
        this.conditionStep = ConditionStep.WHERE;
        return andNotExists(select);
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl and(Condition condition) {
        switch (this.conditionStep) {
            case ON:
                this.joinConditions.addConditions(condition);
                break;
            case WHERE:
                getQuery().addConditions(condition);
                break;
            case HAVING:
                getQuery().addHaving(condition);
                break;
            case QUALIFY:
                getQuery().addQualify(condition);
                break;
        }
        return this;
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public final SelectImpl and(Field<Boolean> condition) {
        return and(DSL.condition(condition));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl and(SQL sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl and(String sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl and(String sql, Object... bindings) {
        return and(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl and(String sql, QueryPart... parts) {
        return and(DSL.condition(sql, parts));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl andNot(Condition condition) {
        return and(condition.not());
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public final SelectImpl andNot(Field<Boolean> condition) {
        return andNot(DSL.condition(condition));
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public final SelectImpl andExists(Select<?> select) {
        return and(DSL.exists(select));
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public final SelectImpl andNotExists(Select<?> select) {
        return and(DSL.notExists(select));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl or(Condition condition) {
        switch (this.conditionStep) {
            case ON:
                this.joinConditions.addConditions(Operator.OR, condition);
                break;
            case WHERE:
                getQuery().addConditions(Operator.OR, condition);
                break;
            case HAVING:
                getQuery().addHaving(Operator.OR, condition);
                break;
            case QUALIFY:
                getQuery().addQualify(Operator.OR, condition);
                break;
        }
        return this;
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public final SelectImpl or(Field<Boolean> condition) {
        return or(DSL.condition(condition));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl or(SQL sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl or(String sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl or(String sql, Object... bindings) {
        return or(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl or(String sql, QueryPart... parts) {
        return or(DSL.condition(sql, parts));
    }

    @Override // org.jooq.SelectQualifyConditionStep
    public final SelectImpl orNot(Condition condition) {
        return or(condition.not());
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public final SelectImpl orNot(Field<Boolean> condition) {
        return orNot(DSL.condition(condition));
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public final SelectImpl orExists(Select<?> select) {
        return or(DSL.exists(select));
    }

    @Override // org.jooq.SelectOnConditionStep, org.jooq.SelectConditionStep, org.jooq.SelectHavingConditionStep, org.jooq.SelectQualifyConditionStep
    public final SelectImpl orNotExists(Select<?> select) {
        return or(DSL.notExists(select));
    }

    @Override // org.jooq.SelectGroupByStep
    public final SelectImpl groupBy(GroupField... fields) {
        getQuery().addGroupBy(fields);
        return this;
    }

    @Override // org.jooq.SelectGroupByStep
    public final SelectImpl groupBy(Collection<? extends GroupField> fields) {
        getQuery().addGroupBy(fields);
        return this;
    }

    @Override // org.jooq.SelectGroupByStep
    public final SelectImpl groupByDistinct(GroupField... fields) {
        getQuery().addGroupBy(fields);
        getQuery().setGroupByDistinct(true);
        return this;
    }

    @Override // org.jooq.SelectGroupByStep
    public final SelectImpl groupByDistinct(Collection<? extends GroupField> fields) {
        getQuery().addGroupBy(fields);
        getQuery().setGroupByDistinct(true);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep1 orderBy(OrderField t1) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep2 orderBy(OrderField t1, OrderField t2) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep3 orderBy(OrderField t1, OrderField t2, OrderField t3) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep4 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep5 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep6 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep7 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep8 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep9 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep10 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep11 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep12 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep13 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep14 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep15 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14, OrderField t15) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep16 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14, OrderField t15, OrderField t16) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep17 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14, OrderField t15, OrderField t16, OrderField t17) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep18 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14, OrderField t15, OrderField t16, OrderField t17, OrderField t18) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep19 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14, OrderField t15, OrderField t16, OrderField t17, OrderField t18, OrderField t19) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep20 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14, OrderField t15, OrderField t16, OrderField t17, OrderField t18, OrderField t19, OrderField t20) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep21 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14, OrderField t15, OrderField t16, OrderField t17, OrderField t18, OrderField t19, OrderField t20, OrderField t21) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOrderByStep
    public final SelectSeekStep22 orderBy(OrderField t1, OrderField t2, OrderField t3, OrderField t4, OrderField t5, OrderField t6, OrderField t7, OrderField t8, OrderField t9, OrderField t10, OrderField t11, OrderField t12, OrderField t13, OrderField t14, OrderField t15, OrderField t16, OrderField t17, OrderField t18, OrderField t19, OrderField t20, OrderField t21, OrderField t22) {
        return orderBy((OrderField<?>[]) new OrderField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22});
    }

    @Override // org.jooq.SelectOrderByStep
    public final SelectImpl orderBy(OrderField<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override // org.jooq.SelectOrderByStep
    public final SelectImpl orderBy(Collection<? extends OrderField<?>> fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override // org.jooq.SelectOrderByStep
    public final SelectImpl orderBy(int... fieldIndexes) {
        getQuery().addOrderBy(fieldIndexes);
        return this;
    }

    @Override // org.jooq.SelectSeekStep1
    public final SelectSeekLimitStep<R> seek(Object t1) {
        return seek(t1);
    }

    @Override // org.jooq.SelectSeekStep1
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1) {
        return seekBefore(t1);
    }

    @Override // org.jooq.SelectSeekStep1
    public final SelectSeekLimitStep<R> seekAfter(Object t1) {
        return seekAfter(t1);
    }

    @Override // org.jooq.SelectSeekStep2
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2) {
        return seek(t1, t2);
    }

    @Override // org.jooq.SelectSeekStep2
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2) {
        return seekBefore(t1, t2);
    }

    @Override // org.jooq.SelectSeekStep2
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2) {
        return seekAfter(t1, t2);
    }

    @Override // org.jooq.SelectSeekStep3
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3) {
        return seek(t1, t2, t3);
    }

    @Override // org.jooq.SelectSeekStep3
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3) {
        return seekBefore(t1, t2, t3);
    }

    @Override // org.jooq.SelectSeekStep3
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3) {
        return seekAfter(t1, t2, t3);
    }

    @Override // org.jooq.SelectSeekStep4
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4) {
        return seek(t1, t2, t3, t4);
    }

    @Override // org.jooq.SelectSeekStep4
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4) {
        return seekBefore(t1, t2, t3, t4);
    }

    @Override // org.jooq.SelectSeekStep4
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4) {
        return seekAfter(t1, t2, t3, t4);
    }

    @Override // org.jooq.SelectSeekStep5
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5) {
        return seek(t1, t2, t3, t4, t5);
    }

    @Override // org.jooq.SelectSeekStep5
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5) {
        return seekBefore(t1, t2, t3, t4, t5);
    }

    @Override // org.jooq.SelectSeekStep5
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5) {
        return seekAfter(t1, t2, t3, t4, t5);
    }

    @Override // org.jooq.SelectSeekStep6
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6) {
        return seek(t1, t2, t3, t4, t5, t6);
    }

    @Override // org.jooq.SelectSeekStep6
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6) {
        return seekBefore(t1, t2, t3, t4, t5, t6);
    }

    @Override // org.jooq.SelectSeekStep6
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6) {
        return seekAfter(t1, t2, t3, t4, t5, t6);
    }

    @Override // org.jooq.SelectSeekStep7
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7) {
        return seek(t1, t2, t3, t4, t5, t6, t7);
    }

    @Override // org.jooq.SelectSeekStep7
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7);
    }

    @Override // org.jooq.SelectSeekStep7
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7);
    }

    @Override // org.jooq.SelectSeekStep8
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8);
    }

    @Override // org.jooq.SelectSeekStep8
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8);
    }

    @Override // org.jooq.SelectSeekStep8
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8);
    }

    @Override // org.jooq.SelectSeekStep9
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }

    @Override // org.jooq.SelectSeekStep9
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }

    @Override // org.jooq.SelectSeekStep9
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }

    @Override // org.jooq.SelectSeekStep10
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
    }

    @Override // org.jooq.SelectSeekStep10
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
    }

    @Override // org.jooq.SelectSeekStep10
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
    }

    @Override // org.jooq.SelectSeekStep11
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override // org.jooq.SelectSeekStep11
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override // org.jooq.SelectSeekStep11
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override // org.jooq.SelectSeekStep12
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
    }

    @Override // org.jooq.SelectSeekStep12
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
    }

    @Override // org.jooq.SelectSeekStep12
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
    }

    @Override // org.jooq.SelectSeekStep13
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override // org.jooq.SelectSeekStep13
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override // org.jooq.SelectSeekStep13
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override // org.jooq.SelectSeekStep14
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14);
    }

    @Override // org.jooq.SelectSeekStep14
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14);
    }

    @Override // org.jooq.SelectSeekStep14
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14);
    }

    @Override // org.jooq.SelectSeekStep15
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15);
    }

    @Override // org.jooq.SelectSeekStep15
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15);
    }

    @Override // org.jooq.SelectSeekStep15
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15);
    }

    @Override // org.jooq.SelectSeekStep16
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16);
    }

    @Override // org.jooq.SelectSeekStep16
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16);
    }

    @Override // org.jooq.SelectSeekStep16
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16);
    }

    @Override // org.jooq.SelectSeekStep17
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17);
    }

    @Override // org.jooq.SelectSeekStep17
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17);
    }

    @Override // org.jooq.SelectSeekStep17
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17);
    }

    @Override // org.jooq.SelectSeekStep18
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18);
    }

    @Override // org.jooq.SelectSeekStep18
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18);
    }

    @Override // org.jooq.SelectSeekStep18
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18);
    }

    @Override // org.jooq.SelectSeekStep19
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19);
    }

    @Override // org.jooq.SelectSeekStep19
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19);
    }

    @Override // org.jooq.SelectSeekStep19
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19);
    }

    @Override // org.jooq.SelectSeekStep20
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20);
    }

    @Override // org.jooq.SelectSeekStep20
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20);
    }

    @Override // org.jooq.SelectSeekStep20
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20);
    }

    @Override // org.jooq.SelectSeekStep21
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20, Object t21) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21);
    }

    @Override // org.jooq.SelectSeekStep21
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20, Object t21) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21);
    }

    @Override // org.jooq.SelectSeekStep21
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20, Object t21) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21);
    }

    @Override // org.jooq.SelectSeekStep22
    public final SelectSeekLimitStep<R> seek(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20, Object t21, Object t22) {
        return seek(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22);
    }

    @Override // org.jooq.SelectSeekStep22
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20, Object t21, Object t22) {
        return seekBefore(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22);
    }

    @Override // org.jooq.SelectSeekStep22
    public final SelectSeekLimitStep<R> seekAfter(Object t1, Object t2, Object t3, Object t4, Object t5, Object t6, Object t7, Object t8, Object t9, Object t10, Object t11, Object t12, Object t13, Object t14, Object t15, Object t16, Object t17, Object t18, Object t19, Object t20, Object t21, Object t22) {
        return seekAfter(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep1
    public final SelectSeekLimitStep<R> seek(Field t1) {
        return seek((Field<?>[]) new Field[]{t1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep1
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1) {
        return seekBefore((Field<?>[]) new Field[]{t1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep1
    public final SelectSeekLimitStep<R> seekAfter(Field t1) {
        return seekAfter((Field<?>[]) new Field[]{t1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep2
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2) {
        return seek((Field<?>[]) new Field[]{t1, t2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep2
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep2
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep3
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep3
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep3
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep4
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep4
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep4
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep5
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep5
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep5
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep6
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep6
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep6
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep7
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep7
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep7
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep8
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep8
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep8
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep9
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep9
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep9
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep10
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep10
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep10
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep11
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep11
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep11
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep12
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep12
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep12
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep13
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep13
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep13
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep14
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep14
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep14
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep15
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep15
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep15
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep16
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep16
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep16
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep17
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep17
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep17
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep18
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep18
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep18
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep19
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep19
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep19
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep20
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep20
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep20
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep21
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20, Field t21) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep21
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20, Field t21) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep21
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20, Field t21) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep22
    public final SelectSeekLimitStep<R> seek(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20, Field t21, Field t22) {
        return seek((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep22
    @Deprecated
    public final SelectSeekLimitStep<R> seekBefore(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20, Field t21, Field t22) {
        return seekBefore((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectSeekStep22
    public final SelectSeekLimitStep<R> seekAfter(Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20, Field t21, Field t22) {
        return seekAfter((Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22});
    }

    private final List<? extends Field<?>> seekValues(Object[] values) {
        SelectQuery<R> query = getQuery();
        if (query instanceof SelectQueryImpl) {
            SelectQueryImpl<R> s = (SelectQueryImpl) query;
            return Tools.fields(values, (DataType<?>[]) Tools.map(s.getOrderBy().fields(), f -> {
                return f.getDataType();
            }, x$0 -> {
                return new DataType[x$0];
            }));
        }
        return Tools.fields(values);
    }

    @Override // org.jooq.SelectSeekStepN
    public final SelectSeekLimitStep<R> seek(Object... values) {
        getQuery().addSeekAfter(seekValues(values));
        return this;
    }

    @Override // org.jooq.SelectSeekStepN
    public final SelectSeekLimitStep<R> seek(Field<?>... fields) {
        getQuery().addSeekAfter(fields);
        return this;
    }

    @Override // org.jooq.SelectSeekStepN
    public final SelectSeekLimitStep<R> seekAfter(Object... values) {
        getQuery().addSeekAfter(seekValues(values));
        return this;
    }

    @Override // org.jooq.SelectSeekStepN
    public final SelectSeekLimitStep<R> seekAfter(Field<?>... fields) {
        getQuery().addSeekAfter(fields);
        return this;
    }

    @Override // org.jooq.SelectSeekStepN
    public final SelectSeekLimitStep<R> seekBefore(Object... values) {
        getQuery().addSeekBefore(Tools.fields(values));
        return this;
    }

    @Override // org.jooq.SelectSeekStepN
    public final SelectSeekLimitStep<R> seekBefore(Field<?>... fields) {
        getQuery().addSeekBefore(fields);
        return this;
    }

    @Override // org.jooq.SelectSeekLimitStep, org.jooq.SelectLimitAfterOffsetStep
    public final SelectImpl limit(Number l) {
        this.limit = l;
        this.limitParam = null;
        return limitOffset();
    }

    @Override // org.jooq.SelectLimitStep, org.jooq.SelectSeekLimitStep, org.jooq.SelectLimitAfterOffsetStep
    public final SelectImpl limit(Field<? extends Number> l) {
        this.limit = null;
        this.limitParam = l;
        return limitOffset();
    }

    @Override // org.jooq.SelectLimitStep
    public final SelectImpl limit(Number o, Number l) {
        this.offset = o;
        this.offsetParam = null;
        this.limit = l;
        this.limitParam = null;
        return limitOffset();
    }

    @Override // org.jooq.SelectLimitStep
    public final SelectImpl limit(Number o, Field<? extends Number> l) {
        this.offset = o;
        this.offsetParam = null;
        this.limit = null;
        this.limitParam = l;
        return limitOffset();
    }

    @Override // org.jooq.SelectLimitStep
    public final SelectImpl limit(Field<? extends Number> o, Number l) {
        this.offset = null;
        this.offsetParam = o;
        this.limit = l;
        this.limitParam = null;
        return limitOffset();
    }

    @Override // org.jooq.SelectLimitStep
    public final SelectImpl limit(Field<? extends Number> o, Field<? extends Number> l) {
        this.offset = null;
        this.offsetParam = o;
        this.limit = null;
        this.limitParam = l;
        return limitOffset();
    }

    @Override // org.jooq.SelectOffsetStep
    public final SelectImpl offset(Number o) {
        this.offset = o;
        this.offsetParam = null;
        return limitOffset();
    }

    @Override // org.jooq.SelectLimitStep, org.jooq.SelectOffsetStep
    public final SelectImpl offset(Field<? extends Number> o) {
        this.offset = null;
        this.offsetParam = o;
        return limitOffset();
    }

    private final SelectImpl limitOffset() {
        if (this.limit != null) {
            if (this.offset != null) {
                getQuery().addLimit(this.offset, this.limit);
            } else if (this.offsetParam != null) {
                getQuery().addLimit(this.offsetParam, this.limit);
            } else {
                getQuery().addLimit(this.limit);
            }
        } else if (this.limitParam != null) {
            if (this.offset != null) {
                getQuery().addLimit(this.offset, this.limitParam);
            } else if (this.offsetParam != null) {
                getQuery().addLimit(this.offsetParam, this.limitParam);
            } else {
                getQuery().addLimit(this.limitParam);
            }
        } else if (this.offset != null) {
            getQuery().addOffset(this.offset);
        } else if (this.offsetParam != null) {
            getQuery().addOffset(this.offsetParam);
        }
        return this;
    }

    @Override // org.jooq.SelectLimitPercentAfterOffsetStep
    public final SelectImpl percent() {
        getQuery().setLimitPercent(true);
        return this;
    }

    @Override // org.jooq.SelectWithTiesAfterOffsetStep
    public final SelectImpl withTies() {
        getQuery().setWithTies(true);
        return this;
    }

    @Override // org.jooq.SelectForUpdateStep
    public final SelectImpl forUpdate() {
        getQuery().setForUpdate(true);
        return this;
    }

    @Override // org.jooq.SelectForUpdateStep
    public final SelectImpl forNoKeyUpdate() {
        getQuery().setForNoKeyUpdate(true);
        return this;
    }

    @Override // org.jooq.SelectForUpdateOfStep
    public final SelectImpl of(Field<?>... fields) {
        getQuery().setForLockModeOf(fields);
        return this;
    }

    @Override // org.jooq.SelectForUpdateOfStep
    public final SelectImpl of(Collection<? extends Field<?>> fields) {
        getQuery().setForLockModeOf(fields);
        return this;
    }

    @Override // org.jooq.SelectForUpdateOfStep
    public final SelectImpl of(Table<?>... tables) {
        getQuery().setForLockModeOf(tables);
        return this;
    }

    @Override // org.jooq.SelectForUpdateWaitStep
    public final SelectImpl wait(int seconds) {
        getQuery().setForLockModeWait(seconds);
        return this;
    }

    @Override // org.jooq.SelectForUpdateWaitStep
    public final SelectImpl noWait() {
        getQuery().setForLockModeNoWait();
        return this;
    }

    @Override // org.jooq.SelectForUpdateWaitStep
    public final SelectImpl skipLocked() {
        getQuery().setForLockModeSkipLocked();
        return this;
    }

    @Override // org.jooq.SelectForUpdateStep
    public final SelectImpl forShare() {
        getQuery().setForShare(true);
        return this;
    }

    @Override // org.jooq.SelectForUpdateStep
    public final SelectImpl forKeyShare() {
        getQuery().setForKeyShare(true);
        return this;
    }

    @Override // org.jooq.SelectForUpdateStep
    public final SelectFinalStep<R> withCheckOption() {
        getQuery().setWithCheckOption();
        return this;
    }

    @Override // org.jooq.SelectForUpdateStep
    public final SelectFinalStep<R> withReadOnly() {
        getQuery().setWithReadOnly();
        return this;
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl union(Select<? extends R> select) {
        return new SelectImpl(getDelegate().union((Select) select));
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl unionDistinct(Select<? extends R> select) {
        return new SelectImpl(getDelegate().unionDistinct((Select) select));
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl unionAll(Select<? extends R> select) {
        return new SelectImpl(getDelegate().unionAll((Select) select));
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl except(Select<? extends R> select) {
        return new SelectImpl(getDelegate().except((Select) select));
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl exceptDistinct(Select<? extends R> select) {
        return new SelectImpl(getDelegate().exceptDistinct((Select) select));
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl exceptAll(Select<? extends R> select) {
        return new SelectImpl(getDelegate().exceptAll((Select) select));
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl intersect(Select<? extends R> select) {
        return new SelectImpl(getDelegate().intersect((Select) select));
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl intersectDistinct(Select<? extends R> select) {
        return new SelectImpl(getDelegate().intersectDistinct((Select) select));
    }

    @Override // org.jooq.SelectUnionStep, org.jooq.Select
    public final SelectImpl intersectAll(Select<? extends R> select) {
        return new SelectImpl(getDelegate().intersectAll((Select) select));
    }

    @Override // org.jooq.SelectHavingStep
    public final SelectImpl having(Condition conditions) {
        this.conditionStep = ConditionStep.HAVING;
        getQuery().addHaving(conditions);
        return this;
    }

    @Override // org.jooq.SelectHavingStep
    public final SelectImpl having(Condition... conditions) {
        this.conditionStep = ConditionStep.HAVING;
        getQuery().addHaving(conditions);
        return this;
    }

    @Override // org.jooq.SelectHavingStep
    public final SelectImpl having(Collection<? extends Condition> conditions) {
        this.conditionStep = ConditionStep.HAVING;
        getQuery().addHaving(conditions);
        return this;
    }

    @Override // org.jooq.SelectHavingStep
    public final SelectImpl having(Field<Boolean> condition) {
        return having(DSL.condition(condition));
    }

    @Override // org.jooq.SelectHavingStep
    public final SelectImpl having(SQL sql) {
        return having(DSL.condition(sql));
    }

    @Override // org.jooq.SelectHavingStep
    public final SelectImpl having(String sql) {
        return having(DSL.condition(sql));
    }

    @Override // org.jooq.SelectHavingStep
    public final SelectImpl having(String sql, Object... bindings) {
        return having(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.SelectHavingStep
    public final SelectImpl having(String sql, QueryPart... parts) {
        return having(DSL.condition(sql, parts));
    }

    @Override // org.jooq.SelectWindowStep
    public final SelectImpl window(WindowDefinition... definitions) {
        getQuery().addWindow(definitions);
        return this;
    }

    @Override // org.jooq.SelectWindowStep
    public final SelectImpl window(Collection<? extends WindowDefinition> definitions) {
        getQuery().addWindow(definitions);
        return this;
    }

    @Override // org.jooq.SelectQualifyStep
    public final SelectImpl qualify(Condition conditions) {
        this.conditionStep = ConditionStep.QUALIFY;
        getQuery().addQualify(conditions);
        return this;
    }

    @Override // org.jooq.SelectQualifyStep
    public final SelectImpl qualify(Condition... conditions) {
        this.conditionStep = ConditionStep.QUALIFY;
        getQuery().addQualify(conditions);
        return this;
    }

    @Override // org.jooq.SelectQualifyStep
    public final SelectImpl qualify(Collection<? extends Condition> conditions) {
        this.conditionStep = ConditionStep.QUALIFY;
        getQuery().addQualify(conditions);
        return this;
    }

    @Override // org.jooq.SelectQualifyStep
    public final SelectImpl qualify(Field<Boolean> condition) {
        return qualify(DSL.condition(condition));
    }

    @Override // org.jooq.SelectQualifyStep
    public final SelectImpl qualify(SQL sql) {
        return qualify(DSL.condition(sql));
    }

    @Override // org.jooq.SelectQualifyStep
    public final SelectImpl qualify(String sql) {
        return qualify(DSL.condition(sql));
    }

    @Override // org.jooq.SelectQualifyStep
    public final SelectImpl qualify(String sql, Object... bindings) {
        return qualify(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.SelectQualifyStep
    public final SelectImpl qualify(String sql, QueryPart... parts) {
        return qualify(DSL.condition(sql, parts));
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl on(Condition conditions) {
        this.conditionStep = ConditionStep.ON;
        if (this.joinTable == null) {
            this.joinConditions.addConditions(conditions);
        } else {
            this.joinConditions = new ConditionProviderImpl();
            this.joinConditions.addConditions(conditions);
            getQuery().addJoin(this.joinTable, this.joinType, this.joinHint, this.joinConditions);
            this.joinTable = null;
            this.joinPartitionBy = null;
            this.joinType = null;
            this.joinHint = null;
        }
        return this;
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl on(Condition... conditions) {
        this.conditionStep = ConditionStep.ON;
        if (this.joinTable == null) {
            this.joinConditions.addConditions(conditions);
        } else {
            this.joinConditions = new ConditionProviderImpl();
            this.joinConditions.addConditions(conditions);
            getQuery().addJoin(this.joinTable, this.joinType, this.joinHint, this.joinConditions);
            this.joinTable = null;
            this.joinPartitionBy = null;
            this.joinType = null;
            this.joinHint = null;
        }
        return this;
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl on(Field<Boolean> condition) {
        return on(DSL.condition(condition));
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl on(SQL sql) {
        return on(DSL.condition(sql));
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl on(String sql) {
        return on(DSL.condition(sql));
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl on(String sql, Object... bindings) {
        return on(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl on(String sql, QueryPart... parts) {
        return on(DSL.condition(sql, parts));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOnStep
    public final SelectImpl onKey() {
        this.conditionStep = ConditionStep.ON;
        if (this.joinTable != null) {
            getQuery().addJoinOnKey(this.joinTable, this.joinType, this.joinHint);
            this.joinConditions = ((JoinTable) getDelegate().getFrom().get(getDelegate().getFrom().size() - 1)).condition;
            this.joinTable = null;
            this.joinPartitionBy = null;
            this.joinType = null;
            this.joinHint = null;
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOnStep
    public final SelectImpl onKey(TableField<?, ?>... keyFields) {
        this.conditionStep = ConditionStep.ON;
        if (this.joinTable != null) {
            getQuery().addJoinOnKey(this.joinTable, this.joinType, this.joinHint, keyFields);
            this.joinConditions = ((JoinTable) getDelegate().getFrom().get(getDelegate().getFrom().size() - 1)).condition;
            this.joinTable = null;
            this.joinPartitionBy = null;
            this.joinType = null;
            this.joinHint = null;
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectOnStep
    public final SelectImpl onKey(ForeignKey<?, ?> key) {
        this.conditionStep = ConditionStep.ON;
        if (this.joinTable != null) {
            getQuery().addJoinOnKey(this.joinTable, this.joinType, this.joinHint, key);
            this.joinConditions = ((JoinTable) getDelegate().getFrom().get(getDelegate().getFrom().size() - 1)).condition;
            this.joinTable = null;
            this.joinPartitionBy = null;
            this.joinType = null;
            this.joinHint = null;
        }
        return this;
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl using(Field<?>... fields) {
        return using((Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.SelectOnStep
    public final SelectImpl using(Collection<? extends Field<?>> fields) {
        if (this.joinTable != null) {
            getQuery().addJoinUsing(this.joinTable, this.joinType, this.joinHint, fields);
            this.joinTable = null;
            this.joinPartitionBy = null;
            this.joinType = null;
            this.joinHint = null;
        }
        return this;
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(TableLike<?> table) {
        return innerJoin(table);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(Path<?> path) {
        return innerJoin(path);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl innerJoin(TableLike<?> table) {
        return join(table, JoinType.JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl innerJoin(Path<?> path) {
        return join((TableLike<?>) path, JoinType.JOIN).on(DSL.noCondition());
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftJoin(TableLike<?> table) {
        return leftOuterJoin(table);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftJoin(Path<?> path) {
        return leftOuterJoin(path);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftOuterJoin(TableLike<?> table) {
        return join(table, JoinType.LEFT_OUTER_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftOuterJoin(Path<?> path) {
        return join((TableLike<?>) path, JoinType.LEFT_OUTER_JOIN).on(DSL.noCondition());
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightJoin(TableLike<?> table) {
        return rightOuterJoin(table);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightJoin(Path<?> path) {
        return rightOuterJoin(path);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightOuterJoin(TableLike<?> table) {
        return join(table, JoinType.RIGHT_OUTER_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightOuterJoin(Path<?> path) {
        return join((TableLike<?>) path, JoinType.RIGHT_OUTER_JOIN).on(DSL.noCondition());
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullJoin(TableLike<?> table) {
        return fullOuterJoin(table);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullJoin(Path<?> path) {
        return fullOuterJoin(path);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullOuterJoin(TableLike<?> table) {
        return join(table, JoinType.FULL_OUTER_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullOuterJoin(Path<?> path) {
        return join((TableLike<?>) path, JoinType.FULL_OUTER_JOIN).on(DSL.noCondition());
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(TableLike<?> table, JoinType type) {
        return join(table, type, (QOM.JoinHint) null);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(TableLike<?> table, JoinType type, QOM.JoinHint hint) {
        switch (type) {
            case CROSS_JOIN:
            case NATURAL_JOIN:
            case NATURAL_LEFT_OUTER_JOIN:
            case NATURAL_RIGHT_OUTER_JOIN:
            case NATURAL_FULL_OUTER_JOIN:
            case CROSS_APPLY:
            case OUTER_APPLY:
                getQuery().addJoin(table, type, hint, new Condition[0]);
                this.joinTable = null;
                this.joinPartitionBy = null;
                this.joinType = null;
                this.joinHint = null;
                return this;
            default:
                this.conditionStep = ConditionStep.ON;
                this.joinTable = table;
                this.joinType = type;
                this.joinHint = hint;
                this.joinPartitionBy = null;
                this.joinConditions = null;
                return this;
        }
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl crossJoin(TableLike<?> table) {
        return join(table, JoinType.CROSS_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalLeftOuterJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_LEFT_OUTER_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalRightOuterJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_RIGHT_OUTER_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalFullOuterJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_FULL_OUTER_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftSemiJoin(TableLike<?> table) {
        return join(table, JoinType.LEFT_SEMI_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftSemiJoin(Path<?> path) {
        return join((TableLike<?>) path, JoinType.LEFT_SEMI_JOIN).on(DSL.noCondition());
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftAntiJoin(TableLike<?> table) {
        return join(table, JoinType.LEFT_ANTI_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftAntiJoin(Path<?> path) {
        return join((TableLike<?>) path, JoinType.LEFT_ANTI_JOIN).on(DSL.noCondition());
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl crossApply(TableLike<?> table) {
        return join(table, JoinType.CROSS_APPLY);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl outerApply(TableLike<?> table) {
        return join(table, JoinType.OUTER_APPLY);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl straightJoin(TableLike<?> table) {
        return join(table, JoinType.STRAIGHT_JOIN);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl straightJoin(Path<?> path) {
        return join((TableLike<?>) path, JoinType.STRAIGHT_JOIN).on(DSL.noCondition());
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(SQL sql) {
        return innerJoin(sql);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(String sql) {
        return innerJoin(sql);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(String sql, Object... bindings) {
        return innerJoin(sql, bindings);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(String sql, QueryPart... parts) {
        return innerJoin(sql, parts);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl join(Name name) {
        return innerJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl innerJoin(SQL sql) {
        return innerJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl innerJoin(String sql) {
        return innerJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl innerJoin(String sql, Object... bindings) {
        return innerJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl innerJoin(String sql, QueryPart... parts) {
        return innerJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl innerJoin(Name name) {
        return innerJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftJoin(SQL sql) {
        return leftOuterJoin(sql);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftJoin(String sql) {
        return leftOuterJoin(sql);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftJoin(String sql, Object... bindings) {
        return leftOuterJoin(sql, bindings);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftJoin(String sql, QueryPart... parts) {
        return leftOuterJoin(sql, parts);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftJoin(Name name) {
        return leftOuterJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftOuterJoin(SQL sql) {
        return leftOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftOuterJoin(String sql) {
        return leftOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftOuterJoin(String sql, Object... bindings) {
        return leftOuterJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftOuterJoin(String sql, QueryPart... parts) {
        return leftOuterJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl leftOuterJoin(Name name) {
        return leftOuterJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightJoin(SQL sql) {
        return rightOuterJoin(sql);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightJoin(String sql) {
        return rightOuterJoin(sql);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightJoin(String sql, Object... bindings) {
        return rightOuterJoin(sql, bindings);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightJoin(String sql, QueryPart... parts) {
        return rightOuterJoin(sql, parts);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightJoin(Name name) {
        return rightOuterJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightOuterJoin(SQL sql) {
        return rightOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightOuterJoin(String sql) {
        return rightOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightOuterJoin(String sql, Object... bindings) {
        return rightOuterJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightOuterJoin(String sql, QueryPart... parts) {
        return rightOuterJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl rightOuterJoin(Name name) {
        return rightOuterJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullJoin(SQL sql) {
        return fullOuterJoin(sql);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullJoin(String sql) {
        return fullOuterJoin(sql);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullJoin(String sql, Object... bindings) {
        return fullOuterJoin(sql, bindings);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullJoin(String sql, QueryPart... parts) {
        return fullOuterJoin(sql, parts);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullJoin(Name name) {
        return fullOuterJoin(name);
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullOuterJoin(SQL sql) {
        return fullOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullOuterJoin(String sql) {
        return fullOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullOuterJoin(String sql, Object... bindings) {
        return fullOuterJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullOuterJoin(String sql, QueryPart... parts) {
        return fullOuterJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl fullOuterJoin(Name name) {
        return fullOuterJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectJoinStep<R> crossJoin(SQL sql) {
        return crossJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectJoinStep<R> crossJoin(String sql) {
        return crossJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectJoinStep<R> crossJoin(String sql, Object... bindings) {
        return crossJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectJoinStep<R> crossJoin(String sql, QueryPart... parts) {
        return crossJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl crossJoin(Name name) {
        return crossJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalJoin(SQL sql) {
        return naturalJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalJoin(String sql) {
        return naturalJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalJoin(String sql, Object... bindings) {
        return naturalJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalJoin(String sql, QueryPart... parts) {
        return naturalJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalJoin(Name name) {
        return naturalJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalLeftOuterJoin(SQL sql) {
        return naturalLeftOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalLeftOuterJoin(String sql) {
        return naturalLeftOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalLeftOuterJoin(String sql, Object... bindings) {
        return naturalLeftOuterJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalLeftOuterJoin(String sql, QueryPart... parts) {
        return naturalLeftOuterJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalLeftOuterJoin(Name name) {
        return naturalLeftOuterJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalRightOuterJoin(SQL sql) {
        return naturalRightOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalRightOuterJoin(String sql) {
        return naturalRightOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalRightOuterJoin(String sql, Object... bindings) {
        return naturalRightOuterJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalRightOuterJoin(String sql, QueryPart... parts) {
        return naturalRightOuterJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalRightOuterJoin(Name name) {
        return naturalRightOuterJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalFullOuterJoin(SQL sql) {
        return naturalFullOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalFullOuterJoin(String sql) {
        return naturalFullOuterJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalFullOuterJoin(String sql, Object... bindings) {
        return naturalFullOuterJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalFullOuterJoin(String sql, QueryPart... parts) {
        return naturalFullOuterJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl naturalFullOuterJoin(Name name) {
        return naturalFullOuterJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl crossApply(SQL sql) {
        return crossApply((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl crossApply(String sql) {
        return crossApply((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl crossApply(String sql, Object... bindings) {
        return crossApply((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl crossApply(String sql, QueryPart... parts) {
        return crossApply((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl crossApply(Name name) {
        return crossApply((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl outerApply(SQL sql) {
        return outerApply((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl outerApply(String sql) {
        return outerApply((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl outerApply(String sql, Object... bindings) {
        return outerApply((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl outerApply(String sql, QueryPart... parts) {
        return outerApply((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl outerApply(Name name) {
        return outerApply((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl straightJoin(SQL sql) {
        return straightJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl straightJoin(String sql) {
        return straightJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl straightJoin(String sql, Object... bindings) {
        return straightJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl straightJoin(String sql, QueryPart... parts) {
        return straightJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.SelectJoinStep
    public final SelectImpl straightJoin(Name name) {
        return straightJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> maxRows(int i) {
        return (CloseableResultQuery<R>) getDelegate().maxRows(i);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> fetchSize(int i) {
        return (CloseableResultQuery<R>) getDelegate().fetchSize(i);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetConcurrency(int i) {
        return (CloseableResultQuery<R>) getDelegate().resultSetConcurrency(i);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetType(int i) {
        return (CloseableResultQuery<R>) getDelegate().resultSetType(i);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetHoldability(int i) {
        return (CloseableResultQuery<R>) getDelegate().resultSetHoldability(i);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(Field<?>... fieldArr) {
        return (CloseableResultQuery<R>) getDelegate().intern(fieldArr);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(int... iArr) {
        return (CloseableResultQuery<R>) getDelegate().intern(iArr);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(String... strArr) {
        return (CloseableResultQuery<R>) getDelegate().intern(strArr);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(Name... nameArr) {
        return (CloseableResultQuery<R>) getDelegate().intern(nameArr);
    }

    @Override // org.jooq.ResultQuery
    public final Class<? extends R> getRecordType() {
        return (Class<? extends R>) getDelegate().getRecordType();
    }

    @Override // org.jooq.Select
    public final List<Field<?>> getSelect() {
        return getDelegate().getSelect();
    }

    @Override // org.jooq.ResultQuery
    public final Result<R> getResult() {
        return (Result<R>) getDelegate().getResult();
    }

    @Override // org.jooq.ResultQuery
    public final Result<R> fetch() {
        return (Result<R>) getDelegate().fetch();
    }

    @Override // org.jooq.Publisher, java.util.concurrent.Flow.Publisher
    public final void subscribe(Flow.Subscriber<? super R> subscriber) {
        getDelegate().subscribe(subscriber);
    }

    @Override // org.jooq.impl.ResultQueryTrait, org.reactivestreams.Publisher
    public final void subscribe(Subscriber<? super R> subscriber) {
        getDelegate().subscribe(subscriber);
    }

    @Override // org.jooq.impl.ResultQueryTrait, org.jooq.ResultQuery
    public final Cursor<R> fetchLazy() {
        return (Cursor<R>) getDelegate().fetchLazy();
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Cursor<R> fetchLazyNonAutoClosing() {
        return (Cursor<R>) getDelegate().fetchLazyNonAutoClosing();
    }

    @Override // org.jooq.impl.ResultQueryTrait, org.jooq.ResultQuery
    public final Results fetchMany() {
        return getDelegate().fetchMany();
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset() {
        return getDelegate().asMultiset();
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(String alias) {
        return getDelegate().asMultiset(alias);
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(Name alias) {
        return getDelegate().asMultiset(alias);
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(Field<?> alias) {
        return getDelegate().asMultiset(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable() {
        return getDelegate().asTable();
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias) {
        return getDelegate().asTable(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias, String... fieldAliases) {
        return getDelegate().asTable(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias, Collection<? extends String> fieldAliases) {
        return getDelegate().asTable(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name alias) {
        return getDelegate().asTable(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name alias, Name... fieldAliases) {
        return getDelegate().asTable(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name alias, Collection<? extends Name> fieldAliases) {
        return getDelegate().asTable(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> alias) {
        return getDelegate().asTable(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> alias, Field<?>... fieldAliases) {
        return getDelegate().asTable(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> alias, Collection<? extends Field<?>> fieldAliases) {
        return getDelegate().asTable(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias, java.util.function.Function<? super Field<?>, ? extends String> aliasFunction) {
        return getDelegate().asTable(alias, aliasFunction);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> aliasFunction) {
        return getDelegate().asTable(alias, aliasFunction);
    }

    @Override // org.jooq.FieldLike
    public final <T> Field<T> asField() {
        return getDelegate().asField();
    }

    @Override // org.jooq.FieldLike
    public final <T> Field<T> asField(String alias) {
        return getDelegate().asField(alias);
    }

    @Override // org.jooq.FieldLike
    public final <T> Field<T> asField(java.util.function.Function<? super Field<T>, ? extends String> aliasFunction) {
        return getDelegate().asField(aliasFunction);
    }

    @Override // org.jooq.impl.ResultQueryTrait, org.jooq.Fields
    public final Row fieldsRow() {
        return getDelegate().fieldsRow();
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final <X extends Record> CloseableResultQuery<X> coerce(Table<X> table) {
        return getDelegate().coerce((Table) table);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<Record> coerce(Collection<? extends Field<?>> fields) {
        return getDelegate().coerce(fields);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition compare(Comparator comparator, R record) {
        if (Tools.degree(this) == 1) {
            return DSL.field(this).compare(comparator, (Comparator) record.get(0, field(0).getType()));
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition compare(Comparator comparator, Select<? extends R> select) {
        if (Tools.degree(this) == 1) {
            return DSL.field(this).compare(comparator, (Select) select);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends R> select) {
        if (Tools.degree(this) == 1) {
            return DSL.field(this).compare(comparator, (QuantifiedSelect) select);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition eq(R record) {
        return compare(Comparator.EQUALS, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition eq(Select<? extends R> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition eq(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition equal(R record) {
        return compare(Comparator.EQUALS, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition equal(Select<? extends R> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition equal(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition ne(R record) {
        return compare(Comparator.NOT_EQUALS, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition ne(Select<? extends R> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition ne(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notEqual(R record) {
        return compare(Comparator.NOT_EQUALS, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notEqual(Select<? extends R> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notEqual(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lt(R record) {
        return compare(Comparator.LESS, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lt(Select<? extends R> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lt(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lessThan(R record) {
        return compare(Comparator.LESS, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lessThan(Select<? extends R> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lessThan(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition le(R record) {
        return compare(Comparator.LESS_OR_EQUAL, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition le(Select<? extends R> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition le(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lessOrEqual(R record) {
        return compare(Comparator.LESS_OR_EQUAL, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lessOrEqual(Select<? extends R> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition lessOrEqual(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition gt(R record) {
        return compare(Comparator.GREATER, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition gt(Select<? extends R> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition gt(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition greaterThan(R record) {
        return compare(Comparator.GREATER, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition greaterThan(Select<? extends R> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition greaterThan(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition ge(R record) {
        return compare(Comparator.GREATER_OR_EQUAL, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition ge(Select<? extends R> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition ge(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition greaterOrEqual(R record) {
        return compare(Comparator.GREATER_OR_EQUAL, (Comparator) record);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition greaterOrEqual(Select<? extends R> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition greaterOrEqual(QuantifiedSelect<? extends R> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    private final Object[] values(int index, R... records) {
        Class<?> type = field(0).getType();
        return Tools.map(records, r -> {
            return r.get(index, type);
        }, x$0 -> {
            return new Object[x$0];
        });
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition in(R... records) {
        if (Tools.degree(this) == 1) {
            return DSL.field(this).in(values(0, records));
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition in(Select<? extends R> select) {
        if (Tools.degree(this) == 1) {
            return DSL.field(this).in(select);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notIn(R... records) {
        if (Tools.degree(this) == 1) {
            return DSL.field(this).notIn(values(0, records));
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notIn(Select<? extends R> select) {
        if (Tools.degree(this) == 1) {
            return DSL.field(this).notIn(select);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition isDistinctFrom(R record) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition isDistinctFrom(Select<? extends R> select) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition isDistinctFrom(QuantifiedSelect<? extends R> select) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition isNotDistinctFrom(R record) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition isNotDistinctFrom(Select<? extends R> select) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition isNotDistinctFrom(QuantifiedSelect<? extends R> select) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final BetweenAndStep<R> between(R minValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition between(R minValue, R maxValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final BetweenAndStep<R> between(Select<? extends R> minValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition between(Select<? extends R> minValue, Select<? extends R> maxValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final BetweenAndStepR<R> betweenSymmetric(R minValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition betweenSymmetric(R minValue, R maxValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final BetweenAndStepR<R> betweenSymmetric(Select<? extends R> minValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition betweenSymmetric(Select<? extends R> minValue, Select<? extends R> maxValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final BetweenAndStepR<R> notBetween(R minValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notBetween(R minValue, R maxValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final BetweenAndStepR<R> notBetween(Select<? extends R> minValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notBetween(Select<? extends R> minValue, Select<? extends R> maxValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final BetweenAndStepR<R> notBetweenSymmetric(R minValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notBetweenSymmetric(R minValue, R maxValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final BetweenAndStepR<R> notBetweenSymmetric(Select<? extends R> minValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition notBetweenSymmetric(Select<? extends R> minValue, Select<? extends R> maxValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition isNull() {
        return new SelectIsNull(this);
    }

    @Override // org.jooq.SelectCorrelatedSubqueryStep
    public final Condition isNotNull() {
        return new SelectIsNotNull(this);
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields(ThrowingSupplier<? extends ResultSetMetaData, SQLException> rs) throws SQLException {
        return getDelegate().getFields(rs);
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields() {
        return getDelegate().getFields();
    }

    @Override // org.jooq.Select
    public final QOM.With $with() {
        return getDelegate().$with();
    }

    @Override // org.jooq.Select
    public final Select<?> $with(QOM.With newWith) {
        return getDelegate().$with(newWith);
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<? extends SelectFieldOrAsterisk> $select() {
        return getDelegate().$select();
    }

    @Override // org.jooq.Select
    public final Select<?> $select(Collection<? extends SelectFieldOrAsterisk> newSelect) {
        return getDelegate().$select(newSelect);
    }

    @Override // org.jooq.Select
    public final boolean $distinct() {
        return getDelegate().$distinct();
    }

    @Override // org.jooq.Select
    public final Select<R> $distinct(boolean newDistinct) {
        return getDelegate().$distinct(newDistinct);
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<? extends SelectFieldOrAsterisk> $distinctOn() {
        return getDelegate().$distinctOn();
    }

    @Override // org.jooq.Select
    public final Select<R> $distinctOn(Collection<? extends SelectFieldOrAsterisk> newDistinctOn) {
        return getDelegate().$distinctOn(newDistinctOn);
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<? extends Table<?>> $from() {
        return getDelegate().$from();
    }

    @Override // org.jooq.Select
    public final Select<R> $from(Collection<? extends Table<?>> newFrom) {
        return getDelegate().$from(newFrom);
    }

    @Override // org.jooq.Select
    public final Condition $where() {
        return getDelegate().$where();
    }

    @Override // org.jooq.Select
    public final Select<R> $where(Condition newWhere) {
        return getDelegate().$where(newWhere);
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<? extends GroupField> $groupBy() {
        return getDelegate().$groupBy();
    }

    @Override // org.jooq.Select
    public final Select<R> $groupBy(Collection<? extends GroupField> newGroupBy) {
        return getDelegate().$groupBy(newGroupBy);
    }

    @Override // org.jooq.Select
    public final boolean $groupByDistinct() {
        return getDelegate().$groupByDistinct();
    }

    @Override // org.jooq.Select
    public final Select<R> $groupByDistinct(boolean newGroupByDistinct) {
        return getDelegate().$groupByDistinct(newGroupByDistinct);
    }

    @Override // org.jooq.Select
    public final Condition $having() {
        return getDelegate().$having();
    }

    @Override // org.jooq.Select
    public final Select<R> $having(Condition newHaving) {
        return getDelegate().$having(newHaving);
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<? extends WindowDefinition> $window() {
        return getDelegate().$window();
    }

    @Override // org.jooq.Select
    public final Select<R> $window(Collection<? extends WindowDefinition> newWindow) {
        return getDelegate().$window(newWindow);
    }

    @Override // org.jooq.Select
    public final Condition $qualify() {
        return getDelegate().$qualify();
    }

    @Override // org.jooq.Select
    public final Select<R> $qualify(Condition newQualify) {
        return getDelegate().$qualify(newQualify);
    }

    @Override // org.jooq.Select
    public QOM.UnmodifiableList<? extends SortField<?>> $orderBy() {
        return getDelegate().$orderBy();
    }

    @Override // org.jooq.Select
    public final Select<R> $orderBy(Collection<? extends SortField<?>> newOrderBy) {
        return getDelegate().$orderBy(newOrderBy);
    }

    @Override // org.jooq.Select
    public final Field<? extends Number> $limit() {
        return getDelegate().$limit();
    }

    @Override // org.jooq.Select
    public final Select<R> $limit(Field<? extends Number> newLimit) {
        return getDelegate().$limit(newLimit);
    }

    @Override // org.jooq.Select
    public final boolean $limitPercent() {
        return getDelegate().$limitPercent();
    }

    @Override // org.jooq.Select
    public final Select<R> $limitPercent(boolean newLimitPercent) {
        return getDelegate().$limitPercent(newLimitPercent);
    }

    @Override // org.jooq.Select
    public final boolean $limitWithTies() {
        return getDelegate().$limitWithTies();
    }

    @Override // org.jooq.Select
    public final Select<R> $limitWithTies(boolean newLimitWithTies) {
        return getDelegate().$limitWithTies(newLimitWithTies);
    }

    @Override // org.jooq.Select
    public final Field<? extends Number> $offset() {
        return getDelegate().$offset();
    }

    @Override // org.jooq.Select
    public final Select<R> $offset(Field<? extends Number> newOffset) {
        return getDelegate().$offset(newOffset);
    }
}
