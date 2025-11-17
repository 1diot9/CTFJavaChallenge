package org.jooq.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jooq.BetweenAndStep;
import org.jooq.Binding;
import org.jooq.Clause;
import org.jooq.Collation;
import org.jooq.Comment;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.LikeEscapeStep;
import org.jooq.Name;
import org.jooq.QuantifiedSelect;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.WindowIgnoreNullsStep;
import org.jooq.WindowPartitionByStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractField.class */
public abstract class AbstractField<T> extends AbstractTypedNamed<T> implements Field<T>, QOM.Aliasable<Field<?>> {
    private static final Clause[] CLAUSES = {Clause.FIELD};

    @Override // org.jooq.QueryPartInternal
    public abstract void accept(Context<?> context);

    @Override // org.jooq.Field, org.jooq.SelectField
    public /* bridge */ /* synthetic */ SelectField as(Field field) {
        return as((Field<?>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractField(Name name, DataType<T> type) {
        this(name, type, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractField(Name name, DataType<T> type, Comment comment) {
        this(name, type, comment, type.getBinding());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public AbstractField(Name name, DataType<T> type, Comment comment, Binding<?, T> binding) {
        super(name, comment, type.asConvertedDataType(binding));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isNullable() {
        return true;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPossiblyNullable() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int projectionSize() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean parenthesised(Context<?> ctx) {
        return false;
    }

    @Override // org.jooq.Field
    public final Field<T> field(Record record) {
        return record.field(this);
    }

    @Override // org.jooq.Field
    public final T get(Record record) {
        return (T) record.get(this);
    }

    @Override // org.jooq.Field
    public final T getValue(Record record) {
        return (T) record.getValue(this);
    }

    @Override // org.jooq.Field
    public final T original(Record record) {
        return (T) record.original(this);
    }

    @Override // org.jooq.Field
    public final boolean changed(Record record) {
        return record.changed(this);
    }

    @Override // org.jooq.Field
    public final void reset(Record record) {
        record.reset(this);
    }

    @Override // org.jooq.Field
    public final Record1<T> from(Record record) {
        return record.into((Field) this);
    }

    @Override // org.jooq.impl.QOM.Aliasable
    public Name $alias() {
        return null;
    }

    @Override // org.jooq.impl.QOM.Aliasable
    public Field<?> $aliased() {
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field, org.jooq.SelectField
    public final <U> Field<U> convert(Binding<T, U> binding) {
        return (Field<U>) coerce(getDataType().asConvertedDataType(binding));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field, org.jooq.SelectField
    public final <U> Field<U> convert(Converter<T, U> converter) {
        return (Field<U>) coerce(getDataType().asConvertedDataType(converter));
    }

    @Override // org.jooq.Field, org.jooq.SelectField
    public final <U> Field<U> convert(Class<U> cls, java.util.function.Function<? super T, ? extends U> function, java.util.function.Function<? super U, ? extends T> function2) {
        return (Field<U>) coerce(getDataType().asConvertedDataType(cls, function, function2));
    }

    @Override // org.jooq.Field, org.jooq.SelectField
    public final <U> Field<U> convertFrom(Class<U> cls, java.util.function.Function<? super T, ? extends U> function) {
        return (Field<U>) coerce(getDataType().asConvertedDataTypeFrom(cls, function));
    }

    @Override // org.jooq.Field, org.jooq.SelectField
    public final <U> Field<U> convertFrom(java.util.function.Function<? super T, ? extends U> function) {
        return (Field<U>) coerce(getDataType().asConvertedDataTypeFrom(function));
    }

    @Override // org.jooq.Field, org.jooq.SelectField
    public final <U> Field<U> convertTo(Class<U> cls, java.util.function.Function<? super U, ? extends T> function) {
        return (Field<U>) coerce(getDataType().asConvertedDataTypeTo(cls, function));
    }

    @Override // org.jooq.Field, org.jooq.SelectField
    public final <U> Field<U> convertTo(java.util.function.Function<? super U, ? extends T> function) {
        return (Field<U>) coerce(getDataType().asConvertedDataTypeTo(function));
    }

    @Override // org.jooq.Field, org.jooq.SelectField
    public final Field<T> as(String alias) {
        return as(DSL.name(alias));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field, org.jooq.SelectField
    public Field<T> as(Name alias) {
        Field<Boolean> field;
        if (this instanceof Condition) {
            Condition c = (Condition) this;
            field = DSL.field(c);
        } else {
            field = this;
        }
        return new FieldAlias(field, alias);
    }

    @Override // org.jooq.Field, org.jooq.SelectField
    public final Field<T> as(Field<?> otherField) {
        return as(otherField.getUnqualifiedName());
    }

    @Override // org.jooq.Field
    public final Field<T> as(java.util.function.Function<? super Field<T>, ? extends String> aliasFunction) {
        return as(aliasFunction.apply(this));
    }

    @Override // org.jooq.Field
    public final Field<T> comment(String comment) {
        return comment(DSL.comment(comment));
    }

    @Override // org.jooq.Field
    public final Field<T> comment(Comment comment) {
        return DSL.field(getQualifiedName(), getDataType(), comment);
    }

    @Override // org.jooq.Field
    public final <Z> Field<Z> cast(Field<Z> field) {
        return cast(field.getDataType());
    }

    @Override // org.jooq.Field
    public final <Z> Field<Z> cast(DataType<Z> type) {
        return new Cast(this, type);
    }

    @Override // org.jooq.Field
    public final <Z> Field<Z> cast(Class<Z> type) {
        return cast(DefaultDataType.getDataType((SQLDialect) null, type));
    }

    @Override // org.jooq.Field
    public final <Z> Field<Z> coerce(Field<Z> field) {
        return coerce(field.getDataType());
    }

    @Override // org.jooq.Field
    public final <Z> Field<Z> coerce(DataType<Z> type) {
        return new Coerce(this, type);
    }

    @Override // org.jooq.Field
    public final <Z> Field<Z> coerce(Class<Z> type) {
        return coerce(DefaultDataType.getDataType((SQLDialect) null, type));
    }

    @Override // org.jooq.Field
    public final SortField<T> asc() {
        return sort(SortOrder.ASC);
    }

    @Override // org.jooq.Field
    public final SortField<T> desc() {
        return sort(SortOrder.DESC);
    }

    @Override // org.jooq.Field
    public final SortField<T> sortDefault() {
        return sort(SortOrder.DEFAULT);
    }

    @Override // org.jooq.Field
    public final SortField<T> sort(SortOrder order) {
        if (!(this instanceof NoField)) {
            return new SortFieldImpl(this, order);
        }
        NoField<T> n = (NoField) this;
        return n;
    }

    @Override // org.jooq.Field
    public final SortField<Integer> sortAsc(Collection<T> sortList) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int i = 0;
        for (T value : sortList) {
            int i2 = i;
            i++;
            linkedHashMap.put(value, Integer.valueOf(i2));
        }
        return sort(linkedHashMap);
    }

    @Override // org.jooq.Field
    @SafeVarargs
    public final SortField<Integer> sortAsc(T... sortList) {
        return sortAsc(Arrays.asList(sortList));
    }

    @Override // org.jooq.Field
    public final SortField<Integer> sortDesc(Collection<T> sortList) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int i = sortList.size();
        for (T value : sortList) {
            int i2 = i;
            i--;
            linkedHashMap.put(value, Integer.valueOf(i2));
        }
        return sort(linkedHashMap);
    }

    @Override // org.jooq.Field
    @SafeVarargs
    public final SortField<Integer> sortDesc(T... sortList) {
        return sortDesc(Arrays.asList(sortList));
    }

    @Override // org.jooq.Field
    public final <Z> SortField<Z> sort(Map<T, Z> map) {
        if (map == null || map.isEmpty()) {
            return DSL.noField(this).sortDefault();
        }
        return DSL.case_((Field) this).mapValues(map).asc();
    }

    @Override // org.jooq.Field
    public final SortField<T> nullsFirst() {
        return sortDefault().nullsFirst();
    }

    @Override // org.jooq.Field
    public final SortField<T> nullsLast() {
        return sortDefault().nullsLast();
    }

    @Override // org.jooq.Field
    public final Condition eq(T arg2) {
        return new Eq(this, Tools.field(arg2, this));
    }

    @Override // org.jooq.Field
    public final Condition eq(Select<? extends Record1<T>> arg2) {
        return new Eq(this, DSL.field(arg2));
    }

    @Override // org.jooq.Field
    public final Condition eq(Field<T> arg2) {
        return new Eq(this, Tools.nullSafe((Field) arg2, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition eq(QuantifiedSelect<? extends Record1<T>> arg2) {
        return new EqQuantified(this, arg2);
    }

    @Override // org.jooq.Field
    public final Condition equal(T arg2) {
        return eq((AbstractField<T>) arg2);
    }

    @Override // org.jooq.Field
    public final Condition equal(Select<? extends Record1<T>> arg2) {
        return eq((Select) arg2);
    }

    @Override // org.jooq.Field
    public final Condition equal(Field<T> arg2) {
        return eq((Field) arg2);
    }

    @Override // org.jooq.Field
    public final Condition equal(QuantifiedSelect<? extends Record1<T>> arg2) {
        return eq((QuantifiedSelect) arg2);
    }

    @Override // org.jooq.Field
    public final Condition ge(T arg2) {
        return new Ge(this, Tools.field(arg2, this));
    }

    @Override // org.jooq.Field
    public final Condition ge(Select<? extends Record1<T>> arg2) {
        return new Ge(this, DSL.field(arg2));
    }

    @Override // org.jooq.Field
    public final Condition ge(Field<T> arg2) {
        return new Ge(this, Tools.nullSafe((Field) arg2, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition ge(QuantifiedSelect<? extends Record1<T>> arg2) {
        return new GeQuantified(this, arg2);
    }

    @Override // org.jooq.Field
    public final Condition greaterOrEqual(T arg2) {
        return ge((AbstractField<T>) arg2);
    }

    @Override // org.jooq.Field
    public final Condition greaterOrEqual(Select<? extends Record1<T>> arg2) {
        return ge((Select) arg2);
    }

    @Override // org.jooq.Field
    public final Condition greaterOrEqual(Field<T> arg2) {
        return ge((Field) arg2);
    }

    @Override // org.jooq.Field
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record1<T>> arg2) {
        return ge((QuantifiedSelect) arg2);
    }

    @Override // org.jooq.Field
    public final Condition greaterThan(T arg2) {
        return gt((AbstractField<T>) arg2);
    }

    @Override // org.jooq.Field
    public final Condition greaterThan(Select<? extends Record1<T>> arg2) {
        return gt((Select) arg2);
    }

    @Override // org.jooq.Field
    public final Condition greaterThan(Field<T> arg2) {
        return gt((Field) arg2);
    }

    @Override // org.jooq.Field
    public final Condition greaterThan(QuantifiedSelect<? extends Record1<T>> arg2) {
        return gt((QuantifiedSelect) arg2);
    }

    @Override // org.jooq.Field
    public final Condition gt(T arg2) {
        return new Gt(this, Tools.field(arg2, this));
    }

    @Override // org.jooq.Field
    public final Condition gt(Select<? extends Record1<T>> arg2) {
        return new Gt(this, DSL.field(arg2));
    }

    @Override // org.jooq.Field
    public final Condition gt(Field<T> arg2) {
        return new Gt(this, Tools.nullSafe((Field) arg2, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition gt(QuantifiedSelect<? extends Record1<T>> arg2) {
        return new GtQuantified(this, arg2);
    }

    @Override // org.jooq.Field
    public final Condition in(Select<? extends Record1<T>> arg2) {
        return new In(this, arg2);
    }

    @Override // org.jooq.Field
    public final Condition isDistinctFrom(T arg2) {
        return new IsDistinctFrom(this, Tools.field(arg2, this));
    }

    @Override // org.jooq.Field
    public final Condition isDistinctFrom(Select<? extends Record1<T>> arg2) {
        return new IsDistinctFrom(this, DSL.field(arg2));
    }

    @Override // org.jooq.Field
    public final Condition isDistinctFrom(Field<T> arg2) {
        return new IsDistinctFrom(this, Tools.nullSafe((Field) arg2, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition isNull() {
        return new IsNull(this);
    }

    @Override // org.jooq.Field
    public final Condition isNotDistinctFrom(T arg2) {
        return new IsNotDistinctFrom(this, Tools.field(arg2, this));
    }

    @Override // org.jooq.Field
    public final Condition isNotDistinctFrom(Select<? extends Record1<T>> arg2) {
        return new IsNotDistinctFrom(this, DSL.field(arg2));
    }

    @Override // org.jooq.Field
    public final Condition isNotDistinctFrom(Field<T> arg2) {
        return new IsNotDistinctFrom(this, Tools.nullSafe((Field) arg2, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition isNotNull() {
        return new IsNotNull(this);
    }

    @Override // org.jooq.Field
    public final Condition le(T arg2) {
        return new Le(this, Tools.field(arg2, this));
    }

    @Override // org.jooq.Field
    public final Condition le(Select<? extends Record1<T>> arg2) {
        return new Le(this, DSL.field(arg2));
    }

    @Override // org.jooq.Field
    public final Condition le(Field<T> arg2) {
        return new Le(this, Tools.nullSafe((Field) arg2, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition le(QuantifiedSelect<? extends Record1<T>> arg2) {
        return new LeQuantified(this, arg2);
    }

    @Override // org.jooq.Field
    public final Condition lessOrEqual(T arg2) {
        return le((AbstractField<T>) arg2);
    }

    @Override // org.jooq.Field
    public final Condition lessOrEqual(Select<? extends Record1<T>> arg2) {
        return le((Select) arg2);
    }

    @Override // org.jooq.Field
    public final Condition lessOrEqual(Field<T> arg2) {
        return le((Field) arg2);
    }

    @Override // org.jooq.Field
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record1<T>> arg2) {
        return le((QuantifiedSelect) arg2);
    }

    @Override // org.jooq.Field
    public final Condition lessThan(T arg2) {
        return lt((AbstractField<T>) arg2);
    }

    @Override // org.jooq.Field
    public final Condition lessThan(Select<? extends Record1<T>> arg2) {
        return lt((Select) arg2);
    }

    @Override // org.jooq.Field
    public final Condition lessThan(Field<T> arg2) {
        return lt((Field) arg2);
    }

    @Override // org.jooq.Field
    public final Condition lessThan(QuantifiedSelect<? extends Record1<T>> arg2) {
        return lt((QuantifiedSelect) arg2);
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep like(String pattern) {
        return new Like(this, Tools.field(pattern));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep like(Field<String> pattern) {
        return new Like(this, Tools.nullSafe((Field) pattern, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep like(QuantifiedSelect<? extends Record1<String>> pattern) {
        return new LikeQuantified(this, pattern);
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep likeIgnoreCase(String pattern) {
        return new LikeIgnoreCase(this, Tools.field(pattern));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep likeIgnoreCase(Field<String> pattern) {
        return new LikeIgnoreCase(this, Tools.nullSafe((Field) pattern, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition lt(T arg2) {
        return new Lt(this, Tools.field(arg2, this));
    }

    @Override // org.jooq.Field
    public final Condition lt(Select<? extends Record1<T>> arg2) {
        return new Lt(this, DSL.field(arg2));
    }

    @Override // org.jooq.Field
    public final Condition lt(Field<T> arg2) {
        return new Lt(this, Tools.nullSafe((Field) arg2, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition lt(QuantifiedSelect<? extends Record1<T>> arg2) {
        return new LtQuantified(this, arg2);
    }

    @Override // org.jooq.Field
    public final Condition ne(T arg2) {
        return new Ne(this, Tools.field(arg2, this));
    }

    @Override // org.jooq.Field
    public final Condition ne(Select<? extends Record1<T>> arg2) {
        return new Ne(this, DSL.field(arg2));
    }

    @Override // org.jooq.Field
    public final Condition ne(Field<T> arg2) {
        return new Ne(this, Tools.nullSafe((Field) arg2, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition ne(QuantifiedSelect<? extends Record1<T>> arg2) {
        return new NeQuantified(this, arg2);
    }

    @Override // org.jooq.Field
    public final Condition notEqual(T arg2) {
        return ne((AbstractField<T>) arg2);
    }

    @Override // org.jooq.Field
    public final Condition notEqual(Select<? extends Record1<T>> arg2) {
        return ne((Select) arg2);
    }

    @Override // org.jooq.Field
    public final Condition notEqual(Field<T> arg2) {
        return ne((Field) arg2);
    }

    @Override // org.jooq.Field
    public final Condition notEqual(QuantifiedSelect<? extends Record1<T>> arg2) {
        return ne((QuantifiedSelect) arg2);
    }

    @Override // org.jooq.Field
    public final Condition notIn(Select<? extends Record1<T>> arg2) {
        return new NotIn(this, arg2);
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep notLike(String pattern) {
        return new NotLike(this, Tools.field(pattern));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep notLike(Field<String> pattern) {
        return new NotLike(this, Tools.nullSafe((Field) pattern, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep notLike(QuantifiedSelect<? extends Record1<String>> pattern) {
        return new NotLikeQuantified(this, pattern);
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep notLikeIgnoreCase(String pattern) {
        return new NotLikeIgnoreCase(this, Tools.field(pattern));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep notLikeIgnoreCase(Field<String> pattern) {
        return new NotLikeIgnoreCase(this, Tools.nullSafe((Field) pattern, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep notSimilarTo(String pattern) {
        return new NotSimilarTo(this, Tools.field(pattern));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep notSimilarTo(Field<String> pattern) {
        return new NotSimilarTo(this, Tools.nullSafe((Field) pattern, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep notSimilarTo(QuantifiedSelect<? extends Record1<String>> pattern) {
        return new NotSimilarToQuantified(this, pattern);
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep similarTo(String pattern) {
        return new SimilarTo(this, Tools.field(pattern));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep similarTo(Field<String> pattern) {
        return new SimilarTo(this, Tools.nullSafe((Field) pattern, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final LikeEscapeStep similarTo(QuantifiedSelect<? extends Record1<String>> pattern) {
        return new SimilarToQuantified(this, pattern);
    }

    @Override // org.jooq.Field
    public final Condition isDocument() {
        return new IsDocument(this);
    }

    @Override // org.jooq.Field
    public final Condition isNotDocument() {
        return new IsNotDocument(this);
    }

    @Override // org.jooq.Field
    public final Condition isJson() {
        return new IsJson(this);
    }

    @Override // org.jooq.Field
    public final Condition isNotJson() {
        return new IsNotJson(this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field
    public final Field<T> bitAnd(T t) {
        return DSL.bitAnd(this, (Number) t);
    }

    @Override // org.jooq.Field
    public final Field<T> bitAnd(Field<T> arg2) {
        return DSL.bitAnd(this, arg2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field
    public final Field<T> bitNand(T t) {
        return DSL.bitNand(this, (Number) t);
    }

    @Override // org.jooq.Field
    public final Field<T> bitNand(Field<T> arg2) {
        return DSL.bitNand(this, arg2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field
    public final Field<T> bitNor(T t) {
        return DSL.bitNor(this, (Number) t);
    }

    @Override // org.jooq.Field
    public final Field<T> bitNor(Field<T> arg2) {
        return DSL.bitNor(this, arg2);
    }

    @Override // org.jooq.Field
    public final Field<T> bitNot() {
        return DSL.bitNot(this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field
    public final Field<T> bitOr(T t) {
        return DSL.bitOr(this, (Number) t);
    }

    @Override // org.jooq.Field
    public final Field<T> bitOr(Field<T> arg2) {
        return DSL.bitOr(this, arg2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field
    public final Field<T> bitXNor(T t) {
        return DSL.bitXNor(this, (Number) t);
    }

    @Override // org.jooq.Field
    public final Field<T> bitXNor(Field<T> arg2) {
        return DSL.bitXNor(this, arg2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Field
    public final Field<T> bitXor(T t) {
        return DSL.bitXor(this, (Number) t);
    }

    @Override // org.jooq.Field
    public final Field<T> bitXor(Field<T> arg2) {
        return DSL.bitXor(this, arg2);
    }

    @Override // org.jooq.Field
    public final Field<T> mod(Number divisor) {
        return new Mod(this, Tools.field(divisor));
    }

    @Override // org.jooq.Field
    public final Field<T> mod(Field<? extends Number> divisor) {
        return new Mod(this, Tools.nullSafe((Field) divisor, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Field<T> modulo(Number divisor) {
        return mod(divisor);
    }

    @Override // org.jooq.Field
    public final Field<T> modulo(Field<? extends Number> divisor) {
        return mod(divisor);
    }

    @Override // org.jooq.Field
    public final Field<T> rem(Number divisor) {
        return mod(divisor);
    }

    @Override // org.jooq.Field
    public final Field<T> rem(Field<? extends Number> divisor) {
        return mod(divisor);
    }

    @Override // org.jooq.Field
    public final Field<BigDecimal> power(Number exponent) {
        return DSL.power(this, exponent);
    }

    @Override // org.jooq.Field
    public final Field<BigDecimal> power(Field<? extends Number> exponent) {
        return DSL.power(this, exponent);
    }

    @Override // org.jooq.Field
    public final Field<BigDecimal> pow(Number exponent) {
        return power(exponent);
    }

    @Override // org.jooq.Field
    public final Field<BigDecimal> pow(Field<? extends Number> exponent) {
        return power(exponent);
    }

    @Override // org.jooq.Field
    public final Field<T> shl(Number count) {
        return DSL.shl(this, count);
    }

    @Override // org.jooq.Field
    public final Field<T> shl(Field<? extends Number> count) {
        return DSL.shl(this, count);
    }

    @Override // org.jooq.Field
    public final Field<T> shr(Number count) {
        return DSL.shr(this, count);
    }

    @Override // org.jooq.Field
    public final Field<T> shr(Field<? extends Number> count) {
        return DSL.shr(this, count);
    }

    @Override // org.jooq.Field
    public final Condition contains(T content) {
        return new Contains(this, Tools.field(content, this));
    }

    @Override // org.jooq.Field
    public final Condition contains(Field<T> content) {
        return new Contains(this, Tools.nullSafe((Field) content, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition containsIgnoreCase(T content) {
        return new ContainsIgnoreCase(this, Tools.field(content, this));
    }

    @Override // org.jooq.Field
    public final Condition containsIgnoreCase(Field<T> content) {
        return new ContainsIgnoreCase(this, Tools.nullSafe((Field) content, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition endsWith(T suffix) {
        return new EndsWith(this, Tools.field(suffix, this));
    }

    @Override // org.jooq.Field
    public final Condition endsWith(Field<T> suffix) {
        return new EndsWith(this, Tools.nullSafe((Field) suffix, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition endsWithIgnoreCase(T suffix) {
        return new EndsWithIgnoreCase(this, Tools.field(suffix, this));
    }

    @Override // org.jooq.Field
    public final Condition endsWithIgnoreCase(Field<T> suffix) {
        return new EndsWithIgnoreCase(this, Tools.nullSafe((Field) suffix, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition startsWith(T prefix) {
        return new StartsWith(this, Tools.field(prefix, this));
    }

    @Override // org.jooq.Field
    public final Condition startsWith(Field<T> prefix) {
        return new StartsWith(this, Tools.nullSafe((Field) prefix, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition startsWithIgnoreCase(T prefix) {
        return new StartsWithIgnoreCase(this, Tools.field(prefix, this));
    }

    @Override // org.jooq.Field
    public final Condition startsWithIgnoreCase(Field<T> prefix) {
        return new StartsWithIgnoreCase(this, Tools.nullSafe((Field) prefix, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Field<T> neg() {
        return new Neg(this, false);
    }

    @Override // org.jooq.Field
    public final Field<T> unaryMinus() {
        return neg();
    }

    @Override // org.jooq.Field
    public final Field<T> unaryPlus() {
        return this;
    }

    @Override // org.jooq.Field
    public final Field<T> add(Number value) {
        return add(Tools.field(value));
    }

    @Override // org.jooq.Field
    public final Field<T> add(Field<?> value) {
        if (getDataType().isDateTime()) {
            Field<?> rhs = Tools.nullSafe(value);
            if (rhs.getDataType().isNumeric() || rhs.getDataType().isInterval()) {
                return new Expression(ExpressionOperator.ADD, false, this, rhs);
            }
        }
        return new Add(this, Tools.nullSafe((Field) value, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Field<T> sub(Number value) {
        return sub(Tools.field(value));
    }

    @Override // org.jooq.Field
    public final Field<T> sub(Field<?> value) {
        if (getDataType().isDateTime()) {
            Field<?> rhs = Tools.nullSafe(value);
            if (rhs.getDataType().isNumeric() || rhs.getDataType().isInterval()) {
                return new Expression(ExpressionOperator.SUBTRACT, false, this, rhs);
            }
        }
        return new Sub(this, Tools.nullSafe((Field) value, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Field<T> mul(Number value) {
        return mul(Tools.field(value));
    }

    @Override // org.jooq.Field
    public final Field<T> mul(Field<? extends Number> value) {
        Field nullSafe;
        if (getDataType().isTemporal() || Tools.nullSafe(value).getDataType().isTemporal()) {
            nullSafe = Tools.nullSafe(value);
        } else {
            nullSafe = Tools.nullSafe((Field) value, (DataType<?>) getDataType());
        }
        return new Mul(this, nullSafe);
    }

    @Override // org.jooq.Field
    public final Field<T> div(Number value) {
        return div(Tools.field(value));
    }

    @Override // org.jooq.Field
    public final Field<T> div(Field<? extends Number> value) {
        Field nullSafe;
        if (getDataType().isTemporal() || Tools.nullSafe(value).getDataType().isTemporal()) {
            nullSafe = Tools.nullSafe(value);
        } else {
            nullSafe = Tools.nullSafe((Field) value, (DataType<?>) getDataType());
        }
        return new Div(this, nullSafe);
    }

    @Override // org.jooq.Field
    public final Field<T> plus(Number value) {
        return add(value);
    }

    @Override // org.jooq.Field
    public final Field<T> plus(Field<?> value) {
        return add(value);
    }

    @Override // org.jooq.Field
    public final Field<T> subtract(Number value) {
        return sub(value);
    }

    @Override // org.jooq.Field
    public final Field<T> subtract(Field<?> value) {
        return sub(value);
    }

    @Override // org.jooq.Field
    public final Field<T> minus(Number value) {
        return sub(value);
    }

    @Override // org.jooq.Field
    public final Field<T> minus(Field<?> value) {
        return sub(value);
    }

    @Override // org.jooq.Field
    public final Field<T> multiply(Number value) {
        return mul(value);
    }

    @Override // org.jooq.Field
    public final Field<T> multiply(Field<? extends Number> value) {
        return mul(value);
    }

    @Override // org.jooq.Field
    public final Field<T> times(Number value) {
        return mul(value);
    }

    @Override // org.jooq.Field
    public final Field<T> times(Field<? extends Number> value) {
        return mul(value);
    }

    @Override // org.jooq.Field
    public final Field<T> divide(Number value) {
        return div(value);
    }

    @Override // org.jooq.Field
    public final Field<T> divide(Field<? extends Number> value) {
        return div(value);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractField$BooleanValues.class */
    private static class BooleanValues {
        static final List<Field<String>> TRUE_VALUES = Tools.map(Convert.TRUE_VALUES, v -> {
            return DSL.inline(v);
        });
        static final List<Field<String>> FALSE_VALUES = Tools.map(Convert.FALSE_VALUES, v -> {
            return DSL.inline(v);
        });

        private BooleanValues() {
        }
    }

    @Override // org.jooq.Field
    public final Condition isTrue() {
        Class<T> type = getType();
        if (type == String.class) {
            return in(BooleanValues.TRUE_VALUES);
        }
        if (Number.class.isAssignableFrom(type)) {
            return equal((Field) DSL.inline((Number) getDataType().convert((Object) 1)));
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return equal((Field) DSL.inline((Object) true, (DataType) getDataType()));
        }
        return Tools.castIfNeeded(this, String.class).in(BooleanValues.TRUE_VALUES);
    }

    @Override // org.jooq.Field
    public final Condition isFalse() {
        Class<T> type = getType();
        if (type == String.class) {
            return in(BooleanValues.FALSE_VALUES);
        }
        if (Number.class.isAssignableFrom(type)) {
            return equal((Field) DSL.inline((Number) getDataType().convert((Object) 0)));
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return equal((Field) DSL.inline((Object) false, (DataType) getDataType()));
        }
        return Tools.castIfNeeded(this, String.class).in(BooleanValues.FALSE_VALUES);
    }

    @Override // org.jooq.Field
    public final Condition similarTo(String value, char escape) {
        return similarTo(Tools.field(value), escape);
    }

    @Override // org.jooq.Field
    public final Condition similarTo(Field<String> field, char escape) {
        return similarTo(field).escape(escape);
    }

    @Override // org.jooq.Field
    public final Condition notSimilarTo(String value, char escape) {
        return notSimilarTo(Tools.field(value), escape);
    }

    @Override // org.jooq.Field
    public final Condition notSimilarTo(Field<String> field, char escape) {
        return notSimilarTo(field).escape(escape);
    }

    @Override // org.jooq.Field
    public final Condition like(String value, char escape) {
        return like(value).escape(escape);
    }

    @Override // org.jooq.Field
    public final Condition like(Field<String> field, char escape) {
        return like(field).escape(escape);
    }

    @Override // org.jooq.Field
    public final Condition likeIgnoreCase(String value, char escape) {
        return likeIgnoreCase(Tools.field(value), escape);
    }

    @Override // org.jooq.Field
    public final Condition likeIgnoreCase(Field<String> field, char escape) {
        return likeIgnoreCase(field).escape(escape);
    }

    @Override // org.jooq.Field
    public final Condition likeRegex(String pattern) {
        return likeRegex(Tools.field(pattern));
    }

    @Override // org.jooq.Field
    public final Condition likeRegex(Field<String> pattern) {
        return new RegexpLike(this, Tools.nullSafe((Field) pattern, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition notLike(String value, char escape) {
        return notLike(Tools.field(value), escape);
    }

    @Override // org.jooq.Field
    public final Condition notLike(Field<String> field, char escape) {
        return notLike(field).escape(escape);
    }

    @Override // org.jooq.Field
    public final Condition notLikeIgnoreCase(String value, char escape) {
        return notLikeIgnoreCase(Tools.field(value), escape);
    }

    @Override // org.jooq.Field
    public final Condition notLikeIgnoreCase(Field<String> field, char escape) {
        return notLikeIgnoreCase(field).escape(escape);
    }

    @Override // org.jooq.Field
    public final Condition notLikeRegex(String pattern) {
        return likeRegex(pattern).not();
    }

    @Override // org.jooq.Field
    public final Condition notLikeRegex(Field<String> pattern) {
        return likeRegex(pattern).not();
    }

    @Override // org.jooq.Field
    public final Condition notContains(T value) {
        return contains((AbstractField<T>) value).not();
    }

    @Override // org.jooq.Field
    public final Condition notContains(Field<T> value) {
        return contains((Field) value).not();
    }

    @Override // org.jooq.Field
    public final Condition notContainsIgnoreCase(T value) {
        return containsIgnoreCase((AbstractField<T>) value).not();
    }

    @Override // org.jooq.Field
    public final Condition notContainsIgnoreCase(Field<T> value) {
        return containsIgnoreCase((Field) value).not();
    }

    private final boolean isAccidentalSelect(T[] values) {
        return values != null && values.length == 1 && (values[0] instanceof Select);
    }

    private final boolean isAccidentalCollection(T[] values) {
        return values != null && values.length == 1 && (values[0] instanceof Collection);
    }

    @Override // org.jooq.Field
    public final Condition in(T... values) {
        if (isAccidentalSelect(values)) {
            return in((Select) values[0]);
        }
        if (isAccidentalCollection(values)) {
            return in((Collection<?>) values[0]);
        }
        return new InList(this, Tools.fields(values, this));
    }

    @Override // org.jooq.Field
    public final Condition in(Field<?>... values) {
        return new InList(this, Tools.nullSafeList(values, getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition in(Collection<?> values) {
        return in((Field<?>[]) Tools.map(values, v -> {
            return Tools.field(v, this);
        }, x$0 -> {
            return new Field[x$0];
        }));
    }

    @Override // org.jooq.Field
    public final Condition in(Result<? extends Record1<T>> result) {
        return in(result.getValues(0, getType()));
    }

    @Override // org.jooq.Field
    public final Condition notIn(T... values) {
        if (isAccidentalSelect(values)) {
            return notIn((Select) values[0]);
        }
        if (isAccidentalCollection(values)) {
            return notIn((Collection<?>) values[0]);
        }
        return new NotInList(this, Tools.fields(values, this));
    }

    @Override // org.jooq.Field
    public final Condition notIn(Field<?>... values) {
        return new NotInList(this, Tools.nullSafeList(values, getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition notIn(Collection<?> values) {
        return notIn((Field<?>[]) Tools.map(values, v -> {
            return Tools.field(v, this);
        }, x$0 -> {
            return new Field[x$0];
        }));
    }

    @Override // org.jooq.Field
    public final Condition notIn(Result<? extends Record1<T>> result) {
        return notIn(result.getValues(0, getType()));
    }

    @Override // org.jooq.Field
    public final Condition between(T minValue, T maxValue) {
        return between((Field) Tools.field(minValue, this), (Field) Tools.field(maxValue, this));
    }

    @Override // org.jooq.Field
    public final Condition between(Field<T> minValue, Field<T> maxValue) {
        return between((Field) Tools.nullSafe((Field) minValue, (DataType<?>) getDataType())).and((Field) Tools.nullSafe((Field) maxValue, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition betweenSymmetric(T minValue, T maxValue) {
        return betweenSymmetric((Field) Tools.field(minValue, this), (Field) Tools.field(maxValue, this));
    }

    @Override // org.jooq.Field
    public final Condition betweenSymmetric(Field<T> minValue, Field<T> maxValue) {
        return betweenSymmetric((Field) Tools.nullSafe((Field) minValue, (DataType<?>) getDataType())).and((Field) Tools.nullSafe((Field) maxValue, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition notBetween(T minValue, T maxValue) {
        return notBetween((Field) Tools.field(minValue, this), (Field) Tools.field(maxValue, this));
    }

    @Override // org.jooq.Field
    public final Condition notBetween(Field<T> minValue, Field<T> maxValue) {
        return notBetween((Field) Tools.nullSafe((Field) minValue, (DataType<?>) getDataType())).and((Field) Tools.nullSafe((Field) maxValue, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final Condition notBetweenSymmetric(T minValue, T maxValue) {
        return notBetweenSymmetric((Field) Tools.field(minValue, this), (Field) Tools.field(maxValue, this));
    }

    @Override // org.jooq.Field
    public final Condition notBetweenSymmetric(Field<T> minValue, Field<T> maxValue) {
        return notBetweenSymmetric((Field) Tools.nullSafe((Field) minValue, (DataType<?>) getDataType())).and((Field) Tools.nullSafe((Field) maxValue, (DataType<?>) getDataType()));
    }

    @Override // org.jooq.Field
    public final BetweenAndStep<T> between(T minValue) {
        return between((Field) Tools.field(minValue, this));
    }

    @Override // org.jooq.Field
    public final BetweenAndStep<T> between(Field<T> minValue) {
        return new BetweenCondition(this, Tools.nullSafe((Field) minValue, (DataType<?>) getDataType()), false, false);
    }

    @Override // org.jooq.Field
    public final BetweenAndStep<T> betweenSymmetric(T minValue) {
        return betweenSymmetric((Field) Tools.field(minValue, this));
    }

    @Override // org.jooq.Field
    public final BetweenAndStep<T> betweenSymmetric(Field<T> minValue) {
        return new BetweenCondition(this, Tools.nullSafe((Field) minValue, (DataType<?>) getDataType()), false, true);
    }

    @Override // org.jooq.Field
    public final BetweenAndStep<T> notBetween(T minValue) {
        return notBetween((Field) Tools.field(minValue, this));
    }

    @Override // org.jooq.Field
    public final BetweenAndStep<T> notBetween(Field<T> minValue) {
        return new BetweenCondition(this, Tools.nullSafe((Field) minValue, (DataType<?>) getDataType()), true, false);
    }

    @Override // org.jooq.Field
    public final BetweenAndStep<T> notBetweenSymmetric(T minValue) {
        return notBetweenSymmetric((Field) Tools.field(minValue, this));
    }

    @Override // org.jooq.Field
    public final BetweenAndStep<T> notBetweenSymmetric(Field<T> minValue) {
        return new BetweenCondition(this, Tools.nullSafe((Field) minValue, (DataType<?>) getDataType()), true, true);
    }

    @Override // org.jooq.Field
    public final Condition equalIgnoreCase(String value) {
        return equalIgnoreCase(Tools.field(value));
    }

    @Override // org.jooq.Field
    public final Condition equalIgnoreCase(Field<String> value) {
        return DSL.lower((Field<String>) Tools.castIfNeeded(this, String.class)).equal(DSL.lower(value));
    }

    @Override // org.jooq.Field
    public final Condition notEqualIgnoreCase(String value) {
        return notEqualIgnoreCase(Tools.field(value));
    }

    @Override // org.jooq.Field
    public final Condition notEqualIgnoreCase(Field<String> value) {
        return DSL.lower((Field<String>) Tools.castIfNeeded(this, String.class)).notEqual(DSL.lower(value));
    }

    @Override // org.jooq.Field
    public final Condition compare(Comparator comparator, T value) {
        return compare(comparator, (Field) Tools.field(value, this));
    }

    @Override // org.jooq.Field
    public final Condition compare(Comparator comparator, Field<T> field) {
        switch (comparator) {
            case EQUALS:
                return new Eq(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case GREATER:
                return new Gt(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case GREATER_OR_EQUAL:
                return new Ge(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case LESS:
                return new Lt(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case LESS_OR_EQUAL:
                return new Le(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case NOT_EQUALS:
                return new Ne(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case LIKE:
                return new Like(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case LIKE_IGNORE_CASE:
                return new LikeIgnoreCase(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case SIMILAR_TO:
                return new SimilarTo(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case NOT_LIKE:
                return new NotLike(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case NOT_LIKE_IGNORE_CASE:
                return new NotLikeIgnoreCase(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case NOT_SIMILAR_TO:
                return new NotSimilarTo(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case IS_DISTINCT_FROM:
                return new IsDistinctFrom(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case IS_NOT_DISTINCT_FROM:
                return new IsNotDistinctFrom(this, Tools.nullSafe((Field) field, (DataType<?>) getDataType()));
            case IN:
                if (field instanceof ScalarSubquery) {
                    ScalarSubquery<?> s = (ScalarSubquery) field;
                    return new In(this, s.query);
                }
                break;
            case NOT_IN:
                if (field instanceof ScalarSubquery) {
                    ScalarSubquery<?> s2 = (ScalarSubquery) field;
                    return new NotIn(this, s2.query);
                }
                break;
        }
        throw new IllegalArgumentException("Comparator not supported: " + String.valueOf(comparator));
    }

    @Override // org.jooq.Field
    public final Condition compare(Comparator comparator, Select<? extends Record1<T>> query) {
        return compare(comparator, (Field) new ScalarSubquery(query, getDataType(), true));
    }

    @Override // org.jooq.Field
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record1<T>> query) {
        switch (comparator) {
            case EQUALS:
                return new EqQuantified(this, query);
            case GREATER:
                return new GtQuantified(this, query);
            case GREATER_OR_EQUAL:
                return new GeQuantified(this, query);
            case LESS:
                return new LtQuantified(this, query);
            case LESS_OR_EQUAL:
                return new LeQuantified(this, query);
            case NOT_EQUALS:
                return new NeQuantified(this, query);
            case LIKE:
                return new LikeQuantified(this, query);
            case LIKE_IGNORE_CASE:
            case NOT_LIKE_IGNORE_CASE:
            default:
                throw new IllegalArgumentException("Comparator not supported: " + String.valueOf(comparator));
            case SIMILAR_TO:
                return new SimilarToQuantified(this, query);
            case NOT_LIKE:
                return new NotLikeQuantified(this, query);
            case NOT_SIMILAR_TO:
                return new NotSimilarToQuantified(this, query);
        }
    }

    private final <Z extends Number> Field<Z> numeric() {
        if (getDataType().isNumeric()) {
            return this;
        }
        return (Field<Z>) cast(BigDecimal.class);
    }

    private final Field<String> varchar() {
        if (getDataType().isString()) {
            return this;
        }
        return cast(String.class);
    }

    private final <Z extends Date> Field<Z> date() {
        if (getDataType().isTemporal()) {
            return this;
        }
        return (Field<Z>) cast(Timestamp.class);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> sign() {
        return DSL.sign((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> abs() {
        return DSL.abs(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> round() {
        return DSL.round(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> round(int decimals) {
        return DSL.round(numeric(), decimals);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> floor() {
        return DSL.floor(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> ceil() {
        return DSL.ceil(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> sqrt() {
        return DSL.sqrt((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> exp() {
        return DSL.exp((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> ln() {
        return DSL.ln((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> log(int base) {
        return DSL.log((Field<? extends Number>) numeric(), base);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> acos() {
        return DSL.acos((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> asin() {
        return DSL.asin((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> atan() {
        return DSL.atan((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> atan2(Number y) {
        return DSL.atan2((Field<? extends Number>) numeric(), y);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> atan2(Field<? extends Number> y) {
        return DSL.atan2((Field<? extends Number>) numeric(), y);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> cos() {
        return DSL.cos((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> sin() {
        return DSL.sin((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> tan() {
        return DSL.tan((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> cot() {
        return DSL.cot((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> sinh() {
        return DSL.sinh((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> cosh() {
        return DSL.cosh((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> tanh() {
        return DSL.tanh((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> coth() {
        return DSL.coth((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> deg() {
        return DSL.deg((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> rad() {
        return DSL.rad((Field<? extends Number>) numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> count() {
        return DSL.count((Field<?>) this);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> countDistinct() {
        return DSL.countDistinct((Field<?>) this);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> max() {
        return DSL.max(this);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> min() {
        return DSL.min(this);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> sum() {
        return DSL.sum(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> avg() {
        return DSL.avg(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> median() {
        return DSL.median(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> stddevPop() {
        return DSL.stddevPop(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> stddevSamp() {
        return DSL.stddevSamp(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> varPop() {
        return DSL.varPop(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<BigDecimal> varSamp() {
        return DSL.varSamp(numeric());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<Integer> countOver() {
        return DSL.count((Field<?>) this).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<T> maxOver() {
        return DSL.max(this).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<T> minOver() {
        return DSL.min(this).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<BigDecimal> sumOver() {
        return DSL.sum(numeric()).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<BigDecimal> avgOver() {
        return DSL.avg(numeric()).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> firstValue() {
        return DSL.firstValue(this);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lastValue() {
        return DSL.lastValue(this);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lead() {
        return DSL.lead(this);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lead(int offset) {
        return DSL.lead(this, offset);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lead(int offset, T defaultValue) {
        return DSL.lead(this, offset, defaultValue);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lead(int offset, Field<T> defaultValue) {
        return DSL.lead((Field) this, offset, (Field) defaultValue);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lag() {
        return DSL.lag(this);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lag(int offset) {
        return DSL.lag(this, offset);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lag(int offset, T defaultValue) {
        return DSL.lag(this, offset, defaultValue);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowIgnoreNullsStep<T> lag(int offset, Field<T> defaultValue) {
        return DSL.lag((Field) this, offset, (Field) defaultValue);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<BigDecimal> stddevPopOver() {
        return DSL.stddevPop(numeric()).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<BigDecimal> stddevSampOver() {
        return DSL.stddevSamp(numeric()).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<BigDecimal> varPopOver() {
        return DSL.varPop(numeric()).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final WindowPartitionByStep<BigDecimal> varSampOver() {
        return DSL.varSamp(numeric()).over();
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> upper() {
        return DSL.upper(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> lower() {
        return DSL.lower(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> trim() {
        return DSL.trim(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> rtrim() {
        return DSL.rtrim(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> ltrim() {
        return DSL.ltrim(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> rpad(Field<? extends Number> length) {
        return DSL.rpad(varchar(), length);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> rpad(int length) {
        return DSL.rpad(varchar(), length);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> rpad(Field<? extends Number> length, Field<String> character) {
        return DSL.rpad(varchar(), length, character);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> rpad(int length, char character) {
        return DSL.rpad(varchar(), length, character);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> lpad(Field<? extends Number> length) {
        return DSL.lpad(varchar(), length);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> lpad(int length) {
        return DSL.lpad(varchar(), length);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> lpad(Field<? extends Number> length, Field<String> character) {
        return DSL.lpad(varchar(), length, character);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> lpad(int length, char character) {
        return DSL.lpad(varchar(), length, character);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> repeat(Number count) {
        return DSL.repeat(varchar(), count == null ? 0 : count.intValue());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> repeat(Field<? extends Number> count) {
        return DSL.repeat(varchar(), count);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> replace(Field<String> search) {
        return DSL.replace(varchar(), search);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> replace(String search) {
        return DSL.replace(varchar(), search);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> replace(Field<String> search, Field<String> replace) {
        return DSL.replace(varchar(), search, replace);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> replace(String search, String replace) {
        return DSL.replace(varchar(), search, replace);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> position(String search) {
        return DSL.position(varchar(), search);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> position(Field<String> search) {
        return DSL.position(varchar(), search);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> ascii() {
        return DSL.ascii(varchar());
    }

    @Override // org.jooq.Field
    public final Field<String> collate(String collation) {
        return collate(DSL.collation(collation));
    }

    @Override // org.jooq.Field
    public final Field<String> collate(Name collation) {
        return collate(DSL.collation(collation));
    }

    @Override // org.jooq.Field
    public final Field<String> collate(Collation collation) {
        return new Collated(this, collation);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> concat(Field<?>... fields) {
        return DSL.concat(Tools.combine((Field<?>) this, fields));
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> concat(String... values) {
        return DSL.concat(Tools.combine((Field<?>) this, (Field<?>[]) Tools.fieldsArray(values)));
    }

    @Override // org.jooq.Field
    public final Field<String> concat(char... values) {
        return concat(new String(values));
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> substring(int startingPosition) {
        return DSL.substring(varchar(), startingPosition);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> substring(Field<? extends Number> startingPosition) {
        return DSL.substring(varchar(), startingPosition);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> substring(int startingPosition, int length) {
        return DSL.substring(varchar(), startingPosition, length);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<String> substring(Field<? extends Number> startingPosition, Field<? extends Number> length) {
        return DSL.substring(varchar(), startingPosition, length);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> length() {
        return DSL.length(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> charLength() {
        return DSL.charLength(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> bitLength() {
        return DSL.bitLength(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> octetLength() {
        return DSL.octetLength(varchar());
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<Integer> extract(DatePart datePart) {
        return DSL.extract((Field<?>) date(), datePart);
    }

    @Override // org.jooq.Field
    @SafeVarargs
    @Deprecated
    public final Field<T> greatest(T... others) {
        return DSL.greatest((Field) this, (Field<?>[]) Tools.fieldsArray(others));
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> greatest(Field<?>... others) {
        return DSL.greatest((Field) this, others);
    }

    @Override // org.jooq.Field
    @SafeVarargs
    @Deprecated
    public final Field<T> least(T... others) {
        return DSL.least((Field) this, (Field<?>[]) Tools.fieldsArray(others));
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> least(Field<?>... others) {
        return DSL.least((Field) this, others);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> nvl(T defaultValue) {
        return DSL.nvl((Field) this, (Object) defaultValue);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> nvl(Field<T> defaultValue) {
        return DSL.nvl((Field) this, (Field) defaultValue);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final <Z> Field<Z> nvl2(Z valueIfNotNull, Z valueIfNull) {
        return DSL.nvl2(this, valueIfNotNull, valueIfNull);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final <Z> Field<Z> nvl2(Field<Z> valueIfNotNull, Field<Z> valueIfNull) {
        return DSL.nvl2((Field<?>) this, (Field) valueIfNotNull, (Field) valueIfNull);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> nullif(T other) {
        return DSL.nullif((Field) this, (Object) other);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> nullif(Field<T> other) {
        return DSL.nullif((Field) this, (Field) other);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final <Z> Field<Z> decode(T search, Z result) {
        return DSL.decode((Field) this, Tools.field(search, this), Tools.field(result));
    }

    @Override // org.jooq.Field
    @Deprecated
    public final <Z> Field<Z> decode(T search, Z result, Object... more) {
        Field<Z> r = Tools.field(result);
        DataType[] dataTypeArr = new DataType[more.length];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= dataTypeArr.length - 1) {
                break;
            }
            dataTypeArr[i2] = getDataType();
            dataTypeArr[i2 + 1] = r.getDataType();
            i = i2 + 2;
        }
        if (dataTypeArr.length % 2 == 1) {
            dataTypeArr[dataTypeArr.length - 1] = r.getDataType();
        }
        return DSL.decode((Field) this, Tools.field(search, this), (Field) r, Tools.fieldsArray(more, (DataType<?>[]) dataTypeArr));
    }

    @Override // org.jooq.Field
    @Deprecated
    public final <Z> Field<Z> decode(Field<T> search, Field<Z> result) {
        return DSL.decode((Field) this, (Field) search, (Field) result);
    }

    @Override // org.jooq.Field
    @Deprecated
    public final <Z> Field<Z> decode(Field<T> search, Field<Z> result, Field<?>... more) {
        return DSL.decode((Field) this, (Field) search, (Field) result, more);
    }

    @Override // org.jooq.Field
    @SafeVarargs
    @Deprecated
    public final Field<T> coalesce(T option, T... options) {
        return DSL.coalesce((Field) this, Tools.combine((Field<?>) Tools.field(option, this), (Field<?>[]) Tools.fields(options, this).toArray(Tools.EMPTY_FIELD)));
    }

    @Override // org.jooq.Field
    @Deprecated
    public final Field<T> coalesce(Field<T> option, Field<?>... options) {
        return DSL.coalesce((Field) this, Tools.combine((Field<?>) option, options));
    }
}
