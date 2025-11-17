package org.jooq.impl;

import java.util.stream.Stream;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.Name;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldsTrait.class */
public interface FieldsTrait extends Fields {
    @Override // org.jooq.Fields
    default Field<?>[] fields() {
        return fieldsRow().fields();
    }

    @Override // org.jooq.Fields
    default Stream<Field<?>> fieldStream() {
        return fieldsRow().fieldStream();
    }

    @Override // org.jooq.Fields
    default <T> Field<T> field(Field<T> field) {
        return fieldsRow().field(field);
    }

    @Override // org.jooq.Fields
    @Deprecated
    default Field<?> field(String name) {
        return fieldsRow().field(name);
    }

    @Override // org.jooq.Fields
    @Deprecated
    default <T> Field<T> field(String name, Class<T> type) {
        return fieldsRow().field(name, type);
    }

    @Override // org.jooq.Fields
    @Deprecated
    default <T> Field<T> field(String name, DataType<T> dataType) {
        return fieldsRow().field(name, dataType);
    }

    @Override // org.jooq.Fields
    @Deprecated
    default Field<?> field(Name name) {
        return fieldsRow().field(name);
    }

    @Override // org.jooq.Fields
    @Deprecated
    default <T> Field<T> field(Name name, Class<T> type) {
        return fieldsRow().field(name, type);
    }

    @Override // org.jooq.Fields
    @Deprecated
    default <T> Field<T> field(Name name, DataType<T> dataType) {
        return fieldsRow().field(name, dataType);
    }

    @Override // org.jooq.Fields
    default Field<?> field(int index) {
        return fieldsRow().field(index);
    }

    @Override // org.jooq.Fields
    default <T> Field<T> field(int index, Class<T> type) {
        return fieldsRow().field(index, type);
    }

    @Override // org.jooq.Fields
    default <T> Field<T> field(int index, DataType<T> dataType) {
        return fieldsRow().field(index, dataType);
    }

    @Override // org.jooq.Fields
    default Field<?>[] fields(Field<?>... fields) {
        return fieldsRow().fields(fields);
    }

    @Override // org.jooq.Fields
    default Field<?>[] fields(String... names) {
        return fieldsRow().fields(names);
    }

    @Override // org.jooq.Fields
    default Field<?>[] fields(Name... names) {
        return fieldsRow().fields(names);
    }

    @Override // org.jooq.Fields
    default Field<?>[] fields(int... indexes) {
        return fieldsRow().fields(indexes);
    }

    @Override // org.jooq.Fields
    default int indexOf(Field<?> field) {
        return fieldsRow().indexOf(field);
    }

    @Override // org.jooq.Fields
    default int indexOf(String name) {
        return fieldsRow().indexOf(name);
    }

    @Override // org.jooq.Fields
    default int indexOf(Name name) {
        return fieldsRow().indexOf(name);
    }

    @Override // org.jooq.Fields
    default Class<?>[] types() {
        return FieldsImpl.fieldsRow0(this).types();
    }

    @Override // org.jooq.Fields
    default Class<?> type(int index) {
        return FieldsImpl.fieldsRow0(this).type(index);
    }

    @Override // org.jooq.Fields
    default Class<?> type(String name) {
        return FieldsImpl.fieldsRow0(this).type(name);
    }

    @Override // org.jooq.Fields
    default Class<?> type(Name name) {
        return FieldsImpl.fieldsRow0(this).type(name);
    }

    @Override // org.jooq.Fields
    default DataType<?>[] dataTypes() {
        return FieldsImpl.fieldsRow0(this).dataTypes();
    }

    @Override // org.jooq.Fields
    default DataType<?> dataType(int index) {
        return FieldsImpl.fieldsRow0(this).dataType(index);
    }

    @Override // org.jooq.Fields
    default DataType<?> dataType(String name) {
        return FieldsImpl.fieldsRow0(this).dataType(name);
    }

    @Override // org.jooq.Fields
    default DataType<?> dataType(Name name) {
        return FieldsImpl.fieldsRow0(this).dataType(name);
    }
}
