package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jooq.Binding;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.DivideByOnStep;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Generator;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.JoinType;
import org.jooq.Name;
import org.jooq.Package;
import org.jooq.Path;
import org.jooq.QualifiedAsterisk;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RecordType;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.RowId;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOnStep;
import org.jooq.TableOptionalOnStep;
import org.jooq.TableOptions;
import org.jooq.TablePartitionByStep;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;
import org.jooq.tools.JooqLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractTable.class */
public abstract class AbstractTable<R extends Record> extends AbstractNamed implements Table<R>, FieldsTrait, QOM.Aliasable<Table<R>> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) AbstractTable.class);
    private static final Clause[] CLAUSES = {Clause.TABLE};
    private final TableOptions options;
    private Schema tableschema;
    private transient DataType<R> tabletype;
    private transient Identity<R, ?> identity;
    private transient Row fieldsRow;
    transient PrimaryKeyWithEmbeddables<R> primaryKeyWithEmbeddables;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract FieldsImpl<R> fields0();

    @Override // org.jooq.Table
    public /* bridge */ /* synthetic */ TableOnStep straightJoin(TableLike tableLike) {
        return straightJoin((TableLike<?>) tableLike);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTable(TableOptions options, Name name) {
        this(options, name, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTable(TableOptions options, Name name, Schema schema) {
        this(options, name, schema, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTable(TableOptions options, Name name, Schema schema, Comment comment) {
        super(qualify(schema, name), comment);
        this.options = options;
        this.tableschema = schema;
    }

    public Name $alias() {
        return null;
    }

    public Table<R> $aliased() {
        return this;
    }

    @Override // org.jooq.Typed
    public final Class<R> getType() {
        return getDataType().getType();
    }

    @Override // org.jooq.Typed
    public final DataType<R> getDataType(Configuration configuration) {
        return getDataType();
    }

    @Override // org.jooq.Typed
    public final DataType<R> $dataType() {
        return getDataType();
    }

    @Override // org.jooq.Typed
    public final Binding<?, R> getBinding() {
        return getDataType().getBinding();
    }

    @Override // org.jooq.Typed
    public final ContextConverter<?, R> getConverter() {
        return getDataType().getConverter();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convert(Binding<R, U> binding) {
        return tf().convert((Binding) binding);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convert(Converter<R, U> converter) {
        return tf().convert((Converter) converter);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convert(Class<U> toType, java.util.function.Function<? super R, ? extends U> function, java.util.function.Function<? super U, ? extends R> function2) {
        return tf().convert((Class) toType, (java.util.function.Function) function, (java.util.function.Function) function2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convertFrom(Class<U> toType, java.util.function.Function<? super R, ? extends U> function) {
        return tf().convertFrom((Class) toType, (java.util.function.Function) function);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convertFrom(java.util.function.Function<? super R, ? extends U> function) {
        return tf().convertFrom((java.util.function.Function) function);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convertTo(Class<U> toType, java.util.function.Function<? super U, ? extends R> function) {
        return tf().convertTo((Class) toType, (java.util.function.Function) function);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convertTo(java.util.function.Function<? super U, ? extends R> function) {
        return tf().convertTo((java.util.function.Function) function);
    }

    @Override // org.jooq.SelectField
    public final SelectField<R> as(Field<?> otherField) {
        return as(otherField.getUnqualifiedName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TableAsField<R> tf() {
        return new TableAsField<>(this);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.Table
    public final R from(Record record) {
        return (R) record.into((Table) this);
    }

    @Override // org.jooq.Table
    public final QualifiedAsterisk asterisk() {
        return new QualifiedAsteriskImpl(this);
    }

    @Override // org.jooq.RecordQualifier, org.jooq.Typed
    public final DataType<R> getDataType() {
        if (this.tabletype == null) {
            this.tabletype = new TableDataType(this);
        }
        return this.tabletype;
    }

    @Override // org.jooq.Table
    public final RecordType<R> recordType() {
        return fields0();
    }

    @Override // org.jooq.RecordQualifier
    public final R newRecord() {
        return (R) DSL.using(new DefaultConfiguration()).newRecord(this);
    }

    @Override // org.jooq.Fields
    public Row fieldsRow() {
        if (this.fieldsRow == null) {
            this.fieldsRow = Tools.row0(fields0());
        }
        return this.fieldsRow;
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields() {
        return fieldsRow().fields();
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset() {
        return DSL.multiset(this);
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(String alias) {
        return DSL.multiset(this).as(alias);
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(Name alias) {
        return DSL.multiset(this).as(alias);
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(Field<?> alias) {
        return DSL.multiset(this).as(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable() {
        return this;
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias) {
        return as(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias, String... fieldAliases) {
        return as(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias, Collection<? extends String> fieldAliases) {
        return as(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name alias) {
        return as(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name alias, Name... fieldAliases) {
        return as(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name alias, Collection<? extends Name> fieldAliases) {
        return as(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> alias) {
        return as(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> alias, Field<?>... fieldAliases) {
        return as(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> alias, Collection<? extends Field<?>> fieldAliases) {
        return as(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias, java.util.function.Function<? super Field<?>, ? extends String> aliasFunction) {
        return as(alias, aliasFunction);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> aliasFunction) {
        return as(alias, aliasFunction);
    }

    @Override // org.jooq.Table, org.jooq.SelectField
    public Table<R> as(String alias) {
        return as(DSL.name(alias));
    }

    @Override // org.jooq.Table
    public final Table<R> as(String alias, String... fieldAliases) {
        return as(DSL.name(alias), Tools.names(fieldAliases));
    }

    @Override // org.jooq.Table
    public final Table<R> as(String alias, Collection<? extends String> fieldAliases) {
        return as(DSL.name(alias), Tools.names(fieldAliases));
    }

    @Override // org.jooq.Table
    public final Table<R> as(String alias, java.util.function.Function<? super Field<?>, ? extends String> aliasFunction) {
        return as(alias, (String[]) Tools.map(fields(), f -> {
            return (String) aliasFunction.apply(f);
        }, x$0 -> {
            return new String[x$0];
        }));
    }

    @Override // org.jooq.Table
    public final Table<R> as(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> aliasFunction) {
        return as(alias, (String[]) Tools.map(fields(), (f, i) -> {
            return (String) aliasFunction.apply(f, Integer.valueOf(i));
        }, x$0 -> {
            return new String[x$0];
        }));
    }

    @Override // org.jooq.Table, org.jooq.SelectField
    public Table<R> as(Name alias) {
        return new TableAlias(this, alias);
    }

    public Table<R> as(Name alias, Name... fieldAliases) {
        return new TableAlias(this, alias, fieldAliases);
    }

    @Override // org.jooq.Table
    public final Table<R> as(Name alias, Collection<? extends Name> fieldAliases) {
        return as(alias, fieldAliases == null ? null : (Name[]) fieldAliases.toArray(Tools.EMPTY_NAME));
    }

    @Override // org.jooq.Table
    public final Table<R> as(Name alias, java.util.function.Function<? super Field<?>, ? extends Name> aliasFunction) {
        return as(alias, (Name[]) Tools.map(fields(), f -> {
            return (Name) aliasFunction.apply(f);
        }, x$0 -> {
            return new Name[x$0];
        }));
    }

    @Override // org.jooq.Table
    public final Table<R> as(Name alias, BiFunction<? super Field<?>, ? super Integer, ? extends Name> aliasFunction) {
        return as(alias, (Name[]) Tools.map(fields(), (f, i) -> {
            return (Name) aliasFunction.apply(f, Integer.valueOf(i));
        }, x$0 -> {
            return new Name[x$0];
        }));
    }

    @Override // org.jooq.Table
    public final TableOptions.TableType getTableType() {
        return this.options.type();
    }

    @Override // org.jooq.Table
    public final TableOptions getOptions() {
        return this.options;
    }

    @Override // org.jooq.Qualified
    public final Catalog getCatalog() {
        if (getSchema() == null) {
            return null;
        }
        return getSchema().getCatalog();
    }

    @Override // org.jooq.RecordQualifier
    public final Package getPackage() {
        return null;
    }

    @Override // org.jooq.Qualified
    public Schema getSchema() {
        Schema schema;
        if (this.tableschema == null) {
            if (getQualifiedName().qualified()) {
                schema = DSL.schema(getQualifiedName().qualifier());
            } else {
                schema = null;
            }
            this.tableschema = schema;
        }
        return this.tableschema;
    }

    @Override // org.jooq.Table
    public Identity<R, ?> getIdentity() {
        if (this.identity == null) {
            for (Field<?> f : fields()) {
                if ((f instanceof TableField) && f.getDataType().identity()) {
                    if (this.identity == null) {
                        this.identity = new IdentityImpl(this, (TableField) f);
                    } else {
                        log.info("Multiple identities", "There are multiple identity fields in table " + String.valueOf(this) + ", which is not supported by jOOQ");
                    }
                }
            }
            if (this.identity == null) {
                this.identity = IdentityImpl.NULL;
            }
        }
        if (this.identity == IdentityImpl.NULL) {
            return null;
        }
        return this.identity;
    }

    @Override // org.jooq.Table
    public UniqueKey<R> getPrimaryKey() {
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractTable$PrimaryKeyWithEmbeddables.class */
    static final class PrimaryKeyWithEmbeddables<R extends Record> extends Record {
        private final UniqueKey<R> primaryKey;

        PrimaryKeyWithEmbeddables(UniqueKey<R> primaryKey) {
            this.primaryKey = primaryKey;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, PrimaryKeyWithEmbeddables.class), PrimaryKeyWithEmbeddables.class, "primaryKey", "FIELD:Lorg/jooq/impl/AbstractTable$PrimaryKeyWithEmbeddables;->primaryKey:Lorg/jooq/UniqueKey;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, PrimaryKeyWithEmbeddables.class), PrimaryKeyWithEmbeddables.class, "primaryKey", "FIELD:Lorg/jooq/impl/AbstractTable$PrimaryKeyWithEmbeddables;->primaryKey:Lorg/jooq/UniqueKey;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, PrimaryKeyWithEmbeddables.class, Object.class), PrimaryKeyWithEmbeddables.class, "primaryKey", "FIELD:Lorg/jooq/impl/AbstractTable$PrimaryKeyWithEmbeddables;->primaryKey:Lorg/jooq/UniqueKey;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public UniqueKey<R> primaryKey() {
            return this.primaryKey;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final UniqueKey<R> getPrimaryKeyWithEmbeddables() {
        if (this.primaryKeyWithEmbeddables == null) {
            UniqueKey<R> uniqueKey = getPrimaryKey();
            this.primaryKeyWithEmbeddables = new PrimaryKeyWithEmbeddables<>(uniqueKey);
        }
        return ((PrimaryKeyWithEmbeddables) this.primaryKeyWithEmbeddables).primaryKey;
    }

    @Override // org.jooq.Table
    public TableField<R, ?> getRecordVersion() {
        return null;
    }

    @Override // org.jooq.Table
    public TableField<R, ?> getRecordTimestamp() {
        return null;
    }

    @Override // org.jooq.Table
    public List<Index> getIndexes() {
        return Collections.emptyList();
    }

    @Override // org.jooq.Table
    public List<UniqueKey<R>> getKeys() {
        List<UniqueKey<R>> result = new ArrayList<>();
        UniqueKey<R> pk = getPrimaryKey();
        if (pk != null) {
            result.add(pk);
        }
        result.addAll(getUniqueKeys());
        return result;
    }

    @Override // org.jooq.Table
    public List<UniqueKey<R>> getUniqueKeys() {
        return Collections.emptyList();
    }

    @Override // org.jooq.Table
    public final <O extends Record> List<ForeignKey<O, R>> getReferencesFrom(Table<O> table) {
        return table.getReferencesTo(this);
    }

    public List<ForeignKey<R, ?>> getReferences() {
        return Collections.emptyList();
    }

    @Override // org.jooq.Table
    public final <O extends Record> List<ForeignKey<R, O>> getReferencesTo(Table<O> other) {
        List<ForeignKey<R, O>> result = new ArrayList<>();
        for (ForeignKey<R, ?> reference : getReferences()) {
            Tools.traverseJoins((Table<?>) other, (Consumer<? super Table<?>>) o -> {
                if (Tools.unwrap(o).equals(reference.getKey().getTable())) {
                    result.add(reference);
                }
            });
        }
        return Collections.unmodifiableList(result);
    }

    @Override // org.jooq.Table
    public List<Check<R>> getChecks() {
        return Collections.emptyList();
    }

    @Deprecated
    protected static final <R extends Record, T> TableField<R, T> createField(String name, DataType<T> type, Table<R> table) {
        return createField(DSL.name(name), type, table, null, null, null, null);
    }

    @Deprecated
    protected static final <R extends Record, T> TableField<R, T> createField(String name, DataType<T> type, Table<R> table, String comment) {
        return createField(DSL.name(name), type, table, comment, null, null, null);
    }

    @Deprecated
    protected static final <R extends Record, T, U> TableField<R, U> createField(String name, DataType<T> type, Table<R> table, String comment, Converter<T, U> converter) {
        return createField(DSL.name(name), type, table, comment, converter, null, null);
    }

    @Deprecated
    protected static final <R extends Record, T, U> TableField<R, U> createField(String name, DataType<T> type, Table<R> table, String comment, Binding<T, U> binding) {
        return createField(DSL.name(name), type, table, comment, (Converter) null, binding);
    }

    @Deprecated
    protected static final <R extends Record, T, X, U> TableField<R, U> createField(String name, DataType<T> type, Table<R> table, String comment, Converter<X, U> converter, Binding<T, X> binding) {
        return createField(DSL.name(name), type, table, comment, converter, binding);
    }

    @Deprecated
    protected final <T> TableField<R, T> createField(String name, DataType<T> type) {
        return createField(DSL.name(name), type, this, null, null, null, null);
    }

    @Deprecated
    protected final <T> TableField<R, T> createField(String name, DataType<T> type, String comment) {
        return createField(DSL.name(name), type, this, comment, null, null, null);
    }

    @Deprecated
    protected final <T, U> TableField<R, U> createField(String name, DataType<T> type, String comment, Converter<T, U> converter) {
        return createField(DSL.name(name), type, this, comment, converter, null, null);
    }

    @Deprecated
    protected final <T, U> TableField<R, U> createField(String name, DataType<T> type, String comment, Binding<T, U> binding) {
        return createField(DSL.name(name), type, this, comment, (Converter) null, binding);
    }

    @Deprecated
    protected final <T, X, U> TableField<R, U> createField(String name, DataType<T> type, String comment, Converter<X, U> converter, Binding<T, X> binding) {
        return createField(DSL.name(name), type, this, comment, converter, binding);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final <R extends Record, T> TableField<R, T> createField(Name name, DataType<T> type, Table<R> table) {
        return createField(name, type, table, null, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final <R extends Record, T> TableField<R, T> createField(Name name, DataType<T> type, Table<R> table, String comment) {
        return createField(name, type, table, comment, null, null, null);
    }

    protected static final <R extends Record, T, U> TableField<R, U> createField(Name name, DataType<T> type, Table<R> table, String comment, Converter<T, U> converter) {
        return createField(name, type, table, comment, converter, null, null);
    }

    protected static final <R extends Record, T, U> TableField<R, U> createField(Name name, DataType<T> type, Table<R> table, String comment, Binding<T, U> binding) {
        return createField(name, type, table, comment, null, binding, null);
    }

    protected static final <R extends Record, T, X, U> TableField<R, U> createField(Name name, DataType<T> type, Table<R> table, String comment, Converter<X, U> converter, Binding<T, X> binding) {
        return createField(name, type, table, comment, converter, binding, null);
    }

    protected static final <R extends Record, TR extends Table<R>, T> TableField<R, T> createField(Name name, DataType<T> type, TR table, String comment, Generator<R, TR, T> generator) {
        return createField(name, type, table, comment, null, null, generator);
    }

    protected static final <R extends Record, TR extends Table<R>, T, U> TableField<R, U> createField(Name name, DataType<T> type, TR table, String comment, Converter<T, U> converter, Generator<R, TR, U> generator) {
        return createField(name, type, table, comment, converter, null, generator);
    }

    protected static final <R extends Record, TR extends Table<R>, T, U> TableField<R, U> createField(Name name, DataType<T> type, TR table, String comment, Binding<T, U> binding, Generator<R, TR, U> generator) {
        return createField(name, type, table, comment, null, binding, generator);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected static final <R extends Record, TR extends Table<R>, T, X, U> TableField<R, U> createField(Name name, DataType<T> type, TR table, String comment, Converter<X, U> converter, Binding<T, X> binding, Generator<R, TR, U> generator) {
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
        TableFieldImpl<R, U> tableField = new TableFieldImpl<>(name, dataType, table, DSL.comment(comment), newBinding);
        if (table instanceof TableImpl) {
            TableImpl<?> t = (TableImpl) table;
            t.fields.add(tableField);
        }
        return tableField;
    }

    protected final <T> TableField<R, T> createField(Name name, DataType<T> type) {
        return createField(name, type, this, null, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final <T> TableField<R, T> createField(Name name, DataType<T> type, String comment) {
        return createField(name, type, this, comment, null, null, null);
    }

    protected final <T, U> TableField<R, U> createField(Name name, DataType<T> type, String comment, Converter<T, U> converter) {
        return createField(name, type, this, comment, converter, null, null);
    }

    protected final <T, U> TableField<R, U> createField(Name name, DataType<T> type, String comment, Binding<T, U> binding) {
        return createField(name, type, this, comment, null, binding, null);
    }

    protected final <T, X, U> TableField<R, U> createField(Name name, DataType<T> type, String comment, Converter<X, U> converter, Binding<T, X> binding) {
        return createField(name, type, this, comment, converter, binding, null);
    }

    protected final <TR extends Table<R>, T> TableField<R, T> createField0(Name name, DataType<T> type, TR table, String comment, Generator<R, TR, T> generator) {
        return createField(name, type, table, comment, null, null, generator);
    }

    protected final <TR extends Table<R>, T, U> TableField<R, U> createField0(Name name, DataType<T> type, TR table, String comment, Converter<T, U> converter, Generator<R, TR, U> generator) {
        return createField(name, type, table, comment, converter, null, generator);
    }

    protected final <TR extends Table<R>, T, U> TableField<R, U> createField0(Name name, DataType<T> type, TR table, String comment, Binding<T, U> binding, Generator<R, TR, U> generator) {
        return createField(name, type, table, comment, null, binding, generator);
    }

    protected final <TR extends Table<R>, T, X, U> TableField<R, U> createField0(Name name, DataType<T> type, TR table, String comment, Converter<X, U> converter, Binding<T, X> binding, Generator<R, TR, U> generator) {
        return createField(name, type, table, comment, converter, binding, generator);
    }

    @Override // org.jooq.Table
    public final Condition eq(Table<R> arg2) {
        return new TableEq(this, arg2);
    }

    @Override // org.jooq.Table
    public final Condition equal(Table<R> arg2) {
        return eq(arg2);
    }

    @Override // org.jooq.Table
    public final Condition ne(Table<R> arg2) {
        return new TableNe(this, arg2);
    }

    @Override // org.jooq.Table
    public final Condition notEqual(Table<R> arg2) {
        return ne(arg2);
    }

    @Override // org.jooq.Table
    public Field<RowId> rowid() {
        return new QualifiedRowid(this);
    }

    Table<R> hintedTable(String keywords, String... indexes) {
        return new HintedTable(this, keywords, indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> useIndex(String... indexes) {
        return hintedTable("use index", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> useIndexForJoin(String... indexes) {
        return hintedTable("use index for join", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> useIndexForOrderBy(String... indexes) {
        return hintedTable("use index for order by", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> useIndexForGroupBy(String... indexes) {
        return hintedTable("use index for group by", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> ignoreIndex(String... indexes) {
        return hintedTable("ignore index", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> ignoreIndexForJoin(String... indexes) {
        return hintedTable("ignore index for join", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> ignoreIndexForOrderBy(String... indexes) {
        return hintedTable("ignore index for order by", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> ignoreIndexForGroupBy(String... indexes) {
        return hintedTable("ignore index for group by", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> forceIndex(String... indexes) {
        return hintedTable("force index", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> forceIndexForJoin(String... indexes) {
        return hintedTable("force index for join", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> forceIndexForOrderBy(String... indexes) {
        return hintedTable("force index for order by", indexes);
    }

    @Override // org.jooq.Table
    public final Table<R> forceIndexForGroupBy(String... indexes) {
        return hintedTable("force index for group by", indexes);
    }

    @Override // org.jooq.Table
    public Table<R> as(Table<?> otherTable) {
        return as(otherTable.getUnqualifiedName());
    }

    @Override // org.jooq.Table
    public final Table<R> as(Table<?> otherTable, Field<?>... otherFields) {
        return as(otherTable.getUnqualifiedName(), (Name[]) Tools.map(otherFields, (v0) -> {
            return v0.getUnqualifiedName();
        }, x$0 -> {
            return new Name[x$0];
        }));
    }

    @Override // org.jooq.Table
    public final Table<R> as(Table<?> otherTable, Collection<? extends Field<?>> otherFields) {
        return as(otherTable.getUnqualifiedName(), Tools.map(otherFields, (v0) -> {
            return v0.getUnqualifiedName();
        }));
    }

    @Override // org.jooq.Table
    public final Table<R> as(Table<?> otherTable, java.util.function.Function<? super Field<?>, ? extends Field<?>> aliasFunction) {
        return as(otherTable.getUnqualifiedName(), f -> {
            return ((Field) aliasFunction.apply(f)).getUnqualifiedName();
        });
    }

    @Override // org.jooq.Table
    public final Table<R> as(Table<?> otherTable, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> aliasFunction) {
        return as(otherTable.getUnqualifiedName(), (f, i) -> {
            return ((Field) aliasFunction.apply(f, i)).getUnqualifiedName();
        });
    }

    @Override // org.jooq.Table
    public Table<Record> withOrdinality() {
        return new WithOrdinalityTable(this);
    }

    @Override // org.jooq.Table
    public final DivideByOnStep divideBy(Table<?> divisor) {
        return new DivideBy(this, divisor);
    }

    @Override // org.jooq.Table
    public final TableOnStep<R> leftSemiJoin(TableLike<?> table) {
        return join(table, JoinType.LEFT_SEMI_JOIN);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<R> leftSemiJoin(Path<?> path) {
        return (TableOptionalOnStep<R>) join(path, JoinType.LEFT_SEMI_JOIN);
    }

    @Override // org.jooq.Table
    public final TableOnStep<R> leftAntiJoin(TableLike<?> table) {
        return join(table, JoinType.LEFT_ANTI_JOIN);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<R> leftAntiJoin(Path<?> path) {
        return (TableOptionalOnStep<R>) join(path, JoinType.LEFT_ANTI_JOIN);
    }

    @Override // org.jooq.Table
    public Table<R> where(Condition condition) {
        return new InlineDerivedTable(this, condition, false);
    }

    @Override // org.jooq.Table
    public Table<R> where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    @Override // org.jooq.Table
    public Table<R> where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    @Override // org.jooq.Table
    public Table<R> where(Field<Boolean> field) {
        return where(DSL.condition(field));
    }

    @Override // org.jooq.Table
    public Table<R> where(SQL sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.Table
    public Table<R> where(String sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.Table
    public Table<R> where(String sql, Object... bindings) {
        return where(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.Table
    public Table<R> where(String sql, QueryPart... parts) {
        return where(DSL.condition(sql, parts));
    }

    @Override // org.jooq.Table
    public Table<R> whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    @Override // org.jooq.Table
    public Table<R> whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }

    public TableOptionalOnStep<Record> join(TableLike<?> table, JoinType type) {
        return join(table, type, null);
    }

    public TableOptionalOnStep<Record> join(TableLike<?> table, JoinType type, QOM.JoinHint hint) {
        if (this instanceof NoTable) {
            return new NoTableJoin(table.asTable());
        }
        if (this instanceof NoTableJoin) {
            NoTableJoin n = (NoTableJoin) this;
            return n.table.join(table, type, hint);
        }
        if (table instanceof NoTable) {
            return new NoTableJoin(this);
        }
        if (table instanceof NoTableJoin) {
            NoTableJoin n2 = (NoTableJoin) table;
            return join(n2.table, type, hint);
        }
        switch (type) {
            case CROSS_APPLY:
                return new CrossApply(this, table);
            case CROSS_JOIN:
                return new CrossJoin(this, table);
            case FULL_OUTER_JOIN:
                return new FullJoin(this, table, hint);
            case JOIN:
                return new Join(this, table, hint);
            case LEFT_ANTI_JOIN:
                return new LeftAntiJoin(this, table);
            case LEFT_OUTER_JOIN:
                return new LeftJoin(this, table, hint);
            case LEFT_SEMI_JOIN:
                return new LeftSemiJoin(this, table);
            case NATURAL_FULL_OUTER_JOIN:
                return new NaturalFullJoin(this, table, hint);
            case NATURAL_JOIN:
                return new NaturalJoin(this, table, hint);
            case NATURAL_LEFT_OUTER_JOIN:
                return new NaturalLeftJoin(this, table, hint);
            case NATURAL_RIGHT_OUTER_JOIN:
                return new NaturalRightJoin(this, table, hint);
            case OUTER_APPLY:
                return new OuterApply(this, table);
            case RIGHT_OUTER_JOIN:
                return new RightJoin(this, table, hint);
            case STRAIGHT_JOIN:
                return new StraightJoin(this, table, hint);
            default:
                throw new IllegalArgumentException("Unsupported join type: " + String.valueOf(type));
        }
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> join(TableLike<?> table) {
        return innerJoin(table);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> join(Path<?> path) {
        return innerJoin(path);
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> join(SQL sql) {
        return innerJoin(sql);
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> join(String sql) {
        return innerJoin(sql);
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> join(String sql, Object... bindings) {
        return innerJoin(sql, bindings);
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> join(String sql, QueryPart... parts) {
        return innerJoin(sql, parts);
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> join(Name name) {
        return innerJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> innerJoin(TableLike<?> table) {
        return join(table, JoinType.JOIN);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> innerJoin(Path<?> path) {
        return join(path, JoinType.JOIN);
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> innerJoin(SQL sql) {
        return innerJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> innerJoin(String sql) {
        return innerJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> innerJoin(String sql, Object... bindings) {
        return innerJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> innerJoin(String sql, QueryPart... parts) {
        return innerJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final TableOnStep<Record> innerJoin(Name name) {
        return innerJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftJoin(TableLike<?> table) {
        return leftOuterJoin(table);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> leftJoin(Path<?> path) {
        return leftOuterJoin(path);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftJoin(SQL sql) {
        return leftOuterJoin(sql);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftJoin(String sql) {
        return leftOuterJoin(sql);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftJoin(String sql, Object... bindings) {
        return leftOuterJoin(sql, bindings);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftJoin(String sql, QueryPart... parts) {
        return leftOuterJoin(sql, parts);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftJoin(Name name) {
        return leftOuterJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftOuterJoin(TableLike<?> table) {
        return (TablePartitionByStep) join(table, JoinType.LEFT_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> leftOuterJoin(Path<?> path) {
        return join(path, JoinType.LEFT_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftOuterJoin(SQL sql) {
        return leftOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftOuterJoin(String sql) {
        return leftOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftOuterJoin(String sql, Object... bindings) {
        return leftOuterJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftOuterJoin(String sql, QueryPart... parts) {
        return leftOuterJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> leftOuterJoin(Name name) {
        return leftOuterJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightJoin(TableLike<?> table) {
        return rightOuterJoin(table);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> rightJoin(Path<?> path) {
        return rightOuterJoin(path);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightJoin(SQL sql) {
        return rightOuterJoin(sql);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightJoin(String sql) {
        return rightOuterJoin(sql);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightJoin(String sql, Object... bindings) {
        return rightOuterJoin(sql, bindings);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightJoin(String sql, QueryPart... parts) {
        return rightOuterJoin(sql, parts);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightJoin(Name name) {
        return rightOuterJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightOuterJoin(TableLike<?> table) {
        return (TablePartitionByStep) join(table, JoinType.RIGHT_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> rightOuterJoin(Path<?> path) {
        return join(path, JoinType.RIGHT_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightOuterJoin(SQL sql) {
        return rightOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightOuterJoin(String sql) {
        return rightOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightOuterJoin(String sql, Object... bindings) {
        return rightOuterJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightOuterJoin(String sql, QueryPart... parts) {
        return rightOuterJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> rightOuterJoin(Name name) {
        return rightOuterJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullOuterJoin(TableLike<?> table) {
        return (TablePartitionByStep) join(table, JoinType.FULL_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> fullOuterJoin(Path<?> path) {
        return join(path, JoinType.FULL_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullOuterJoin(SQL sql) {
        return fullOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullOuterJoin(String sql) {
        return fullOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullOuterJoin(String sql, Object... bindings) {
        return fullOuterJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullOuterJoin(String sql, QueryPart... parts) {
        return fullOuterJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullOuterJoin(Name name) {
        return fullOuterJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullJoin(TableLike<?> table) {
        return fullOuterJoin(table);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> fullJoin(Path<?> path) {
        return fullOuterJoin(path);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullJoin(SQL sql) {
        return fullOuterJoin(sql);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullJoin(String sql) {
        return fullOuterJoin(sql);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullJoin(String sql, Object... bindings) {
        return fullOuterJoin(sql, bindings);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullJoin(String sql, QueryPart... parts) {
        return fullOuterJoin(sql, parts);
    }

    @Override // org.jooq.Table
    public final TablePartitionByStep<Record> fullJoin(Name name) {
        return fullOuterJoin(name);
    }

    @Override // org.jooq.Table
    public final Table<Record> crossJoin(TableLike<?> table) {
        return join(table, JoinType.CROSS_JOIN);
    }

    @Override // org.jooq.Table
    public final Table<Record> crossJoin(SQL sql) {
        return crossJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossJoin(String sql) {
        return crossJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossJoin(String sql, Object... bindings) {
        return crossJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossJoin(String sql, QueryPart... parts) {
        return crossJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossJoin(Name name) {
        return crossJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_JOIN);
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalJoin(SQL sql) {
        return naturalJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalJoin(String sql) {
        return naturalJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalJoin(String sql, Object... bindings) {
        return naturalJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalJoin(String sql, QueryPart... parts) {
        return naturalJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalJoin(Name name) {
        return naturalJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalLeftOuterJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_LEFT_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalLeftOuterJoin(SQL sql) {
        return naturalLeftOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalLeftOuterJoin(String sql) {
        return naturalLeftOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalLeftOuterJoin(String sql, Object... bindings) {
        return naturalLeftOuterJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalLeftOuterJoin(String sql, QueryPart... parts) {
        return naturalLeftOuterJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalLeftOuterJoin(Name name) {
        return naturalLeftOuterJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalRightOuterJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_RIGHT_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalRightOuterJoin(SQL sql) {
        return naturalRightOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalRightOuterJoin(String sql) {
        return naturalRightOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalRightOuterJoin(String sql, Object... bindings) {
        return naturalRightOuterJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalRightOuterJoin(String sql, QueryPart... parts) {
        return naturalRightOuterJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalRightOuterJoin(Name name) {
        return naturalRightOuterJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalFullOuterJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_FULL_OUTER_JOIN);
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalFullOuterJoin(SQL sql) {
        return naturalFullOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalFullOuterJoin(String sql) {
        return naturalFullOuterJoin(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalFullOuterJoin(String sql, Object... bindings) {
        return naturalFullOuterJoin(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalFullOuterJoin(String sql, QueryPart... parts) {
        return naturalFullOuterJoin(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final Table<Record> naturalFullOuterJoin(Name name) {
        return naturalFullOuterJoin(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossApply(TableLike<?> table) {
        return join(table, JoinType.CROSS_APPLY);
    }

    @Override // org.jooq.Table
    public final Table<Record> crossApply(SQL sql) {
        return crossApply(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossApply(String sql) {
        return crossApply(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossApply(String sql, Object... bindings) {
        return crossApply(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossApply(String sql, QueryPart... parts) {
        return crossApply(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final Table<Record> crossApply(Name name) {
        return crossApply(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final Table<Record> outerApply(TableLike<?> table) {
        return join(table, JoinType.OUTER_APPLY);
    }

    @Override // org.jooq.Table
    public final Table<Record> outerApply(SQL sql) {
        return outerApply(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> outerApply(String sql) {
        return outerApply(DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final Table<Record> outerApply(String sql, Object... bindings) {
        return outerApply(DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final Table<Record> outerApply(String sql, QueryPart... parts) {
        return outerApply(DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final Table<Record> outerApply(Name name) {
        return outerApply(DSL.table(name));
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> straightJoin(TableLike<?> table) {
        return join(table, JoinType.STRAIGHT_JOIN);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> straightJoin(Path<?> path) {
        return join(path, JoinType.STRAIGHT_JOIN);
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> straightJoin(SQL sql) {
        return straightJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> straightJoin(String sql) {
        return straightJoin((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> straightJoin(String sql, Object... bindings) {
        return straightJoin((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> straightJoin(String sql, QueryPart... parts) {
        return straightJoin((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.Table
    public final TableOptionalOnStep<Record> straightJoin(Name name) {
        return straightJoin((TableLike<?>) DSL.table(name));
    }

    @Override // org.jooq.Qualified
    public final Schema $schema() {
        return getSchema();
    }
}
