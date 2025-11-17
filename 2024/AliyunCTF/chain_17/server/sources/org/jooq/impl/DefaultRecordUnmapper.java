package org.jooq.impl;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordType;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordUnmapper.class */
public class DefaultRecordUnmapper<E, R extends Record> implements RecordUnmapper<E, R> {
    private final Class<? extends E> type;
    private final RecordType<R> rowType;
    private final AbstractRow<R> row;
    private final Class<? extends AbstractRecord> recordType;
    private final Field<?>[] fields;
    private final Configuration configuration;
    private RecordUnmapper<E, R> delegate;

    public DefaultRecordUnmapper(Class<? extends E> type, RecordType<R> rowType, Configuration configuration) {
        this.type = type;
        this.rowType = rowType;
        this.row = Tools.row0((FieldsImpl) rowType);
        this.recordType = Tools.recordType(rowType.size());
        this.fields = rowType.fields();
        this.configuration = configuration;
        init();
    }

    private final void init() {
        if (this.type.isArray()) {
            this.delegate = new ArrayUnmapper();
            return;
        }
        if (Map.class.isAssignableFrom(this.type)) {
            this.delegate = new MapUnmapper();
        } else if (Iterable.class.isAssignableFrom(this.type)) {
            this.delegate = new IterableUnmapper();
        } else {
            this.delegate = new PojoUnmapper();
        }
    }

    @Override // org.jooq.RecordUnmapper
    public final R unmap(E source) {
        return this.delegate.unmap(source);
    }

    private final Record newRecord() {
        return Tools.newRecord(false, this.recordType, this.row, this.configuration).operate(null);
    }

    private static final void setValue(Record record, Object source, java.lang.reflect.Field member, Field<?> field) throws IllegalAccessException {
        Class<?> mType = member.getType();
        if (mType.isPrimitive()) {
            if (mType == Byte.TYPE) {
                Tools.setValue(record, field, Byte.valueOf(member.getByte(source)));
                return;
            }
            if (mType == Short.TYPE) {
                Tools.setValue(record, field, Short.valueOf(member.getShort(source)));
                return;
            }
            if (mType == Integer.TYPE) {
                Tools.setValue(record, field, Integer.valueOf(member.getInt(source)));
                return;
            }
            if (mType == Long.TYPE) {
                Tools.setValue(record, field, Long.valueOf(member.getLong(source)));
                return;
            }
            if (mType == Float.TYPE) {
                Tools.setValue(record, field, Float.valueOf(member.getFloat(source)));
                return;
            }
            if (mType == Double.TYPE) {
                Tools.setValue(record, field, Double.valueOf(member.getDouble(source)));
                return;
            } else if (mType == Boolean.TYPE) {
                Tools.setValue(record, field, Boolean.valueOf(member.getBoolean(source)));
                return;
            } else {
                if (mType == Character.TYPE) {
                    Tools.setValue(record, field, Character.valueOf(member.getChar(source)));
                    return;
                }
                return;
            }
        }
        Tools.setValue(record, field, member.get(source));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordUnmapper$ArrayUnmapper.class */
    public final class ArrayUnmapper implements RecordUnmapper<E, R> {
        private ArrayUnmapper() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.jooq.RecordUnmapper
        public final R unmap(E e) {
            if (e instanceof Object[]) {
                Object[] array = (Object[]) e;
                int size = DefaultRecordUnmapper.this.rowType.size();
                AbstractRecord record = (AbstractRecord) DefaultRecordUnmapper.this.newRecord();
                for (int i = 0; i < size && i < array.length; i++) {
                    Tools.setValue(record, DefaultRecordUnmapper.this.rowType.field(i), i, array[i]);
                }
                return record;
            }
            throw new MappingException("Object[] expected. Got: " + DefaultRecordUnmapper.klass(e));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordUnmapper$IterableUnmapper.class */
    public final class IterableUnmapper implements RecordUnmapper<E, R> {
        private IterableUnmapper() {
        }

        @Override // org.jooq.RecordUnmapper
        public final R unmap(E source) {
            if (source instanceof Iterable) {
                Iterable<?> iterable = (Iterable) source;
                Iterator<?> it = iterable.iterator();
                int size = DefaultRecordUnmapper.this.rowType.size();
                AbstractRecord record = (AbstractRecord) DefaultRecordUnmapper.this.newRecord();
                for (int i = 0; i < size && it.hasNext(); i++) {
                    Tools.setValue(record, DefaultRecordUnmapper.this.rowType.field(i), i, it.next());
                }
                return record;
            }
            throw new MappingException("Iterable expected. Got: " + DefaultRecordUnmapper.klass(source));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordUnmapper$MapUnmapper.class */
    public final class MapUnmapper implements RecordUnmapper<E, R> {
        private MapUnmapper() {
        }

        @Override // org.jooq.RecordUnmapper
        public R unmap(E e) {
            if (e instanceof Map) {
                Map map = (Map) e;
                R r = (R) DefaultRecordUnmapper.this.newRecord();
                for (int i = 0; i < DefaultRecordUnmapper.this.fields.length; i++) {
                    String name = DefaultRecordUnmapper.this.fields[i].getName();
                    if (map.containsKey(name)) {
                        Tools.setValue(r, DefaultRecordUnmapper.this.fields[i], map.get(name));
                    }
                }
                return r;
            }
            throw new MappingException("Map expected. Got: " + DefaultRecordUnmapper.klass(e));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordUnmapper$PojoUnmapper.class */
    public final class PojoUnmapper implements RecordUnmapper<E, R> {
        private PojoUnmapper() {
        }

        @Override // org.jooq.RecordUnmapper
        public R unmap(E e) {
            List<java.lang.reflect.Field> matchingMembers;
            Method matchingGetter;
            R r = (R) DefaultRecordUnmapper.this.newRecord();
            try {
                boolean hasColumnAnnotations = Tools.hasColumnAnnotations(DefaultRecordUnmapper.this.configuration, DefaultRecordUnmapper.this.type);
                for (Field<?> field : DefaultRecordUnmapper.this.fields) {
                    if (hasColumnAnnotations) {
                        matchingMembers = Tools.getAnnotatedMembers(DefaultRecordUnmapper.this.configuration, DefaultRecordUnmapper.this.type, field.getName(), true);
                        matchingGetter = Tools.getAnnotatedGetter(DefaultRecordUnmapper.this.configuration, DefaultRecordUnmapper.this.type, field.getName(), true);
                    } else {
                        matchingMembers = Tools.getMatchingMembers(DefaultRecordUnmapper.this.configuration, DefaultRecordUnmapper.this.type, field.getName(), true);
                        matchingGetter = Tools.getMatchingGetter(DefaultRecordUnmapper.this.configuration, DefaultRecordUnmapper.this.type, field.getName(), true);
                    }
                    if (matchingGetter != null) {
                        Tools.setValue(r, field, matchingGetter.invoke(e, new Object[0]));
                    } else if (matchingMembers.size() > 0) {
                        DefaultRecordUnmapper.setValue(r, e, matchingMembers.get(0), field);
                    }
                }
                return r;
            } catch (Exception e2) {
                throw new MappingException("An error occurred when mapping record from " + String.valueOf(DefaultRecordUnmapper.this.type), e2);
            }
        }
    }

    private static final String klass(Object o) {
        return o == null ? "null" : o.getClass().toString();
    }
}
