package org.jooq.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import org.jooq.Attachable;
import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.RecordHandler;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.jooq.Records;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.tools.jdbc.MockResultSet;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ResultImpl.class */
public final class ResultImpl<R extends Record> extends AbstractResult<R> implements Result<R> {
    private final List<R> records;

    @Override // org.jooq.Result
    public /* bridge */ /* synthetic */ RecordType recordType() {
        return super.recordType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultImpl(Configuration configuration, Collection<? extends Field<?>> fields) {
        this(configuration, Tools.row0(fields));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultImpl(Configuration configuration, Field<?>... fields) {
        this(configuration, Tools.row0(fields));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultImpl(Configuration configuration, AbstractRow fields) {
        super(configuration, fields);
        this.records = new ArrayList();
    }

    @Override // org.jooq.impl.AbstractFormattable
    final List<? extends Attachable> getAttachables() {
        return this.records;
    }

    @Override // org.jooq.Result
    public final <X, A> X collect(Collector<? super R, A, X> collector) {
        return (X) stream().collect(collector);
    }

    @Override // org.jooq.Result, java.util.List, java.util.Collection
    public final boolean isEmpty() {
        return this.records.isEmpty();
    }

    @Override // org.jooq.Result
    public final boolean isNotEmpty() {
        return !this.records.isEmpty();
    }

    @Override // org.jooq.Result
    public final <T> T getValue(int i, Field<T> field) {
        return (T) get(i).get(field);
    }

    @Override // org.jooq.Result
    public final Object getValue(int index, int fieldIndex) {
        return get(index).get(fieldIndex);
    }

    @Override // org.jooq.Result
    public final Object getValue(int index, String fieldName) {
        return get(index).get(fieldName);
    }

    @Override // org.jooq.Result
    public final <T> List<T> getValues(Field<T> field) {
        return (List) collect(Records.intoList(recordType().mapper(field)));
    }

    @Override // org.jooq.Result
    public final <U> List<U> getValues(Field<?> field, Class<? extends U> type) {
        return (List) collect(Records.intoList(recordType().mapper(field, Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <T, U> List<U> getValues(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (List) collect(Records.intoList(recordType().mapper(field, converter)));
    }

    @Override // org.jooq.Result
    public final List<?> getValues(int fieldIndex) {
        return (List) collect(Records.intoList(recordType().mapper(fieldIndex)));
    }

    @Override // org.jooq.Result
    public final <U> List<U> getValues(int fieldIndex, Class<? extends U> type) {
        return (List) collect(Records.intoList(recordType().mapper(fieldIndex, Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <U> List<U> getValues(int fieldIndex, Converter<?, ? extends U> converter) {
        return (List) collect(Records.intoList(recordType().mapper(fieldIndex, converter)));
    }

    @Override // org.jooq.Result
    public final List<?> getValues(String fieldName) {
        return (List) collect(Records.intoList(recordType().mapper(fieldName)));
    }

    @Override // org.jooq.Result
    public final <U> List<U> getValues(String fieldName, Class<? extends U> type) {
        return (List) collect(Records.intoList(recordType().mapper(fieldName, Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <U> List<U> getValues(String fieldName, Converter<?, ? extends U> converter) {
        return (List) collect(Records.intoList(recordType().mapper(fieldName, converter)));
    }

    @Override // org.jooq.Result
    public final List<?> getValues(Name fieldName) {
        return (List) collect(Records.intoList(recordType().mapper(fieldName)));
    }

    @Override // org.jooq.Result
    public final <U> List<U> getValues(Name fieldName, Class<? extends U> type) {
        return (List) collect(Records.intoList(recordType().mapper(fieldName, Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <U> List<U> getValues(Name fieldName, Converter<?, ? extends U> converter) {
        return (List) collect(Records.intoList(recordType().mapper(fieldName, converter)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addRecord(R record) {
        this.records.add(record);
    }

    @Override // org.jooq.Result
    public final List<Map<String, Object>> intoMaps() {
        return Tools.map(this, (v0) -> {
            return v0.intoMap();
        });
    }

    @Override // org.jooq.Result
    public final <K> Map<K, R> intoMap(Field<K> key) {
        return (Map) collect(Records.intoMap(recordType().mapper(key)));
    }

    @Override // org.jooq.Result
    public final Map<?, R> intoMap(int keyFieldIndex) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldIndex)));
    }

    @Override // org.jooq.Result
    public final Map<?, R> intoMap(String keyFieldName) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldName)));
    }

    @Override // org.jooq.Result
    public final Map<?, R> intoMap(Name keyFieldName) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldName)));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, V> intoMap(Field<K> key, Field<V> value) {
        return (Map) collect(Records.intoMap(recordType().mapper(key), recordType().mapper(value)));
    }

    @Override // org.jooq.Result
    public final Map<?, ?> intoMap(int keyFieldIndex, int valueFieldIndex) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldIndex), recordType().mapper(valueFieldIndex)));
    }

    @Override // org.jooq.Result
    public final Map<?, ?> intoMap(String keyFieldName, String valueFieldName) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldName), recordType().mapper(valueFieldName)));
    }

    @Override // org.jooq.Result
    public final Map<?, ?> intoMap(Name keyFieldName, Name valueFieldName) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldName), recordType().mapper(valueFieldName)));
    }

    @Override // org.jooq.Result
    public final Map<Record, R> intoMap(int[] keyFieldIndexes) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldIndexes)));
    }

    @Override // org.jooq.Result
    public final Map<Record, R> intoMap(String[] keyFieldNames) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldNames)));
    }

    @Override // org.jooq.Result
    public final Map<Record, R> intoMap(Name[] keyFieldNames) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldNames)));
    }

    @Override // org.jooq.Result
    public final Map<Record, R> intoMap(Field<?>[] keys) {
        return (Map) collect(Records.intoMap(recordType().mapper(keys)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Record> intoMap(int[] keyFieldIndexes, int[] valueFieldIndexes) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldIndexes), recordType().mapper(valueFieldIndexes)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Record> intoMap(String[] keyFieldNames, String[] valueFieldNames) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldNames), recordType().mapper(valueFieldNames)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Record> intoMap(Name[] keyFieldNames, Name[] valueFieldNames) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldNames), recordType().mapper(valueFieldNames)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Record> intoMap(Field<?>[] keys, Field<?>[] values) {
        return (Map) collect(Records.intoMap(recordType().mapper(keys), recordType().mapper(values)));
    }

    @Override // org.jooq.Result
    public final <E> Map<List<?>, E> intoMap(int[] keyFieldIndexes, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldIndexes).andThen((v0) -> {
            return v0.intoList();
        }), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<List<?>, E> intoMap(String[] keyFieldNames, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldNames).andThen((v0) -> {
            return v0.intoList();
        }), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<List<?>, E> intoMap(Name[] keyFieldNames, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldNames).andThen((v0) -> {
            return v0.intoList();
        }), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<List<?>, E> intoMap(Field<?>[] keys, Class<? extends E> type) {
        return intoMap(keys, recordType().mapper(Tools.configuration(this), type));
    }

    @Override // org.jooq.Result
    public final <E> Map<List<?>, E> intoMap(int[] keyFieldIndexes, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldIndexes).andThen((v0) -> {
            return v0.intoList();
        }), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<List<?>, E> intoMap(String[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldNames).andThen((v0) -> {
            return v0.intoList();
        }), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<List<?>, E> intoMap(Name[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldNames).andThen((v0) -> {
            return v0.intoList();
        }), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<List<?>, E> intoMap(Field<?>[] keys, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(keys).andThen((v0) -> {
            return v0.intoList();
        }), mapper));
    }

    @Override // org.jooq.Result
    public final <K> Map<K, R> intoMap(Class<? extends K> keyType) {
        return (Map) collect(Records.intoMap(recordType().mapper(Tools.configuration(this), keyType)));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, V> intoMap(Class<? extends K> keyType, Class<? extends V> valueType) {
        return (Map) collect(Records.intoMap(recordType().mapper(Tools.configuration(this), keyType), recordType().mapper(Tools.configuration(this), valueType)));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, V> intoMap(Class<? extends K> keyType, RecordMapper<? super R, V> valueMapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(Tools.configuration(this), keyType), valueMapper));
    }

    @Override // org.jooq.Result
    public final <K> Map<K, R> intoMap(RecordMapper<? super R, K> keyMapper) {
        return (Map) collect(Records.intoMap(keyMapper));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, V> intoMap(RecordMapper<? super R, K> keyMapper, Class<V> valueType) {
        return (Map) collect(Records.intoMap(keyMapper, recordType().mapper(Tools.configuration(this), valueType)));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, V> intoMap(RecordMapper<? super R, K> keyMapper, RecordMapper<? super R, V> valueMapper) {
        return (Map) collect(Records.intoMap(keyMapper, valueMapper));
    }

    @Override // org.jooq.Result
    public final <S extends Record> Map<S, R> intoMap(Table<S> table) {
        return (Map) collect(Records.intoMap(recordType().mapper(table)));
    }

    @Override // org.jooq.Result
    public final <S extends Record, T extends Record> Map<S, T> intoMap(Table<S> keyTable, Table<T> valueTable) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyTable), recordType().mapper(valueTable)));
    }

    @Override // org.jooq.Result
    public final <E, S extends Record> Map<S, E> intoMap(Table<S> table, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(recordType().mapper(table), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E, S extends Record> Map<S, E> intoMap(Table<S> table, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(table), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, E> intoMap(int keyFieldIndex, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldIndex), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, E> intoMap(String keyFieldName, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldName), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, E> intoMap(Name keyFieldName, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldName), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <K, E> Map<K, E> intoMap(Field<K> key, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(recordType().mapper(key), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, E> intoMap(int keyFieldIndex, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldIndex), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, E> intoMap(String keyFieldName, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldName), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, E> intoMap(Name keyFieldName, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(keyFieldName), mapper));
    }

    @Override // org.jooq.Result
    public final <K, E> Map<K, E> intoMap(Field<K> key, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(recordType().mapper(key), mapper));
    }

    @Override // org.jooq.Result
    public final <K> Map<K, Result<R>> intoGroups(Field<K> key) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(key)));
    }

    @Override // org.jooq.Result
    public final Map<?, Result<R>> intoGroups(int keyFieldIndex) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldIndex)));
    }

    @Override // org.jooq.Result
    public final Map<?, Result<R>> intoGroups(String keyFieldName) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldName)));
    }

    @Override // org.jooq.Result
    public final Map<?, Result<R>> intoGroups(Name keyFieldName) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldName)));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, List<V>> intoGroups(Field<K> key, Field<V> value) {
        return (Map) collect(Records.intoGroups(recordType().mapper(key), recordType().mapper(value)));
    }

    @Override // org.jooq.Result
    public final Map<?, List<?>> intoGroups(int keyFieldIndex, int valueFieldIndex) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldIndex), recordType().mapper(valueFieldIndex)));
    }

    @Override // org.jooq.Result
    public final Map<?, List<?>> intoGroups(String keyFieldName, String valueFieldName) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldName), recordType().mapper(valueFieldName)));
    }

    @Override // org.jooq.Result
    public final Map<?, List<?>> intoGroups(Name keyFieldName, Name valueFieldName) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldName), recordType().mapper(valueFieldName)));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, List<E>> intoGroups(int keyFieldIndex, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldIndex), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, List<E>> intoGroups(String keyFieldName, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldName), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, List<E>> intoGroups(Name keyFieldName, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldName), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <K, E> Map<K, List<E>> intoGroups(Field<K> key, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(key), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <K, E> Map<K, List<E>> intoGroups(Field<K> key, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(key), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, List<E>> intoGroups(int keyFieldIndex, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldIndex), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, List<E>> intoGroups(String keyFieldName, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldName), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<?, List<E>> intoGroups(Name keyFieldName, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldName), mapper));
    }

    @Override // org.jooq.Result
    public final Map<Record, Result<R>> intoGroups(int[] keyFieldIndexes) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldIndexes)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Result<R>> intoGroups(String[] keyFieldNames) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldNames)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Result<R>> intoGroups(Name[] keyFieldNames) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldNames)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Result<R>> intoGroups(Field<?>[] keys) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keys)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Result<Record>> intoGroups(int[] keyFieldIndexes, int[] valueFieldIndexes) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldIndexes), recordType().mapper(valueFieldIndexes)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Result<Record>> intoGroups(String[] keyFieldNames, String[] valueFieldNames) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldNames), recordType().mapper(valueFieldNames)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Result<Record>> intoGroups(Name[] keyFieldNames, Name[] valueFieldNames) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyFieldNames), recordType().mapper(valueFieldNames)));
    }

    @Override // org.jooq.Result
    public final Map<Record, Result<Record>> intoGroups(Field<?>[] keys, Field<?>[] values) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keys), recordType().mapper(values)));
    }

    @Override // org.jooq.Result
    public final <E> Map<Record, List<E>> intoGroups(int[] keyFieldIndexes, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldIndexes), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<Record, List<E>> intoGroups(String[] keyFieldNames, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldNames), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<Record, List<E>> intoGroups(Name[] keyFieldNames, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldNames), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<Record, List<E>> intoGroups(Field<?>[] keys, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keys), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E> Map<Record, List<E>> intoGroups(int[] keyFieldIndexes, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldIndexes), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<Record, List<E>> intoGroups(String[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldNames), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<Record, List<E>> intoGroups(Name[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keyFieldNames), mapper));
    }

    @Override // org.jooq.Result
    public final <E> Map<Record, List<E>> intoGroups(Field<?>[] keys, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(keys), mapper));
    }

    @Override // org.jooq.Result
    public final <K> Map<K, Result<R>> intoGroups(Class<? extends K> keyType) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(Tools.configuration(this), keyType)));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, List<V>> intoGroups(Class<? extends K> keyType, Class<? extends V> valueType) {
        return (Map) collect(Records.intoGroups(recordType().mapper(Tools.configuration(this), keyType), recordType().mapper(Tools.configuration(this), valueType)));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, List<V>> intoGroups(Class<? extends K> keyType, RecordMapper<? super R, V> valueMapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(Tools.configuration(this), keyType), valueMapper));
    }

    @Override // org.jooq.Result
    public final <K> Map<K, Result<R>> intoGroups(RecordMapper<? super R, K> keyMapper) {
        return (Map) collect(Records.intoResultGroups(keyMapper));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, List<V>> intoGroups(RecordMapper<? super R, K> keyMapper, Class<V> valueType) {
        return (Map) collect(Records.intoGroups(keyMapper, recordType().mapper(Tools.configuration(this), valueType)));
    }

    @Override // org.jooq.Result
    public final <K, V> Map<K, List<V>> intoGroups(RecordMapper<? super R, K> keyMapper, RecordMapper<? super R, V> valueMapper) {
        return (Map) collect(Records.intoGroups(keyMapper, valueMapper));
    }

    @Override // org.jooq.Result
    public final <S extends Record> Map<S, Result<R>> intoGroups(Table<S> table) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(table)));
    }

    @Override // org.jooq.Result
    public final <S extends Record, T extends Record> Map<S, Result<T>> intoGroups(Table<S> keyTable, Table<T> valueTable) {
        return (Map) collect(Records.intoResultGroups(recordType().mapper(keyTable), recordType().mapper(valueTable)));
    }

    @Override // org.jooq.Result
    public final <E, S extends Record> Map<S, List<E>> intoGroups(Table<S> table, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(recordType().mapper(table), recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <E, S extends Record> Map<S, List<E>> intoGroups(Table<S> table, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(recordType().mapper(table), mapper));
    }

    @Override // org.jooq.Result
    public final Object[][] intoArrays() {
        return (Object[][]) collect(Records.intoArray(new Object[0], (v0) -> {
            return v0.intoArray();
        }));
    }

    @Override // org.jooq.Result
    public final Object[] intoArray(int fieldIndex) {
        return (Object[]) collect(Records.intoArray(field(safeIndex(fieldIndex)).getType(), recordType().mapper(fieldIndex)));
    }

    @Override // org.jooq.Result
    public final <U> U[] intoArray(int i, Class<? extends U> cls) {
        return (U[]) ((Object[]) collect(Records.intoArray(cls, recordType().mapper(i, Tools.configuration(this), cls))));
    }

    @Override // org.jooq.Result
    public final <U> U[] intoArray(int i, Converter<?, ? extends U> converter) {
        return (U[]) ((Object[]) collect(Records.intoArray(converter.toType(), recordType().mapper(i, converter))));
    }

    @Override // org.jooq.Result
    public final Object[] intoArray(String fieldName) {
        return (Object[]) collect(Records.intoArray(field(Tools.indexOrFail(this, fieldName)).getType(), recordType().mapper(fieldName)));
    }

    @Override // org.jooq.Result
    public final <U> U[] intoArray(String str, Class<? extends U> cls) {
        return (U[]) ((Object[]) collect(Records.intoArray(cls, recordType().mapper(str, Tools.configuration(this), cls))));
    }

    @Override // org.jooq.Result
    public final <U> U[] intoArray(String str, Converter<?, ? extends U> converter) {
        return (U[]) ((Object[]) collect(Records.intoArray(converter.toType(), recordType().mapper(str, converter))));
    }

    @Override // org.jooq.Result
    public final Object[] intoArray(Name fieldName) {
        return (Object[]) collect(Records.intoArray(field(Tools.indexOrFail(this, fieldName)).getType(), recordType().mapper(fieldName)));
    }

    @Override // org.jooq.Result
    public final <U> U[] intoArray(Name name, Class<? extends U> cls) {
        return (U[]) ((Object[]) collect(Records.intoArray(cls, recordType().mapper(name, Tools.configuration(this), cls))));
    }

    @Override // org.jooq.Result
    public final <U> U[] intoArray(Name name, Converter<?, ? extends U> converter) {
        return (U[]) ((Object[]) collect(Records.intoArray(converter.toType(), recordType().mapper(name, converter))));
    }

    @Override // org.jooq.Result
    public final <T> T[] intoArray(Field<T> field) {
        return (T[]) ((Object[]) collect(Records.intoArray(field.getType(), recordType().mapper(field))));
    }

    @Override // org.jooq.Result
    public final <U> U[] intoArray(Field<?> field, Class<? extends U> cls) {
        return (U[]) ((Object[]) collect(Records.intoArray(cls, recordType().mapper(field, Tools.configuration(this), cls))));
    }

    @Override // org.jooq.Result
    public final <T, U> U[] intoArray(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (U[]) ((Object[]) collect(Records.intoArray(converter.toType(), recordType().mapper(field, converter))));
    }

    @Override // org.jooq.Result
    public final <E> Set<E> intoSet(RecordMapper<? super R, E> mapper) {
        return (Set) collect(Records.intoSet(mapper));
    }

    @Override // org.jooq.Result
    public final Set<?> intoSet(int fieldIndex) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldIndex)));
    }

    @Override // org.jooq.Result
    public final <U> Set<U> intoSet(int fieldIndex, Class<? extends U> type) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldIndex, Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <U> Set<U> intoSet(int fieldIndex, Converter<?, ? extends U> converter) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldIndex, converter)));
    }

    @Override // org.jooq.Result
    public final Set<?> intoSet(String fieldName) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldName)));
    }

    @Override // org.jooq.Result
    public final <U> Set<U> intoSet(String fieldName, Class<? extends U> type) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldName, Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <U> Set<U> intoSet(String fieldName, Converter<?, ? extends U> converter) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldName, converter)));
    }

    @Override // org.jooq.Result
    public final Set<?> intoSet(Name fieldName) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldName)));
    }

    @Override // org.jooq.Result
    public final <U> Set<U> intoSet(Name fieldName, Class<? extends U> type) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldName, Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <U> Set<U> intoSet(Name fieldName, Converter<?, ? extends U> converter) {
        return (Set) collect(Records.intoSet(recordType().mapper(fieldName, converter)));
    }

    @Override // org.jooq.Result
    public final <T> Set<T> intoSet(Field<T> field) {
        return (Set) collect(Records.intoSet(recordType().mapper(field)));
    }

    @Override // org.jooq.Result
    public final <U> Set<U> intoSet(Field<?> field, Class<? extends U> type) {
        return (Set) collect(Records.intoSet(recordType().mapper(field, Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <T, U> Set<U> intoSet(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (Set) collect(Records.intoSet(recordType().mapper(field, converter)));
    }

    @Override // org.jooq.Result
    public final Result<Record> into(Field<?>... f) {
        Result<Record> result = new ResultImpl<>(Tools.configuration(this), f);
        Iterator<R> it = iterator();
        while (it.hasNext()) {
            Record record = it.next();
            result.add(record.into(f));
        }
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1> Result<Record1<T1>> into(Field<T1> field) {
        return (Result<Record1<T1>>) into((Field<?>[]) new Field[]{field});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2> Result<Record2<T1, T2>> into(Field<T1> field, Field<T2> field2) {
        return (Result<Record2<T1, T2>>) into((Field<?>[]) new Field[]{field, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3> Result<Record3<T1, T2, T3>> into(Field<T1> field, Field<T2> field2, Field<T3> field3) {
        return (Result<Record3<T1, T2, T3>>) into((Field<?>[]) new Field[]{field, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4> Result<Record4<T1, T2, T3, T4>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return (Result<Record4<T1, T2, T3, T4>>) into((Field<?>[]) new Field[]{field, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5> Result<Record5<T1, T2, T3, T4, T5>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return (Result<Record5<T1, T2, T3, T4, T5>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6> Result<Record6<T1, T2, T3, T4, T5, T6>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return (Result<Record6<T1, T2, T3, T4, T5, T6>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7> Result<Record7<T1, T2, T3, T4, T5, T6, T7>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return (Result<Record7<T1, T2, T3, T4, T5, T6, T7>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8> Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return (Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return (Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return (Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return (Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return (Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return (Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return (Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return (Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return (Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return (Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return (Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return (Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return (Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return (Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Result
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return (Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) into((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.Result
    public final <E> List<E> into(Class<? extends E> type) {
        return (List) collect(Records.intoList(recordType().mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.Result
    public final <Z extends Record> Result<Z> into(Table<Z> table) {
        Result<Z> list = new ResultImpl<>(Tools.configuration(this), (AbstractRow) table.fieldsRow());
        Iterator<R> it = iterator();
        while (it.hasNext()) {
            R record = it.next();
            list.add(record.into(table));
        }
        return list;
    }

    @Override // org.jooq.Result
    public final <H extends RecordHandler<? super R>> H into(H handler) {
        forEach(handler);
        return handler;
    }

    @Override // org.jooq.Result
    public final ResultSet intoResultSet() {
        return new MockResultSet(this);
    }

    @Override // org.jooq.Result
    public final <E> List<E> map(RecordMapper<? super R, E> mapper) {
        return Tools.map(this, t -> {
            return mapper.map(t);
        });
    }

    @Override // org.jooq.Result
    public final <T extends Comparable<? super T>> Result<R> sortAsc(Field<T> field) {
        return sortAsc(field, new NaturalComparator());
    }

    @Override // org.jooq.Result
    public final Result<R> sortAsc(int fieldIndex) {
        return sortAsc(fieldIndex, new NaturalComparator());
    }

    @Override // org.jooq.Result
    public final Result<R> sortAsc(String fieldName) {
        return sortAsc(fieldName, new NaturalComparator());
    }

    @Override // org.jooq.Result
    public final Result<R> sortAsc(Name fieldName) {
        return sortAsc(fieldName, new NaturalComparator());
    }

    @Override // org.jooq.Result
    public final <T> Result<R> sortAsc(Field<T> field, Comparator<? super T> comparator) {
        return sortAsc(Tools.indexOrFail((Fields) fieldsRow(), (Field<?>) field), comparator);
    }

    @Override // org.jooq.Result
    public final Result<R> sortAsc(int fieldIndex, Comparator<?> comparator) {
        return sortAsc(new RecordComparator(fieldIndex, comparator));
    }

    @Override // org.jooq.Result
    public final Result<R> sortAsc(String fieldName, Comparator<?> comparator) {
        return sortAsc(Tools.indexOrFail(fieldsRow(), fieldName), comparator);
    }

    @Override // org.jooq.Result
    public final Result<R> sortAsc(Name fieldName, Comparator<?> comparator) {
        return sortAsc(Tools.indexOrFail(fieldsRow(), fieldName), comparator);
    }

    @Override // org.jooq.Result
    public final Result<R> sortAsc(Comparator<? super R> comparator) {
        sort(comparator);
        return this;
    }

    @Override // org.jooq.Result
    public final <T extends Comparable<? super T>> Result<R> sortDesc(Field<T> field) {
        return sortAsc(field, Collections.reverseOrder(new NaturalComparator()));
    }

    @Override // org.jooq.Result
    public final Result<R> sortDesc(int fieldIndex) {
        return sortAsc(fieldIndex, Collections.reverseOrder(new NaturalComparator()));
    }

    @Override // org.jooq.Result
    public final Result<R> sortDesc(String fieldName) {
        return sortAsc(fieldName, Collections.reverseOrder(new NaturalComparator()));
    }

    @Override // org.jooq.Result
    public final Result<R> sortDesc(Name fieldName) {
        return sortAsc(fieldName, Collections.reverseOrder(new NaturalComparator()));
    }

    @Override // org.jooq.Result
    public final <T> Result<R> sortDesc(Field<T> field, Comparator<? super T> comparator) {
        return sortAsc(field, Collections.reverseOrder(comparator));
    }

    @Override // org.jooq.Result
    public final Result<R> sortDesc(int fieldIndex, Comparator<?> comparator) {
        return sortAsc(fieldIndex, Collections.reverseOrder(comparator));
    }

    @Override // org.jooq.Result
    public final Result<R> sortDesc(String fieldName, Comparator<?> comparator) {
        return sortAsc(fieldName, Collections.reverseOrder(comparator));
    }

    @Override // org.jooq.Result
    public final Result<R> sortDesc(Name fieldName, Comparator<?> comparator) {
        return sortAsc(fieldName, Collections.reverseOrder(comparator));
    }

    @Override // org.jooq.Result
    public final Result<R> sortDesc(Comparator<? super R> comparator) {
        return sortAsc(Collections.reverseOrder(comparator));
    }

    @Override // org.jooq.Result
    public final Result<R> intern(Field<?>... f) {
        return intern(this.fields.fields.indexesOf(f));
    }

    @Override // org.jooq.Result
    public final Result<R> intern(int... fieldIndexes) {
        for (int fieldIndex : fieldIndexes) {
            if (this.fields.field(fieldIndex).getType() == String.class) {
                Iterator<R> it = iterator();
                while (it.hasNext()) {
                    Record record = it.next();
                    ((AbstractRecord) record).intern0(fieldIndex);
                }
            }
        }
        return this;
    }

    @Override // org.jooq.Result
    public final Result<R> intern(String... fieldNames) {
        return intern(this.fields.fields.indexesOf(fieldNames));
    }

    @Override // org.jooq.Result
    public final Result<R> intern(Name... fieldNames) {
        return intern(this.fields.fields.indexesOf(fieldNames));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ResultImpl$RecordComparator.class */
    public static class RecordComparator<T, R extends Record> implements Comparator<R> {
        private final Comparator<? super T> comparator;
        private final int fieldIndex;

        RecordComparator(int fieldIndex, Comparator<? super T> comparator) {
            this.fieldIndex = fieldIndex;
            this.comparator = comparator;
        }

        @Override // java.util.Comparator
        public final int compare(R record1, R record2) {
            return this.comparator.compare(record1.get(this.fieldIndex), record2.get(this.fieldIndex));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ResultImpl$NaturalComparator.class */
    private static class NaturalComparator<T extends Comparable<? super T>> implements Comparator<T> {
        private NaturalComparator() {
        }

        @Override // java.util.Comparator
        public final int compare(T o1, T o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return o1.compareTo(o2);
        }
    }

    private final int safeIndex(int index) {
        if (index >= 0 && index < this.fields.size()) {
            return index;
        }
        throw new IllegalArgumentException("No field at index " + index + " in Record type " + String.valueOf(this.fields));
    }

    @Override // org.jooq.Result
    public final <O extends UpdatableRecord<O>> Result<O> fetchParents(ForeignKey<R, O> key) {
        return key.fetchParents(this);
    }

    @Override // org.jooq.Result
    public final <O extends TableRecord<O>> Result<O> fetchChildren(ForeignKey<O, R> key) {
        return key.fetchChildren(this);
    }

    @Override // org.jooq.Result
    public final <O extends UpdatableRecord<O>> Table<O> parents(ForeignKey<R, O> key) {
        return key.parents(this);
    }

    @Override // org.jooq.Result
    public final <O extends TableRecord<O>> Table<O> children(ForeignKey<O, R> key) {
        return key.children(this);
    }

    public String toString() {
        return format(Tools.configuration(this).formattingProvider().txtFormat().maxRows(50).maxColWidth(50));
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        return this.records.hashCode();
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ResultImpl) {
            ResultImpl<?> r = (ResultImpl) obj;
            return this.records.equals(r.records);
        }
        return false;
    }

    @Override // java.util.List, java.util.Collection
    public final int size() {
        return this.records.size();
    }

    @Override // java.util.List, java.util.Collection
    public final boolean contains(Object o) {
        return this.records.contains(o);
    }

    @Override // java.util.List, java.util.Collection
    public final Object[] toArray() {
        return this.records.toArray();
    }

    @Override // java.util.List, java.util.Collection
    public final <T> T[] toArray(T[] tArr) {
        return (T[]) this.records.toArray(tArr);
    }

    @Override // java.util.List, java.util.Collection
    public final boolean add(R e) {
        return this.records.add(e);
    }

    @Override // java.util.List, java.util.Collection
    public final boolean remove(Object o) {
        return this.records.remove(o);
    }

    @Override // java.util.List, java.util.Collection
    public final boolean containsAll(Collection<?> c) {
        return this.records.containsAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public final boolean addAll(Collection<? extends R> c) {
        return this.records.addAll(c);
    }

    @Override // java.util.List
    public final boolean addAll(int index, Collection<? extends R> c) {
        return this.records.addAll(index, c);
    }

    @Override // java.util.List, java.util.Collection
    public final boolean removeAll(Collection<?> c) {
        return this.records.removeAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public final boolean retainAll(Collection<?> c) {
        return this.records.retainAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public final void clear() {
        this.records.clear();
    }

    @Override // java.util.List
    public final R get(int index) {
        return this.records.get(index);
    }

    @Override // java.util.List
    public final R set(int index, R element) {
        return this.records.set(index, element);
    }

    @Override // java.util.List
    public final void add(int index, R element) {
        this.records.add(index, element);
    }

    @Override // java.util.List
    public final R remove(int index) {
        return this.records.remove(index);
    }

    @Override // java.util.List
    public final int indexOf(Object o) {
        return this.records.indexOf(o);
    }

    @Override // java.util.List
    public final int lastIndexOf(Object o) {
        return this.records.lastIndexOf(o);
    }

    @Override // java.lang.Iterable, java.util.List, java.util.Collection
    public final Iterator<R> iterator() {
        return this.records.iterator();
    }

    @Override // java.util.List
    public final ListIterator<R> listIterator() {
        return this.records.listIterator();
    }

    @Override // java.util.List
    public final ListIterator<R> listIterator(int index) {
        return this.records.listIterator(index);
    }

    @Override // java.util.List
    public final List<R> subList(int fromIndex, int toIndex) {
        return this.records.subList(fromIndex, toIndex);
    }
}
