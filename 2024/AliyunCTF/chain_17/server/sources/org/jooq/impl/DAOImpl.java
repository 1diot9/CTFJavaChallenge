package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DAO;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordContext;
import org.jooq.RecordListener;
import org.jooq.RecordListenerProvider;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.SelectWhereStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.UpdatableRecord;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DAOImpl.class */
public abstract class DAOImpl<R extends UpdatableRecord<R>, P, T> implements DAO<R, P, T> {
    private final Table<R> table;
    private final Class<P> type;
    private RecordMapper<R, P> mapper;
    private Configuration configuration;

    protected DAOImpl(Table<R> table, Class<P> type) {
        this(table, type, null);
    }

    protected DAOImpl(Table<R> table, Class<P> type, Configuration configuration) {
        this.table = table;
        this.type = type;
        setConfiguration(configuration);
    }

    public void setConfiguration(Configuration configuration) {
        FieldsImpl fieldsImpl = (FieldsImpl) this.table.recordType();
        Configuration configuration2 = Tools.configuration(configuration);
        this.configuration = configuration2;
        this.mapper = fieldsImpl.mapper(configuration2, this.type);
    }

    public DSLContext ctx() {
        return configuration().dsl();
    }

    @Override // org.jooq.DAO
    public Configuration configuration() {
        return this.configuration;
    }

    @Override // org.jooq.DAO
    public Settings settings() {
        return Tools.settings(configuration());
    }

    @Override // org.jooq.DAO
    public SQLDialect dialect() {
        return Tools.configuration(configuration()).dialect();
    }

    @Override // org.jooq.DAO
    public SQLDialect family() {
        return dialect().family();
    }

    @Override // org.jooq.DAO
    public RecordMapper<R, P> mapper() {
        return this.mapper;
    }

    @Override // org.jooq.DAO
    public void insert(P object) {
        insert((Collection) Collections.singletonList(object));
    }

    @Override // org.jooq.DAO
    public void insert(P... objects) {
        insert((Collection) Arrays.asList(objects));
    }

    @Override // org.jooq.DAO
    public void insert(Collection<P> objects) {
        if (objects.size() > 1) {
            if (!Boolean.FALSE.equals(settings().isReturnRecordToPojo())) {
                for (R record : records(objects, false)) {
                    record.insert();
                }
                return;
            }
            ctx().batchInsert(records(objects, false)).execute();
            return;
        }
        if (objects.size() == 1) {
            records(objects, false).get(0).insert();
        }
    }

    @Override // org.jooq.DAO
    public void update(P object) {
        update((Collection) Collections.singletonList(object));
    }

    @Override // org.jooq.DAO
    public void update(P... objects) {
        update((Collection) Arrays.asList(objects));
    }

    @Override // org.jooq.DAO
    public void update(Collection<P> objects) {
        if (objects.size() > 1) {
            if (returnAnyOnUpdatableRecord()) {
                for (R record : records(objects, true)) {
                    record.update();
                }
                return;
            }
            ctx().batchUpdate(records(objects, true)).execute();
            return;
        }
        if (objects.size() == 1) {
            records(objects, true).get(0).update();
        }
    }

    @Override // org.jooq.DAO
    public void merge(P object) {
        merge((Collection) Collections.singletonList(object));
    }

    @Override // org.jooq.DAO
    public void merge(P... objects) {
        merge((Collection) Arrays.asList(objects));
    }

    @Override // org.jooq.DAO
    public void merge(Collection<P> objects) {
        if (objects.size() > 1) {
            if (returnAnyOnUpdatableRecord()) {
                for (R record : records(objects, false)) {
                    record.merge();
                }
                return;
            }
            ctx().batchMerge(records(objects, false)).execute();
            return;
        }
        if (objects.size() == 1) {
            records(objects, false).get(0).merge();
        }
    }

    @Override // org.jooq.DAO
    public void delete(P object) {
        delete((Collection) Collections.singletonList(object));
    }

    @Override // org.jooq.DAO
    public void delete(P... objects) {
        delete((Collection) Arrays.asList(objects));
    }

    @Override // org.jooq.DAO
    public void delete(Collection<P> objects) {
        if (objects.size() > 1) {
            if (returnAnyOnUpdatableRecord()) {
                for (R record : records(objects, true)) {
                    record.delete();
                }
                return;
            }
            ctx().batchDelete(records(objects, true)).execute();
            return;
        }
        if (objects.size() == 1) {
            records(objects, true).get(0).delete();
        }
    }

    @Override // org.jooq.DAO
    public void deleteById(T ids) {
        deleteById((Collection) Collections.singletonList(ids));
    }

    @Override // org.jooq.DAO
    public void deleteById(T... ids) {
        deleteById((Collection) Arrays.asList(ids));
    }

    @Override // org.jooq.DAO
    public void deleteById(Collection<T> ids) {
        Field<?>[] pk = pk();
        if (pk != null) {
            ctx().delete(this.table).where(equal(pk, (Collection) ids)).execute();
        }
    }

    @Override // org.jooq.DAO
    public boolean exists(P object) {
        return existsById(getId(object));
    }

    @Override // org.jooq.DAO
    public boolean existsById(T id) {
        Field<?>[] pk = pk();
        return pk != null && ((Integer) ctx().selectCount().from(this.table).where(equal(pk, (Field<?>[]) id)).fetchOne(0, Integer.class)).intValue() > 0;
    }

    @Override // org.jooq.DAO
    public long count() {
        return ((Long) ctx().selectCount().from(this.table).fetchOne(0, Long.class)).longValue();
    }

    @Override // org.jooq.DAO
    public List<P> findAll() {
        return (List<P>) ctx().selectFrom(this.table).fetch(mapper());
    }

