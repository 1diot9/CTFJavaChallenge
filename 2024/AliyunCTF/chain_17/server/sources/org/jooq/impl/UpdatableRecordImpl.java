package org.jooq.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jooq.AggregateFunction;
import org.jooq.ConditionProvider;
import org.jooq.Configuration;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.GroupField;
import org.jooq.InsertQuery;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.StoreQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;
import org.jooq.UniqueKey;
import org.jooq.UpdatableRecord;
import org.jooq.conf.SettingsTools;
import org.jooq.conf.UpdateUnchangedRecords;
import org.jooq.exception.DataChangedException;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.BatchCRUD;
import org.jooq.impl.RecordDelegate;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UpdatableRecordImpl.class */
public class UpdatableRecordImpl<R extends UpdatableRecord<R>> extends TableRecordImpl<R> implements UpdatableRecord<R> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) UpdatableRecordImpl.class);
    private static final Set<SQLDialect> NO_SUPPORT_FOR_UPDATE = SQLDialect.supportedBy(SQLDialect.SQLITE);
    private static final Set<SQLDialect> NO_SUPPORT_MERGE_RETURNING = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.IGNITE);

    public UpdatableRecordImpl(Table<R> table) {
        super(table);
    }

    public Record key() {
        AbstractRecord result = (AbstractRecord) Tools.newRecord(this.fetched, AbstractRecord.class, Tools.row0(getPrimaryKey().getFieldsArray())).operate(null);
        result.setValues(result.fields.fields.fields, this);
        return result;
    }

    @Override // org.jooq.UpdatableRecord
    public final <O extends TableRecord<O>> O fetchChild(ForeignKey<O, R> key) {
        return (O) Tools.filterOne(fetchChildren(key));
    }

    @Override // org.jooq.UpdatableRecord
    public final <O extends TableRecord<O>> Result<O> fetchChildren(ForeignKey<O, R> key) {
        return key.fetchChildren((ForeignKey<O, R>) this);
    }

    @Override // org.jooq.UpdatableRecord
    public final <O extends TableRecord<O>> Table<O> children(ForeignKey<O, R> key) {
        return key.children((ForeignKey<O, R>) this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractRecord
    public final UniqueKey<R> getPrimaryKey() {
        GroupField table = getTable();
        if (table instanceof AbstractTable) {
            AbstractTable<R> t = (AbstractTable) table;
            return t.getPrimaryKeyWithEmbeddables();
        }
        return getTable().getPrimaryKey();
    }

    @Override // org.jooq.UpdatableRecord
    public final int store() {
        return store(this.fields.fields.fields);
    }

    @Override // org.jooq.UpdatableRecord
    public final int store(Field<?>... storeFields) {
        int[] result = new int[1];
        RecordDelegate.delegate(configuration(), this, RecordDelegate.RecordLifecycleType.STORE).operate(record -> {
            result[0] = store0(storeFields);
            return record;
        });
        return result[0];
    }

    @Override // org.jooq.UpdatableRecord
    public final int store(Collection<? extends Field<?>> storeFields) {
        return store((Field<?>[]) storeFields.toArray(Tools.EMPTY_FIELD));
    }

    @Override // org.jooq.UpdatableRecord
    public final int update() {
        return update(this.fields.fields.fields);
    }

    @Override // org.jooq.UpdatableRecord
    public int update(Field<?>... storeFields) {
        return storeUpdate(storeFields, getPrimaryKey().getFieldsArray());
    }

    @Override // org.jooq.UpdatableRecord
    public final int update(Collection<? extends Field<?>> storeFields) {
        return update((Field<?>[]) storeFields.toArray(Tools.EMPTY_FIELD));
    }

    @Override // org.jooq.UpdatableRecord
    public final int merge() {
        return merge(this.fields.fields.fields);
    }

    @Override // org.jooq.UpdatableRecord
    public int merge(Field<?>... storeFields) {
        return storeMerge(storeFields, getPrimaryKey().getFieldsArray());
    }

    @Override // org.jooq.UpdatableRecord
    public final int merge(Collection<? extends Field<?>> storeFields) {
        return merge((Field<?>[]) storeFields.toArray(Tools.EMPTY_FIELD));
    }

    private final int store0(Field<?>[] storeFields) {
        int result;
        TableField<R, ?>[] keys = getPrimaryKey().getFieldsArray();
        boolean executeUpdate = false;
        if (SettingsTools.updatablePrimaryKeys(Tools.settings(this))) {
            executeUpdate = this.fetched;
        } else {
            for (TableField<R, ?> field : keys) {
                if (changed(field) || (!field.getDataType().nullable() && get(field) == null)) {
                    executeUpdate = false;
                    break;
                }
                executeUpdate = true;
            }
        }
        if (executeUpdate) {
            result = storeUpdate(storeFields, keys);
        } else {
            result = storeInsert(storeFields);
        }
        return result;
    }

    private final int storeUpdate(Field<?>[] storeFields, TableField<R, ?>[] keys) {
        int[] result = new int[1];
        RecordDelegate.delegate(configuration(), this, RecordDelegate.RecordLifecycleType.UPDATE).operate(record -> {
            result[0] = storeUpdate0(storeFields, keys);
            return record;
        });
        return result[0];
    }

    private final int storeUpdate0(Field<?>[] storeFields, TableField<R, ?>[] keys) {
        return storeMergeOrUpdate0(storeFields, keys, create().updateQuery(getTable()), false);
    }

    private final int storeMerge(Field<?>[] storeFields, TableField<R, ?>[] keys) {
        int[] result = new int[1];
        RecordDelegate.delegate(configuration(), this, RecordDelegate.RecordLifecycleType.MERGE).operate(record -> {
            result[0] = storeMerge0(storeFields, keys);
            return record;
        });
        return Math.min(result[0], 1);
    }

    private final int storeMerge0(Field<?>[] storeFields, TableField<R, ?>[] keys) {
        if (lockingActive()) {
            if (lockValuePresent()) {
                return storeUpdate0(storeFields, keys);
            }
            return storeInsert0(storeFields);
        }
        InsertQuery<R> merge = create().insertQuery(getTable());
        merge.onDuplicateKeyUpdate(true);
        return storeMergeOrUpdate0(storeFields, keys, merge, true);
    }

    private final boolean lockingActive() {
        return isExecuteWithOptimisticLocking() && (isTimestampOrVersionAvailable() || isExecuteWithOptimisticLockingIncludeUnversioned());
    }

    private final boolean lockValuePresent() {
        return (getRecordVersion() == null && getRecordTimestamp() == null && (getTable().getRecordVersion() != null || getTable().getRecordTimestamp() != null || !this.fetched)) ? false : true;
    }

    /* JADX WARN: Incorrect types in method signature: <Q::Lorg/jooq/StoreQuery<TR;>;:Lorg/jooq/ConditionProvider;>([Lorg/jooq/Field<*>;[Lorg/jooq/TableField<TR;*>;TQ;Z)I */
    /* JADX WARN: Multi-variable type inference failed */
    private final int storeMergeOrUpdate0(Field[] fieldArr, TableField[] tableFieldArr, StoreQuery storeQuery, boolean merge) {
        Collection<Field<?>> returningIfNeeded;
        List<Field<?>> changedFields = addChangedValues(fieldArr, storeQuery, merge);
        if (!merge) {
            Tools.addConditions((ConditionProvider) storeQuery, this, tableFieldArr);
        }
        if (changedFields.isEmpty()) {
            switch ((UpdateUnchangedRecords) StringUtils.defaultIfNull(create().settings().getUpdateUnchangedRecords(), UpdateUnchangedRecords.NEVER)) {
                case NEVER:
                    if (log.isDebugEnabled()) {
                        log.debug("Query is not executable", storeQuery);
                        return 0;
                    }
                    return 0;
                case SET_PRIMARY_KEY_TO_ITSELF:
                    for (TableField tableField : tableFieldArr) {
                        storeQuery.addValue((Field) tableField, (Field) tableField);
                    }
                    break;
                case SET_NON_PRIMARY_KEY_TO_THEMSELVES:
                    for (Field field : fieldArr) {
                        if (!Arrays.asList(tableFieldArr).contains(field)) {
                            storeQuery.addValue(field, field);
                        }
                    }
                    break;
                case SET_NON_PRIMARY_KEY_TO_RECORD_VALUES:
                    for (Field field2 : fieldArr) {
                        if (!Arrays.asList(tableFieldArr).contains(field2)) {
                            changed((Field<?>) field2, true);
                        }
                    }
                    addChangedValues(fieldArr, storeQuery, merge);
                    break;
            }
        }
        BigInteger version = addRecordVersion(storeQuery, merge);
        Timestamp timestamp = addRecordTimestamp(storeQuery, merge);
        if (isExecuteWithOptimisticLocking()) {
            if (isTimestampOrVersionAvailable()) {
                addConditionForVersionAndTimestamp((ConditionProvider) storeQuery);
            } else if (isExecuteWithOptimisticLockingIncludeUnversioned()) {
                checkIfChanged(tableFieldArr);
            }
        }
        if (merge && NO_SUPPORT_MERGE_RETURNING.contains(create().dialect())) {
            returningIfNeeded = null;
        } else {
            returningIfNeeded = setReturningIfNeeded(storeQuery);
        }
        Collection<Field<?>> key = returningIfNeeded;
        try {
            int result = storeQuery.execute();
            checkIfChanged(result, version, timestamp);
            if (result > 0) {
                for (Field<?> changedField : changedFields) {
                    changed(changedField, false);
                }
                getReturningIfNeeded(storeQuery, key);
            }
            return result;
        } catch (BatchCRUD.QueryCollectorSignal e) {
            e.version = version;
            e.timestamp = timestamp;
            throw e;
        }
    }

    @Override // org.jooq.UpdatableRecord
    public final int delete() {
        int[] result = new int[1];
        RecordDelegate.delegate(configuration(), this, RecordDelegate.RecordLifecycleType.DELETE).operate(record -> {
            result[0] = delete0();
            return record;
        });
        return result[0];
    }

    private final int delete0() {
        TableField<R, ?>[] keys = getPrimaryKey().getFieldsArray();
        try {
            DeleteQuery<R> delete1 = create().deleteQuery(getTable());
            Tools.addConditions(delete1, this, keys);
            if (isExecuteWithOptimisticLocking()) {
                if (isTimestampOrVersionAvailable()) {
                    addConditionForVersionAndTimestamp(delete1);
                } else if (isExecuteWithOptimisticLockingIncludeUnversioned()) {
                    checkIfChanged(keys);
                }
            }
            int result = delete1.execute();
            checkIfChanged(result, null, null);
            changed(true);
            this.fetched = false;
            return result;
        } catch (Throwable th) {
            changed(true);
            this.fetched = false;
            throw th;
        }
    }

    @Override // org.jooq.UpdatableRecord
    public final void refresh() {
        refresh(this.fields.fields.fields);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdatableRecord
    public final void refresh(Field<?>... refreshFields) {
        SelectQuery<Record> select = create().selectQuery();
        select.addSelect(refreshFields);
        select.addFrom(getTable());
        Tools.addConditions(select, this, getPrimaryKey().getFieldsArray());
        if (select.execute() == 1) {
            AbstractRecord source = (AbstractRecord) select.getResult().get(0);
            RecordDelegate.delegate(configuration(), this, RecordDelegate.RecordLifecycleType.REFRESH).operate(record -> {
                setValues(refreshFields, source);
                return record;
            });
            return;
        }
        throw new NoDataFoundException("Exactly one row expected for refresh. Record does not exist in database.");
    }

    @Override // org.jooq.UpdatableRecord
    public final void refresh(Collection<? extends Field<?>> refreshFields) {
        refresh((Field<?>[]) refreshFields.toArray(Tools.EMPTY_FIELD));
    }

    @Override // org.jooq.UpdatableRecord
    public final R copy() {
        return (R) Tools.newRecord(false, getTable(), configuration()).operate(copy -> {
            List<TableField<R, ?>> key = getPrimaryKey().getFields();
            for (AggregateFunction aggregateFunction : this.fields.fields.fields) {
                if (!key.contains(aggregateFunction)) {
                    copy.set(aggregateFunction, get(aggregateFunction));
                }
            }
            return copy;
        });
    }

    private final boolean isExecuteWithOptimisticLocking() {
        Configuration configuration = configuration();
        return configuration != null && Boolean.TRUE.equals(configuration.settings().isExecuteWithOptimisticLocking());
    }

    private final boolean isExecuteWithOptimisticLockingIncludeUnversioned() {
        Configuration configuration = configuration();
        return configuration == null || !Boolean.TRUE.equals(configuration.settings().isExecuteWithOptimisticLockingExcludeUnversioned());
    }

    private final void addConditionForVersionAndTimestamp(ConditionProvider query) {
        TableField<R, ?> v = getTable().getRecordVersion();
        TableField<R, ?> t = getTable().getRecordTimestamp();
        if (v != null) {
            Tools.addCondition(query, this, v);
        }
        if (t != null) {
            Tools.addCondition(query, this, t);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void checkIfChanged(TableField<R, ?>[] keys) {
        SelectQuery<R> select = create().selectQuery(getTable());
        Tools.addConditions(select, this, keys);
        if (!NO_SUPPORT_FOR_UPDATE.contains(create().dialect())) {
            select.setForUpdate(true);
        }
        R record = select.fetchOne();
        if (record == null) {
            throw new DataChangedException("Database record no longer exists");
        }
        for (AggregateFunction aggregateFunction : this.fields.fields.fields) {
            Object thisObject = original(aggregateFunction);
            Object thatObject = record.original(aggregateFunction);
            if (!StringUtils.equals(thisObject, thatObject)) {
                if (thisObject == null && !this.fetched) {
                    throw new DataChangedException("Cannot detect whether unversioned record has been changed. Either make sure the record is fetched from the database, or use a version or timestamp column to version the record.");
                }
                throw new DataChangedException("Database record has been changed");
            }
        }
    }

    private final void checkIfChanged(int result, BigInteger version, Timestamp timestamp) {
        if (result > 0) {
            setRecordVersionAndTimestamp(version, timestamp);
        } else if (isExecuteWithOptimisticLocking()) {
            throw new DataChangedException("Database record has been changed or doesn't exist any longer");
        }
    }
}
