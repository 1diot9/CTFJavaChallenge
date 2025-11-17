package org.jooq.impl;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jooq.Attachable;
import org.jooq.CSVFormat;
import org.jooq.ChartFormat;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.EmbeddableRecord;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.JSONFormat;
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
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.TXTFormat;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.XMLFormat;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.InvalidResultException;
import org.jooq.exception.MappingException;
import org.jooq.tools.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractRecord.class */
public abstract class AbstractRecord extends AbstractStore implements Record {
    final AbstractRow<? extends AbstractRecord> fields;
    final Object[] values;
    final Object[] originals;
    final java.util.BitSet changed;
    boolean fetched;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public AbstractRecord(Collection<? extends Field<?>> fields) {
        this(Tools.row0((Field<?>[]) fields.toArray(Tools.EMPTY_FIELD)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public AbstractRecord(Field<?>... fields) {
        this(Tools.row0(fields));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public AbstractRecord(AbstractRow<?> abstractRow) {
        int size = abstractRow.size();
        this.fields = abstractRow;
        this.values = new Object[size];
        this.originals = new Object[size];
        this.changed = new java.util.BitSet(size);
    }

    @Override // org.jooq.impl.AbstractFormattable
    final List<Attachable> getAttachables() {
        List<Attachable> result = null;
        int size = size();
        for (int i = 0; i < size; i++) {
            Object obj = this.values[i];
            if (obj instanceof Attachable) {
                Attachable a = (Attachable) obj;
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(a);
            }
        }
        return result == null ? Collections.emptyList() : result;
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields() {
        return this.fields.fields();
    }

    @Override // org.jooq.Fields
    public final Stream<Field<?>> fieldStream() {
        return this.fields.fieldStream();
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(Field<T> field) {
        return this.fields.field(field);
    }

    @Override // org.jooq.Fields
    public final Field<?> field(String name) {
        return this.fields.field(name);
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(String name, Class<T> type) {
        return this.fields.field(name, type);
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(String name, DataType<T> dataType) {
        return this.fields.field(name, dataType);
    }

    @Override // org.jooq.Fields
    public final Field<?> field(Name name) {
        return this.fields.field(name);
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(Name name, Class<T> type) {
        return this.fields.field(name, type);
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(Name name, DataType<T> dataType) {
        return this.fields.field(name, dataType);
    }

    @Override // org.jooq.Fields
    public final Field<?> field(int index) {
        if (index < 0 || index >= this.fields.size()) {
            return null;
        }
        return this.fields.field(index);
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(int index, Class<T> type) {
        return this.fields.field(index, type);
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(int index, DataType<T> dataType) {
        return this.fields.field(index, dataType);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(Field<?>... f) {
        return this.fields.fields(f);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(String... fieldNames) {
        return this.fields.fields(fieldNames);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(Name... fieldNames) {
        return this.fields.fields(fieldNames);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(int... fieldIndexes) {
        return this.fields.fields(fieldIndexes);
    }

    @Override // org.jooq.Fields
    public final int indexOf(Field<?> field) {
        return this.fields.indexOf(field);
    }

    @Override // org.jooq.Fields
    public final int indexOf(String fieldName) {
        return this.fields.indexOf(fieldName);
    }

    @Override // org.jooq.Fields
    public final int indexOf(Name fieldName) {
        return this.fields.indexOf(fieldName);
    }

    @Override // org.jooq.Fields
    public final Class<?>[] types() {
        return this.fields.types();
    }

    @Override // org.jooq.Fields
    public final Class<?> type(int fieldIndex) {
        return this.fields.type(fieldIndex);
    }

    @Override // org.jooq.Fields
    public final Class<?> type(String fieldName) {
        return this.fields.type(fieldName);
    }

    @Override // org.jooq.Fields
    public final Class<?> type(Name fieldName) {
        return this.fields.type(fieldName);
    }

    @Override // org.jooq.Fields
    public final DataType<?>[] dataTypes() {
        return this.fields.dataTypes();
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(int fieldIndex) {
        return this.fields.dataType(fieldIndex);
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(String fieldName) {
        return this.fields.dataType(fieldName);
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(Name fieldName) {
        return this.fields.dataType(fieldName);
    }

    @Override // org.jooq.impl.AbstractStore, org.jooq.Record
    public final int size() {
        return this.fields.size();
    }

    @Override // org.jooq.Record
    public final <T> T get(Field<T> field) {
        int indexOf = this.fields.indexOf((Field<?>) field);
        if (indexOf >= 0) {
            return (T) get(indexOf);
        }
        if (Tools.nonReplacingEmbeddable(field)) {
            return (T) Tools.newRecord(this.fetched, ((EmbeddableTableField) field).recordType).operate(new TransferRecordState(Tools.embeddedFields((Field<?>) field)));
        }
        throw Tools.indexFail((Fields) this.fields, (Field<?>) field);
    }

    @Override // org.jooq.Record
    public final <U> U get(Field<?> field, Class<? extends U> cls) {
        Object obj = get(field);
        return (U) Tools.converterOrFail(this, obj, field.getType(), cls).from(obj, Tools.converterContext(this));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T, U> U get(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (U) ContextConverter.scoped(converter).from(get(field), Tools.converterContext(this));
    }

    @Override // org.jooq.impl.AbstractStore, org.jooq.Record
    public final Object get(int index) {
        return this.values[safeIndex(index)];
    }

    @Override // org.jooq.Record
    public final <U> U get(int i, Class<? extends U> cls) {
        Object obj = get(i);
        return (U) Tools.converterOrFail(this, obj, field(safeIndex(i)).getType(), cls).from(obj, Tools.converterContext(this));
    }

    @Override // org.jooq.Record
    public final <U> U get(int i, Converter<?, ? extends U> converter) {
        return (U) Convert.convert(get(i), converter);
    }

    @Override // org.jooq.Record
    public final Object get(String fieldName) {
        return get(Tools.indexOrFail(this.fields, fieldName));
    }

    @Override // org.jooq.Record
    public final <T> T get(String str, Class<? extends T> cls) {
        return (T) get(Tools.indexOrFail(this.fields, str), cls);
    }

    @Override // org.jooq.Record
    public final <U> U get(String str, Converter<?, ? extends U> converter) {
        return (U) Convert.convert(get(str), converter);
    }

    @Override // org.jooq.Record
    public final Object get(Name fieldName) {
        return get(Tools.indexOrFail(this.fields, fieldName));
    }

    @Override // org.jooq.Record
    public final <T> T get(Name name, Class<? extends T> cls) {
        return (T) get(Tools.indexOrFail(this.fields, name), cls);
    }

    @Override // org.jooq.Record
    public final <U> U get(Name name, Converter<?, ? extends U> converter) {
        return (U) Convert.convert(get(name), converter);
    }

    @Deprecated
    protected final void setValue(int index, Object value) {
        set(index, value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void set(int index, Object value) {
        set(index, field(index), value);
    }

    @Override // org.jooq.Record
    public final <T> void set(Field<T> field, T value) {
        int index = this.fields.indexOf((Field<?>) field);
        set((Field<int>) field, index, (int) value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <T> void set(Field<T> field, int index, T value) {
        Object[] objArr;
        if (index >= 0) {
            set(index, (Field<?>) field, (Object) value);
            return;
        }
        if (Tools.nonReplacingEmbeddable(field)) {
            Field<?>[] f = Tools.embeddedFields((Field<?>) field);
            if (value instanceof EmbeddableRecord) {
                EmbeddableRecord e = (EmbeddableRecord) value;
                objArr = e.intoArray();
            } else {
                objArr = new Object[f.length];
            }
            Object[] v = objArr;
            for (int i = 0; i < f.length; i++) {
                set(Tools.indexOrFail(this.fields, f[i]), f[i], v[i]);
            }
            return;
        }
        throw Tools.indexFail((Fields) this.fields, (Field<?>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void set(int index, Field<?> field, Object value) {
        UniqueKey<?> key = getPrimaryKey();
        if (key == null || !key.getFields().contains(field)) {
            this.changed.set(index);
        } else if (this.changed.get(index)) {
            this.changed.set(index);
        } else if (SettingsTools.updatablePrimaryKeys(Tools.settings(this))) {
            this.changed.set(index);
        } else if (this.originals[index] == null) {
            this.changed.set(index);
        } else {
            this.changed.set(index, this.changed.get(index) || !StringUtils.equals(this.values[index], value));
            if (this.changed.get(index)) {
                changed(true);
            }
        }
        this.values[index] = value;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T, U> void set(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        set((Field<Field<T>>) field, (Field<T>) ContextConverter.scoped(converter).to(value, Tools.converterContext(this)));
    }

    public <T> Record with(Field<T> field, T value) {
        set((Field<Field<T>>) field, (Field<T>) value);
        return this;
    }

    public <T, U> Record with(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        set((Field) field, (Field<T>) value, (Converter<? extends T, ? super Field<T>>) converter);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setValues(Field<?>[] fields, AbstractRecord record) {
        this.fetched = record.fetched;
        for (Field<?> field : fields) {
            int targetIndex = Tools.indexOrFail(this.fields, field);
            int sourceIndex = Tools.indexOrFail(record.fields, field);
            this.values[targetIndex] = record.get(sourceIndex);
            this.originals[targetIndex] = record.original(sourceIndex);
            this.changed.set(targetIndex, record.changed(sourceIndex));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void intern0(int fieldIndex) {
        safeIndex(fieldIndex);
        if (field(fieldIndex).getType() == String.class) {
            this.values[fieldIndex] = intern((String) this.values[fieldIndex]);
            this.originals[fieldIndex] = intern((String) this.originals[fieldIndex]);
        }
    }

    final int safeIndex(int index) {
        if (index >= 0 && index < this.values.length) {
            return index;
        }
        throw new IllegalArgumentException("No field at index " + index + " in Record type " + String.valueOf(this.fields));
    }

    final String intern(String string) {
        if (string == null) {
            return null;
        }
        return string.intern();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UniqueKey<?> getPrimaryKey() {
        return null;
    }

    @Override // org.jooq.Record
    public Record original() {
        return Tools.newRecord(this.fetched, getClass(), this.fields, configuration()).operate(record -> {
            for (int i = 0; i < this.originals.length; i++) {
                Object obj = this.originals[i];
                record.originals[i] = obj;
                record.values[i] = obj;
            }
            return record;
        });
    }

    @Override // org.jooq.Record
    public final <T> T original(Field<T> field) {
        return (T) original(Tools.indexOrFail((Fields) this.fields, (Field<?>) field));
    }

    @Override // org.jooq.Record
    public final Object original(int fieldIndex) {
        return this.originals[safeIndex(fieldIndex)];
    }

    @Override // org.jooq.Record
    public final Object original(String fieldName) {
        return original(Tools.indexOrFail(this.fields, fieldName));
    }

    @Override // org.jooq.Record
    public final Object original(Name fieldName) {
        return original(Tools.indexOrFail(this.fields, fieldName));
    }

    @Override // org.jooq.Record
    public final boolean changed() {
        return !this.changed.isEmpty();
    }

    @Override // org.jooq.Record
    public final boolean changed(Field<?> field) {
        return changed(Tools.indexOrFail(this.fields, field));
    }

    @Override // org.jooq.Record
    public final boolean changed(int fieldIndex) {
        return this.changed.get(safeIndex(fieldIndex));
    }

    @Override // org.jooq.Record
    public final boolean changed(String fieldName) {
        return changed(Tools.indexOrFail(this.fields, fieldName));
    }

    @Override // org.jooq.Record
    public final boolean changed(Name fieldName) {
        return changed(Tools.indexOrFail(this.fields, fieldName));
    }

    @Override // org.jooq.Record
    public final void changed(boolean c) {
        this.changed.set(0, this.values.length, c);
        if (!c) {
            System.arraycopy(this.values, 0, this.originals, 0, this.values.length);
        }
    }

    @Override // org.jooq.Record
    public final void changed(Field<?> field, boolean c) {
        changed(Tools.indexOrFail(this.fields, field), c);
    }

    @Override // org.jooq.Record
    public final void changed(int fieldIndex, boolean c) {
        safeIndex(fieldIndex);
        this.changed.set(fieldIndex, c);
        if (!c) {
            this.originals[fieldIndex] = this.values[fieldIndex];
        }
    }

    @Override // org.jooq.Record
    public final void changed(String fieldName, boolean c) {
        changed(Tools.indexOrFail(this.fields, fieldName), c);
    }

    @Override // org.jooq.Record
    public final void changed(Name fieldName, boolean c) {
        changed(Tools.indexOrFail(this.fields, fieldName), c);
    }

    @Override // org.jooq.Record
    public final void reset() {
        this.changed.clear();
        System.arraycopy(this.originals, 0, this.values, 0, this.originals.length);
    }

    @Override // org.jooq.Record
    public final void reset(Field<?> field) {
        reset(Tools.indexOrFail(this.fields, field));
    }

    @Override // org.jooq.Record
    public final void reset(int fieldIndex) {
        safeIndex(fieldIndex);
        this.changed.clear(fieldIndex);
        this.values[fieldIndex] = this.originals[fieldIndex];
    }

    @Override // org.jooq.Record
    public final void reset(String fieldName) {
        reset(Tools.indexOrFail(this.fields, fieldName));
    }

    @Override // org.jooq.Record
    public final void reset(Name fieldName) {
        reset(Tools.indexOrFail(this.fields, fieldName));
    }

    @Override // org.jooq.Record
    public final Object[] intoArray() {
        return (Object[]) into(Object[].class);
    }

    @Override // org.jooq.Record
    public final List<Object> intoList() {
        return Arrays.asList(intoArray());
    }

    @Override // org.jooq.Record
    public final Stream<Object> intoStream() {
        return (Stream) into(Stream.class);
    }

    @Override // org.jooq.Record
    public final Map<String, Object> intoMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        int size = this.fields.size();
        for (int i = 0; i < size; i++) {
            Field<?> field = this.fields.field(i);
            if (map.put(field.getName(), get(i)) != null) {
                throw new InvalidResultException("Field " + field.getName() + " is not unique in Record : " + String.valueOf(this));
            }
        }
        return map;
    }

    @Override // org.jooq.Record
    public final Record into(Field<?>... f) {
        return Tools.newRecord(this.fetched, Record.class, Tools.row0(f), configuration()).operate(new TransferRecordState(f));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1> Record1<T1> into(Field<T1> field1) {
        return (Record1) into((Field<?>[]) new Field[]{field1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2> Record2<T1, T2> into(Field<T1> field1, Field<T2> field2) {
        return (Record2) into((Field<?>[]) new Field[]{field1, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3> Record3<T1, T2, T3> into(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return (Record3) into((Field<?>[]) new Field[]{field1, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4> Record4<T1, T2, T3, T4> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return (Record4) into((Field<?>[]) new Field[]{field1, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5> Record5<T1, T2, T3, T4, T5> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return (Record5) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6> Record6<T1, T2, T3, T4, T5, T6> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return (Record6) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7> Record7<T1, T2, T3, T4, T5, T6, T7> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return (Record7) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8> Record8<T1, T2, T3, T4, T5, T6, T7, T8> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return (Record8) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return (Record9) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return (Record10) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return (Record11) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return (Record12) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return (Record13) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return (Record14) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return (Record15) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return (Record16) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return (Record17) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return (Record18) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return (Record19) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return (Record20) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return (Record21) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Record
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> into(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return (Record22) into((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.Record
    public final <E> E into(Class<? extends E> type) {
        return this.fields.fields.mapper(Tools.configuration(this), type).map(this);
    }

    @Override // org.jooq.Record
    public <E> E into(E e) {
        if (e == null) {
            throw new NullPointerException("Cannot copy Record into null");
        }
        Class<?> cls = e.getClass();
        try {
            return (E) new DefaultRecordMapper(this.fields.fields, cls, e, configuration()).map(this);
        } catch (MappingException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new MappingException("An error occurred when mapping record to " + String.valueOf(cls), e3);
        }
    }

    @Override // org.jooq.Record
    public final <R extends Record> R into(Table<R> table) {
        return (R) Tools.newRecord(this.fetched, table, configuration()).operate(new TransferRecordState(table.fields()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <R extends Record> R intoRecord(R r) {
        return (R) Tools.newRecord(this.fetched, () -> {
            return r;
        }, configuration()).operate(new TransferRecordState(null));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <R extends Record> R intoRecord(Class<R> cls) {
        return (R) Tools.newRecord(this.fetched, cls, this.fields, configuration()).operate(new TransferRecordState(null));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractRecord$TransferRecordState.class */
    public class TransferRecordState<R extends Record> implements ThrowingFunction<R, R, MappingException> {
        private final Field<?>[] targetFields;

        TransferRecordState(Field<?>[] targetFields) {
            this.targetFields = targetFields;
        }

        @Override // org.jooq.impl.ThrowingFunction
        public R apply(R target) throws MappingException {
            AbstractRecord source = AbstractRecord.this;
            try {
                if (target instanceof AbstractRecord) {
                    AbstractRecord t = (AbstractRecord) target;
                    int targetIndex = 0;
                    while (true) {
                        if (targetIndex >= (this.targetFields != null ? this.targetFields.length : t.size())) {
                            break;
                        }
                        Field<?> targetField = this.targetFields != null ? this.targetFields[targetIndex] : t.field(targetIndex);
                        int sourceIndex = AbstractRecord.this.fields.indexOf(targetField);
                        if (sourceIndex >= 0) {
                            DataType<?> targetType = targetField.getDataType();
                            t.values[targetIndex] = targetType.convert(AbstractRecord.this.values[sourceIndex]);
                            t.originals[targetIndex] = targetType.convert(AbstractRecord.this.originals[sourceIndex]);
                            t.changed.set(targetIndex, AbstractRecord.this.changed.get(sourceIndex));
                        }
                        targetIndex++;
                    }
                } else {
                    for (Field<?> targetField2 : target.fields()) {
                        Field<?> sourceField = AbstractRecord.this.field(targetField2);
                        if (sourceField != null) {
                            Tools.setValue(target, targetField2, source, sourceField);
                        }
                    }
                }
                return target;
            } catch (Exception e) {
                throw new MappingException("An error occurred when mapping record to " + String.valueOf(target), e);
            }
        }
    }

    @Override // org.jooq.Record
    public final ResultSet intoResultSet() {
        return asResult().intoResultSet();
    }

    @Override // org.jooq.Record
    public final <E> E map(RecordMapper<Record, E> mapper) {
        return mapper.map(this);
    }

    private final void from0(Object source, int[] targetIndexMapping) {
        if (source == null) {
            return;
        }
        from(Tools.configuration(this).recordUnmapperProvider().provide(source.getClass(), this.fields.fields).unmap(prepareArrayOrIterableForUnmap(source, targetIndexMapping)), targetIndexMapping);
        Tools.resetChangedOnNotNull(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetChangedOnNotNull() {
        Tools.resetChangedOnNotNull(this);
    }

    private final Object prepareArrayOrIterableForUnmap(Object source, int[] targetIndexMapping) {
        Iterable<?> iterable;
        if (targetIndexMapping == null) {
            return source;
        }
        boolean array = source instanceof Object[];
        if (array) {
            iterable = Arrays.asList((Object[]) source);
        } else if (source instanceof Iterable) {
            iterable = (Iterable) source;
        } else {
            iterable = null;
        }
        Iterable<?> iterable2 = iterable;
        if (iterable2 != null) {
            Object[] result = new Object[size()];
            Iterator<?> it = iterable2.iterator();
            for (int i = 0; it.hasNext() && i < targetIndexMapping.length; i++) {
                int index = targetIndexMapping[i];
                Object o = it.next();
                if (index >= 0 && index < result.length) {
                    result[index] = o;
                }
            }
            return array ? result : Arrays.asList(result);
        }
        return source;
    }

    @Override // org.jooq.Record
    public final void from(Object source) {
        from0(source, null);
    }

    @Override // org.jooq.Record
    public final void from(Object source, Field<?>... f) {
        from0(source, this.fields.fields.indexesOf(f));
    }

    @Override // org.jooq.Record
    public final void from(Object source, String... fieldNames) {
        from0(source, this.fields.fields.indexesOf(fieldNames));
    }

    @Override // org.jooq.Record
    public final void from(Object source, Name... fieldNames) {
        from0(source, this.fields.fields.indexesOf(fieldNames));
    }

    @Override // org.jooq.Record
    public final void from(Object source, int... fieldIndexes) {
        from0(source, fieldIndexes);
    }

    @Override // org.jooq.Record
    public final void fromMap(Map<String, ?> map) {
        from(map);
    }

    @Override // org.jooq.Record
    public final void fromMap(Map<String, ?> map, Field<?>... f) {
        from(map, f);
    }

    @Override // org.jooq.Record
    public final void fromMap(Map<String, ?> map, String... fieldNames) {
        from(map, fieldNames);
    }

    @Override // org.jooq.Record
    public final void fromMap(Map<String, ?> map, Name... fieldNames) {
        from(map, fieldNames);
    }

    @Override // org.jooq.Record
    public final void fromMap(Map<String, ?> map, int... fieldIndexes) {
        from(map, fieldIndexes);
    }

    @Override // org.jooq.Record
    public final void fromArray(Object... array) {
        from(array);
    }

    @Override // org.jooq.Record
    public final void fromArray(Object[] array, Field<?>... f) {
        from(array, f);
    }

    @Override // org.jooq.Record
    public final void fromArray(Object[] array, String... fieldNames) {
        from(array, fieldNames);
    }

    @Override // org.jooq.Record
    public final void fromArray(Object[] array, Name... fieldNames) {
        from(array, fieldNames);
    }

    @Override // org.jooq.Record
    public final void fromArray(Object[] array, int... fieldIndexes) {
        from(array, fieldIndexes);
    }

    protected final void from(Record source) {
        for (Field<?> field : this.fields.fields.fields) {
            Field<?> sourceField = source.field(field);
            if (sourceField != null && source.changed(sourceField)) {
                Tools.setValue(this, field, source, sourceField);
            }
        }
    }

    final void from(Record source, int[] indexMapping) {
        int s = source.size();
        int t = indexMapping == null ? s : indexMapping.length;
        for (int i = 0; i < s && i < t; i++) {
            int j = indexMapping == null ? i : indexMapping[i];
            if (source.field(j) != null && source.changed(j)) {
                set((Field<int>) field(j), j, (int) source.get(j));
            }
        }
    }

    @Override // org.jooq.Formattable
    public final void format(Writer writer, TXTFormat format) {
        asResult().format(writer, format);
    }

    @Override // org.jooq.Formattable
    public final void formatCSV(Writer writer, CSVFormat format) {
        asResult().formatCSV(writer, format);
    }

    @Override // org.jooq.Formattable
    public final void formatJSON(Writer writer, JSONFormat format) {
        JSONFormat format2 = format.mutable(true);
        try {
            switch (format2.recordFormat()) {
                case ARRAY:
                    AbstractResult.formatJSONArray0(this, this.fields, format2, 0, writer);
                    break;
                case OBJECT:
                    AbstractResult.formatJSONMap0(this, this.fields, format2, 0, writer);
                    break;
                default:
                    throw new IllegalArgumentException("Format not supported: " + String.valueOf(format2));
            }
            writer.flush();
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing JSON", e);
        }
    }

    @Override // org.jooq.Formattable
    public final void formatXML(Writer writer, XMLFormat format) {
        try {
            AbstractResult.formatXMLRecord(writer, format.mutable(true), 0, this, this.fields);
            writer.flush();
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing XML", e);
        }
    }

    @Override // org.jooq.Formattable
    public final void formatHTML(Writer writer) {
        Result<AbstractRecord> result = asResult();
        result.formatHTML(writer);
    }

    @Override // org.jooq.Formattable
    public final void formatChart(Writer writer, ChartFormat format) {
        Result<AbstractRecord> result = asResult();
        result.formatChart(writer, format);
    }

    @Override // org.jooq.Formattable
    public final void formatInsert(Writer writer) {
        formatInsert(writer, (Table<?>) null, this.fields.fields.fields);
    }

    @Override // org.jooq.Formattable
    public final void formatInsert(Writer writer, Table<?> table, Field<?>... f) {
        Result<AbstractRecord> result = asResult();
        result.formatInsert(writer, table, f);
    }

    @Override // org.jooq.Formattable
    public final Document intoXML(XMLFormat format) {
        Result<AbstractRecord> result = asResult();
        return result.intoXML(format);
    }

    @Override // org.jooq.Formattable
    public final <H extends ContentHandler> H intoXML(H h, XMLFormat xMLFormat) throws SAXException {
        return (H) asResult().intoXML(h, xMLFormat);
    }

    public String toString() {
        return (String) ThreadGuard.run(ThreadGuard.RECORD_TOSTRING, () -> {
            return asResult().toString();
        }, () -> {
            return valuesRow().toString();
        });
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Record, java.lang.Comparable
    public int compareTo(Record that) {
        if (that == this) {
            return 0;
        }
        if (that == null) {
            throw new NullPointerException();
        }
        if (size() != that.size()) {
            throw new ClassCastException(String.format("Trying to compare incomparable records (wrong degree):\n%s\n%s", this, that));
        }
        Class<?>[] thisTypes = fieldsRow().types();
        Class<?>[] thatTypes = that.fieldsRow().types();
        if (!Arrays.asList(thisTypes).equals(Arrays.asList(thatTypes))) {
            throw new ClassCastException(String.format("Trying to compare incomparable records (type mismatch):\n%s\n%s", this, that));
        }
        for (int i = 0; i < size(); i++) {
            Object thisValue = get(i);
            Object thatValue = that.get(i);
            if (thisValue != null || thatValue != null) {
                if (thisValue == null) {
                    return 1;
                }
                if (thatValue == null) {
                    return -1;
                }
                if (thisValue.getClass().isArray() && thatValue.getClass().isArray()) {
                    if (thisValue.getClass() == byte[].class) {
                        int compare = compare((byte[]) thisValue, (byte[]) thatValue);
                        if (compare != 0) {
                            return compare;
                        }
                    } else if (!thisValue.getClass().getComponentType().isPrimitive()) {
                        int compare2 = compare((Object[]) thisValue, (Object[]) thatValue);
                        if (compare2 != 0) {
                            return compare2;
                        }
                    } else {
                        throw new ClassCastException(String.format("Unsupported data type in natural ordering: %s", thisValue.getClass()));
                    }
                } else {
                    int compare3 = compare0(thisValue, thatValue);
                    if (compare3 != 0) {
                        return compare3;
                    }
                }
            }
        }
        return 0;
    }

    final int compare(byte[] array1, byte[] array2) {
        int length = Math.min(array1.length, array2.length);
        for (int i = 0; i < length; i++) {
            int v1 = array1[i] & 255;
            int v2 = array2[i] & 255;
            if (v1 != v2) {
                return v1 < v2 ? -1 : 1;
            }
        }
        return array1.length - array2.length;
    }

    final int compare(Object[] array1, Object[] array2) {
        int length = Math.min(array1.length, array2.length);
        for (int i = 0; i < length; i++) {
            int compare = compare0(array1[i], array2[i]);
            if (compare != 0) {
                return compare;
            }
        }
        return array1.length - array2.length;
    }

    final int compare0(Object object1, Object object2) {
        if (object1 == object2) {
            return 0;
        }
        if (object1 == null) {
            return -1;
        }
        if (object2 == null) {
            return 1;
        }
        return object1.hashCode() - object2.hashCode();
    }

    @Override // org.jooq.Record
    public final <T> T getValue(Field<T> field) {
        return (T) get(field);
    }

    @Override // org.jooq.Record
    public final <T> T getValue(Field<?> field, Class<? extends T> cls) {
        return (T) get(field, cls);
    }

    @Override // org.jooq.Record
    public final <T, U> U getValue(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (U) get(field, converter);
    }

    @Override // org.jooq.Record
    public final Object getValue(int index) {
        return get(index);
    }

    @Override // org.jooq.Record
    public final <T> T getValue(int i, Class<? extends T> cls) {
        return (T) get(i, cls);
    }

    @Override // org.jooq.Record
    public final <U> U getValue(int i, Converter<?, ? extends U> converter) {
        return (U) get(i, converter);
    }

    @Override // org.jooq.Record
    public final Object getValue(String fieldName) {
        return get(fieldName);
    }

    @Override // org.jooq.Record
    public final <T> T getValue(String str, Class<? extends T> cls) {
        return (T) get(str, cls);
    }

    @Override // org.jooq.Record
    public final <U> U getValue(String str, Converter<?, ? extends U> converter) {
        return (U) get(str, converter);
    }

    @Override // org.jooq.Record
    public final Object getValue(Name fieldName) {
        return get(fieldName);
    }

    @Override // org.jooq.Record
    public final <T> T getValue(Name name, Class<? extends T> cls) {
        return (T) get(name, cls);
    }

    @Override // org.jooq.Record
    public final <U> U getValue(Name name, Converter<?, ? extends U> converter) {
        return (U) get(name, converter);
    }

    @Override // org.jooq.Record
    public final <T> void setValue(Field<T> field, T value) {
        set((Field<Field<T>>) field, (Field<T>) value);
    }

    @Override // org.jooq.Record
    public final <T, U> void setValue(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        set((Field) field, (Field<T>) value, (Converter<? extends T, ? super Field<T>>) converter);
    }

    final Result<AbstractRecord> asResult() {
        Result<AbstractRecord> result = new ResultImpl<>(configuration(), this.fields);
        result.add(this);
        return result;
    }
}
