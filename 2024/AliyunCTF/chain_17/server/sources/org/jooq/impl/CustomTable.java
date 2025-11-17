package org.jooq.impl;

import java.util.List;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.TableRecord;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CustomTable.class */
public abstract class CustomTable<R extends TableRecord<R>> extends TableImpl<R> implements QOM.UOpaque {
    @Override // org.jooq.impl.TableImpl, org.jooq.RecordQualifier
    public abstract Class<? extends R> getRecordType();

    protected CustomTable(Name name) {
        super(name);
    }

    protected CustomTable(Name name, Schema schema) {
        super(name, schema);
    }

    @Deprecated
    protected CustomTable(String name) {
        super(name);
    }

    @Deprecated
    protected CustomTable(String name, Schema schema) {
        super(name, schema);
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public Identity<R, ?> getIdentity() {
        return super.getIdentity();
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public UniqueKey<R> getPrimaryKey() {
        return super.getPrimaryKey();
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public List<UniqueKey<R>> getUniqueKeys() {
        return super.getUniqueKeys();
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public List<ForeignKey<R, ?>> getReferences() {
        return super.getReferences();
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return super.declaresTables();
    }
}
