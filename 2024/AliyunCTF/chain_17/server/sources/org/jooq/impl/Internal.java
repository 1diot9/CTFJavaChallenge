package org.jooq.impl;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jooq.Binding;
import org.jooq.Check;
import org.jooq.Comment;
import org.jooq.Constants;
import org.jooq.Converter;
import org.jooq.ConverterContext;
import org.jooq.DataType;
import org.jooq.Domain;
import org.jooq.EmbeddableRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Generator;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.ParamMode;
import org.jooq.Parameter;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RecordQualifier;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.Support;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UDTField;
import org.jooq.UDTPathField;
import org.jooq.UDTPathTableField;
import org.jooq.UniqueKey;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.QOM;
import org.jooq.tools.reflect.Reflect;
import org.jooq.tools.reflect.ReflectException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Internal.class */
public final class Internal {
    private static final Lazy<ConverterContext> CONVERTER_SCOPE = Lazy.of(() -> {
        return new DefaultConverterContext(Tools.CONFIG.get());
    });
    private static final Lazy<Integer> JAVA_VERSION = Lazy.of(() -> {
        try {
            return (Integer) Reflect.onClass((Class<?>) Runtime.class).call("version").call("feature").get();
        } catch (ReflectException e) {
            return 8;
        }
    });

    public static final <R extends Record, T, P extends UDTPathTableField<R, ?, T>> P createUDTPathTableField(Name name, DataType<T> dataType, Table<R> table, Class<P> cls) {
        return (P) createUDTPathTableField(name, dataType, table, null, cls, null, null, null);
    }

    public static final <R extends Record, T, P extends UDTPathTableField<R, ?, T>> P createUDTPathTableField(Name name, DataType<T> dataType, Table<R> table, String str, Class<P> cls) {
        return (P) createUDTPathTableField(name, dataType, table, str, cls, null, null, null);
    }

    public static final <R extends Record, T, U, P extends UDTPathTableField<R, ?, U>> P createUDTPathTableField(Name name, DataType<T> dataType, Table<R> table, String str, Class<P> cls, Converter<T, U> converter) {
        return (P) createUDTPathTableField(name, dataType, table, str, cls, converter, null, null);
    }

    public static final <R extends Record, T, U, P extends UDTPathTableField<R, ?, U>> P createUDTPathTableField(Name name, DataType<T> dataType, Table<R> table, String str, Class<P> cls, Binding<T, U> binding) {
        return (P) createUDTPathTableField(name, dataType, table, str, cls, null, binding, null);
    }

    public static final <R extends Record, T, X, U, P extends UDTPathTableField<R, ?, U>> P createUDTPathTableField(Name name, DataType<T> dataType, Table<R> table, String str, Class<P> cls, Converter<X, U> converter, Binding<T, X> binding) {
        return (P) createUDTPathTableField(name, dataType, table, str, cls, converter, binding, null);
    }

    public static final <R extends Record, TR extends Table<R>, T, P extends UDTPathTableField<R, ?, T>> P createUDTPathTableField(Name name, DataType<T> dataType, TR tr, String str, Class<P> cls, Generator<R, TR, T> generator) {
        return (P) createUDTPathTableField(name, dataType, tr, str, cls, null, null, generator);
    }

    public static final <R extends Record, TR extends Table<R>, T, U, P extends UDTPathTableField<R, ?, U>> P createUDTPathTableField(Name name, DataType<T> dataType, TR tr, String str, Class<P> cls, Converter<T, U> converter, Generator<R, TR, U> generator) {
        return (P) createUDTPathTableField(name, dataType, tr, str, cls, converter, null, generator);
    }

