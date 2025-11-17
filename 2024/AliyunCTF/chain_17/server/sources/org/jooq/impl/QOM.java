package org.jooq.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collector;
import org.apache.tomcat.util.net.Constants;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.AggregateFunction;
import org.jooq.Catalog;
import org.jooq.CheckReturnValue;
import org.jooq.Collation;
import org.jooq.Comment;
import org.jooq.CommonTableExpression;
import org.jooq.Condition;
import org.jooq.Constraint;
import org.jooq.DDLQuery;
import org.jooq.DMLQuery;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
import org.jooq.Function0;
import org.jooq.Function2;
import org.jooq.Function3;
import org.jooq.Function4;
import org.jooq.GroupField;
import org.jooq.Index;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.JSONEntry;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.Param;
import org.jooq.Privilege;
import org.jooq.Query;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Role;
import org.jooq.Row;
import org.jooq.RowCountQuery;
import org.jooq.RowId;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Sequence;
import org.jooq.SortField;
import org.jooq.Statement;
import org.jooq.Table;
import org.jooq.TableElement;
import org.jooq.TableLike;
import org.jooq.Type;
import org.jooq.WindowDefinition;
import org.jooq.WindowSpecification;
import org.jooq.XML;
import org.jooq.XMLAttributes;
import org.jooq.types.DayToSecond;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.web.servlet.tags.form.TextareaTag;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM.class */
public final class QOM {

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Add.class */
    public interface Add<T> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, Add<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Aliasable.class */
    public interface Aliasable<Q extends QueryPart> extends QueryPart {
        @NotNull
        Q $aliased();

        @Nullable
        Name $alias();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$AlterDatabase.class */
    public interface AlterDatabase extends DDLQuery {
        @NotNull
        Catalog $database();

        boolean $ifExists();

        @NotNull
        Catalog $renameTo();

        @CheckReturnValue
        @NotNull
        AlterDatabase $database(Catalog catalog);

        @CheckReturnValue
        @NotNull
        AlterDatabase $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterDatabase $renameTo(Catalog catalog);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$AlterDomain.class */
    public interface AlterDomain<T> extends DDLQuery {
        @NotNull
        Domain<T> $domain();

        boolean $ifExists();

        @Nullable
        Constraint $addConstraint();

        @Nullable
        Constraint $dropConstraint();

        boolean $dropConstraintIfExists();

        @Nullable
        Domain<?> $renameTo();

        @Nullable
        Constraint $renameConstraint();

        boolean $renameConstraintIfExists();

        @Nullable
        Field<T> $setDefault();

        boolean $dropDefault();

        boolean $setNotNull();

        boolean $dropNotNull();

        @Nullable
        Cascade $cascade();

        @Nullable
        Constraint $renameConstraintTo();

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $domain(Domain<T> domain);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $addConstraint(Constraint constraint);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $dropConstraint(Constraint constraint);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $dropConstraintIfExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $renameTo(Domain<?> domain);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $renameConstraint(Constraint constraint);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $renameConstraintIfExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $setDefault(Field<T> field);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $dropDefault(boolean z);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $setNotNull(boolean z);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $dropNotNull(boolean z);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $cascade(Cascade cascade);

        @CheckReturnValue
        @NotNull
        AlterDomain<T> $renameConstraintTo(Constraint constraint);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$AlterIndex.class */
    public interface AlterIndex extends DDLQuery {
        @NotNull
        Index $index();

        boolean $ifExists();

        @Nullable
        Table<?> $on();

        @NotNull
        Index $renameTo();

        @CheckReturnValue
        @NotNull
        AlterIndex $index(Index index);

        @CheckReturnValue
        @NotNull
        AlterIndex $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterIndex $on(Table<?> table);

        @CheckReturnValue
        @NotNull
        AlterIndex $renameTo(Index index);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$AlterSchema.class */
    public interface AlterSchema extends DDLQuery {
        @NotNull
        Schema $schema();

        boolean $ifExists();

        @NotNull
        Schema $renameTo();

        @CheckReturnValue
        @NotNull
        AlterSchema $schema(Schema schema);

        @CheckReturnValue
        @NotNull
        AlterSchema $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterSchema $renameTo(Schema schema);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$AlterSequence.class */
    public interface AlterSequence<T extends Number> extends DDLQuery {
        @NotNull
        Sequence<T> $sequence();

        boolean $ifExists();

        @Nullable
        Sequence<?> $renameTo();

        boolean $restart();

        @Nullable
        Field<T> $restartWith();

        @Nullable
        Field<T> $startWith();

        @Nullable
        Field<T> $incrementBy();

        @Nullable
        Field<T> $minvalue();

        boolean $noMinvalue();

        @Nullable
        Field<T> $maxvalue();

        boolean $noMaxvalue();

        @Nullable
        CycleOption $cycle();

        @Nullable
        Field<T> $cache();

        boolean $noCache();

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $sequence(Sequence<T> sequence);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $renameTo(Sequence<?> sequence);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $restart(boolean z);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $restartWith(Field<T> field);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $startWith(Field<T> field);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $incrementBy(Field<T> field);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $minvalue(Field<T> field);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $noMinvalue(boolean z);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $maxvalue(Field<T> field);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $noMaxvalue(boolean z);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $cycle(CycleOption cycleOption);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $cache(Field<T> field);

