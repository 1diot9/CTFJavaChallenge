package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowAsField.class */
public final class RowAsField<ROW extends Row, REC extends Record> extends AbstractRowAsField<REC> implements QOM.RowAsField<REC> {
    static final Set<SQLDialect> NO_NATIVE_SUPPORT = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    final ROW row;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowAsField(ROW row) {
        this(row, DSL.name("nested"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowAsField(ROW row, Name as) {
        super(as, new RecordDataType(row));
        this.row = row;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractRowAsField
    public final ROW fields0() {
        return this.row;
    }

    @Override // org.jooq.impl.AbstractRowAsField
    final Class<REC> getRecordType() {
        return (Class<REC>) Tools.recordType(this.row.size());
    }

    @Override // org.jooq.impl.AbstractRowAsField
    final void acceptDefault(Context<?> ctx) {
        if (NO_NATIVE_SUPPORT.contains(ctx.dialect())) {
            ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, true, c -> {
                c.visit(new SelectFieldList(emulatedFields(ctx.configuration()).fields.fields));
            });
        } else {
            ctx.data(Tools.BooleanDataKey.DATA_ROW_CONTENT, true, c2 -> {
                c2.visit(Keywords.K_ROW).sql(' ').visit(this.row);
            });
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.Field, org.jooq.SelectField
    public Field<REC> as(Name alias) {
        return new RowAsField(this.row, alias);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public final Field<?> $aliased() {
        return new RowAsField(this.row);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public final Name $alias() {
        return getQualifiedName();
    }

    @Override // org.jooq.impl.QOM.RowAsField
    public final Row $row() {
        return this.row;
    }
}