    public static final <R extends Record, TR extends Table<R>, T, U, P extends UDTPathTableField<R, ?, U>> P createUDTPathTableField(Name name, DataType<T> dataType, TR tr, String str, Class<P> cls, Binding<T, U> binding, Generator<R, TR, U> generator) {
        return (P) createUDTPathTableField(name, dataType, tr, str, cls, null, binding, generator);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static final <R extends Record, TR extends Table<R>, T, X, U, P extends UDTPathTableField<R, ?, U>> P createUDTPathTableField(Name name, DataType<T> type, TR table, String comment, Class<P> returnType, Converter<X, U> converter, Binding<T, X> binding, Generator<R, TR, U> generator) {
        DataType<T> asConvertedDataType;
        Binding<? super T, U> newBinding = DefaultBinding.newBinding(converter, type, binding);
        if (converter == null && binding == null) {
            asConvertedDataType = type;
        } else {
            asConvertedDataType = type.asConvertedDataType(newBinding);
        }
        DataType<T> dataType = asConvertedDataType;
        if (generator != 0) {
            dataType = dataType.generatedAlwaysAs((Generator) generator).generationLocation(QOM.GenerationLocation.CLIENT);
        }
        try {
            P p = (P) newInstance(name, table, null, comment, returnType, newBinding, dataType);
            if (table instanceof TableImpl) {
                TableImpl<?> t = (TableImpl) table;
                t.fields.add(p);
            }
            return p;
        } catch (Exception e) {
            throw new DataTypeException("Cannot instantiate " + String.valueOf(returnType) + ".", e);
        }
    }

    public static final <T, P extends UDTField<?, T>> P createUDTPathField(Name name, DataType<T> dataType, UDTPathField<?, ?, ?> uDTPathField, Class<P> cls) {
        return (P) createUDTPathField(name, dataType, uDTPathField, null, cls, null, null);
    }

    public static final <T, P extends UDTField<?, T>> P createUDTPathField(Name name, DataType<T> dataType, UDTPathField<?, ?, ?> uDTPathField, String str, Class<P> cls) {
        return (P) createUDTPathField(name, dataType, uDTPathField, str, cls, null, null);
    }

    public static final <T, U, P extends UDTField<?, U>> P createUDTPathField(Name name, DataType<T> dataType, UDTPathField<?, ?, ?> uDTPathField, String str, Class<P> cls, Converter<T, U> converter) {
        return (P) createUDTPathField(name, dataType, uDTPathField, str, cls, converter, null);
    }

    public static final <T, U, P extends UDTField<?, U>> P createUDTPathField(Name name, DataType<T> dataType, UDTPathField<?, ?, ?> uDTPathField, String str, Class<P> cls, Binding<T, U> binding) {
        return (P) createUDTPathField(name, dataType, uDTPathField, str, cls, null, binding);
    }

    public static final <T, X, U, P extends UDTField<?, U>> P createUDTPathField(Name name, DataType<T> dataType, UDTPathField<?, ?, ?> uDTPathField, String str, Class<P> cls, Converter<X, U> converter, Binding<T, X> binding) {
        DataType<U> asConvertedDataType;
        Binding<? super T, U> newBinding = DefaultBinding.newBinding(converter, dataType, binding);
        if (converter == null && binding == null) {
            asConvertedDataType = dataType;
        } else {
            asConvertedDataType = dataType.asConvertedDataType(newBinding);
        }
        try {
            return (P) newInstance(name, null, uDTPathField, str, cls, newBinding, asConvertedDataType);
        } catch (Exception e) {
            throw new DataTypeException("Cannot instantiate " + String.valueOf(cls) + ".", e);
        }
    }

    private static <T, X, U, P extends UDTField<?, U>> P newInstance(Name name, RecordQualifier<?> qualifier, UDTPathField<?, ?, ?> path, String comment, Class<P> returnType, Binding<T, U> actualBinding, DataType<U> actualType) throws Exception {
        if (returnType == UDTField.class) {
            return new UDTPathFieldImpl(name, actualType, path.asQualifier(), path.getUDT(), DSL.comment(comment), actualBinding);
        }
        Constructor<P> constructor = returnType.getConstructor(Name.class, DataType.class, RecordQualifier.class, Comment.class, Binding.class);
        Object[] objArr = new Object[5];
        objArr[0] = name;
        objArr[1] = actualType;
        objArr[2] = qualifier == null ? path.asQualifier() : qualifier;
        objArr[3] = DSL.comment(comment);
        objArr[4] = actualBinding;
        return constructor.newInstance(objArr);
    }

    @SafeVarargs
    @NotNull
    public static final <R extends Record, E extends EmbeddableRecord<E>> TableField<R, E> createEmbeddable(Name name, Class<E> recordType, Table<R> table, TableField<R, ?>... fields) {
        return createEmbeddable(name, recordType, false, table, fields);
    }

    @SafeVarargs
    @NotNull
    public static final <R extends Record, E extends EmbeddableRecord<E>> TableField<R, E> createEmbeddable(Name name, Class<E> recordType, boolean replacesFields, Table<R> table, TableField<R, ?>... fields) {
        return new EmbeddableTableField(name, recordType, replacesFields, table, fields);
    }

    @NotNull
    public static final Index createIndex(Name name, Table<?> table, OrderField<?>[] sortFields, boolean unique) {
        return new IndexImpl(name, table, sortFields, null, unique);
    }

    @NotNull
    public static final <R extends Record, T> Identity<R, T> createIdentity(Table<R> table, TableField<R, T> field) {
        return new IdentityImpl(table, field);
    }

    @SafeVarargs
    @NotNull
    public static final <R extends Record> UniqueKey<R> createUniqueKey(Table<R> table, TableField<R, ?>... fields) {
        return createUniqueKey((Table) table, (Name) null, (TableField[]) fields, true);
    }

    @SafeVarargs
    @NotNull
    public static final <R extends Record> UniqueKey<R> createUniqueKey(Table<R> table, Name name, TableField<R, ?>... fields) {
        return createUniqueKey((Table) table, name, (TableField[]) fields, true);
    }

    @NotNull
    public static final <R extends Record> UniqueKey<R> createUniqueKey(Table<R> table, Name name, TableField<R, ?>[] fields, boolean enforced) {
        return new UniqueKeyImpl(table, name, fields, enforced);
    }

    @NotNull
    public static final <R extends Record, ER extends EmbeddableRecord<ER>> UniqueKey<R> createUniqueKey(Table<R> table, Name name, TableField<R, ER> embeddableField, boolean enforced) {
        return createUniqueKey(table, name, fields(embeddableField), enforced);
    }

    @SafeVarargs
    @Deprecated
    @NotNull
    public static final <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key, Table<R> table, TableField<R, ?>... fields) {
        return createForeignKey((Table) table, (Name) null, (TableField[]) fields, (UniqueKey) key, (TableField[]) key.getFieldsArray(), true);
    }

