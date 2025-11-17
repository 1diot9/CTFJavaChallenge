package org.jooq.impl;

import java.util.List;
import java.util.stream.Stream;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Schema;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LazyCatalog.class */
public final class LazyCatalog extends AbstractNamed implements Catalog {
    final LazySupplier<Catalog> supplier;
    transient Catalog catalog;

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public /* bridge */ /* synthetic */ Name getQualifiedName() {
        return super.getQualifiedName();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean generatesCast() {
        return super.generatesCast();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresCTE() {
        return super.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresWindows() {
        return super.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresTables() {
        return super.declaresTables();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean rendersContent(Context context) {
        return super.rendersContent(context);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    @Deprecated
    public /* bridge */ /* synthetic */ Clause[] clauses(Context context) {
        return super.clauses(context);
    }

    public LazyCatalog(Name name, LazySupplier<Catalog> supplier) {
        super(name, CommentImpl.NO_COMMENT);
        this.supplier = supplier;
    }

    private final Catalog catalog() {
        if (this.catalog == null) {
            try {
                this.catalog = this.supplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.catalog;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(catalog());
    }

    @Override // org.jooq.Catalog
    public final List<Schema> getSchemas() {
        return catalog().getSchemas();
    }

    @Override // org.jooq.Catalog
    public final Schema getSchema(String name) {
        return catalog().getSchema(name);
    }

    @Override // org.jooq.Catalog
    public final Schema getSchema(Name name) {
        return catalog().getSchema(name);
    }

    @Override // org.jooq.Catalog
    public final Stream<Schema> schemaStream() {
        return catalog().schemaStream();
    }
}