        @CheckReturnValue
        @NotNull
        AlterSequence<T> $noCache(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$AlterType.class */
    public interface AlterType extends DDLQuery {
        @NotNull
        Name $type();

        boolean $ifExists();

        @Nullable
        Name $renameTo();

        @Nullable
        Schema $setSchema();

        @Nullable
        Field<String> $addValue();

        @Nullable
        Field<String> $renameValue();

        @Nullable
        Field<String> $renameValueTo();

        @CheckReturnValue
        @NotNull
        AlterType $type(Name name);

        @CheckReturnValue
        @NotNull
        AlterType $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterType $renameTo(Name name);

        @CheckReturnValue
        @NotNull
        AlterType $setSchema(Schema schema);

        @CheckReturnValue
        @NotNull
        AlterType $addValue(Field<String> field);

        @CheckReturnValue
        @NotNull
        AlterType $renameValue(Field<String> field);

        @CheckReturnValue
        @NotNull
        AlterType $renameValueTo(Field<String> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$AlterView.class */
    public interface AlterView extends DDLQuery {
        @NotNull
        Table<?> $view();

        @NotNull
        UnmodifiableList<? extends Field<?>> $fields();

        boolean $materialized();

        boolean $ifExists();

        @Nullable
        Comment $comment();

        @Nullable
        Table<?> $renameTo();

        @Nullable
        Select<?> $as();

        @CheckReturnValue
        @NotNull
        AlterView $view(Table<?> table);

        @CheckReturnValue
        @NotNull
        AlterView $fields(Collection<? extends Field<?>> collection);

        @CheckReturnValue
        @NotNull
        AlterView $materialized(boolean z);

        @CheckReturnValue
        @NotNull
        AlterView $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        AlterView $comment(Comment comment);

        @CheckReturnValue
        @NotNull
        AlterView $renameTo(Table<?> table);

        @CheckReturnValue
        @NotNull
        AlterView $as(Select<?> select);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$And.class */
    public interface And extends UCommutativeOperator<Condition, And>, CombinedCondition<And> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$AnyValue.class */
    public interface AnyValue<T> extends AggregateFunction<T> {
        @NotNull
        Field<T> $field();

        @CheckReturnValue
        @NotNull
        AnyValue<T> $field(Field<T> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Array.class */
    public interface Array<T> extends Field<T[]> {
        @NotNull
        UnmodifiableList<? extends Field<T>> $elements();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayAppend.class */
    public interface ArrayAppend<T> extends UOperator2<Field<T[]>, Field<T>, ArrayAppend<T>>, Field<T[]> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayConcat.class */
    public interface ArrayConcat<T> extends UReturnsNullOnNullInput, UOperator2<Field<T[]>, Field<T[]>, ArrayConcat<T>>, Field<T[]> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayOverlap.class */
    public interface ArrayOverlap<T> extends UReturnsNullOnNullInput, UOperator2<Field<T[]>, Field<T[]>, ArrayOverlap<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayPrepend.class */
    public interface ArrayPrepend<T> extends UOperator2<Field<T>, Field<T[]>, ArrayPrepend<T>>, Field<T[]> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayQuery.class */
    public interface ArrayQuery<T> extends Field<T[]> {
        @NotNull
        Select<? extends Record1<T>> $query();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayRemove.class */
    public interface ArrayRemove<T> extends UOperator2<Field<T[]>, Field<T>, ArrayRemove<T>>, Field<T[]> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayReplace.class */
    public interface ArrayReplace<T> extends UOperator3<Field<T[]>, Field<T>, Field<T>, ArrayReplace<T>>, Field<T[]> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Avg.class */
    public interface Avg extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();

        boolean $distinct();

        @CheckReturnValue
        @NotNull
        Avg $field(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        Avg $distinct(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Between.class */
    public interface Between<T> extends Condition, UOperator3<Field<T>, Field<T>, Field<T>, Between<T>> {
        boolean $symmetric();

        @NotNull
        Between<T> $symmetric(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitAnd.class */
    public interface BitAnd<T extends Number> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, BitAnd<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitAndAgg.class */
    public interface BitAndAgg<T extends Number> extends AggregateFunction<T> {
        @NotNull
        Field<T> $value();

        @CheckReturnValue
        @NotNull
        BitAndAgg<T> $value(Field<T> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitNand.class */
    public interface BitNand<T extends Number> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, BitNand<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitNandAgg.class */
    public interface BitNandAgg<T extends Number> extends AggregateFunction<T> {
        @NotNull
        Field<T> $value();

        @CheckReturnValue
        @NotNull
        BitNandAgg<T> $value(Field<T> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitNor.class */
    public interface BitNor<T extends Number> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, BitNor<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitNorAgg.class */
    public interface BitNorAgg<T extends Number> extends AggregateFunction<T> {
        @NotNull
        Field<T> $value();

        @CheckReturnValue
        @NotNull
        BitNorAgg<T> $value(Field<T> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitNot.class */
    public interface BitNot<T extends Number> extends UReturnsNullOnNullInput, UOperator1<Field<T>, BitNot<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitOr.class */
    public interface BitOr<T extends Number> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, BitOr<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitOrAgg.class */
    public interface BitOrAgg<T extends Number> extends AggregateFunction<T> {
        @NotNull
        Field<T> $value();

        @CheckReturnValue
        @NotNull
        BitOrAgg<T> $value(Field<T> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitXNor.class */
    public interface BitXNor<T extends Number> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, BitXNor<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitXNorAgg.class */
    public interface BitXNorAgg<T extends Number> extends AggregateFunction<T> {
        @NotNull
        Field<T> $value();

        @CheckReturnValue
        @NotNull
        BitXNorAgg<T> $value(Field<T> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitXor.class */
    public interface BitXor<T extends Number> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, BitXor<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitXorAgg.class */
    public interface BitXorAgg<T extends Number> extends AggregateFunction<T> {
        @NotNull
        Field<T> $value();

        @CheckReturnValue
        @NotNull
        BitXorAgg<T> $value(Field<T> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BoolAnd.class */
    public interface BoolAnd extends AggregateFunction<Boolean> {
        @NotNull
        Condition $condition();

        @CheckReturnValue
        @NotNull
        BoolAnd $condition(Condition condition);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BoolOr.class */
    public interface BoolOr extends AggregateFunction<Boolean> {
        @NotNull
        Condition $condition();

        @CheckReturnValue
        @NotNull
        BoolOr $condition(Condition condition);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Cast.class */
    public interface Cast<T> extends Field<T> {
        @NotNull
        Field<?> $field();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Check.class */
    public interface Check extends Constraint {
        @NotNull
        Condition $condition();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Choose.class */
    public interface Choose<T> extends Field<T>, UOperator2<Field<Integer>, UnmodifiableList<? extends Field<T>>, Choose<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Coalesce.class */
    public interface Coalesce<T> extends Field<T>, UOperator1<UnmodifiableList<? extends Field<T>>, Coalesce<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Coerce.class */
    public interface Coerce<T> extends Field<T> {
        @NotNull
        Field<?> $field();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Collated.class */
    public interface Collated extends Field<String> {
        @NotNull
        Field<?> $field();

        @NotNull
        Collation $collation();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CombinedCondition.class */
    public interface CombinedCondition<R extends CombinedCondition<R>> extends Condition, UCommutativeOperator<Condition, R> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CommentOn.class */
    public interface CommentOn extends DDLQuery {
        @Nullable
        Table<?> $table();

        boolean $isView();

        boolean $isMaterializedView();

        @Nullable
        Field<?> $field();

        @NotNull
        Comment $comment();

        @CheckReturnValue
        @NotNull
        CommentOn $table(Table<?> table);

        @CheckReturnValue
        @NotNull
        CommentOn $isView(boolean z);

        @CheckReturnValue
        @NotNull
        CommentOn $isMaterializedView(boolean z);

        @CheckReturnValue
        @NotNull
        CommentOn $field(Field<?> field);

        @CheckReturnValue
        @NotNull
        CommentOn $comment(Comment comment);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Commit.class */
    public interface Commit extends UEmpty, Query {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CompareCondition.class */
    public interface CompareCondition<T, R extends CompareCondition<T, R>> extends Condition, UOperator2<Field<T>, Field<T>, R> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Concat.class */
    public interface Concat extends Field<String>, UOperator1<UnmodifiableList<? extends Field<?>>, Concat> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Convert.class */
    public interface Convert<T> extends Field<T> {
        @NotNull
        Field<?> $field();

        int $style();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Corr.class */
    public interface Corr extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        Corr $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        Corr $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Count.class */
    public interface Count extends AggregateFunction<Integer> {
        @NotNull
        Field<?> $field();

        boolean $distinct();

        @CheckReturnValue
        @NotNull
        Count $field(Field<?> field);

        @CheckReturnValue
        @NotNull
        Count $distinct(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CountTable.class */
    public interface CountTable extends AggregateFunction<Integer> {
        @NotNull
        Table<?> $table();

        boolean $distinct();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CovarPop.class */
    public interface CovarPop extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        CovarPop $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        CovarPop $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CovarSamp.class */
    public interface CovarSamp extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        CovarSamp $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        CovarSamp $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CreateDatabase.class */
    public interface CreateDatabase extends DDLQuery {
        @NotNull
        Catalog $database();

        boolean $ifNotExists();

        @CheckReturnValue
        @NotNull
        CreateDatabase $database(Catalog catalog);

        @CheckReturnValue
        @NotNull
        CreateDatabase $ifNotExists(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CreateDomain.class */
    public interface CreateDomain<T> extends DDLQuery {
        @NotNull
        Domain<?> $domain();

        boolean $ifNotExists();

        @NotNull
        DataType<T> $dataType();

        @Nullable
        Field<T> $default_();

        @NotNull
        UnmodifiableList<? extends Constraint> $constraints();

        @CheckReturnValue
        @NotNull
        CreateDomain<T> $domain(Domain<?> domain);

        @CheckReturnValue
        @NotNull
        CreateDomain<T> $ifNotExists(boolean z);

        @CheckReturnValue
        @NotNull
        CreateDomain<T> $dataType(DataType<T> dataType);

        @CheckReturnValue
        @NotNull
        CreateDomain<T> $default_(Field<T> field);

        @CheckReturnValue
        @NotNull
        CreateDomain<T> $constraints(Collection<? extends Constraint> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CreateIndex.class */
    public interface CreateIndex extends DDLQuery {
        boolean $unique();

        @Nullable
        Index $index();

        boolean $ifNotExists();

        @Nullable
        Table<?> $table();

        @NotNull
        UnmodifiableList<? extends OrderField<?>> $on();

        @NotNull
        UnmodifiableList<? extends Field<?>> $include();

        @Nullable
        Condition $where();

        boolean $excludeNullKeys();

        @CheckReturnValue
        @NotNull
        CreateIndex $unique(boolean z);

        @CheckReturnValue
        @NotNull
        CreateIndex $index(Index index);

        @CheckReturnValue
        @NotNull
        CreateIndex $ifNotExists(boolean z);

        @CheckReturnValue
        @NotNull
        CreateIndex $table(Table<?> table);

        @CheckReturnValue
        @NotNull
        CreateIndex $on(Collection<? extends OrderField<?>> collection);

        @CheckReturnValue
        @NotNull
        CreateIndex $include(Collection<? extends Field<?>> collection);

        @CheckReturnValue
        @NotNull
        CreateIndex $where(Condition condition);

        @CheckReturnValue
        @NotNull
        CreateIndex $excludeNullKeys(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CreateSchema.class */
    public interface CreateSchema extends DDLQuery {
        @NotNull
        Schema $schema();

        boolean $ifNotExists();

        @CheckReturnValue
        @NotNull
        CreateSchema $schema(Schema schema);

        @CheckReturnValue
        @NotNull
        CreateSchema $ifNotExists(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CreateSequence.class */
    public interface CreateSequence extends DDLQuery {
        @NotNull
        Sequence<?> $sequence();

        boolean $ifNotExists();

        @Nullable
        Field<? extends Number> $startWith();

        @Nullable
        Field<? extends Number> $incrementBy();

        @Nullable
        Field<? extends Number> $minvalue();

        boolean $noMinvalue();

        @Nullable
        Field<? extends Number> $maxvalue();

        boolean $noMaxvalue();

        @Nullable
        CycleOption $cycle();

        @Nullable
        Field<? extends Number> $cache();

        boolean $noCache();

        @CheckReturnValue
        @NotNull
        CreateSequence $sequence(Sequence<?> sequence);

        @CheckReturnValue
        @NotNull
        CreateSequence $ifNotExists(boolean z);

        @CheckReturnValue
        @NotNull
        CreateSequence $startWith(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        CreateSequence $incrementBy(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        CreateSequence $minvalue(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        CreateSequence $noMinvalue(boolean z);

        @CheckReturnValue
        @NotNull
        CreateSequence $maxvalue(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        CreateSequence $noMaxvalue(boolean z);

        @CheckReturnValue
        @NotNull
        CreateSequence $cycle(CycleOption cycleOption);

        @CheckReturnValue
        @NotNull
        CreateSequence $cache(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        CreateSequence $noCache(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CreateTable.class */
    public interface CreateTable extends DDLQuery {
        @NotNull
        Table<?> $table();

        boolean $temporary();

        boolean $ifNotExists();

        @NotNull
        UnmodifiableList<? extends TableElement> $tableElements();

        @Nullable
        Select<?> $select();

        @Nullable
        WithOrWithoutData $withData();

        @Nullable
        TableCommitAction $onCommit();

        @Nullable
        Comment $comment();

        @Nullable
        SQL $storage();

        @CheckReturnValue
        @NotNull
        CreateTable $table(Table<?> table);

        @CheckReturnValue
        @NotNull
        CreateTable $temporary(boolean z);

        @CheckReturnValue
        @NotNull
        CreateTable $ifNotExists(boolean z);

        @CheckReturnValue
        @NotNull
        CreateTable $tableElements(Collection<? extends TableElement> collection);

        @CheckReturnValue
        @NotNull
        CreateTable $select(Select<?> select);

        @CheckReturnValue
        @NotNull
        CreateTable $withData(WithOrWithoutData withOrWithoutData);

        @CheckReturnValue
        @NotNull
        CreateTable $onCommit(TableCommitAction tableCommitAction);

        @CheckReturnValue
        @NotNull
        CreateTable $comment(Comment comment);

        @CheckReturnValue
        @NotNull
        CreateTable $storage(SQL sql);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CreateType.class */
    public interface CreateType extends DDLQuery {
        @NotNull
        Type<?> $type();

        boolean $ifNotExists();

        @NotNull
        UnmodifiableList<? extends Field<String>> $values();

        @NotNull
        UnmodifiableList<? extends Field<?>> $attributes();

        @CheckReturnValue
        @NotNull
        CreateType $type(Type<?> type);

        @CheckReturnValue
        @NotNull
        CreateType $ifNotExists(boolean z);

        @CheckReturnValue
        @NotNull
        CreateType $values(Collection<? extends Field<String>> collection);

        @CheckReturnValue
        @NotNull
        CreateType $attributes(Collection<? extends Field<?>> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CreateView.class */
    public interface CreateView<R extends Record> extends DDLQuery {
        @NotNull
        Table<?> $view();

        @NotNull
        UnmodifiableList<? extends Field<?>> $fields();

        boolean $orReplace();

        boolean $materialized();

        boolean $ifNotExists();

        @Nullable
        ResultQuery<? extends R> $query();

        @CheckReturnValue
        @NotNull
        CreateView<R> $view(Table<?> table);

        @CheckReturnValue
        @NotNull
        CreateView<R> $fields(Collection<? extends Field<?>> collection);

        @CheckReturnValue
        @NotNull
        CreateView<R> $orReplace(boolean z);

        @CheckReturnValue
        @NotNull
        CreateView<R> $materialized(boolean z);

        @CheckReturnValue
        @NotNull
        CreateView<R> $ifNotExists(boolean z);

        @CheckReturnValue
        @NotNull
        CreateView<R> $query(ResultQuery<? extends R> resultQuery);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CrossApply.class */
    public interface CrossApply<R extends Record> extends JoinTable<R, CrossApply<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CrossJoin.class */
    public interface CrossJoin<R extends Record> extends JoinTable<R, CrossJoin<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Cube.class */
    public interface Cube extends GroupField, UOperator1<UnmodifiableList<? extends FieldOrRow>, Cube> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CumeDist.class */
    public interface CumeDist extends WindowFunction<BigDecimal> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CurrentCatalog.class */
    public interface CurrentCatalog extends UOperator0<CurrentCatalog>, Field<String> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CurrentDate.class */
    public interface CurrentDate<T> extends Field<T>, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CurrentSchema.class */
    public interface CurrentSchema extends UOperator0<CurrentSchema>, Field<String> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CurrentTime.class */
    public interface CurrentTime<T> extends Field<T>, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CurrentTimestamp.class */
    public interface CurrentTimestamp<T> extends Field<T>, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CurrentUser.class */
    public interface CurrentUser extends UOperator0<CurrentUser>, Field<String> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DataChangeDeltaTable.class */
    public interface DataChangeDeltaTable<R extends Record> extends Table<R> {
        @NotNull
        ResultOption $resultOption();

        @NotNull
        DMLQuery<R> $query();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Default.class */
    public interface Default<T> extends Field<T>, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Delete.class */
    public interface Delete<R extends Record> extends DMLQuery<R> {
        @Nullable
        With $with();

        @NotNull
        Table<R> $from();

        @CheckReturnValue
        @NotNull
        Delete<?> $from(Table<?> table);

        @NotNull
        UnmodifiableList<? extends Table<?>> $using();

        @CheckReturnValue
        @NotNull
        Delete<R> $using(Collection<? extends Table<?>> collection);

        @Nullable
        Condition $where();

        @CheckReturnValue
        @NotNull
        Delete<R> $where(Condition condition);

        @NotNull
        UnmodifiableList<? extends SortField<?>> $orderBy();

        @CheckReturnValue
        @NotNull
        Delete<R> $orderBy(Collection<? extends SortField<?>> collection);

        @Nullable
        Field<? extends Number> $limit();

        @CheckReturnValue
        @NotNull
        Delete<R> $limit(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DeleteReturning.class */
    public interface DeleteReturning<R extends Record> extends ResultQuery<R> {
        @NotNull
        Delete<?> $delete();

        @CheckReturnValue
        @NotNull
        DeleteReturning<R> $delete(Delete<?> delete);

        @NotNull
        UnmodifiableList<? extends SelectFieldOrAsterisk> $returning();

        @CheckReturnValue
        @NotNull
        DeleteReturning<?> $returning(Collection<? extends SelectFieldOrAsterisk> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DenseRank.class */
    public interface DenseRank extends WindowFunction<Integer> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DerivedTable.class */
    public interface DerivedTable<R extends Record> extends Table<R>, UOperator1<Select<R>, DerivedTable<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Div.class */
    public interface Div<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<T>, Div<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DropDatabase.class */
    public interface DropDatabase extends DDLQuery {
        @NotNull
        Catalog $database();

        boolean $ifExists();

        @CheckReturnValue
        @NotNull
        DropDatabase $database(Catalog catalog);

        @CheckReturnValue
        @NotNull
        DropDatabase $ifExists(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DropDomain.class */
    public interface DropDomain extends DDLQuery {
        @NotNull
        Domain<?> $domain();

        boolean $ifExists();

        @Nullable
        Cascade $cascade();

        @CheckReturnValue
        @NotNull
        DropDomain $domain(Domain<?> domain);

        @CheckReturnValue
        @NotNull
        DropDomain $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        DropDomain $cascade(Cascade cascade);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DropIndex.class */
    public interface DropIndex extends DDLQuery {
        @NotNull
        Index $index();

        boolean $ifExists();

        @Nullable
        Table<?> $on();

        @Nullable
        Cascade $cascade();

        @CheckReturnValue
        @NotNull
        DropIndex $index(Index index);

        @CheckReturnValue
        @NotNull
        DropIndex $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        DropIndex $on(Table<?> table);

        @CheckReturnValue
        @NotNull
        DropIndex $cascade(Cascade cascade);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DropSchema.class */
    public interface DropSchema extends DDLQuery {
        @NotNull
        Schema $schema();

        boolean $ifExists();

        @Nullable
        Cascade $cascade();

        @CheckReturnValue
        @NotNull
        DropSchema $schema(Schema schema);

        @CheckReturnValue
        @NotNull
        DropSchema $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        DropSchema $cascade(Cascade cascade);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DropSequence.class */
    public interface DropSequence extends DDLQuery {
        @NotNull
        Sequence<?> $sequence();

        boolean $ifExists();

        @CheckReturnValue
        @NotNull
        DropSequence $sequence(Sequence<?> sequence);

        @CheckReturnValue
        @NotNull
        DropSequence $ifExists(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DropTable.class */
    public interface DropTable extends DDLQuery {
        boolean $temporary();

        @NotNull
        Table<?> $table();

        boolean $ifExists();

        @Nullable
        Cascade $cascade();

        @CheckReturnValue
        @NotNull
        DropTable $temporary(boolean z);

        @CheckReturnValue
        @NotNull
        DropTable $table(Table<?> table);

        @CheckReturnValue
        @NotNull
        DropTable $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        DropTable $cascade(Cascade cascade);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DropType.class */
    public interface DropType extends DDLQuery {
        @NotNull
        UnmodifiableList<? extends Type<?>> $types();

        boolean $ifExists();

        @Nullable
        Cascade $cascade();

        @CheckReturnValue
        @NotNull
        DropType $types(Collection<? extends Type<?>> collection);

        @CheckReturnValue
        @NotNull
        DropType $ifExists(boolean z);

        @CheckReturnValue
        @NotNull
        DropType $cascade(Cascade cascade);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DropView.class */
    public interface DropView extends DDLQuery {
        @NotNull
        Table<?> $view();

        boolean $materialized();

        boolean $ifExists();

        @CheckReturnValue
        @NotNull
        DropView $view(Table<?> table);

        @CheckReturnValue
        @NotNull
        DropView $materialized(boolean z);

        @CheckReturnValue
        @NotNull
        DropView $ifExists(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Dual.class */
    public interface Dual extends Table<Record>, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$EmptyGroupingSet.class */
    public interface EmptyGroupingSet extends GroupField, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Eq.class */
    public interface Eq<T> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, Eq<T>>, CompareCondition<T, Eq<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$EqQuantified.class */
    public interface EqQuantified<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, org.jooq.QuantifiedSelect<? extends Record1<T>>, EqQuantified<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Euler.class */
    public interface Euler extends UOperator0<Euler>, Field<BigDecimal> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Extract.class */
    public interface Extract extends Field<Integer> {
        @NotNull
        Field<?> $field();

        @NotNull
        DatePart $datePart();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$False.class */
    public interface False extends Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$FieldFunction.class */
    public interface FieldFunction<T> extends Field<Integer>, UOperator2<Field<T>, UnmodifiableList<? extends Field<T>>, FieldFunction<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$FirstValue.class */
    public interface FirstValue<T> extends WindowFunction<T> {
        @NotNull
        Field<T> $field();

        @Nullable
        NullTreatment $nullTreatment();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ForeignKey.class */
    public interface ForeignKey extends Constraint {
        @NotNull
        UnmodifiableList<? extends Field<?>> $fields();

        @NotNull
        Constraint $references();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$FullJoin.class */
    public interface FullJoin<R extends Record> extends QualifiedJoin<R, FullJoin<R>> {
        @NotNull
        UnmodifiableList<Field<?>> $partitionBy1();

        @NotNull
        FullJoin<R> $partitionBy1(Collection<? extends Field<?>> collection);

        @NotNull
        UnmodifiableList<Field<?>> $partitionBy2();

        @NotNull
        FullJoin<R> $partitionBy2(Collection<? extends Field<?>> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Function.class */
    public interface Function<T> extends Field<T> {
        @NotNull
        UnmodifiableList<? extends Field<?>> $args();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$GeQuantified.class */
    public interface GeQuantified<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, org.jooq.QuantifiedSelect<? extends Record1<T>>, GeQuantified<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Grant.class */
    public interface Grant extends DDLQuery {
        @NotNull
        UnmodifiableList<? extends Privilege> $privileges();

        @NotNull
        Table<?> $on();

        @Nullable
        Role $to();

        boolean $toPublic();

        boolean $withGrantOption();

        @CheckReturnValue
        @NotNull
        Grant $privileges(Collection<? extends Privilege> collection);

        @CheckReturnValue
        @NotNull
        Grant $on(Table<?> table);

        @CheckReturnValue
        @NotNull
        Grant $to(Role role);

        @CheckReturnValue
        @NotNull
        Grant $toPublic(boolean z);

        @CheckReturnValue
        @NotNull
        Grant $withGrantOption(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Greatest.class */
    public interface Greatest<T> extends Field<T>, UOperator1<UnmodifiableList<? extends Field<T>>, Greatest<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$GroupingSets.class */
    public interface GroupingSets extends GroupField, UOperator1<UnmodifiableList<? extends UnmodifiableList<? extends FieldOrRow>>, GroupingSets> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$GtQuantified.class */
    public interface GtQuantified<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, org.jooq.QuantifiedSelect<? extends Record1<T>>, GtQuantified<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$HintedTable.class */
    public interface HintedTable<R extends Record> extends Table<R> {
        @NotNull
        Table<R> $table();

        @CheckReturnValue
        @NotNull
        <O extends Record> HintedTable<O> $table(Table<O> table);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$In.class */
    public interface In<T> extends UOperator2<Field<T>, Select<? extends Record1<T>>, In<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Insert.class */
    public interface Insert<R extends Record> extends DMLQuery<R> {
        @Nullable
        With $with();

        @NotNull
        Table<R> $into();

        @CheckReturnValue
        @NotNull
        Insert<?> $into(Table<?> table);

        @NotNull
        UnmodifiableList<? extends Field<?>> $columns();

        @CheckReturnValue
        @NotNull
        Insert<?> $columns(Collection<? extends Field<?>> collection);

        @Nullable
        Select<?> $select();

        @CheckReturnValue
        @NotNull
        Insert<?> $select(Select<?> select);

        boolean $defaultValues();

        @CheckReturnValue
        @NotNull
        Insert<?> $defaultValues(boolean z);

        @NotNull
        UnmodifiableList<? extends Row> $values();

        @CheckReturnValue
        @NotNull
        Insert<?> $values(Collection<? extends Row> collection);

        boolean $onDuplicateKeyIgnore();

        @CheckReturnValue
        @NotNull
        Insert<?> $onDuplicateKeyIgnore(boolean z);

        boolean $onDuplicateKeyUpdate();

        @CheckReturnValue
        @NotNull
        Insert<?> $onDuplicateKeyUpdate(boolean z);

        @NotNull
        UnmodifiableList<? extends Field<?>> $onConflict();

        @CheckReturnValue
        @NotNull
        Insert<?> $onConflict(Collection<? extends Field<?>> collection);

        @Nullable
        Condition $onConflictWhere();

        @CheckReturnValue
        @NotNull
        Insert<?> $onConflictWhere(Condition condition);

        @NotNull
        UnmodifiableMap<? extends FieldOrRow, ? extends FieldOrRowOrSelect> $updateSet();

        @CheckReturnValue
        @NotNull
        Insert<?> $updateSet(Map<? extends FieldOrRow, ? extends FieldOrRowOrSelect> map);

        @Nullable
        Condition $updateWhere();

        @CheckReturnValue
        @NotNull
        Insert<?> $updateWhere(Condition condition);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$InsertReturning.class */
    public interface InsertReturning<R extends Record> extends ResultQuery<R> {
        @NotNull
        Insert<?> $insert();

        @CheckReturnValue
        @NotNull
        InsertReturning<R> $insert(Insert<?> insert);

        @NotNull
        UnmodifiableList<? extends SelectFieldOrAsterisk> $returning();

        @CheckReturnValue
        @NotNull
        InsertReturning<?> $returning(Collection<? extends SelectFieldOrAsterisk> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IsDistinctFrom.class */
    public interface IsDistinctFrom<T> extends UCommutativeOperator<Field<T>, IsDistinctFrom<T>>, CompareCondition<T, IsDistinctFrom<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IsNotDistinctFrom.class */
    public interface IsNotDistinctFrom<T> extends UCommutativeOperator<Field<T>, IsNotDistinctFrom<T>>, CompareCondition<T, IsNotDistinctFrom<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Join.class */
    public interface Join<R extends Record> extends QualifiedJoin<R, Join<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JoinTable.class */
    public interface JoinTable<R extends Record, J extends JoinTable<R, J>> extends Table<R> {
        @NotNull
        Table<?> $table1();

        @NotNull
        Table<?> $table2();

        @Nullable
        JoinHint $hint();

        @NotNull
        J $table1(Table<?> table);

        @NotNull
        J $table2(Table<?> table);

        @NotNull
        J $hint(JoinHint joinHint);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Lag.class */
    public interface Lag<T> extends WindowFunction<T> {
        @NotNull
        Field<T> $field();

        @Nullable
        Field<Integer> $offset();

        @Nullable
        Field<T> $defaultValue();

        @Nullable
        NullTreatment $nullTreatment();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$LastValue.class */
    public interface LastValue<T> extends WindowFunction<T> {
        @NotNull
        Field<T> $field();

        @Nullable
        NullTreatment $nullTreatment();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Lateral.class */
    public interface Lateral<R extends Record> extends Table<R>, UOperator1<Table<R>, Lateral<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$LeQuantified.class */
    public interface LeQuantified<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, org.jooq.QuantifiedSelect<? extends Record1<T>>, LeQuantified<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Lead.class */
    public interface Lead<T> extends WindowFunction<T> {
        @NotNull
        Field<T> $field();

        @Nullable
        Field<Integer> $offset();

        @Nullable
        Field<T> $defaultValue();

        @Nullable
        NullTreatment $nullTreatment();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Least.class */
    public interface Least<T> extends Field<T>, UOperator1<UnmodifiableList<? extends Field<T>>, Least<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$LeftAntiJoin.class */
    public interface LeftAntiJoin<R extends Record> extends QualifiedJoin<R, LeftAntiJoin<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$LeftJoin.class */
    public interface LeftJoin<R extends Record> extends QualifiedJoin<R, LeftJoin<R>> {
        @NotNull
        UnmodifiableList<Field<?>> $partitionBy2();

        @NotNull
        LeftJoin<R> $partitionBy2(Collection<? extends Field<?>> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$LeftSemiJoin.class */
    public interface LeftSemiJoin<R extends Record> extends QualifiedJoin<R, LeftSemiJoin<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$LtQuantified.class */
    public interface LtQuantified<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, org.jooq.QuantifiedSelect<? extends Record1<T>>, LtQuantified<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Max.class */
    public interface Max<T> extends AggregateFunction<T> {
        @NotNull
        Field<T> $field();

        boolean $distinct();

        @CheckReturnValue
        @NotNull
        Max<T> $field(Field<T> field);

        @CheckReturnValue
        @NotNull
        Max<T> $distinct(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Median.class */
    public interface Median extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();

        @CheckReturnValue
        @NotNull
        Median $field(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Min.class */
    public interface Min<T> extends AggregateFunction<T> {
        @NotNull
        Field<T> $field();

        boolean $distinct();

        @CheckReturnValue
        @NotNull
        Min<T> $field(Field<T> field);

        @CheckReturnValue
        @NotNull
        Min<T> $distinct(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Mul.class */
    public interface Mul<T> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, Mul<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Multiset.class */
    public interface Multiset<R extends Record> extends Field<Result<R>> {
        @NotNull
        TableLike<R> $table();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$MultisetAgg.class */
    public interface MultisetAgg<R extends Record> extends AggregateFunction<Result<R>> {
        @NotNull
        Row $row();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NaturalFullJoin.class */
    public interface NaturalFullJoin<R extends Record> extends JoinTable<R, NaturalFullJoin<R>> {
        @NotNull
        UnmodifiableList<Field<?>> $partitionBy1();

        @NotNull
        NaturalFullJoin<R> $partitionBy1(Collection<? extends Field<?>> collection);

        @NotNull
        UnmodifiableList<Field<?>> $partitionBy2();

        @NotNull
        NaturalFullJoin<R> $partitionBy2(Collection<? extends Field<?>> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NaturalJoin.class */
    public interface NaturalJoin<R extends Record> extends JoinTable<R, NaturalJoin<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NaturalLeftJoin.class */
    public interface NaturalLeftJoin<R extends Record> extends JoinTable<R, NaturalLeftJoin<R>> {
        @NotNull
        UnmodifiableList<Field<?>> $partitionBy2();

        @NotNull
        NaturalLeftJoin<R> $partitionBy2(Collection<? extends Field<?>> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NaturalRightJoin.class */
    public interface NaturalRightJoin<R extends Record> extends JoinTable<R, NaturalRightJoin<R>> {
        @NotNull
        UnmodifiableList<Field<?>> $partitionBy1();

        @NotNull
        NaturalRightJoin<R> $partitionBy1(Collection<? extends Field<?>> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Ne.class */
    public interface Ne<T> extends UReturnsNullOnNullInput, UCommutativeOperator<Field<T>, Ne<T>>, CompareCondition<T, Ne<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NeQuantified.class */
    public interface NeQuantified<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, org.jooq.QuantifiedSelect<? extends Record1<T>>, NeQuantified<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Neg.class */
    public interface Neg<T> extends UReturnsNullOnNullInput, Field<T>, UOperator1<Field<T>, Neg<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotIn.class */
    public interface NotIn<T> extends UOperator2<Field<T>, Select<? extends Record1<T>>, NotIn<T>>, Condition {
    }

    @Deprecated(forRemoval = true)
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotYetImplementedException.class */
    public static class NotYetImplementedException extends RuntimeException {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NthValue.class */
    public interface NthValue<T> extends WindowFunction<T> {
        @NotNull
        Field<T> $field();

        @Nullable
        FromFirstOrLast $fromFirstOrLast();

        @Nullable
        NullTreatment $nullTreatment();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Ntile.class */
    public interface Ntile extends WindowFunction<Integer> {
        @NotNull
        Field<Integer> $tiles();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Null.class */
    public interface Null extends Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NullStatement.class */
    public interface NullStatement extends Statement {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Or.class */
    public interface Or extends UCommutativeOperator<Condition, Or>, CombinedCondition<Or> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$OuterApply.class */
    public interface OuterApply<R extends Record> extends JoinTable<R, OuterApply<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$PercentRank.class */
    public interface PercentRank extends WindowFunction<BigDecimal> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Pi.class */
    public interface Pi extends UOperator0<Pi>, Field<BigDecimal> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$PrimaryKey.class */
    public interface PrimaryKey extends Constraint {
        @NotNull
        UnmodifiableList<? extends Field<?>> $fields();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Product.class */
    public interface Product extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();

        boolean $distinct();

        @CheckReturnValue
        @NotNull
        Product $field(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        Product $distinct(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$QualifiedJoin.class */
    public interface QualifiedJoin<R extends Record, J extends QualifiedJoin<R, J>> extends JoinTable<R, J> {
        @Nullable
        Condition $on();

        @NotNull
        J $on(Condition condition);

        @NotNull
        UnmodifiableList<Field<?>> $using();

        @NotNull
        J $using(Collection<? extends Field<?>> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Quantified.class */
    public interface Quantified extends QueryPart {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Rand.class */
    public interface Rand extends UOperator0<Rand>, Field<BigDecimal> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Rank.class */
    public interface Rank extends WindowFunction<Integer> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RatioToReport.class */
    public interface RatioToReport extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegexpLike.class */
    public interface RegexpLike extends Condition {
        @NotNull
        Field<?> $search();

        @NotNull
        Field<String> $pattern();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrAvgX.class */
    public interface RegrAvgX extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrAvgX $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrAvgX $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrAvgY.class */
    public interface RegrAvgY extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrAvgY $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrAvgY $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrCount.class */
    public interface RegrCount extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrCount $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrCount $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrIntercept.class */
    public interface RegrIntercept extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrIntercept $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrIntercept $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrR2.class */
    public interface RegrR2 extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrR2 $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrR2 $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrSlope.class */
    public interface RegrSlope extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrSlope $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrSlope $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrSxx.class */
    public interface RegrSxx extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrSxx $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrSxx $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrSxy.class */
    public interface RegrSxy extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrSxy $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrSxy $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RegrSyy.class */
    public interface RegrSyy extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $y();

        @NotNull
        Field<? extends Number> $x();

        @CheckReturnValue
        @NotNull
        RegrSyy $y(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        RegrSyy $x(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ReleaseSavepoint.class */
    public interface ReleaseSavepoint extends Query {
        @NotNull
        Name $name();

        @CheckReturnValue
        @NotNull
        ReleaseSavepoint $name(Name name);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Revoke.class */
    public interface Revoke extends DDLQuery {
        @NotNull
        UnmodifiableList<? extends Privilege> $privileges();

        boolean $grantOptionFor();

        @NotNull
        Table<?> $on();

        @Nullable
        Role $from();

        boolean $fromPublic();

        @CheckReturnValue
        @NotNull
        Revoke $privileges(Collection<? extends Privilege> collection);

        @CheckReturnValue
        @NotNull
        Revoke $grantOptionFor(boolean z);

        @CheckReturnValue
        @NotNull
        Revoke $on(Table<?> table);

        @CheckReturnValue
        @NotNull
        Revoke $from(Role role);

        @CheckReturnValue
        @NotNull
        Revoke $fromPublic(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RightJoin.class */
    public interface RightJoin<R extends Record> extends QualifiedJoin<R, RightJoin<R>> {
        @NotNull
        UnmodifiableList<Field<?>> $partitionBy1();

        @NotNull
        RightJoin<R> $partitionBy1(Collection<? extends Field<?>> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Rollback.class */
    public interface Rollback extends RowCountQuery {
        @Nullable
        Name $toSavepoint();

        @CheckReturnValue
        @NotNull
        Rollback $toSavepoint(Name name);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Rollup.class */
    public interface Rollup extends GroupField, UOperator1<UnmodifiableList<? extends FieldOrRow>, Rollup> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowAsField.class */
    public interface RowAsField<R extends Record> extends Field<R> {
        @NotNull
        Row $row();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowEq.class */
    public interface RowEq<T extends Row> extends UReturnsNullOnNullInput, UCommutativeOperator<T, RowEq<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowNe.class */
    public interface RowNe<T extends Row> extends UReturnsNullOnNullInput, UCommutativeOperator<T, RowNe<T>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowNumber.class */
    public interface RowNumber extends WindowFunction<Integer> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowOverlaps.class */
    public interface RowOverlaps extends Condition, UOperator2<Row, Row, RowOverlaps> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowSubquery.class */
    public interface RowSubquery extends Row, UOperator1<Select<?>, RowSubquery> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowsFrom.class */
    public interface RowsFrom extends Table<Record> {
        @NotNull
        UnmodifiableList<? extends Table<?>> $tables();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Savepoint.class */
    public interface Savepoint extends Query {
        @NotNull
        Name $name();

        @CheckReturnValue
        @NotNull
        Savepoint $name(Name name);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ScalarSubquery.class */
    public interface ScalarSubquery<T> extends Field<T>, UOperator1<Select<? extends Record1<T>>, ScalarSubquery<T>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SetCatalog.class */
    public interface SetCatalog extends RowCountQuery {
        @NotNull
        Catalog $catalog();

        @CheckReturnValue
        @NotNull
        SetCatalog $catalog(Catalog catalog);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SetCommand.class */
    public interface SetCommand extends RowCountQuery {
        @NotNull
        Name $name();

        @NotNull
        Param<?> $value();

        boolean $local();

        @CheckReturnValue
        @NotNull
        SetCommand $name(Name name);

        @CheckReturnValue
        @NotNull
        SetCommand $value(Param<?> param);

        @CheckReturnValue
        @NotNull
        SetCommand $local(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SetSchema.class */
    public interface SetSchema extends RowCountQuery {
        @NotNull
        Schema $schema();

        @CheckReturnValue
        @NotNull
        SetSchema $schema(Schema schema);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$StartTransaction.class */
    public interface StartTransaction extends UEmpty, Query {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$StddevPop.class */
    public interface StddevPop extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();

        @CheckReturnValue
        @NotNull
        StddevPop $field(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$StddevSamp.class */
    public interface StddevSamp extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();

        @CheckReturnValue
        @NotNull
        StddevSamp $field(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$StraightJoin.class */
    public interface StraightJoin<R extends Record> extends QualifiedJoin<R, StraightJoin<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Sub.class */
    public interface Sub<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<T>, Sub<T>>, Field<T> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Sum.class */
    public interface Sum extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();

        boolean $distinct();

        @CheckReturnValue
        @NotNull
        Sum $field(Field<? extends Number> field);

        @CheckReturnValue
        @NotNull
        Sum $distinct(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$TableAsField.class */
    public interface TableAsField<R extends Record> extends Field<R> {
        @NotNull
        Table<R> $table();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$TableEq.class */
    public interface TableEq<R extends Record> extends UReturnsNullOnNullInput, UCommutativeOperator<Table<R>, TableEq<R>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$TableNe.class */
    public interface TableNe<R extends Record> extends UReturnsNullOnNullInput, UCommutativeOperator<Table<R>, TableNe<R>>, Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Tau.class */
    public interface Tau extends UOperator0<Tau>, Field<BigDecimal> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$True.class */
    public interface True extends Condition {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Truncate.class */
    public interface Truncate<R extends Record> extends DDLQuery {
        @NotNull
        UnmodifiableList<? extends Table<?>> $table();

        @Nullable
        IdentityRestartOption $restartIdentity();

        @Nullable
        Cascade $cascade();

        @CheckReturnValue
        @NotNull
        Truncate<R> $table(Collection<? extends Table<?>> collection);

        @CheckReturnValue
        @NotNull
        Truncate<R> $restartIdentity(IdentityRestartOption identityRestartOption);

        @CheckReturnValue
        @NotNull
        Truncate<R> $cascade(Cascade cascade);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Tuple2.class */
    public interface Tuple2<Q1 extends QueryPart, Q2 extends QueryPart> extends QueryPart {
        @NotNull
        Q1 $1();

        @NotNull
        Q2 $2();

        @CheckReturnValue
        @NotNull
        Tuple2<Q1, Q2> $1(Q1 q1);

        @CheckReturnValue
        @NotNull
        Tuple2<Q1, Q2> $2(Q2 q2);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UConvertibleOperator.class */
    interface UConvertibleOperator<Q, R extends UConvertibleOperator<Q, R, C>, C extends UConvertibleOperator<Q, C, R>> extends UOperator2<Q, Q, R> {
        @CheckReturnValue
        @NotNull
        C $converse();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UEmpty.class */
    interface UEmpty extends QueryPart {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UEmptyCondition.class */
    interface UEmptyCondition extends Condition, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UEmptyField.class */
    interface UEmptyField<T> extends Field<T>, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UEmptyQuery.class */
    interface UEmptyQuery extends Query, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UEmptyStatement.class */
    interface UEmptyStatement extends Statement, UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UEmptyTable.class */
    interface UEmptyTable<R extends Record> extends Table<R>, UEmpty {
    }

    @Deprecated(forRemoval = true)
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UNotYetImplemented.class */
    interface UNotYetImplemented extends UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UOpaque.class */
    interface UOpaque extends UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UOperator.class */
    interface UOperator<R extends QueryPart> extends QueryPart {
        @NotNull
        List<?> $args();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UProxy.class */
    interface UProxy<Q extends QueryPart> extends QueryPart {
        Q $delegate();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UReturnsNullOnNullInput.class */
    interface UReturnsNullOnNullInput extends QueryPart {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UTransient.class */
    interface UTransient extends UEmpty {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UniqueKey.class */
    public interface UniqueKey extends Constraint {
        @NotNull
        UnmodifiableList<? extends Field<?>> $fields();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UnmodifiableCollection.class */
    public interface UnmodifiableCollection<Q extends QueryPart> extends QueryPart, Collection<Q> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UnmodifiableMap.class */
    public interface UnmodifiableMap<K extends QueryPart, V extends QueryPart> extends QueryPart, Map<K, V> {
        @NotNull
        UnmodifiableList<Tuple2<K, V>> $tuples();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Update.class */
    public interface Update<R extends Record> extends DMLQuery<R> {
        @Nullable
        With $with();

        @NotNull
        Table<R> $table();

        @CheckReturnValue
        @NotNull
        Update<?> $table(Table<?> table);

        @NotNull
        UnmodifiableList<? extends Table<?>> $from();

        @CheckReturnValue
        @NotNull
        Update<R> $from(Collection<? extends Table<?>> collection);

        @NotNull
        UnmodifiableMap<? extends FieldOrRow, ? extends FieldOrRowOrSelect> $set();

        @CheckReturnValue
        @NotNull
        Update<R> $set(Map<? extends FieldOrRow, ? extends FieldOrRowOrSelect> map);

        @Nullable
        Condition $where();

        @CheckReturnValue
        @NotNull
        Update<R> $where(Condition condition);

        @NotNull
        UnmodifiableList<? extends SortField<?>> $orderBy();

        @CheckReturnValue
        @NotNull
        Update<R> $orderBy(Collection<? extends SortField<?>> collection);

        @Nullable
        Field<? extends Number> $limit();

        @CheckReturnValue
        @NotNull
        Update<R> $limit(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UpdateReturning.class */
    public interface UpdateReturning<R extends Record> extends ResultQuery<R> {
        @NotNull
        Update<?> $update();

        @CheckReturnValue
        @NotNull
        UpdateReturning<R> $update(Update<?> update);

        @NotNull
        UnmodifiableList<? extends SelectFieldOrAsterisk> $returning();

        @CheckReturnValue
        @NotNull
        UpdateReturning<?> $returning(Collection<? extends SelectFieldOrAsterisk> collection);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Uuid.class */
    public interface Uuid extends UOperator0<Uuid>, Field<UUID> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Values.class */
    public interface Values<R extends Record> extends Table<R>, UOperator1<UnmodifiableList<? extends Row>, Values<R>> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$VarPop.class */
    public interface VarPop extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();

        @CheckReturnValue
        @NotNull
        VarPop $field(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$VarSamp.class */
    public interface VarSamp extends AggregateFunction<BigDecimal> {
        @NotNull
        Field<? extends Number> $field();

        @CheckReturnValue
        @NotNull
        VarSamp $field(Field<? extends Number> field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$WindowFunction.class */
    public interface WindowFunction<T> extends Field<T> {
        @Nullable
        WindowSpecification $windowSpecification();

        @Nullable
        WindowDefinition $windowDefinition();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$With.class */
    public interface With extends QueryPart {
        @NotNull
        UnmodifiableList<? extends CommonTableExpression<?>> $commonTableExpressions();

        @CheckReturnValue
        @NotNull
        With $commonTableExpressions(UnmodifiableList<? extends CommonTableExpression<?>> unmodifiableList);

        boolean $recursive();

        @CheckReturnValue
        @NotNull
        With $recursive(boolean z);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$WithOrdinalityTable.class */
    public interface WithOrdinalityTable<R extends Record> extends Table<R> {
        @NotNull
        Table<?> $table();

        @CheckReturnValue
        @NotNull
        WithOrdinalityTable<?> $table(Table<?> table);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLConcat.class */
    public interface XMLConcat extends UOperator1<UnmodifiableList<? extends Field<?>>, XMLConcat>, Field<XML> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLElement.class */
    public interface XMLElement extends Field<XML> {
        @NotNull
        Name $elementName();

        @NotNull
        XMLAttributes $attributes();

        @NotNull
        UnmodifiableList<? extends Field<?>> $content();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLExists.class */
    public interface XMLExists extends Condition {
        @NotNull
        Field<String> $xpath();

        @NotNull
        Field<XML> $passing();

        @Nullable
        XMLPassingMechanism $passingMechanism();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLParse.class */
    public interface XMLParse extends Field<XML> {
        @NotNull
        Field<String> $content();

        @NotNull
        DocumentOrContent $documentOrContent();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLQuery.class */
    public interface XMLQuery extends Field<XML> {
        @NotNull
        Field<String> $xpath();

        @NotNull
        Field<XML> $passing();

        @Nullable
        XMLPassingMechanism $passingMechanism();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Xor.class */
    public interface Xor extends UReturnsNullOnNullInput, UCommutativeOperator<Condition, Xor>, CombinedCondition<Xor> {
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UnmodifiableList.class */
    public interface UnmodifiableList<Q extends QueryPart> extends UnmodifiableCollection<Q>, List<Q> {
        default <R> R $collect(Collector<Q, ?, R> collector) {
            return (R) stream().collect(collector);
        }

        @CheckReturnValue
        @NotNull
        default UnmodifiableList<Q> $concat(Collection<? extends Q> other) {
            QueryPartList<Q> r = new QueryPartList<>(this);
            r.addAll(other);
            return QOM.unmodifiable((List) r);
        }

        @CheckReturnValue
        @NotNull
        default UnmodifiableList<Q> $remove(int position) {
            QueryPartList queryPartList = new QueryPartList();
            for (int i = 0; i < size(); i++) {
                if (i != position) {
                    queryPartList.add((QueryPartList) get(i));
                }
            }
            return QOM.unmodifiable((List) queryPartList);
        }

        @Nullable
        default Q $first() {
            if (isEmpty()) {
                return null;
            }
            return (Q) get(0);
        }

        @Nullable
        default Q $last() {
            if (isEmpty()) {
                return null;
            }
            return (Q) get(size() - 1);
        }

        @CheckReturnValue
        @NotNull
        default UnmodifiableList<Q> $removeFirst() {
            QueryPartList queryPartList = new QueryPartList();
            for (int i = 1; i < size(); i++) {
                queryPartList.add((QueryPartList) get(i));
            }
            return QOM.unmodifiable((List) queryPartList);
        }

        @CheckReturnValue
        @NotNull
        default UnmodifiableList<Q> $removeLast() {
            QueryPartList queryPartList = new QueryPartList();
            for (int i = 0; i < size() - 1; i++) {
                queryPartList.add((QueryPartList) get(i));
            }
            return QOM.unmodifiable((List) queryPartList);
        }

        @CheckReturnValue
        @NotNull
        default UnmodifiableList<Q> $set(int position, Q newValue) {
            QueryPartList queryPartList = new QueryPartList();
            for (int i = 0; i < size(); i++) {
                if (i == position) {
                    queryPartList.add((QueryPartList) newValue);
                } else {
                    queryPartList.add((QueryPartList) get(i));
                }
            }
            return QOM.unmodifiable((List) queryPartList);
        }

        @CheckReturnValue
        @NotNull
        default UnmodifiableList<Q> $setAll(int position, Collection<? extends Q> newValues) {
            QueryPartList queryPartList = new QueryPartList();
            for (int i = 0; i < size(); i++) {
                if (i == position) {
                    queryPartList.addAll(newValues);
                } else {
                    queryPartList.add((QueryPartList) get(i));
                }
            }
            return QOM.unmodifiable((List) queryPartList);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$TableAlias.class */
    public interface TableAlias<R extends Record> extends Table<R>, Aliasable<Table<R>> {
        @NotNull
        Table<R> $table();

        @Override // org.jooq.impl.QOM.Aliasable
        @NotNull
        Name $alias();

        @Override // org.jooq.impl.QOM.Aliasable
        @NotNull
        default Table<R> $aliased() {
            return $table();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$GenerateSeries.class */
    public interface GenerateSeries<T> extends Table<Record1<T>>, UOperator3<Field<T>, Field<T>, Field<T>, GenerateSeries<T>> {
        @NotNull
        default Field<T> $from() {
            return $arg1();
        }

        @NotNull
        default Field<T> $to() {
            return $arg2();
        }

        @Nullable
        default Field<T> $step() {
            return $arg3();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$InList.class */
    public interface InList<T> extends Condition, UOperator2<Field<T>, UnmodifiableList<? extends Field<T>>, InList<T>> {
        @NotNull
        default Field<T> $field() {
            return $arg1();
        }

        @NotNull
        default UnmodifiableList<? extends Field<T>> $list() {
            return $arg2();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotInList.class */
    public interface NotInList<T> extends Condition, UOperator2<Field<T>, UnmodifiableList<? extends Field<T>>, NotInList<T>> {
        @NotNull
        default Field<T> $field() {
            return $arg1();
        }

        @NotNull
        default UnmodifiableList<? extends Field<T>> $list() {
            return $arg2();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowIsNull.class */
    public interface RowIsNull extends Condition, UOperator1<Row, RowIsNull> {
        @NotNull
        default Row $field() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowIsNotNull.class */
    public interface RowIsNotNull extends Condition, UOperator1<Row, RowIsNotNull> {
        @NotNull
        default Row $field() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SelectIsNull.class */
    public interface SelectIsNull extends Condition, UOperator1<Select<?>, SelectIsNull> {
        @NotNull
        default Select<?> $field() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SelectIsNotNull.class */
    public interface SelectIsNotNull extends Condition, UOperator1<Select<?>, SelectIsNotNull> {
        @NotNull
        default Select<?> $field() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$QuantifiedSelect.class */
    public interface QuantifiedSelect<R extends Record> extends Quantified, UOperator2<Quantifier, Select<R>, QuantifiedSelect<R>> {
        @NotNull
        default Quantifier $quantifier() {
            return $arg1();
        }

        @NotNull
        default QuantifiedSelect<R> $quantifier(Quantifier newQuantifier) {
            return (QuantifiedSelect) $arg1(newQuantifier);
        }

        @NotNull
        default Select<R> $query() {
            return (Select) $arg2();
        }

        @NotNull
        default QuantifiedSelect<R> $query(Select<R> newSelect) {
            return (QuantifiedSelect) $arg2(newSelect);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$QuantifiedArray.class */
    public interface QuantifiedArray<T> extends Quantified, UOperator2<Quantifier, Field<T[]>, QuantifiedArray<T>> {
        @NotNull
        default Quantifier $quantifier() {
            return $arg1();
        }

        @NotNull
        default QuantifiedArray<T> $quantifier(Quantifier newQuantifier) {
            return $arg1(newQuantifier);
        }

        @NotNull
        default Field<T[]> $array() {
            return $arg2();
        }

        @NotNull
        default QuantifiedArray<T> $array(Field<T[]> newArray) {
            return $arg2(newArray);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Mode.class */
    public interface Mode<T> extends AggregateFunction<T>, UOperator1<Field<T>, Mode<T>> {
        @NotNull
        default Field<T> $field() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayAgg.class */
    public interface ArrayAgg<T> extends AggregateFunction<T[]>, UOperator1<Field<T>, ArrayAgg<T>> {
        boolean $distinct();

        @NotNull
        default Field<T> $field() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLAgg.class */
    public interface XMLAgg extends AggregateFunction<XML>, UOperator1<Field<XML>, XMLAgg> {
        @NotNull
        default Field<XML> $field() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONArrayAgg.class */
    public interface JSONArrayAgg<J> extends AggregateFunction<J>, UOperator1<Field<?>, JSONArrayAgg<J>> {
        boolean $distinct();

        @Nullable
        JSONOnNull $onNull();

        @Nullable
        DataType<?> $returning();

        @NotNull
        default Field<?> $field() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONObjectAgg.class */
    public interface JSONObjectAgg<J> extends AggregateFunction<J>, UOperator1<JSONEntry<?>, JSONObjectAgg<J>> {
        @Nullable
        JSONOnNull $onNull();

        @Nullable
        DataType<?> $returning();

        @NotNull
        default JSONEntry<?> $entry() {
            return $arg1();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$FieldAlias.class */
    public interface FieldAlias<T> extends Field<T>, Aliasable<Field<?>> {
        @NotNull
        Field<T> $field();

        @Override // org.jooq.impl.QOM.Aliasable
        @NotNull
        Name $alias();

        @Override // org.jooq.impl.QOM.Aliasable
        @NotNull
        default Field<?> $aliased() {
            return $field();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Nvl2.class */
    public interface Nvl2<T> extends Field<T>, UOperator3<Field<?>, Field<T>, Field<T>, Nvl2<T>> {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @NotNull
        default Field<T> $ifNotNull() {
            return $arg2();
        }

        @NotNull
        default Field<T> $ifIfNull() {
            return $arg3();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Iif.class */
    public interface Iif<T> extends Field<T>, UOperator3<Condition, Field<T>, Field<T>, Iif<T>> {
        @NotNull
        default Condition $condition() {
            return $arg1();
        }

        @NotNull
        default Field<T> $ifTrue() {
            return $arg2();
        }

        @NotNull
        default Field<T> $ifFalse() {
            return $arg3();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CaseSimple.class */
    public interface CaseSimple<V, T> extends Field<T>, UOperator3<Field<V>, UnmodifiableList<? extends Tuple2<Field<V>, Field<T>>>, Field<T>, CaseSimple<V, T>> {
        @NotNull
        default Field<V> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default CaseSimple<V, T> $value(Field<V> value) {
            return $arg1(value);
        }

        @NotNull
        default UnmodifiableList<? extends Tuple2<Field<V>, Field<T>>> $when() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default CaseSimple<V, T> $when(UnmodifiableList<? extends Tuple2<Field<V>, Field<T>>> when) {
            return $arg2(when);
        }

        @Nullable
        default Field<T> $else() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default CaseSimple<V, T> $else(Field<T> else_) {
            return $arg3(else_);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CaseSearched.class */
    public interface CaseSearched<T> extends Field<T>, UOperator2<UnmodifiableList<? extends Tuple2<Condition, Field<T>>>, Field<T>, CaseSearched<T>> {
        @NotNull
        default UnmodifiableList<? extends Tuple2<Condition, Field<T>>> $when() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default CaseSearched<T> $when(UnmodifiableList<? extends Tuple2<Condition, Field<T>>> when) {
            return $arg1(when);
        }

        @Nullable
        default Field<T> $else() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default CaseSearched<T> $else(Field<T> else_) {
            return $arg2(else_);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Decode.class */
    public interface Decode<V, T> extends Field<T>, UOperator3<Field<V>, UnmodifiableList<? extends Tuple2<Field<V>, Field<T>>>, Field<T>, Decode<V, T>> {
        @NotNull
        default Field<V> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Decode<V, T> $value(Field<V> value) {
            return $arg1(value);
        }

        @NotNull
        default UnmodifiableList<? extends Tuple2<Field<V>, Field<T>>> $when() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Decode<V, T> $when(UnmodifiableList<? extends Tuple2<Field<V>, Field<T>>> when) {
            return $arg2(when);
        }

        @Nullable
        default Field<T> $else() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Decode<V, T> $else(Field<T> else_) {
            return $arg3(else_);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$TimestampDiff.class */
    public interface TimestampDiff<T> extends Field<DayToSecond>, UOperator2<Field<T>, Field<T>, TimestampDiff<T>> {
        @NotNull
        default Field<T> $minuend() {
            return $arg1();
        }

        @NotNull
        default Field<T> $subtrahend() {
            return $arg2();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Exists.class */
    public interface Exists extends UOperator1<Select<?>, Exists>, Condition {
        @NotNull
        default Select<?> $query() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Exists $query(Select<?> newQuery) {
            return $arg1(newQuery);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Ge.class */
    public interface Ge<T> extends UReturnsNullOnNullInput, UConvertibleOperator<Field<T>, Ge<T>, Le<T>>, CompareCondition<T, Ge<T>> {
        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default Le<T> $converse() {
            return new org.jooq.impl.Le($arg2(), $arg1());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Gt.class */
    public interface Gt<T> extends UReturnsNullOnNullInput, UConvertibleOperator<Field<T>, Gt<T>, Lt<T>>, CompareCondition<T, Gt<T>> {
        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default Lt<T> $converse() {
            return new org.jooq.impl.Lt($arg2(), $arg1());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IsNull.class */
    public interface IsNull extends UOperator1<Field<?>, IsNull>, Condition {
        @NotNull
        default Field<?> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default IsNull $field(Field<?> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IsNotNull.class */
    public interface IsNotNull extends UOperator1<Field<?>, IsNotNull>, Condition {
        @NotNull
        default Field<?> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default IsNotNull $field(Field<?> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Le.class */
    public interface Le<T> extends UReturnsNullOnNullInput, UConvertibleOperator<Field<T>, Le<T>, Ge<T>>, CompareCondition<T, Le<T>> {
        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default Ge<T> $converse() {
            return new org.jooq.impl.Ge($arg2(), $arg1());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Like.class */
    public interface Like extends UReturnsNullOnNullInput, UOperator3<Field<?>, Field<String>, Character, Like>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Like $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<String> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Like $pattern(Field<String> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Like $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$LikeQuantified.class */
    public interface LikeQuantified extends UReturnsNullOnNullInput, UOperator3<Field<?>, org.jooq.QuantifiedSelect<? extends Record1<String>>, Character, LikeQuantified>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default LikeQuantified $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default org.jooq.QuantifiedSelect<? extends Record1<String>> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default LikeQuantified $pattern(org.jooq.QuantifiedSelect<? extends Record1<String>> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default LikeQuantified $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$LikeIgnoreCase.class */
    public interface LikeIgnoreCase extends UReturnsNullOnNullInput, UOperator3<Field<?>, Field<String>, Character, LikeIgnoreCase>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default LikeIgnoreCase $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<String> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default LikeIgnoreCase $pattern(Field<String> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default LikeIgnoreCase $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Lt.class */
    public interface Lt<T> extends UReturnsNullOnNullInput, UConvertibleOperator<Field<T>, Lt<T>, Gt<T>>, CompareCondition<T, Lt<T>> {
        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default Gt<T> $converse() {
            return new org.jooq.impl.Gt($arg2(), $arg1());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Not.class */
    public interface Not extends UReturnsNullOnNullInput, UOperator1<Condition, Not>, Condition {
        @NotNull
        default Condition $condition() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Not $condition(Condition newCondition) {
            return $arg1(newCondition);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotField.class */
    public interface NotField extends UReturnsNullOnNullInput, UOperator1<Field<Boolean>, NotField>, Field<Boolean> {
        @NotNull
        default Field<Boolean> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default NotField $field(Field<Boolean> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotLike.class */
    public interface NotLike extends UReturnsNullOnNullInput, UOperator3<Field<?>, Field<String>, Character, NotLike>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default NotLike $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<String> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default NotLike $pattern(Field<String> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default NotLike $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotLikeQuantified.class */
    public interface NotLikeQuantified extends UReturnsNullOnNullInput, UOperator3<Field<?>, org.jooq.QuantifiedSelect<? extends Record1<String>>, Character, NotLikeQuantified>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default NotLikeQuantified $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default org.jooq.QuantifiedSelect<? extends Record1<String>> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default NotLikeQuantified $pattern(org.jooq.QuantifiedSelect<? extends Record1<String>> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default NotLikeQuantified $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotLikeIgnoreCase.class */
    public interface NotLikeIgnoreCase extends UReturnsNullOnNullInput, UOperator3<Field<?>, Field<String>, Character, NotLikeIgnoreCase>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default NotLikeIgnoreCase $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<String> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default NotLikeIgnoreCase $pattern(Field<String> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default NotLikeIgnoreCase $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotSimilarTo.class */
    public interface NotSimilarTo extends UReturnsNullOnNullInput, UOperator3<Field<?>, Field<String>, Character, NotSimilarTo>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default NotSimilarTo $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<String> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default NotSimilarTo $pattern(Field<String> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default NotSimilarTo $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NotSimilarToQuantified.class */
    public interface NotSimilarToQuantified extends UReturnsNullOnNullInput, UOperator3<Field<?>, org.jooq.QuantifiedSelect<? extends Record1<String>>, Character, NotSimilarToQuantified>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default NotSimilarToQuantified $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default org.jooq.QuantifiedSelect<? extends Record1<String>> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default NotSimilarToQuantified $pattern(org.jooq.QuantifiedSelect<? extends Record1<String>> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default NotSimilarToQuantified $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SimilarTo.class */
    public interface SimilarTo extends UReturnsNullOnNullInput, UOperator3<Field<?>, Field<String>, Character, SimilarTo>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default SimilarTo $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<String> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default SimilarTo $pattern(Field<String> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default SimilarTo $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SimilarToQuantified.class */
    public interface SimilarToQuantified extends UReturnsNullOnNullInput, UOperator3<Field<?>, org.jooq.QuantifiedSelect<? extends Record1<String>>, Character, SimilarToQuantified>, Condition {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default SimilarToQuantified $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default org.jooq.QuantifiedSelect<? extends Record1<String>> $pattern() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default SimilarToQuantified $pattern(org.jooq.QuantifiedSelect<? extends Record1<String>> newPattern) {
            return $arg2(newPattern);
        }

        @Nullable
        default Character $escape() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default SimilarToQuantified $escape(Character newEscape) {
            return $arg3(newEscape);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Unique.class */
    public interface Unique extends UOperator1<Select<?>, Unique>, Condition {
        @NotNull
        default Select<?> $query() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Unique $query(Select<?> newQuery) {
            return $arg1(newQuery);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowGt.class */
    public interface RowGt<T extends Row> extends UReturnsNullOnNullInput, UConvertibleOperator<T, RowGt<T>, RowLt<T>>, Condition {
        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default RowLt<T> $converse() {
            return new org.jooq.impl.RowLt((Row) $arg2(), (Row) $arg1());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowGe.class */
    public interface RowGe<T extends Row> extends UReturnsNullOnNullInput, UConvertibleOperator<T, RowGe<T>, RowLe<T>>, Condition {
        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default RowLe<T> $converse() {
            return new org.jooq.impl.RowLe((Row) $arg2(), (Row) $arg1());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowLt.class */
    public interface RowLt<T extends Row> extends UReturnsNullOnNullInput, UConvertibleOperator<T, RowLt<T>, RowGt<T>>, Condition {
        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default RowGt<T> $converse() {
            return new org.jooq.impl.RowGt((Row) $arg2(), (Row) $arg1());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$RowLe.class */
    public interface RowLe<T extends Row> extends UReturnsNullOnNullInput, UConvertibleOperator<T, RowLe<T>, RowGe<T>>, Condition {
        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default RowGe<T> $converse() {
            return new org.jooq.impl.RowGe((Row) $arg2(), (Row) $arg1());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IsDocument.class */
    public interface IsDocument extends UReturnsNullOnNullInput, UOperator1<Field<?>, IsDocument>, Condition {
        @NotNull
        default Field<?> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default IsDocument $field(Field<?> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IsNotDocument.class */
    public interface IsNotDocument extends UReturnsNullOnNullInput, UOperator1<Field<?>, IsNotDocument>, Condition {
        @NotNull
        default Field<?> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default IsNotDocument $field(Field<?> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IsJson.class */
    public interface IsJson extends UReturnsNullOnNullInput, UOperator1<Field<?>, IsJson>, Condition {
        @NotNull
        default Field<?> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default IsJson $field(Field<?> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IsNotJson.class */
    public interface IsNotJson extends UReturnsNullOnNullInput, UOperator1<Field<?>, IsNotJson>, Condition {
        @NotNull
        default Field<?> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default IsNotJson $field(Field<?> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Excluded.class */
    public interface Excluded<T> extends UOperator1<Field<T>, Excluded<T>>, Field<T> {
        @NotNull
        default Field<T> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Excluded<T> $field(Field<T> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$QualifiedRowid.class */
    public interface QualifiedRowid extends UOperator1<Table<?>, QualifiedRowid>, Field<RowId> {
        @NotNull
        default Table<?> $table() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default QualifiedRowid $table(Table<?> newTable) {
            return $arg1(newTable);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Abs.class */
    public interface Abs<T extends Number> extends UReturnsNullOnNullInput, UOperator1<Field<T>, Abs<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Abs<T> $value(Field<T> newValue) {
            return (Abs) $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Acos.class */
    public interface Acos extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Acos>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Acos $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Acosh.class */
    public interface Acosh extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Acosh>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Acosh $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Acoth.class */
    public interface Acoth extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Acoth>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Acoth $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Asin.class */
    public interface Asin extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Asin>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Asin $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Asinh.class */
    public interface Asinh extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Asinh>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Asinh $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Atan.class */
    public interface Atan extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Atan>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Atan $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Atan2.class */
    public interface Atan2 extends UReturnsNullOnNullInput, UOperator2<Field<? extends Number>, Field<? extends Number>, Atan2>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $x() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Atan2 $x(Field<? extends Number> newX) {
            return $arg1(newX);
        }

        @NotNull
        default Field<? extends Number> $y() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Atan2 $y(Field<? extends Number> newY) {
            return $arg2(newY);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Atanh.class */
    public interface Atanh extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Atanh>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Atanh $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitCount.class */
    public interface BitCount extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, BitCount>, Field<Integer> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default BitCount $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitGet.class */
    public interface BitGet<T extends Number> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<? extends Number>, BitGet<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default BitGet<T> $value(Field<T> newValue) {
            return (BitGet) $arg1(newValue);
        }

        @NotNull
        default Field<? extends Number> $bit() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default BitGet<T> $bit(Field<? extends Number> newBit) {
            return (BitGet) $arg2(newBit);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitSet.class */
    public interface BitSet<T extends Number> extends UReturnsNullOnNullInput, UOperator3<Field<T>, Field<? extends Number>, Field<T>, BitSet<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default BitSet<T> $value(Field<T> newValue) {
            return (BitSet) $arg1(newValue);
        }

        @NotNull
        default Field<? extends Number> $bit() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default BitSet<T> $bit(Field<? extends Number> newBit) {
            return (BitSet) $arg2(newBit);
        }

        @Nullable
        default Field<T> $newValue() {
            return (Field) $arg3();
        }

        @CheckReturnValue
        @NotNull
        default BitSet<T> $newValue(Field<T> newNewValue) {
            return (BitSet) $arg3(newNewValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Ceil.class */
    public interface Ceil<T extends Number> extends UReturnsNullOnNullInput, UOperator1<Field<T>, Ceil<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Ceil<T> $value(Field<T> newValue) {
            return (Ceil) $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Cos.class */
    public interface Cos extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Cos>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Cos $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Cosh.class */
    public interface Cosh extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Cosh>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Cosh $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Cot.class */
    public interface Cot extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Cot>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Cot $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Coth.class */
    public interface Coth extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Coth>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Coth $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Degrees.class */
    public interface Degrees extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Degrees>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $radians() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Degrees $radians(Field<? extends Number> newRadians) {
            return $arg1(newRadians);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Exp.class */
    public interface Exp extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Exp>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Exp $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Floor.class */
    public interface Floor<T extends Number> extends UReturnsNullOnNullInput, UOperator1<Field<T>, Floor<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Floor<T> $value(Field<T> newValue) {
            return (Floor) $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Ln.class */
    public interface Ln extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Ln>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Ln $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Log.class */
    public interface Log extends UReturnsNullOnNullInput, UOperator2<Field<? extends Number>, Field<? extends Number>, Log>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Log $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<? extends Number> $base() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Log $base(Field<? extends Number> newBase) {
            return $arg2(newBase);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Log10.class */
    public interface Log10 extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Log10>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Log10 $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Mod.class */
    public interface Mod<T extends Number> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<? extends Number>, Mod<T>>, Field<T> {
        @NotNull
        default Field<T> $dividend() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Mod<T> $dividend(Field<T> newDividend) {
            return (Mod) $arg1(newDividend);
        }

        @NotNull
        default Field<? extends Number> $divisor() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Mod<T> $divisor(Field<? extends Number> newDivisor) {
            return (Mod) $arg2(newDivisor);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Power.class */
    public interface Power extends UReturnsNullOnNullInput, UOperator2<Field<? extends Number>, Field<? extends Number>, Power>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $base() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Power $base(Field<? extends Number> newBase) {
            return $arg1(newBase);
        }

        @NotNull
        default Field<? extends Number> $exponent() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Power $exponent(Field<? extends Number> newExponent) {
            return $arg2(newExponent);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Radians.class */
    public interface Radians extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Radians>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $degrees() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Radians $degrees(Field<? extends Number> newDegrees) {
            return $arg1(newDegrees);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Round.class */
    public interface Round<T extends Number> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<Integer>, Round<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Round<T> $value(Field<T> newValue) {
            return (Round) $arg1(newValue);
        }

        @Nullable
        default Field<Integer> $decimals() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Round<T> $decimals(Field<Integer> newDecimals) {
            return (Round) $arg2(newDecimals);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Shl.class */
    public interface Shl<T extends Number> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<? extends Number>, Shl<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Shl<T> $value(Field<T> newValue) {
            return (Shl) $arg1(newValue);
        }

        @NotNull
        default Field<? extends Number> $count() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Shl<T> $count(Field<? extends Number> newCount) {
            return (Shl) $arg2(newCount);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Shr.class */
    public interface Shr<T extends Number> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<? extends Number>, Shr<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Shr<T> $value(Field<T> newValue) {
            return (Shr) $arg1(newValue);
        }

        @NotNull
        default Field<? extends Number> $count() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Shr<T> $count(Field<? extends Number> newCount) {
            return (Shr) $arg2(newCount);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Sign.class */
    public interface Sign extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Sign>, Field<Integer> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Sign $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Sin.class */
    public interface Sin extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Sin>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Sin $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Sinh.class */
    public interface Sinh extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Sinh>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Sinh $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Sqrt.class */
    public interface Sqrt extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Sqrt>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Sqrt $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Square.class */
    public interface Square<T extends Number> extends UReturnsNullOnNullInput, UOperator1<Field<T>, Square<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Square<T> $value(Field<T> newValue) {
            return (Square) $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Tan.class */
    public interface Tan extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Tan>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Tan $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Tanh.class */
    public interface Tanh extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Tanh>, Field<BigDecimal> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Tanh $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Trunc.class */
    public interface Trunc<T extends Number> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<Integer>, Trunc<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Trunc<T> $value(Field<T> newValue) {
            return (Trunc) $arg1(newValue);
        }

        @NotNull
        default Field<Integer> $decimals() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Trunc<T> $decimals(Field<Integer> newDecimals) {
            return (Trunc) $arg2(newDecimals);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$WidthBucket.class */
    public interface WidthBucket<T extends Number> extends UReturnsNullOnNullInput, UOperator4<Field<T>, Field<T>, Field<T>, Field<Integer>, WidthBucket<T>>, Field<T> {
        @NotNull
        default Field<T> $field() {
            return (Field) $arg1();
        }

        @CheckReturnValue
        @NotNull
        default WidthBucket<T> $field(Field<T> newField) {
            return (WidthBucket) $arg1(newField);
        }

        @NotNull
        default Field<T> $low() {
            return (Field) $arg2();
        }

        @CheckReturnValue
        @NotNull
        default WidthBucket<T> $low(Field<T> newLow) {
            return (WidthBucket) $arg2(newLow);
        }

        @NotNull
        default Field<T> $high() {
            return (Field) $arg3();
        }

        @CheckReturnValue
        @NotNull
        default WidthBucket<T> $high(Field<T> newHigh) {
            return (WidthBucket) $arg3(newHigh);
        }

        @NotNull
        default Field<Integer> $buckets() {
            return $arg4();
        }

        @CheckReturnValue
        @NotNull
        default WidthBucket<T> $buckets(Field<Integer> newBuckets) {
            return (WidthBucket) $arg4(newBuckets);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Ascii.class */
    public interface Ascii extends UReturnsNullOnNullInput, UOperator1<Field<String>, Ascii>, Field<Integer> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Ascii $string(Field<String> newString) {
            return $arg1(newString);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$BitLength.class */
    public interface BitLength extends UReturnsNullOnNullInput, UOperator1<Field<String>, BitLength>, Field<Integer> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default BitLength $string(Field<String> newString) {
            return $arg1(newString);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CharLength.class */
    public interface CharLength extends UReturnsNullOnNullInput, UOperator1<Field<String>, CharLength>, Field<Integer> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default CharLength $string(Field<String> newString) {
            return $arg1(newString);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Chr.class */
    public interface Chr extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Chr>, Field<String> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Chr $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Contains.class */
    public interface Contains<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<T>, Contains<T>>, CompareCondition<T, Contains<T>> {
        @NotNull
        default Field<T> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Contains<T> $value(Field<T> newValue) {
            return (Contains) $arg1(newValue);
        }

        @NotNull
        default Field<T> $content() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Contains<T> $content(Field<T> newContent) {
            return (Contains) $arg2(newContent);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ContainsIgnoreCase.class */
    public interface ContainsIgnoreCase<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<T>, ContainsIgnoreCase<T>>, CompareCondition<T, ContainsIgnoreCase<T>> {
        @NotNull
        default Field<T> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default ContainsIgnoreCase<T> $value(Field<T> newValue) {
            return (ContainsIgnoreCase) $arg1(newValue);
        }

        @NotNull
        default Field<T> $content() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default ContainsIgnoreCase<T> $content(Field<T> newContent) {
            return (ContainsIgnoreCase) $arg2(newContent);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Digits.class */
    public interface Digits extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Digits>, Field<String> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Digits $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$EndsWith.class */
    public interface EndsWith<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<T>, EndsWith<T>>, CompareCondition<T, EndsWith<T>> {
        @NotNull
        default Field<T> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default EndsWith<T> $string(Field<T> newString) {
            return (EndsWith) $arg1(newString);
        }

        @NotNull
        default Field<T> $suffix() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default EndsWith<T> $suffix(Field<T> newSuffix) {
            return (EndsWith) $arg2(newSuffix);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$EndsWithIgnoreCase.class */
    public interface EndsWithIgnoreCase<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<T>, EndsWithIgnoreCase<T>>, CompareCondition<T, EndsWithIgnoreCase<T>> {
        @NotNull
        default Field<T> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default EndsWithIgnoreCase<T> $string(Field<T> newString) {
            return (EndsWithIgnoreCase) $arg1(newString);
        }

        @NotNull
        default Field<T> $suffix() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default EndsWithIgnoreCase<T> $suffix(Field<T> newSuffix) {
            return (EndsWithIgnoreCase) $arg2(newSuffix);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Left.class */
    public interface Left extends UReturnsNullOnNullInput, UOperator2<Field<String>, Field<? extends Number>, Left>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Left $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<? extends Number> $length() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Left $length(Field<? extends Number> newLength) {
            return $arg2(newLength);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Lower.class */
    public interface Lower extends UReturnsNullOnNullInput, UOperator1<Field<String>, Lower>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Lower $string(Field<String> newString) {
            return $arg1(newString);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Lpad.class */
    public interface Lpad extends UReturnsNullOnNullInput, UOperator3<Field<String>, Field<? extends Number>, Field<String>, Lpad>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Lpad $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<? extends Number> $length() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Lpad $length(Field<? extends Number> newLength) {
            return $arg2(newLength);
        }

        @Nullable
        default Field<String> $character() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Lpad $character(Field<String> newCharacter) {
            return $arg3(newCharacter);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Ltrim.class */
    public interface Ltrim extends UReturnsNullOnNullInput, UOperator2<Field<String>, Field<String>, Ltrim>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Ltrim $string(Field<String> newString) {
            return $arg1(newString);
        }

        @Nullable
        default Field<String> $characters() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Ltrim $characters(Field<String> newCharacters) {
            return $arg2(newCharacters);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Md5.class */
    public interface Md5 extends UReturnsNullOnNullInput, UOperator1<Field<String>, Md5>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Md5 $string(Field<String> newString) {
            return $arg1(newString);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$OctetLength.class */
    public interface OctetLength extends UReturnsNullOnNullInput, UOperator1<Field<String>, OctetLength>, Field<Integer> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default OctetLength $string(Field<String> newString) {
            return $arg1(newString);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Overlay.class */
    public interface Overlay extends UReturnsNullOnNullInput, UOperator4<Field<String>, Field<String>, Field<? extends Number>, Field<? extends Number>, Overlay>, Field<String> {
        @NotNull
        default Field<String> $in() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Overlay $in(Field<String> newIn) {
            return $arg1(newIn);
        }

        @NotNull
        default Field<String> $placing() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Overlay $placing(Field<String> newPlacing) {
            return $arg2(newPlacing);
        }

        @NotNull
        default Field<? extends Number> $startIndex() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Overlay $startIndex(Field<? extends Number> newStartIndex) {
            return $arg3(newStartIndex);
        }

        @Nullable
        default Field<? extends Number> $length() {
            return $arg4();
        }

        @CheckReturnValue
        @NotNull
        default Overlay $length(Field<? extends Number> newLength) {
            return $arg4(newLength);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Position.class */
    public interface Position extends UReturnsNullOnNullInput, UOperator3<Field<String>, Field<String>, Field<? extends Number>, Position>, Field<Integer> {
        @NotNull
        default Field<String> $in() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Position $in(Field<String> newIn) {
            return $arg1(newIn);
        }

        @NotNull
        default Field<String> $search() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Position $search(Field<String> newSearch) {
            return $arg2(newSearch);
        }

        @Nullable
        default Field<? extends Number> $startIndex() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Position $startIndex(Field<? extends Number> newStartIndex) {
            return $arg3(newStartIndex);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Repeat.class */
    public interface Repeat extends UReturnsNullOnNullInput, UOperator2<Field<String>, Field<? extends Number>, Repeat>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Repeat $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<? extends Number> $count() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Repeat $count(Field<? extends Number> newCount) {
            return $arg2(newCount);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Replace.class */
    public interface Replace extends UReturnsNullOnNullInput, UOperator3<Field<String>, Field<String>, Field<String>, Replace>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Replace $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<String> $search() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Replace $search(Field<String> newSearch) {
            return $arg2(newSearch);
        }

        @Nullable
        default Field<String> $replace() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Replace $replace(Field<String> newReplace) {
            return $arg3(newReplace);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Reverse.class */
    public interface Reverse extends UReturnsNullOnNullInput, UOperator1<Field<String>, Reverse>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Reverse $string(Field<String> newString) {
            return $arg1(newString);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Right.class */
    public interface Right extends UReturnsNullOnNullInput, UOperator2<Field<String>, Field<? extends Number>, Right>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Right $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<? extends Number> $length() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Right $length(Field<? extends Number> newLength) {
            return $arg2(newLength);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Rpad.class */
    public interface Rpad extends UReturnsNullOnNullInput, UOperator3<Field<String>, Field<? extends Number>, Field<String>, Rpad>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Rpad $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<? extends Number> $length() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Rpad $length(Field<? extends Number> newLength) {
            return $arg2(newLength);
        }

        @Nullable
        default Field<String> $character() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Rpad $character(Field<String> newCharacter) {
            return $arg3(newCharacter);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Rtrim.class */
    public interface Rtrim extends UReturnsNullOnNullInput, UOperator2<Field<String>, Field<String>, Rtrim>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Rtrim $string(Field<String> newString) {
            return $arg1(newString);
        }

        @Nullable
        default Field<String> $characters() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Rtrim $characters(Field<String> newCharacters) {
            return $arg2(newCharacters);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Space.class */
    public interface Space extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, Space>, Field<String> {
        @NotNull
        default Field<? extends Number> $count() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Space $count(Field<? extends Number> newCount) {
            return $arg1(newCount);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SplitPart.class */
    public interface SplitPart extends UReturnsNullOnNullInput, UOperator3<Field<String>, Field<String>, Field<? extends Number>, SplitPart>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default SplitPart $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<String> $delimiter() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default SplitPart $delimiter(Field<String> newDelimiter) {
            return $arg2(newDelimiter);
        }

        @NotNull
        default Field<? extends Number> $n() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default SplitPart $n(Field<? extends Number> newN) {
            return $arg3(newN);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$StartsWith.class */
    public interface StartsWith<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<T>, StartsWith<T>>, CompareCondition<T, StartsWith<T>> {
        @NotNull
        default Field<T> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default StartsWith<T> $string(Field<T> newString) {
            return (StartsWith) $arg1(newString);
        }

        @NotNull
        default Field<T> $prefix() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default StartsWith<T> $prefix(Field<T> newPrefix) {
            return (StartsWith) $arg2(newPrefix);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$StartsWithIgnoreCase.class */
    public interface StartsWithIgnoreCase<T> extends UReturnsNullOnNullInput, UOperator2<Field<T>, Field<T>, StartsWithIgnoreCase<T>>, CompareCondition<T, StartsWithIgnoreCase<T>> {
        @NotNull
        default Field<T> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default StartsWithIgnoreCase<T> $string(Field<T> newString) {
            return (StartsWithIgnoreCase) $arg1(newString);
        }

        @NotNull
        default Field<T> $prefix() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default StartsWithIgnoreCase<T> $prefix(Field<T> newPrefix) {
            return (StartsWithIgnoreCase) $arg2(newPrefix);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Substring.class */
    public interface Substring extends UReturnsNullOnNullInput, UOperator3<Field<String>, Field<? extends Number>, Field<? extends Number>, Substring>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Substring $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<? extends Number> $startingPosition() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Substring $startingPosition(Field<? extends Number> newStartingPosition) {
            return $arg2(newStartingPosition);
        }

        @Nullable
        default Field<? extends Number> $length() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Substring $length(Field<? extends Number> newLength) {
            return $arg3(newLength);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$SubstringIndex.class */
    public interface SubstringIndex extends UReturnsNullOnNullInput, UOperator3<Field<String>, Field<String>, Field<? extends Number>, SubstringIndex>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default SubstringIndex $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<String> $delimiter() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default SubstringIndex $delimiter(Field<String> newDelimiter) {
            return $arg2(newDelimiter);
        }

        @NotNull
        default Field<? extends Number> $n() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default SubstringIndex $n(Field<? extends Number> newN) {
            return $arg3(newN);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ToChar.class */
    public interface ToChar extends UReturnsNullOnNullInput, UOperator2<Field<?>, Field<String>, ToChar>, Field<String> {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default ToChar $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @Nullable
        default Field<String> $formatMask() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default ToChar $formatMask(Field<String> newFormatMask) {
            return $arg2(newFormatMask);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ToDate.class */
    public interface ToDate extends UReturnsNullOnNullInput, UOperator2<Field<String>, Field<String>, ToDate>, Field<Date> {
        @NotNull
        default Field<String> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default ToDate $value(Field<String> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<String> $formatMask() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default ToDate $formatMask(Field<String> newFormatMask) {
            return $arg2(newFormatMask);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ToHex.class */
    public interface ToHex extends UReturnsNullOnNullInput, UOperator1<Field<? extends Number>, ToHex>, Field<String> {
        @NotNull
        default Field<? extends Number> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default ToHex $value(Field<? extends Number> newValue) {
            return $arg1(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ToTimestamp.class */
    public interface ToTimestamp extends UReturnsNullOnNullInput, UOperator2<Field<String>, Field<String>, ToTimestamp>, Field<Timestamp> {
        @NotNull
        default Field<String> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default ToTimestamp $value(Field<String> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<String> $formatMask() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default ToTimestamp $formatMask(Field<String> newFormatMask) {
            return $arg2(newFormatMask);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Translate.class */
    public interface Translate extends UReturnsNullOnNullInput, UOperator3<Field<String>, Field<String>, Field<String>, Translate>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Translate $string(Field<String> newString) {
            return $arg1(newString);
        }

        @NotNull
        default Field<String> $from() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Translate $from(Field<String> newFrom) {
            return $arg2(newFrom);
        }

        @NotNull
        default Field<String> $to() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default Translate $to(Field<String> newTo) {
            return $arg3(newTo);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Trim.class */
    public interface Trim extends UReturnsNullOnNullInput, UOperator2<Field<String>, Field<String>, Trim>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Trim $string(Field<String> newString) {
            return $arg1(newString);
        }

        @Nullable
        default Field<String> $characters() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Trim $characters(Field<String> newCharacters) {
            return $arg2(newCharacters);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Upper.class */
    public interface Upper extends UReturnsNullOnNullInput, UOperator1<Field<String>, Upper>, Field<String> {
        @NotNull
        default Field<String> $string() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Upper $string(Field<String> newString) {
            return $arg1(newString);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DateAdd.class */
    public interface DateAdd<T> extends UReturnsNullOnNullInput, UOperator3<Field<T>, Field<? extends Number>, DatePart, DateAdd<T>>, Field<T> {
        @NotNull
        default Field<T> $date() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default DateAdd<T> $date(Field<T> newDate) {
            return $arg1(newDate);
        }

        @NotNull
        default Field<? extends Number> $interval() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default DateAdd<T> $interval(Field<? extends Number> newInterval) {
            return $arg2(newInterval);
        }

        @Nullable
        default DatePart $datePart() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default DateAdd<T> $datePart(DatePart newDatePart) {
            return $arg3(newDatePart);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Cardinality.class */
    public interface Cardinality extends UReturnsNullOnNullInput, UOperator1<Field<? extends Object[]>, Cardinality>, Field<Integer> {
        @NotNull
        default Field<? extends Object[]> $array() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Cardinality $array(Field<? extends Object[]> newArray) {
            return $arg1(newArray);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ArrayGet.class */
    public interface ArrayGet<T> extends UReturnsNullOnNullInput, UOperator2<Field<T[]>, Field<Integer>, ArrayGet<T>>, Field<T> {
        @NotNull
        default Field<T[]> $array() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default ArrayGet<T> $array(Field<T[]> newArray) {
            return $arg1(newArray);
        }

        @NotNull
        default Field<Integer> $index() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default ArrayGet<T> $index(Field<Integer> newIndex) {
            return $arg2(newIndex);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Nvl.class */
    public interface Nvl<T> extends UOperator2<Field<T>, Field<T>, Nvl<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Nvl<T> $value(Field<T> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<T> $defaultValue() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Nvl<T> $defaultValue(Field<T> newDefaultValue) {
            return $arg2(newDefaultValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Nullif.class */
    public interface Nullif<T> extends UOperator2<Field<T>, Field<T>, Nullif<T>>, Field<T> {
        @NotNull
        default Field<T> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default Nullif<T> $value(Field<T> newValue) {
            return $arg1(newValue);
        }

        @NotNull
        default Field<T> $other() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default Nullif<T> $other(Field<T> newOther) {
            return $arg2(newOther);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$TryCast.class */
    public interface TryCast<T> extends UOperator2<Field<?>, DataType<T>, TryCast<T>>, Field<T> {
        @NotNull
        default Field<?> $value() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default TryCast<T> $value(Field<?> newValue) {
            return $arg1(newValue);
        }

        @Override // org.jooq.Typed
        @NotNull
        default DataType<T> $dataType() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default TryCast<T> $dataType(DataType<T> newDataType) {
            return $arg2(newDataType);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLComment.class */
    public interface XMLComment extends UOperator1<Field<String>, XMLComment>, Field<XML> {
        @NotNull
        default Field<String> $comment() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default XMLComment $comment(Field<String> newComment) {
            return $arg1(newComment);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLForest.class */
    public interface XMLForest extends UOperator1<UnmodifiableList<? extends Field<?>>, XMLForest>, Field<XML> {
        @NotNull
        default UnmodifiableList<? extends Field<?>> $fields() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default XMLForest $fields(UnmodifiableList<? extends Field<?>> newFields) {
            return $arg1(newFields);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLPi.class */
    public interface XMLPi extends UOperator2<Name, Field<?>, XMLPi>, Field<XML> {
        @NotNull
        default Name $target() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default XMLPi $target(Name newTarget) {
            return $arg1(newTarget);
        }

        @Nullable
        default Field<?> $content() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default XMLPi $content(Field<?> newContent) {
            return $arg2(newContent);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLSerialize.class */
    public interface XMLSerialize<T> extends UOperator3<Boolean, Field<XML>, DataType<T>, XMLSerialize<T>>, Field<T> {
        @NotNull
        default Boolean $content() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default XMLSerialize<T> $content(Boolean newContent) {
            return $arg1(newContent);
        }

        @NotNull
        default Field<XML> $value() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default XMLSerialize<T> $value(Field<XML> newValue) {
            return $arg2(newValue);
        }

        @NotNull
        default DataType<T> $type() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default XMLSerialize<T> $type(DataType<T> newType) {
            return $arg3(newType);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONArray.class */
    public interface JSONArray<T> extends UOperator4<DataType<T>, UnmodifiableList<? extends Field<?>>, JSONOnNull, DataType<?>, JSONArray<T>>, Field<T> {
        @NotNull
        default DataType<T> $type() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONArray<T> $type(DataType<T> newType) {
            return $arg1(newType);
        }

        @NotNull
        default UnmodifiableList<? extends Field<?>> $fields() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONArray<T> $fields(UnmodifiableList<? extends Field<?>> newFields) {
            return $arg2(newFields);
        }

        @Nullable
        default JSONOnNull $onNull() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default JSONArray<T> $onNull(JSONOnNull newOnNull) {
            return $arg3(newOnNull);
        }

        @Nullable
        default DataType<?> $returning() {
            return $arg4();
        }

        @CheckReturnValue
        @NotNull
        default JSONArray<T> $returning(DataType<?> newReturning) {
            return $arg4(newReturning);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONObject.class */
    public interface JSONObject<T> extends UOperator4<DataType<T>, UnmodifiableList<? extends JSONEntry<?>>, JSONOnNull, DataType<?>, JSONObject<T>>, Field<T> {
        @NotNull
        default DataType<T> $type() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONObject<T> $type(DataType<T> newType) {
            return $arg1(newType);
        }

        @NotNull
        default UnmodifiableList<? extends JSONEntry<?>> $entries() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONObject<T> $entries(UnmodifiableList<? extends JSONEntry<?>> newEntries) {
            return $arg2(newEntries);
        }

        @Nullable
        default JSONOnNull $onNull() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default JSONObject<T> $onNull(JSONOnNull newOnNull) {
            return $arg3(newOnNull);
        }

        @Nullable
        default DataType<?> $returning() {
            return $arg4();
        }

        @CheckReturnValue
        @NotNull
        default JSONObject<T> $returning(DataType<?> newReturning) {
            return $arg4(newReturning);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONGetElement.class */
    public interface JSONGetElement extends UReturnsNullOnNullInput, UOperator2<Field<JSON>, Field<Integer>, JSONGetElement>, Field<JSON> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONGetElement $field(Field<JSON> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<Integer> $index() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONGetElement $index(Field<Integer> newIndex) {
            return $arg2(newIndex);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBGetElement.class */
    public interface JSONBGetElement extends UReturnsNullOnNullInput, UOperator2<Field<JSONB>, Field<Integer>, JSONBGetElement>, Field<JSONB> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBGetElement $field(Field<JSONB> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<Integer> $index() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONBGetElement $index(Field<Integer> newIndex) {
            return $arg2(newIndex);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONGetElementAsText.class */
    public interface JSONGetElementAsText extends UReturnsNullOnNullInput, UOperator2<Field<JSON>, Field<Integer>, JSONGetElementAsText>, Field<String> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONGetElementAsText $field(Field<JSON> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<Integer> $index() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONGetElementAsText $index(Field<Integer> newIndex) {
            return $arg2(newIndex);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBGetElementAsText.class */
    public interface JSONBGetElementAsText extends UReturnsNullOnNullInput, UOperator2<Field<JSONB>, Field<Integer>, JSONBGetElementAsText>, Field<String> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBGetElementAsText $field(Field<JSONB> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<Integer> $index() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONBGetElementAsText $index(Field<Integer> newIndex) {
            return $arg2(newIndex);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONGetAttribute.class */
    public interface JSONGetAttribute extends UReturnsNullOnNullInput, UOperator2<Field<JSON>, Field<String>, JSONGetAttribute>, Field<JSON> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONGetAttribute $field(Field<JSON> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $attribute() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONGetAttribute $attribute(Field<String> newAttribute) {
            return $arg2(newAttribute);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBGetAttribute.class */
    public interface JSONBGetAttribute extends UReturnsNullOnNullInput, UOperator2<Field<JSONB>, Field<String>, JSONBGetAttribute>, Field<JSONB> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBGetAttribute $field(Field<JSONB> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $attribute() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONBGetAttribute $attribute(Field<String> newAttribute) {
            return $arg2(newAttribute);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONGetAttributeAsText.class */
    public interface JSONGetAttributeAsText extends UReturnsNullOnNullInput, UOperator2<Field<JSON>, Field<String>, JSONGetAttributeAsText>, Field<String> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONGetAttributeAsText $field(Field<JSON> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $attribute() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONGetAttributeAsText $attribute(Field<String> newAttribute) {
            return $arg2(newAttribute);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBGetAttributeAsText.class */
    public interface JSONBGetAttributeAsText extends UReturnsNullOnNullInput, UOperator2<Field<JSONB>, Field<String>, JSONBGetAttributeAsText>, Field<String> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBGetAttributeAsText $field(Field<JSONB> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $attribute() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONBGetAttributeAsText $attribute(Field<String> newAttribute) {
            return $arg2(newAttribute);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONKeys.class */
    public interface JSONKeys extends UReturnsNullOnNullInput, UOperator1<Field<JSON>, JSONKeys>, Field<JSON> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONKeys $field(Field<JSON> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBKeys.class */
    public interface JSONBKeys extends UReturnsNullOnNullInput, UOperator1<Field<JSONB>, JSONBKeys>, Field<JSONB> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBKeys $field(Field<JSONB> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONSet.class */
    public interface JSONSet extends UOperator3<Field<JSON>, Field<String>, Field<?>, JSONSet>, Field<JSON> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONSet $field(Field<JSON> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $path() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONSet $path(Field<String> newPath) {
            return $arg2(newPath);
        }

        @NotNull
        default Field<?> $value() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default JSONSet $value(Field<?> newValue) {
            return $arg3(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBSet.class */
    public interface JSONBSet extends UOperator3<Field<JSONB>, Field<String>, Field<?>, JSONBSet>, Field<JSONB> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBSet $field(Field<JSONB> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $path() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONBSet $path(Field<String> newPath) {
            return $arg2(newPath);
        }

        @NotNull
        default Field<?> $value() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default JSONBSet $value(Field<?> newValue) {
            return $arg3(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONInsert.class */
    public interface JSONInsert extends UOperator3<Field<JSON>, Field<String>, Field<?>, JSONInsert>, Field<JSON> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONInsert $field(Field<JSON> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $path() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONInsert $path(Field<String> newPath) {
            return $arg2(newPath);
        }

        @NotNull
        default Field<?> $value() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default JSONInsert $value(Field<?> newValue) {
            return $arg3(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBInsert.class */
    public interface JSONBInsert extends UOperator3<Field<JSONB>, Field<String>, Field<?>, JSONBInsert>, Field<JSONB> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBInsert $field(Field<JSONB> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $path() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONBInsert $path(Field<String> newPath) {
            return $arg2(newPath);
        }

        @NotNull
        default Field<?> $value() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default JSONBInsert $value(Field<?> newValue) {
            return $arg3(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONReplace.class */
    public interface JSONReplace extends UOperator3<Field<JSON>, Field<String>, Field<?>, JSONReplace>, Field<JSON> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONReplace $field(Field<JSON> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $path() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONReplace $path(Field<String> newPath) {
            return $arg2(newPath);
        }

        @NotNull
        default Field<?> $value() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default JSONReplace $value(Field<?> newValue) {
            return $arg3(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBReplace.class */
    public interface JSONBReplace extends UOperator3<Field<JSONB>, Field<String>, Field<?>, JSONBReplace>, Field<JSONB> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBReplace $field(Field<JSONB> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $path() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONBReplace $path(Field<String> newPath) {
            return $arg2(newPath);
        }

        @NotNull
        default Field<?> $value() {
            return $arg3();
        }

        @CheckReturnValue
        @NotNull
        default JSONBReplace $value(Field<?> newValue) {
            return $arg3(newValue);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONRemove.class */
    public interface JSONRemove extends UReturnsNullOnNullInput, UOperator2<Field<JSON>, Field<String>, JSONRemove>, Field<JSON> {
        @NotNull
        default Field<JSON> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONRemove $field(Field<JSON> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $path() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONRemove $path(Field<String> newPath) {
            return $arg2(newPath);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONBRemove.class */
    public interface JSONBRemove extends UReturnsNullOnNullInput, UOperator2<Field<JSONB>, Field<String>, JSONBRemove>, Field<JSONB> {
        @NotNull
        default Field<JSONB> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default JSONBRemove $field(Field<JSONB> newField) {
            return $arg1(newField);
        }

        @NotNull
        default Field<String> $path() {
            return $arg2();
        }

        @CheckReturnValue
        @NotNull
        default JSONBRemove $path(Field<String> newPath) {
            return $arg2(newPath);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ConditionAsField.class */
    public interface ConditionAsField extends UReturnsNullOnNullInput, UOperator1<Condition, ConditionAsField>, Field<Boolean> {
        @NotNull
        default Condition $condition() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default ConditionAsField $condition(Condition newCondition) {
            return $arg1(newCondition);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$FieldCondition.class */
    public interface FieldCondition extends UReturnsNullOnNullInput, UOperator1<Field<Boolean>, FieldCondition>, Condition {
        @NotNull
        default Field<Boolean> $field() {
            return $arg1();
        }

        @CheckReturnValue
        @NotNull
        default FieldCondition $field(Field<Boolean> newField) {
            return $arg1(newField);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Cascade.class */
    public enum Cascade {
        CASCADE(DSL.keyword("cascade")),
        RESTRICT(DSL.keyword("restrict"));

        final Keyword keyword;

        Cascade(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$CycleOption.class */
    public enum CycleOption {
        CYCLE(DSL.keyword("cycle")),
        NO_CYCLE(DSL.keyword("no cycle"));

        final Keyword keyword;

        CycleOption(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$IdentityRestartOption.class */
    public enum IdentityRestartOption {
        CONTINUE_IDENTITY(DSL.keyword("continue identity")),
        RESTART_IDENTITY(DSL.keyword("restart identity"));

        final Keyword keyword;

        IdentityRestartOption(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$GenerationLocation.class */
    public enum GenerationLocation {
        CLIENT(DSL.keyword("client")),
        SERVER(DSL.keyword("server"));

        final Keyword keyword;

        GenerationLocation(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$GenerationOption.class */
    public enum GenerationOption {
        STORED(DSL.keyword("stored")),
        VIRTUAL(DSL.keyword("virtual")),
        DEFAULT(DSL.keyword("default"));

        final Keyword keyword;

        GenerationOption(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$WithOrWithoutData.class */
    public enum WithOrWithoutData {
        WITH_DATA(DSL.keyword("with data")),
        WITH_NO_DATA(DSL.keyword("with no data"));

        final Keyword keyword;

        WithOrWithoutData(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$TableCommitAction.class */
    public enum TableCommitAction {
        DELETE_ROWS(DSL.keyword("delete rows")),
        PRESERVE_ROWS(DSL.keyword("preserve rows")),
        DROP(DSL.keyword("drop"));

        final Keyword keyword;

        TableCommitAction(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NullOrdering.class */
    public enum NullOrdering {
        NULLS_FIRST(DSL.keyword("nulls first")),
        NULLS_LAST(DSL.keyword("nulls last"));

        final Keyword keyword;

        NullOrdering(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$NullTreatment.class */
    public enum NullTreatment {
        RESPECT_NULLS(DSL.keyword("respect nulls")),
        IGNORE_NULLS(DSL.keyword("ignore nulls"));

        final Keyword keyword;

        NullTreatment(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$FromFirstOrLast.class */
    public enum FromFirstOrLast {
        FROM_FIRST(DSL.keyword("from first")),
        FROM_LAST(DSL.keyword("from last"));

        final Keyword keyword;

        FromFirstOrLast(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$FrameUnits.class */
    public enum FrameUnits {
        ROWS(DSL.keyword(TextareaTag.ROWS_ATTRIBUTE)),
        RANGE(DSL.keyword("range")),
        GROUPS(DSL.keyword("groups"));

        final Keyword keyword;

        FrameUnits(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$FrameExclude.class */
    public enum FrameExclude {
        CURRENT_ROW(DSL.keyword("current row")),
        TIES(DSL.keyword("ties")),
        GROUP(DSL.keyword("group")),
        NO_OTHERS(DSL.keyword("no others"));

        final Keyword keyword;

        FrameExclude(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JSONOnNull.class */
    public enum JSONOnNull {
        NULL_ON_NULL(DSL.keyword("null on null")),
        ABSENT_ON_NULL(DSL.keyword("absent on null"));

        final Keyword keyword;

        JSONOnNull(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$XMLPassingMechanism.class */
    public enum XMLPassingMechanism {
        BY_REF(DSL.keyword("by ref")),
        BY_VALUE(DSL.keyword("by value")),
        DEFAULT(DSL.keyword("default"));

        final Keyword keyword;

        XMLPassingMechanism(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$DocumentOrContent.class */
    public enum DocumentOrContent {
        DOCUMENT(DSL.keyword("document")),
        CONTENT(DSL.keyword("content"));

        final Keyword keyword;

        DocumentOrContent(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Materialized.class */
    public enum Materialized {
        MATERIALIZED(DSL.keyword("materialized")),
        NOT_MATERIALIZED(DSL.keyword("not materialized"));

        final Keyword keyword;

        Materialized(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$ResultOption.class */
    public enum ResultOption {
        OLD(DSL.keyword("old")),
        NEW(DSL.keyword("new")),
        FINAL(DSL.keyword("final"));

        final Keyword keyword;

        ResultOption(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$Quantifier.class */
    public enum Quantifier {
        ANY(DSL.keyword("any")),
        ALL(DSL.keyword(Constants.SSL_PROTO_ALL));

        final Keyword keyword;

        Quantifier(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$JoinHint.class */
    public enum JoinHint {
        HASH(DSL.keyword("hash")),
        LOOP(DSL.keyword("loop")),
        MERGE(DSL.keyword(BeanDefinitionParserDelegate.MERGE_ATTRIBUTE));

        final Keyword keyword;

        JoinHint(Keyword keyword) {
            this.keyword = keyword;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UOperator0.class */
    interface UOperator0<R extends UOperator0<R>> extends UOperator<R> {
        @NotNull
        Function0<? extends R> $constructor();

        @Override // org.jooq.impl.QOM.UOperator
        @NotNull
        default List<?> $args() {
            return Collections.emptyList();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UOperator1.class */
    interface UOperator1<Q1, R extends UOperator1<Q1, R>> extends UOperator<R> {
        Q1 $arg1();

        @NotNull
        org.jooq.Function1<? super Q1, ? extends R> $constructor();

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg1(Q1 q1) {
            return $constructor().apply(q1);
        }

        @Override // org.jooq.impl.QOM.UOperator
        @NotNull
        default List<?> $args() {
            return Collections.unmodifiableList(Arrays.asList($arg1()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UOperator2.class */
    public interface UOperator2<Q1, Q2, R extends UOperator2<Q1, Q2, R>> extends UOperator<R> {
        Q1 $arg1();

        Q2 $arg2();

        @NotNull
        Function2<? super Q1, ? super Q2, ? extends R> $constructor();

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg1(Q1 q1) {
            return $constructor().apply(q1, $arg2());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg2(Q2 q2) {
            return $constructor().apply($arg1(), q2);
        }

        @Override // org.jooq.impl.QOM.UOperator
        @NotNull
        default List<?> $args() {
            return Collections.unmodifiableList(Arrays.asList($arg1(), $arg2()));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UOperator3.class */
    interface UOperator3<Q1, Q2, Q3, R extends UOperator3<Q1, Q2, Q3, R>> extends UOperator<R> {
        Q1 $arg1();

        Q2 $arg2();

        Q3 $arg3();

        @NotNull
        Function3<? super Q1, ? super Q2, ? super Q3, ? extends R> $constructor();

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg1(Q1 q1) {
            return $constructor().apply(q1, $arg2(), $arg3());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg2(Q2 q2) {
            return $constructor().apply($arg1(), q2, $arg3());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg3(Q3 q3) {
            return $constructor().apply($arg1(), $arg2(), q3);
        }

        @Override // org.jooq.impl.QOM.UOperator
        @NotNull
        default List<?> $args() {
            return Collections.unmodifiableList(Arrays.asList($arg1(), $arg2(), $arg3()));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UOperator4.class */
    interface UOperator4<Q1, Q2, Q3, Q4, R extends UOperator4<Q1, Q2, Q3, Q4, R>> extends UOperator<R> {
        Q1 $arg1();

        Q2 $arg2();

        Q3 $arg3();

        Q4 $arg4();

        @NotNull
        Function4<? super Q1, ? super Q2, ? super Q3, ? super Q4, ? extends R> $constructor();

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg1(Q1 q1) {
            return $constructor().apply(q1, $arg2(), $arg3(), $arg4());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg2(Q2 q2) {
            return $constructor().apply($arg1(), q2, $arg3(), $arg4());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg3(Q3 q3) {
            return $constructor().apply($arg1(), $arg2(), q3, $arg4());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @CheckReturnValue
        @NotNull
        default R $arg4(Q4 q4) {
            return $constructor().apply($arg1(), $arg2(), $arg3(), q4);
        }

        @Override // org.jooq.impl.QOM.UOperator
        @NotNull
        default List<?> $args() {
            return Collections.unmodifiableList(Arrays.asList($arg1(), $arg2(), $arg3(), $arg4()));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QOM$UCommutativeOperator.class */
    interface UCommutativeOperator<Q, R extends UCommutativeOperator<Q, R>> extends UConvertibleOperator<Q, R, R> {
        @CheckReturnValue
        @NotNull
        default R $swap() {
            return (R) $constructor().apply($arg2(), $arg1());
        }

        @Override // org.jooq.impl.QOM.UConvertibleOperator
        @CheckReturnValue
        @NotNull
        default R $converse() {
            return $swap();
        }
    }

    @ApiStatus.Internal
    public static final <Q extends QueryPart> UnmodifiableList<Q> unmodifiable(Q[] array) {
        return unmodifiable((List) QueryPartListView.wrap(array));
    }

    @ApiStatus.Internal
    public static final <Q extends QueryPart> UnmodifiableList<Q> unmodifiable(List<Q> list) {
        return QueryPartListView.wrap(Collections.unmodifiableList(list));
    }

    @ApiStatus.Internal
    public static final <Q extends QueryPart> UnmodifiableList<Q> unmodifiable(Collection<Q> collection) {
        if (collection instanceof List) {
            return unmodifiable((List) collection);
        }
        return new QueryPartList(Collections.unmodifiableCollection(collection));
    }

    @ApiStatus.Internal
    public static final <K extends QueryPart, V extends QueryPart> UnmodifiableMap<K, V> unmodifiable(Map<K, V> map) {
        return new QueryPartMapView(Collections.unmodifiableMap(map));
    }

    @ApiStatus.Internal
    public static final <Q1 extends QueryPart, Q2 extends QueryPart> Tuple2<Q1, Q2> tuple(Q1 q1, Q2 q2) {
        return new TupleImpl2(q1, q2);
    }

    static final <Q extends QueryPart> boolean commutativeCheck(UCommutativeOperator<Q, ?> uCommutativeOperator, Predicate<? super Q> predicate) {
        return predicate.test(uCommutativeOperator.$arg1()) || predicate.test((Object) uCommutativeOperator.$arg2());
    }

    static final <Q extends QueryPart> boolean commutativeCheck(UCommutativeOperator<Q, ?> uCommutativeOperator, BiPredicate<? super Q, ? super Q> biPredicate) {
        return biPredicate.test(uCommutativeOperator.$arg1(), (Object) uCommutativeOperator.$arg2()) || biPredicate.test((Object) uCommutativeOperator.$arg2(), uCommutativeOperator.$arg1());
    }
}
