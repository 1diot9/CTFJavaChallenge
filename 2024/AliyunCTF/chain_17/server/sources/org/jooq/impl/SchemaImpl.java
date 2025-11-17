package org.jooq.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Context;
import org.jooq.Domain;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.UDT;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SchemaImpl.class */
public class SchemaImpl extends AbstractNamed implements Schema, SimpleQueryPart, QOM.UEmpty {
    private static final Clause[] CLAUSES = {Clause.SCHEMA, Clause.SCHEMA_REFERENCE};
    static final Lazy<Schema> DEFAULT_SCHEMA = Lazy.of(() -> {
        return new SchemaImpl("");
    });
    private Catalog catalog;

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

    public SchemaImpl(String name) {
        this(name, (Catalog) null);
    }

    public SchemaImpl(String name, Catalog catalog) {
        this(DSL.name(name), catalog);
    }

    public SchemaImpl(String name, Catalog catalog, String comment) {
        this(DSL.name(name), catalog, DSL.comment(comment));
    }

    public SchemaImpl(Name name) {
        this(name, (Catalog) null);
    }

    public SchemaImpl(Name name, Catalog catalog) {
        this(name, catalog, (Comment) null);
    }

    public SchemaImpl(Name name, Catalog catalog, Comment comment) {
        super(qualify(catalog, name), comment);
        this.catalog = catalog;
    }

    @Override // org.jooq.Schema
    public Catalog getCatalog() {
        Catalog catalog;
        if (this.catalog == null) {
            if (getQualifiedName().qualified()) {
                catalog = DSL.catalog(getQualifiedName().qualifier());
            } else {
                catalog = null;
            }
            this.catalog = catalog;
        }
        return this.catalog;
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Catalog mappedCatalog;
        if (ctx.qualifyCatalog() && (mappedCatalog = Tools.getMappedCatalog(ctx, getCatalog())) != null && !"".equals(mappedCatalog.getName())) {
            ctx.visit(mappedCatalog).sql('.');
        }
        Schema mappedSchema = Tools.getMappedSchema(ctx, this);
        ctx.visit(mappedSchema != null ? mappedSchema.getUnqualifiedName() : getUnqualifiedName());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.Schema
    public final Table<?> getTable(String name) {
        return (Table) find(name, getTables());
    }

    @Override // org.jooq.Schema
    public final Table<?> getTable(Name name) {
        return (Table) find(name, getTables());
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getPrimaryKeys(String name) {
        return findAll(name, getPrimaryKeys());
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getPrimaryKeys(Name name) {
        return findAll(name, getPrimaryKeys());
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getUniqueKeys(String name) {
        return findAll(name, getUniqueKeys());
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getUniqueKeys(Name name) {
        return findAll(name, getUniqueKeys());
    }

    @Override // org.jooq.Schema
    public final List<ForeignKey<?, ?>> getForeignKeys(String name) {
        return findAll(name, getForeignKeys());
    }

    @Override // org.jooq.Schema
    public final List<ForeignKey<?, ?>> getForeignKeys(Name name) {
        return findAll(name, getForeignKeys());
    }

    @Override // org.jooq.Schema
    public final List<Index> getIndexes(String name) {
        return findAll(name, getIndexes());
    }

    @Override // org.jooq.Schema
    public final List<Index> getIndexes(Name name) {
        return findAll(name, getIndexes());
    }

    @Override // org.jooq.Schema
    public final UDT<?> getUDT(String name) {
        return (UDT) find(name, getUDTs());
    }

    @Override // org.jooq.Schema
    public final UDT<?> getUDT(Name name) {
        return (UDT) find(name, getUDTs());
    }

    @Override // org.jooq.Schema
    public final Domain<?> getDomain(String name) {
        return (Domain) find(name, getDomains());
    }

    @Override // org.jooq.Schema
    public final Domain<?> getDomain(Name name) {
        return (Domain) find(name, getDomains());
    }

    @Override // org.jooq.Schema
    public final Sequence<?> getSequence(String name) {
        return (Sequence) find(name, getSequences());
    }

    @Override // org.jooq.Schema
    public final Sequence<?> getSequence(Name name) {
        return (Sequence) find(name, getSequences());
    }

    public List<Table<?>> getTables() {
        return Collections.emptyList();
    }

    @Override // org.jooq.Schema
    public List<UniqueKey<?>> getPrimaryKeys() {
        List<UniqueKey<?>> result = new ArrayList<>();
        for (Table<?> table : getTables()) {
            if (table.getPrimaryKey() != null) {
                result.add(table.getPrimaryKey());
            }
        }
        return result;
    }

    @Override // org.jooq.Schema
    public List<UniqueKey<?>> getUniqueKeys() {
        return Tools.flatMap(getTables(), t -> {
            return t.getKeys();
        });
    }

    @Override // org.jooq.Schema
    public List<ForeignKey<?, ?>> getForeignKeys() {
        return Tools.flatMap(getTables(), t -> {
            return t.getReferences();
        });
    }

    @Override // org.jooq.Schema
    public List<Index> getIndexes() {
        return Tools.flatMap(getTables(), t -> {
            return t.getIndexes();
        });
    }

    public List<UDT<?>> getUDTs() {
        return Collections.emptyList();
    }

    public List<Domain<?>> getDomains() {
        return Collections.emptyList();
    }

    public List<Sequence<?>> getSequences() {
        return Collections.emptyList();
    }

    @Override // org.jooq.Schema
    public final Stream<Table<?>> tableStream() {
        return getTables().stream();
    }

    @Override // org.jooq.Schema
    public final Stream<UniqueKey<?>> primaryKeyStream() {
        return getPrimaryKeys().stream();
    }

    @Override // org.jooq.Schema
    public final Stream<UniqueKey<?>> uniqueKeyStream() {
        return getUniqueKeys().stream();
    }

    @Override // org.jooq.Schema
    public final Stream<ForeignKey<?, ?>> foreignKeyStream() {
        return getForeignKeys().stream();
    }

    @Override // org.jooq.Schema
    public final Stream<Index> indexStream() {
        return getIndexes().stream();
    }

    @Override // org.jooq.Schema
    public final Stream<UDT<?>> udtStream() {
        return getUDTs().stream();
    }

    @Override // org.jooq.Schema
    public final Stream<Domain<?>> domainStream() {
        return getDomains().stream();
    }

    @Override // org.jooq.Schema
    public final Stream<Sequence<?>> sequenceStream() {
        return getSequences().stream();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof SchemaImpl)) {
            return super.equals(that);
        }
        SchemaImpl other = (SchemaImpl) that;
        return StringUtils.equals(StringUtils.defaultIfNull(getCatalog(), CatalogImpl.DEFAULT_CATALOG), StringUtils.defaultIfNull(other.getCatalog(), CatalogImpl.DEFAULT_CATALOG)) && StringUtils.equals(getName(), other.getName());
    }
}
