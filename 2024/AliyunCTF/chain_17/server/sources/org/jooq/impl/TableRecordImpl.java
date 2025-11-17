package org.jooq.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InsertQuery;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.StoreQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.Update;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.BatchCRUD;
import org.jooq.impl.RecordDelegate;
import org.jooq.tools.JooqLogger;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableRecordImpl.class */
public class TableRecordImpl<R extends TableRecord<R>> extends AbstractQualifiedRecord<R> implements TableRecord<R> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) TableRecordImpl.class);
    private static final Set<SQLDialect> REFRESH_GENERATED_KEYS = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> REFRESH_GENERATED_KEYS_ON_UPDATE = SQLDialect.supportedBy(SQLDialect.HSQLDB);
    private static final long[] TRUNCATE = {1000, 100, 10, 1};

    @Override // org.jooq.impl.AbstractQualifiedRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Row valuesRow() {
        return super.valuesRow();
    }

    @Override // org.jooq.impl.AbstractQualifiedRecord, org.jooq.Fields
    public /* bridge */ /* synthetic */ Row fieldsRow() {
        return super.fieldsRow();
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ int compareTo(Record record) {
        return super.compareTo(record);
    }

    @Override // org.jooq.impl.AbstractRecord
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Object into(Object obj) {
        return super.into((TableRecordImpl<R>) obj);
    }

    @Override // org.jooq.impl.AbstractStore, org.jooq.Record
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractStore, org.jooq.Record
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    public TableRecordImpl(Table<R> table) {
        super(table);
    }

    @Override // org.jooq.TableRecord
    public final Table<R> getTable() {
        return (Table) getQualifier();
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public final R original() {
        return (R) super.original();
    }

    @Override // org.jooq.TableRecord
    public final <O extends UpdatableRecord<O>> O fetchParent(ForeignKey<R, O> key) {
        return key.fetchParent(this);
    }

    @Override // org.jooq.TableRecord
    public final <O extends UpdatableRecord<O>> Table<O> parent(ForeignKey<R, O> key) {
        return key.parent(this);
    }

    @Override // org.jooq.TableRecord
    public final int insert() {
        return insert(this.fields.fields.fields);
    }

    @Override // org.jooq.TableRecord
    public final int insert(Field<?>... storeFields) {
        return storeInsert(storeFields);
    }

    @Override // org.jooq.TableRecord
    public final int insert(Collection<? extends Field<?>> storeFields) {
        return insert((Field<?>[]) storeFields.toArray(Tools.EMPTY_FIELD));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int storeInsert(Field<?>[] storeFields) {
        int[] result = new int[1];
        RecordDelegate.delegate(configuration(), this, RecordDelegate.RecordLifecycleType.INSERT).operate(record -> {
            result[0] = storeInsert0(storeFields);
            return record;
        });
        return result[0];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int storeInsert0(Field<?>[] storeFields) {
        DSLContext create = create();
        InsertQuery<R> insert = create.insertQuery(getTable());
        List<Field<?>> changedFields = addChangedValues(storeFields, insert, false);
        if (changedFields.isEmpty()) {
            if (Boolean.FALSE.equals(create.settings().isInsertUnchangedRecords())) {
                if (log.isDebugEnabled()) {
                    log.debug("Query is not executable", insert);
                    return 0;
                }
                return 0;
            }
            insert.setDefaultValues();
        }
        BigInteger version = addRecordVersion(insert, false);
        Timestamp timestamp = addRecordTimestamp(insert, false);
        Collection<Field<?>> key = setReturningIfNeeded(insert);
        try {
            int result = insert.execute();
            if (result > 0) {
                for (Field<?> changedField : changedFields) {
                    changed(changedField, false);
                }
                setRecordVersionAndTimestamp(version, timestamp);
                getReturningIfNeeded(insert, key);
                this.fetched = true;
            }
            return result;
        } catch (BatchCRUD.QueryCollectorSignal e) {
            e.version = version;
            e.timestamp = timestamp;
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public final void getReturningIfNeeded(StoreQuery<R> query, Collection<Field<?>> key) {
        if (!Tools.isEmpty((Collection<?>) key)) {
            R record = query.getReturnedRecord();
            if (record != null) {
                for (Field<?> field : key) {
                    int index = Tools.indexOrFail(fieldsRow(), field);
                    Object value = record.get(field);
                    this.values[index] = value;
                    this.originals[index] = value;
                }
            }
            Configuration c = configuration();
            if (SettingsTools.returnAnyNonIdentityOnUpdatableRecord(c.settings())) {
                if (((REFRESH_GENERATED_KEYS.contains(c.dialect()) && record == null) || (REFRESH_GENERATED_KEYS_ON_UPDATE.contains(c.dialect()) && (query instanceof Update))) && (this instanceof UpdatableRecord)) {
                    ((UpdatableRecord) this).refresh((Field<?>[]) key.toArray(Tools.EMPTY_FIELD));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Collection<Field<?>> setReturningIfNeeded(StoreQuery<R> query) {
        Collection<Field<?>> key = null;
        if (configuration() != null && SettingsTools.returnAnyOnUpdatableRecord(configuration().settings())) {
            key = getReturning(query);
            if (!Tools.isEmpty((Collection<?>) key)) {
                query.setReturning(key);
            }
        }
        return key;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setRecordVersionAndTimestamp(BigInteger version, Timestamp timestamp) {
        if (version != null) {
            TableField<R, ?> field = getTable().getRecordVersion();
            int fieldIndex = Tools.indexOrFail(this.fields, field);
            Object value = field.getDataType().convert(version);
            this.values[fieldIndex] = value;
            this.originals[fieldIndex] = value;
            this.changed.clear(fieldIndex);
        }
        if (timestamp != null) {
            TableField<R, ?> field2 = getTable().getRecordTimestamp();
            int fieldIndex2 = Tools.indexOrFail(this.fields, field2);
            Object value2 = field2.getDataType().convert(timestamp);
            this.values[fieldIndex2] = value2;
            this.originals[fieldIndex2] = value2;
            this.changed.clear(fieldIndex2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<Field<?>> addChangedValues(Field<?>[] storeFields, StoreQuery<R> query, boolean forUpdate) {
        FieldsImpl<Record> f = new FieldsImpl<>(storeFields);
        List<Field<?>> result = new ArrayList<>();
        for (Field<?> field : this.fields.fields.fields) {
            if (changed(field) && f.field(field) != null && writable(field, forUpdate)) {
                addValue(query, field, forUpdate);
                result.add(field);
            }
        }
        return result;
    }

    final boolean writable(Field<?> field, boolean forUpdate) {
        return true;
    }

    final <T> void addValue(StoreQuery<?> store, Field<T> field, Object value, boolean forUpdate) {
        store.addValue((Field) field, (Field) Tools.field(value, field));
        if (forUpdate) {
            ((InsertQuery) store).addValueForUpdate((Field) field, (Field) DSL.excluded(field));
        }
    }

    final <T> void addValue(StoreQuery<?> store, Field<T> field, boolean forUpdate) {
        addValue(store, field, get(field), forUpdate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Timestamp addRecordTimestamp(StoreQuery<?> store, boolean forUpdate) {
        Timestamp result = null;
        TableField<R, ?> timestamp = getTable().getRecordTimestamp();
        if (timestamp != null && isUpdateRecordTimestamp()) {
            Timestamp truncate = truncate(new Timestamp(configuration().clock().millis()), timestamp.getDataType());
            result = truncate;
            addValue(store, timestamp, truncate, forUpdate);
        }
        return result;
    }

    private static final Timestamp truncate(Timestamp ts, DataType<?> type) {
        if (type.isDate()) {
            return new Timestamp(ts.getYear(), ts.getMonth(), ts.getDate(), 0, 0, 0, 0);
        }
        if (!type.precisionDefined() || type.precision() >= 3) {
            return ts;
        }
        return new Timestamp((ts.getTime() / TRUNCATE[type.precision()]) * TRUNCATE[type.precision()]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final BigInteger addRecordVersion(StoreQuery<?> store, boolean forUpdate) {
        BigInteger result = null;
        TableField<R, ?> version = getTable().getRecordVersion();
        if (version != null && isUpdateRecordVersion()) {
            Object value = get(version);
            if (value == null) {
                result = BigInteger.ONE;
            } else {
                result = new BigInteger(value.toString()).add(BigInteger.ONE);
            }
            addValue(store, version, result, forUpdate);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Object getRecordVersion() {
        TableField<R, ?> field = getTable().getRecordVersion();
        if (field != null) {
            return get(field);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Object getRecordTimestamp() {
        TableField<R, ?> field = getTable().getRecordTimestamp();
        if (field != null) {
            return get(field);
        }
        return null;
    }

    final boolean isUpdateRecordVersion() {
        Configuration configuration = configuration();
        return configuration == null || !Boolean.FALSE.equals(configuration.settings().isUpdateRecordVersion());
    }

    final boolean isUpdateRecordTimestamp() {
        Configuration configuration = configuration();
        return configuration == null || !Boolean.FALSE.equals(configuration.settings().isUpdateRecordTimestamp());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isTimestampOrVersionAvailable() {
        return (getTable().getRecordTimestamp() != null && isUpdateRecordTimestamp()) || (getTable().getRecordVersion() != null && isUpdateRecordVersion());
    }

    final Collection<Field<?>> getReturning(StoreQuery<R> query) {
        Settings s = configuration().settings();
        if (Boolean.TRUE.equals(s.isReturnAllOnUpdatableRecord())) {
            return Arrays.asList(fields());
        }
        Collection<Field<?>> result = new LinkedHashSet<>();
        if (!Boolean.FALSE.equals(s.isReturnIdentityOnUpdatableRecord()) && ((query instanceof InsertQuery) || SettingsTools.updatablePrimaryKeys(s))) {
            Tools.let(getTable().getIdentity(), i -> {
                result.add(i.getField());
            });
            Tools.let(getPrimaryKey(), k -> {
                result.addAll(k.getFields());
            });
        }
        if (Boolean.TRUE.equals(s.isReturnDefaultOnUpdatableRecord())) {
            for (Field<?> f : fields()) {
                if (f.getDataType().defaulted()) {
                    result.add(f);
                }
            }
        }
        if (Boolean.TRUE.equals(s.isReturnComputedOnUpdatableRecord())) {
            for (Field<?> f2 : fields()) {
                if (f2.getDataType().computed()) {
                    result.add(f2);
                }
            }
        }
        return result;
    }
}
