package org.jooq.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CatalogImpl.class */
public class CatalogImpl extends AbstractNamed implements Catalog, SimpleQueryPart, QOM.UEmpty {
    private static final Clause[] CLAUSES = {Clause.CATALOG, Clause.CATALOG_REFERENCE};
    static final Catalog DEFAULT_CATALOG = new CatalogImpl("");

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

    public CatalogImpl(Name name) {
        this(name, (Comment) null);
    }

    public CatalogImpl(String name) {
        this(name, (String) null);
    }

    public CatalogImpl(Name name, Comment comment) {
        super(name, comment);
    }

    public CatalogImpl(String name, String comment) {
        super(DSL.name(name), DSL.comment(comment));
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Catalog mappedCatalog = Tools.getMappedCatalog(ctx, this);
        ctx.visit(mappedCatalog != null ? mappedCatalog.getUnqualifiedName() : getUnqualifiedName());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.Catalog
    public final Schema getSchema(String name) {
        return (Schema) find(name, getSchemas());
    }

    @Override // org.jooq.Catalog
    public final Schema getSchema(Name name) {
        return (Schema) find(name, getSchemas());
    }

    public List<Schema> getSchemas() {
        return Collections.emptyList();
    }

    @Override // org.jooq.Catalog
    public final Stream<Schema> schemaStream() {
        return getSchemas().stream();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof CatalogImpl) {
            return StringUtils.equals(getName(), ((CatalogImpl) that).getName());
        }
        return super.equals(that);
    }
}