    @NotNull
    public static final <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(Table<R> table, Name name, TableField<R, ?>[] fkFields, UniqueKey<U> uk, TableField<U, ?>[] ukFields, boolean enforced) {
        ReferenceImpl referenceImpl = new ReferenceImpl(table, name, fkFields, uk, ukFields == null ? uk.getFieldsArray() : ukFields, enforced);
        if (uk instanceof UniqueKeyImpl) {
            UniqueKeyImpl<U> u = (UniqueKeyImpl) uk;
            u.references.add(referenceImpl);
        }
        return referenceImpl;
    }

    @NotNull
    public static final <R extends Record, U extends Record, ER extends EmbeddableRecord<ER>> ForeignKey<R, U> createForeignKey(Table<R> table, Name name, TableField<R, ER> fkEmbeddableField, UniqueKey<U> uk, TableField<U, ER> ukEmbeddableField, boolean enforced) {
        return createForeignKey(table, name, fields(fkEmbeddableField), uk, fields(ukEmbeddableField), enforced);
    }

    @NotNull
    public static final <T extends Number> Sequence<T> createSequence(String name, Schema schema, DataType<T> type) {
        return new SequenceImpl(name, schema, (DataType) type, false);
    }

    @NotNull
    public static final <T extends Number> Sequence<T> createSequence(String name, Schema schema, DataType<T> type, Number startWith, Number incrementBy, Number minvalue, Number maxvalue, boolean cycle, Number cache) {
        return new SequenceImpl(DSL.name(name), schema, type, false, startWith != null ? Tools.field(startWith, type) : null, incrementBy != null ? Tools.field(incrementBy, type) : null, minvalue != null ? Tools.field(minvalue, type) : null, maxvalue != null ? Tools.field(maxvalue, type) : null, cycle, cache != null ? Tools.field(cache, type) : null);
    }

    @NotNull
    public static final <R extends Record> Check<R> createCheck(Table<R> table, Name name, String condition) {
        return createCheck(table, name, condition, true);
    }

    @NotNull
    public static final <R extends Record> Check<R> createCheck(Table<R> table, Name name, String condition, boolean enforced) {
        return new CheckImpl(table, name, DSL.condition(condition), enforced);
    }

    @NotNull
    public static final <T> Domain<T> createDomain(Schema schema, Name name, DataType<T> type, Check<?>... checks) {
        return createDomain(schema, name, type, null, null, checks);
    }

    @NotNull
    public static final <T, U> Domain<U> createDomain(Schema schema, Name name, DataType<T> type, Converter<T, U> converter, Check<?>... checks) {
        return createDomain(schema, name, type, converter, null, checks);
    }

    @NotNull
    public static final <T, U> Domain<U> createDomain(Schema schema, Name name, DataType<T> type, Binding<T, U> binding, Check<?>... checks) {
        return createDomain(schema, name, type, null, binding, checks);
    }

