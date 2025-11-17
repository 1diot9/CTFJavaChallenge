package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jooq.Condition;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.RowN;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.exception.DetachedException;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ReferenceImpl.class */
public final class ReferenceImpl<CHILD extends Record, PARENT extends Record> extends AbstractKey<CHILD> implements ForeignKey<CHILD, PARENT>, QOM.UEmpty {
    private final UniqueKey<PARENT> uk;
    private final TableField<PARENT, ?>[] ukFields;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReferenceImpl(Table<CHILD> table, Name name, TableField<CHILD, ?>[] fkFields, UniqueKey<PARENT> uk, TableField<PARENT, ?>[] ukFields, boolean enforced) {
        super(table, name, fkFields, enforced);
        this.uk = uk;
        this.ukFields = ukFields;
    }

    @Override // org.jooq.ForeignKey
    public final InverseForeignKey<PARENT, CHILD> getInverseKey() {
        return new InverseReferenceImpl(this);
    }

    @Override // org.jooq.ForeignKey
    public final UniqueKey<PARENT> getKey() {
        return this.uk;
    }

    @Override // org.jooq.ForeignKey
    public final List<TableField<PARENT, ?>> getKeyFields() {
        return Arrays.asList(this.ukFields);
    }

    @Override // org.jooq.ForeignKey
    public final TableField<PARENT, ?>[] getKeyFieldsArray() {
        return this.ukFields;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ForeignKey
    public final PARENT fetchParent(CHILD child) {
        return (PARENT) Tools.filterOne(fetchParents(child));
    }

    @Override // org.jooq.ForeignKey
    @SafeVarargs
    public final Result<PARENT> fetchParents(CHILD... records) {
        return fetchParents(Tools.list(records));
    }

    @Override // org.jooq.ForeignKey
    public final Result<PARENT> fetchParents(Collection<? extends CHILD> collection) {
        if (collection == null || collection.size() == 0) {
            return new ResultImpl(new DefaultConfiguration(), this.uk.getFields());
        }
        return (Result<PARENT>) extractDSLContext(collection).selectFrom(parents(collection)).fetch();
    }

    @Override // org.jooq.ForeignKey
    public final Result<CHILD> fetchChildren(PARENT record) {
        return fetchChildren(Tools.list(record));
    }

    @Override // org.jooq.ForeignKey
    @SafeVarargs
    public final Result<CHILD> fetchChildren(PARENT... records) {
        return fetchChildren(Tools.list(records));
    }

    @Override // org.jooq.ForeignKey
    public final Result<CHILD> fetchChildren(Collection<? extends PARENT> collection) {
        if (collection == null || collection.size() == 0) {
            return new ResultImpl(new DefaultConfiguration(), (Collection<? extends Field<?>>) getFields());
        }
        return (Result<CHILD>) extractDSLContext(collection).selectFrom(children(collection)).fetch();
    }

    @Override // org.jooq.ForeignKey
    public final Table<PARENT> parent(CHILD record) {
        return parents(Tools.list(record));
    }

    @Override // org.jooq.ForeignKey
    @SafeVarargs
    public final Table<PARENT> parents(CHILD... records) {
        return parents(Tools.list(records));
    }

    @Override // org.jooq.ForeignKey
    public final Table<PARENT> parents(Collection<? extends CHILD> records) {
        return table(records, this.uk.getTable(), this.uk.getFieldsArray(), getFieldsArray());
    }

    @Override // org.jooq.ForeignKey
    public final Table<CHILD> children(PARENT record) {
        return children(Tools.list(record));
    }

    @Override // org.jooq.ForeignKey
    @SafeVarargs
    public final Table<CHILD> children(PARENT... records) {
        return children(Tools.list(records));
    }

    @Override // org.jooq.ForeignKey
    public final Table<CHILD> children(Collection<? extends PARENT> records) {
        return table(records, getTable(), getFieldsArray(), this.uk.getFieldsArray());
    }

    private static <R1 extends Record, R2 extends Record> Table<R1> table(Collection<? extends R2> records, Table<R1> table, TableField<R1, ?>[] fields1, TableField<R2, ?>[] fields2) {
        Condition in;
        TableField<R1, ?>[] f1 = truncate(fields1, fields2);
        TableField<R2, ?>[] f2 = truncate(fields2, fields1);
        if (f1.length == 1) {
            in = f1[0].in(extractValues(records, f2[0]));
        } else {
            in = DSL.row((SelectField<?>[]) f1).in(extractRows(records, f2));
        }
        return new InlineDerivedTable(table, in, false);
    }

    private static <R extends Record> TableField<R, ?>[] truncate(TableField<R, ?>[] fields1, TableField<?, ?>[] fields2) {
        if (fields1.length <= fields2.length) {
            return fields1;
        }
        return (TableField[]) Arrays.copyOf(fields1, fields2.length);
    }

    private static <R extends Record> List<Object> extractValues(Collection<? extends R> records, TableField<R, ?> field2) {
        return Tools.map(records, r -> {
            return r.get(field2);
        });
    }

    private static <R extends Record> List<RowN> extractRows(Collection<? extends R> records, TableField<R, ?>[] fields) {
        return Tools.map(records, r -> {
            Object[] values = Tools.map(fields, f -> {
                return r.get(f);
            }, x$0 -> {
                return new Object[x$0];
            });
            return DSL.row(values);
        });
    }

    private static <R extends Record> DSLContext extractDSLContext(Collection<? extends R> records) {
        Record record = (Record) Tools.first(records);
        if (record != null) {
            return DSL.using(record.configuration());
        }
        throw new DetachedException("Supply at least one attachable record");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractKey
    public final ConstraintEnforcementStep constraint0() {
        return DSL.constraint(getName()).foreignKey(getFieldsArray()).references(this.uk.getTable(), getKeyFieldsArray());
    }
}
