package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Dual.class */
public final class Dual extends AbstractTable<Record> implements QOM.Dual {
    private static final Table<Record> FORCED_DUAL = DSL.select(DSL.inline("X").as("DUMMY")).asTable("DUAL");
    private static final Name DUAL_FIREBIRD = DSL.unquotedName("RDB$DATABASE");
    private static final Name DUAL_CUBRID = DSL.unquotedName("db_root");
    private static final Name DUAL_DERBY = DSL.unquotedName("SYSIBM", "SYSDUMMY1");
    private final boolean force;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Dual() {
        this(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Dual(boolean force) {
        super(TableOptions.expression(), Names.N_DUAL, (Schema) null);
        this.force = force;
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        return RecordImpl1.class;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<Record> as(Name alias) {
        if (this.force) {
            return FORCED_DUAL.as(alias);
        }
        return new TableAlias(this, alias);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<Record> as(Name alias, Name... fieldAliases) {
        if (this.force) {
            return FORCED_DUAL.as(alias, fieldAliases);
        }
        return new TableAlias(this, alias, fieldAliases);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public boolean declaresTables() {
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.force) {
            ctx.visit(FORCED_DUAL);
            return;
        }
        switch (ctx.family()) {
            case H2:
            case POSTGRES:
            case SQLITE:
            case TRINO:
            case YUGABYTEDB:
                return;
            case FIREBIRD:
                ctx.visit(DUAL_FIREBIRD);
                return;
            case HSQLDB:
                ctx.sql('(').visit(Keywords.K_VALUES).sql("(1)) ").visit(Keywords.K_AS).sql(' ').visit(Names.N_DUAL).sql('(').visit(Names.N_DUAL).sql(')');
                return;
            case CUBRID:
                ctx.visit(DUAL_CUBRID);
                return;
            case DERBY:
                ctx.visit(DUAL_DERBY);
                return;
            default:
                ctx.visit(Keywords.K_DUAL);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record> fields0() {
        return new FieldsImpl<>((SelectField<?>[]) new SelectField[0]);
    }
}