    @NotNull
    public static final <T, X, U> Domain<U> createDomain(Schema schema, Name name, DataType<T> type, Converter<X, U> converter, Binding<T, X> binding, Check<?>... checks) {
        DataType<T> asConvertedDataType;
        Binding<? super T, U> newBinding = DefaultBinding.newBinding(converter, type, binding);
        if (converter == null && binding == null) {
            asConvertedDataType = type;
        } else {
            asConvertedDataType = type.asConvertedDataType(newBinding);
        }
        return new DomainImpl(schema, name, asConvertedDataType, checks);
    }

    @NotNull
    public static final Name createPathAlias(Table<?> path, ForeignKey<?, ?> childPath, InverseForeignKey<?, ?> parentPath) {
        Name name;
        if (childPath != null) {
            name = DSL.name(childPath.getName());
        } else {
            name = DSL.name(parentPath.getName() + ".inverse");
        }
        Name name2 = name;
        if (path instanceof TableImpl) {
            TableImpl<?> t = (TableImpl) path;
            if (t.path != null) {
                name2 = createPathAlias(t.path, t.childPath, t.parentPath).append(name2);
            } else {
                name2 = path.getQualifiedName().append(name2);
            }
        }
        return DSL.name("alias_" + hash(name2));
    }

    @NotNull
    public static final <T> Parameter<T> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed) {
        return createParameter(name, type, isDefaulted, isUnnamed, null, null);
    }

    @NotNull
    public static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Converter<T, U> converter) {
        return createParameter(name, type, isDefaulted, isUnnamed, converter, null);
    }

    @NotNull
    public static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Binding<T, U> binding) {
        return createParameter(name, type, isDefaulted, isUnnamed, null, binding);
    }

    @NotNull
    public static final <T, X, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Converter<X, U> converter, Binding<T, X> binding) {
        DataType<T> asConvertedDataType;
        Binding<? super T, U> newBinding = DefaultBinding.newBinding(converter, type, binding);
        if (converter == null && binding == null) {
            asConvertedDataType = type;
        } else {
            asConvertedDataType = type.asConvertedDataType(newBinding);
        }
        return new ParameterImpl(ParamMode.IN, DSL.name(name), asConvertedDataType, isDefaulted, isUnnamed);
    }

    private Internal() {
    }

    @Deprecated(since = "3.14", forRemoval = true)
    @NotNull
    public static final Index createIndex(String name, Table<?> table, OrderField<?>[] sortFields, boolean unique) {
        return createIndex(DSL.name(name), table, sortFields, unique);
    }

    @SafeVarargs
    @Deprecated(since = "3.14", forRemoval = true)
    @NotNull
    public static final <R extends Record> UniqueKey<R> createUniqueKey(Table<R> table, String name, TableField<R, ?>... fields) {
        return createUniqueKey((Table) table, name, (TableField[]) fields, true);
    }

    @Deprecated(since = "3.14", forRemoval = true)
    @NotNull
    public static final <R extends Record> UniqueKey<R> createUniqueKey(Table<R> table, String name, TableField<R, ?>[] fields, boolean enforced) {
        return createUniqueKey(table, DSL.name(name), fields, enforced);
    }

    @SafeVarargs
    @Deprecated(since = "3.14", forRemoval = true)
    @NotNull
    public static final <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key, Table<R> table, String name, TableField<R, ?>... fields) {
        return createForeignKey(key, table, name, fields, true);
    }

    @Deprecated(since = "3.14", forRemoval = true)
    @NotNull
    public static final <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key, Table<R> table, String name, TableField<R, ?>[] fields, boolean enforced) {
        return createForeignKey(table, DSL.name(name), fields, key, key.getFieldsArray(), enforced);
    }

    @Deprecated(since = "3.14", forRemoval = true)
    @NotNull
    public static final <R extends Record, ER extends EmbeddableRecord<ER>> TableField<R, ?>[] fields(TableField<R, ER> embeddableField) {
        return ((EmbeddableTableField) embeddableField).fields;
    }

    @Deprecated(since = Constants.VERSION_3_16, forRemoval = true)
    @NotNull
    public static final <R extends Record, ER extends EmbeddableRecord<ER>> Row fieldsRow(TableField<R, ER> embeddableField) {
        return embeddableField.getDataType().getRow();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Support
    public static final <T> Field<T> ineg(Field<T> field) {
        return new Neg(field, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Support
    public static final <T> Field<T> iadd(Field<T> lhs, Field<?> rhs) {
        return new IAdd(lhs, Tools.nullSafe((Field) rhs, (DataType<?>) lhs.getDataType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Support
    public static final <T> Field<T> isub(Field<T> lhs, Field<?> rhs) {
        return new ISub(lhs, Tools.nullSafe((Field) rhs, (DataType<?>) lhs.getDataType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Support
    public static final <T> Field<T> imul(Field<T> lhs, Field<?> rhs) {
        return new IMul(lhs, Tools.nullSafe((Field) rhs, (DataType<?>) lhs.getDataType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Support
    public static final <T> Field<T> idiv(Field<T> lhs, Field<?> rhs) {
        return new IDiv(lhs, Tools.nullSafe((Field) rhs, (DataType<?>) lhs.getDataType()));
    }

    public static final <T> Subscriber<T> subscriber(final Consumer<? super Subscription> subscription, final Consumer<? super T> onNext, final Consumer<? super Throwable> onError, final Runnable onComplete) {
        return new Subscriber<T>() { // from class: org.jooq.impl.Internal.1
            @Override // org.reactivestreams.Subscriber
            public void onSubscribe(Subscription s) {
                subscription.accept(s);
            }

            @Override // org.reactivestreams.Subscriber
            public void onNext(T t) {
                onNext.accept(t);
            }

            @Override // org.reactivestreams.Subscriber
            public void onError(Throwable t) {
                onError.accept(t);
            }

            @Override // org.reactivestreams.Subscriber
            public void onComplete() {
                onComplete.run();
            }
        };
    }

    public static final <T> Class<T[]> arrayType(Class<T> cls) {
        return (Class<T[]>) cls.arrayType();
    }

    public static final <R extends Record> Result<R> result(R record) {
        return new ResultImpl(Tools.configuration(record), ((AbstractRecord) record).fields);
    }

    public static final boolean commercial() {
        return Tools.CONFIG.get().commercial();
    }

    public static final boolean commercial(Supplier<String> logMessage) {
        return Tools.CONFIG.get().commercial(logMessage);
    }

    public static final void requireCommercial(Supplier<String> logMessage) throws DataAccessException {
        Tools.CONFIG.get().requireCommercial(logMessage);
    }

    public static final int hash(QueryPart part) {
        return hash0(Tools.CTX.get().render(part));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int hash0(Object object) {
        if (object == null) {
            return 0;
        }
        return 134217727 & object.hashCode();
    }

    public static final ConverterContext converterContext() {
        return CONVERTER_SCOPE.get();
    }

    public static final int javaVersion() {
        return JAVA_VERSION.get().intValue();
    }

    @Deprecated(forRemoval = true)
    public static final Object[] convert(Object[] values, Field<?>[] fields) {
        return Convert.convert(values, fields);
    }

    @Deprecated(forRemoval = true)
    public static final Object[] convert(Object[] values, Class<?>[] types) {
        return Convert.convert(values, types);
    }

    @Deprecated(forRemoval = true)
    public static final <U> U[] convertArray(Object[] objArr, Converter<?, ? extends U> converter) throws DataTypeException {
        return (U[]) Convert.convertArray(objArr, converter);
    }

    @Deprecated(forRemoval = true)
    public static final Object[] convertArray(Object[] from, Class<?> toClass) throws DataTypeException {
        return Convert.convertArray(from, toClass);
    }

    @Deprecated(forRemoval = true)
    public static final <U> U[] convertCollection(Collection collection, Class<? extends U[]> cls) {
        return (U[]) Convert.convertCollection(collection, cls);
    }

    @Deprecated(forRemoval = true)
    public static final <U> U convert(Object obj, Converter<?, ? extends U> converter) throws DataTypeException {
        return (U) Convert.convert(obj, converter);
    }

    @Deprecated(forRemoval = true)
    public static final <T> T convert(Object obj, Class<? extends T> cls) throws DataTypeException {
        return (T) Convert.convert(obj, cls);
    }

    @Deprecated(forRemoval = true)
    public static final <T> List<T> convert(Collection<?> collection, Class<? extends T> type) throws DataTypeException {
        return Convert.convert(collection, (Class) type);
    }

    @Deprecated(forRemoval = true)
    public static final <U> List<U> convert(Collection<?> collection, Converter<?, ? extends U> converter) throws DataTypeException {
        return Convert.convert(collection, (Converter) converter);
    }
}