    @Override // org.jooq.DAO
    public P findById(T t) {
        Field<?>[] pk = pk();
        if (pk != null) {
            return (P) ctx().selectFrom(this.table).where(equal(pk, (Field<?>[]) t)).fetchOne(mapper());
        }
        return null;
    }

    @Override // org.jooq.DAO
    public Optional<P> findOptionalById(T id) {
        return Optional.ofNullable(findById(id));
    }

    @Override // org.jooq.DAO
    public <Z> List<P> fetchRange(Field<Z> field, Z z, Z z2) {
        Condition between;
        SelectWhereStep selectFrom = ctx().selectFrom(this.table);
        if (z == null) {
            if (z2 == null) {
                between = DSL.noCondition();
            } else {
                between = field.le((Field<Z>) z2);
            }
        } else if (z2 == null) {
            between = field.ge((Field<Z>) z);
        } else {
            between = field.between(z, z2);
        }
        return (List<P>) selectFrom.where(between).fetch(mapper());
    }

    @Override // org.jooq.DAO
    public <Z> List<P> fetch(Field<Z> field, Z... values) {
        return fetch(field, Arrays.asList(values));
    }

    @Override // org.jooq.DAO
    public <Z> List<P> fetch(Field<Z> field, Collection<? extends Z> collection) {
        return (List<P>) ctx().selectFrom(this.table).where(field.in(collection)).fetch(mapper());
    }

    @Override // org.jooq.DAO
    public <Z> P fetchOne(Field<Z> field, Z z) {
        return (P) ctx().selectFrom(this.table).where(field.equal((Field<Z>) z)).fetchOne(mapper());
    }

    @Override // org.jooq.DAO
    public <Z> Optional<P> fetchOptional(Field<Z> field, Z value) {
        return Optional.ofNullable(fetchOne(field, value));
    }

    @Override // org.jooq.DAO
    public Table<R> getTable() {
        return this.table;
    }

    @Override // org.jooq.DAO
    public Class<P> getType() {
        return this.type;
    }

    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Record, T] */
    protected T compositeKeyRecord(Object... objArr) {
        UniqueKey<R> primaryKey = this.table.getPrimaryKey();
        if (primaryKey == null) {
            return null;
        }
        TableField<R, ?>[] fieldsArray = primaryKey.getFieldsArray();
        ?? r0 = (T) configuration().dsl().newRecord(fieldsArray);
        for (int i = 0; i < objArr.length; i++) {
            r0.set(fieldsArray[i], fieldsArray[i].getDataType().convert(objArr[i]));
        }
        return r0;
    }

    private Condition equal(Field<?>[] pk, T id) {
        if (pk.length == 1) {
            return pk[0].equal((Field<?>) pk[0].getDataType().convert(id));
        }
        return DSL.row((SelectField<?>[]) pk).equal((Record) id);
    }

    private Condition equal(Field<?>[] pk, Collection<T> ids) {
        if (pk.length == 1) {
            if (ids.size() == 1) {
                return equal(pk, (Field<?>[]) ids.iterator().next());
            }
            return pk[0].in(pk[0].getDataType().convert((Collection<?>) ids));
        }
        return DSL.row((SelectField<?>[]) pk).in((Record[]) ids.toArray(Tools.EMPTY_RECORD));
    }

    private Field<?>[] pk() {
        UniqueKey<R> primaryKey = this.table.getPrimaryKey();
        if (primaryKey == null) {
            return null;
        }
        return primaryKey.getFieldsArray();
    }

    private List<R> records(Collection<P> objects, boolean forUpdate) {
        DSLContext ctx;
        ArrayList arrayList = new ArrayList(objects.size());
        Field<?>[] pk = pk();
        IdentityHashMap identityHashMap = !Boolean.FALSE.equals(settings().isReturnRecordToPojo()) ? new IdentityHashMap() : null;
        if (identityHashMap != null) {
            Consumer<? super RecordContext> end = c -> {
                Record record = c.record();
                if (record != null) {
                    record.into((Record) identityHashMap.get(record));
                }
            };
            ctx = configuration().deriveAppending(RecordListener.onStoreEnd(end).onInsertEnd(end).onUpdateEnd(end).onDeleteEnd(end)).dsl();
        } else {
            ctx = ctx();
        }
        for (P object : objects) {
            UpdatableRecord updatableRecord = (UpdatableRecord) ctx.newRecord(this.table, object);
            if (identityHashMap != null) {
                identityHashMap.put(updatableRecord, object);
            }
            if (forUpdate && pk != null) {
                for (Field<?> field : pk) {
                    updatableRecord.changed(field, false);
                }
            }
            Tools.resetChangedOnNotNull(updatableRecord);
            arrayList.add(updatableRecord);
        }
        return arrayList;
    }

    private RecordListenerProvider[] providers(RecordListenerProvider[] providers, IdentityHashMap<R, Object> mapping) {
        RecordListenerProvider[] result = (RecordListenerProvider[]) Arrays.copyOf(providers, providers.length + 1);
        Consumer<? super RecordContext> end = ctx -> {
            Record record = ctx.record();
            if (record != null) {
                record.into((Record) mapping.get(record));
            }
        };
        result[providers.length] = new DefaultRecordListenerProvider(RecordListener.onStoreEnd(end).onInsertEnd(end).onUpdateEnd(end).onDeleteEnd(end));
        return result;
    }

    private final boolean returnAnyOnUpdatableRecord() {
        return !Boolean.FALSE.equals(settings().isReturnRecordToPojo()) && SettingsTools.returnAnyOnUpdatableRecord(settings());
    }
}
