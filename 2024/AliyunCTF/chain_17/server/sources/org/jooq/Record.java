package org.jooq;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.exception.DataTypeException;
import org.jooq.exception.MappingException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Record.class */
public interface Record extends Fields, Attachable, Comparable<Record>, Formattable {
    @NotNull
    Row valuesRow();

    <T> T get(Field<T> field) throws IllegalArgumentException;

    <U> U get(Field<?> field, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    <T, U> U get(Field<T> field, Converter<? super T, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @Nullable
    Object get(String str) throws IllegalArgumentException;

    <U> U get(String str, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    <U> U get(String str, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @Nullable
    Object get(Name name) throws IllegalArgumentException;

    <U> U get(Name name, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    <U> U get(Name name, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @Nullable
    Object get(int i) throws IllegalArgumentException;

    <U> U get(int i, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    <U> U get(int i, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    <T> void set(Field<T> field, T t);

    <T, U> void set(Field<T> field, U u, Converter<? extends T, ? super U> converter);

    @NotNull
    <T> Record with(Field<T> field, T t);

    @NotNull
    <T, U> Record with(Field<T> field, U u, Converter<? extends T, ? super U> converter);

    int size();

    @NotNull
    Record original();

    <T> T original(Field<T> field);

    @Nullable
    Object original(int i);

    @Nullable
    Object original(String str);

    @Nullable
    Object original(Name name);

    boolean changed();

    boolean changed(Field<?> field);

    boolean changed(int i);

    boolean changed(String str);

    boolean changed(Name name);

    void changed(boolean z);

    void changed(Field<?> field, boolean z);

    void changed(int i, boolean z);

    void changed(String str, boolean z);

    void changed(Name name, boolean z);

    void reset();

    void reset(Field<?> field);

    void reset(int i);

    void reset(String str);

    void reset(Name name);

    @Nullable
    Object[] intoArray();

    @NotNull
    List<Object> intoList();

    @NotNull
    Stream<Object> intoStream();

    @NotNull
    Map<String, Object> intoMap();

    @NotNull
    Record into(Field<?>... fieldArr);

    @NotNull
    <T1> Record1<T1> into(Field<T1> field);

    @NotNull
    <T1, T2> Record2<T1, T2> into(Field<T1> field, Field<T2> field2);

    @NotNull
    <T1, T2, T3> Record3<T1, T2, T3> into(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @NotNull
    <T1, T2, T3, T4> Record4<T1, T2, T3, T4> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @NotNull
    <T1, T2, T3, T4, T5> Record5<T1, T2, T3, T4, T5> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @NotNull
    <T1, T2, T3, T4, T5, T6> Record6<T1, T2, T3, T4, T5, T6> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> Record7<T1, T2, T3, T4, T5, T6, T7> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> Record8<T1, T2, T3, T4, T5, T6, T7, T8> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    <E> E into(Class<? extends E> cls) throws MappingException;

    @NotNull
    <E> E into(E e) throws MappingException;

    @NotNull
    <R extends Record> R into(Table<R> table);

    @NotNull
    ResultSet intoResultSet();

    @NotNull
    <E> E map(RecordMapper<Record, E> recordMapper);

    void from(Object obj) throws MappingException;

    void from(Object obj, Field<?>... fieldArr) throws MappingException;

    void from(Object obj, String... strArr) throws MappingException;

    void from(Object obj, Name... nameArr) throws MappingException;

    void from(Object obj, int... iArr) throws MappingException;

    void fromMap(Map<String, ?> map);

    void fromMap(Map<String, ?> map, Field<?>... fieldArr);

    void fromMap(Map<String, ?> map, String... strArr);

    void fromMap(Map<String, ?> map, Name... nameArr);

    void fromMap(Map<String, ?> map, int... iArr);

    void fromArray(Object... objArr);

    void fromArray(Object[] objArr, Field<?>... fieldArr);

    void fromArray(Object[] objArr, String... strArr);

    void fromArray(Object[] objArr, Name... nameArr);

    void fromArray(Object[] objArr, int... iArr);

    int hashCode();

    boolean equals(Object obj);

    @Override // java.lang.Comparable
    int compareTo(Record record);

    <T> T getValue(Field<T> field) throws IllegalArgumentException;

    <T> T getValue(Field<?> field, Class<? extends T> cls) throws IllegalArgumentException, DataTypeException;

    <T, U> U getValue(Field<T> field, Converter<? super T, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    Object getValue(String str) throws IllegalArgumentException;

    <T> T getValue(String str, Class<? extends T> cls) throws IllegalArgumentException, DataTypeException;

    <U> U getValue(String str, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    Object getValue(Name name) throws IllegalArgumentException;

    <T> T getValue(Name name, Class<? extends T> cls) throws IllegalArgumentException, DataTypeException;

    <U> U getValue(Name name, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    Object getValue(int i) throws IllegalArgumentException;

    <T> T getValue(int i, Class<? extends T> cls) throws IllegalArgumentException, DataTypeException;

    <U> U getValue(int i, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    <T> void setValue(Field<T> field, T t);

    <T, U> void setValue(Field<T> field, U u, Converter<? extends T, ? super U> converter);
}
