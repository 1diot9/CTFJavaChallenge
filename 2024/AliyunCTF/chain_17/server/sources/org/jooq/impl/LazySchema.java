package org.jooq.impl;

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

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LazySchema.class */
public final class LazySchema extends AbstractNamed implements Schema {
    final LazySupplier<Schema> supplier;
    transient Schema schema;

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

    public LazySchema(Name name, Comment comment, LazySupplier<Schema> supplier) {
        super(name, comment);
        this.supplier = supplier;
    }

    private final Schema schema() {
        if (this.schema == null) {
            try {
                this.schema = this.supplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.schema;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(schema());
    }

    @Override // org.jooq.Schema
    public final Catalog getCatalog() {
        return schema().getCatalog();
    }

    @Override // org.jooq.Schema
    public final List<Table<?>> getTables() {
        return schema().getTables();
    }

    @Override // org.jooq.Schema
    public final Table<?> getTable(String name) {
        return schema().getTable(name);
    }

    @Override // org.jooq.Schema
    public final Table<?> getTable(Name name) {
        return schema().getTable(name);
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getPrimaryKeys() {
        return schema().getPrimaryKeys();
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getPrimaryKeys(String name) {
        return schema().getPrimaryKeys(name);
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getPrimaryKeys(Name name) {
        return schema().getPrimaryKeys(name);
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getUniqueKeys() {
        return schema().getUniqueKeys();
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getUniqueKeys(String name) {
        return schema().getUniqueKeys(name);
    }

    @Override // org.jooq.Schema
    public final List<UniqueKey<?>> getUniqueKeys(Name name) {
        return schema().getUniqueKeys(name);
    }

    @Override // org.jooq.Schema
    public final List<ForeignKey<?, ?>> getForeignKeys() {
        return schema().getForeignKeys();
    }

    @Override // org.jooq.Schema
    public final List<ForeignKey<?, ?>> getForeignKeys(String name) {
        return schema().getForeignKeys(name);
    }

    @Override // org.jooq.Schema
    public final List<ForeignKey<?, ?>> getForeignKeys(Name name) {
        return schema().getForeignKeys(name);
    }

    @Override // org.jooq.Schema
    public final List<Index> getIndexes() {
        return schema().getIndexes();
    }

    @Override // org.jooq.Schema
    public final List<Index> getIndexes(String name) {
        return schema().getIndexes(name);
    }

    @Override // org.jooq.Schema
    public final List<Index> getIndexes(Name name) {
        return schema().getIndexes(name);
    }

    @Override // org.jooq.Schema
    public final List<UDT<?>> getUDTs() {
        return schema().getUDTs();
    }

    @Override // org.jooq.Schema
    public final UDT<?> getUDT(String name) {
        return schema().getUDT(name);
    }

    @Override // org.jooq.Schema
    public final UDT<?> getUDT(Name name) {
        return schema().getUDT(name);
    }

    @Override // org.jooq.Schema
    public final List<Domain<?>> getDomains() {
        return schema().getDomains();
    }

    @Override // org.jooq.Schema
    public final Domain<?> getDomain(String name) {
        return schema().getDomain(name);
    }

    @Override // org.jooq.Schema
    public final Domain<?> getDomain(Name name) {
        return schema().getDomain(name);
    }

    @Override // org.jooq.Schema
    public final List<Sequence<?>> getSequences() {
        return schema().getSequences();
    }

    @Override // org.jooq.Schema
    public final Sequence<?> getSequence(String name) {
        return schema().getSequence(name);
    }

    @Override // org.jooq.Schema
    public final Sequence<?> getSequence(Name name) {
        return schema().getSequence(name);
    }

    @Override // org.jooq.Schema
    public final Stream<Table<?>> tableStream() {
        return schema().tableStream();
    }

    @Override // org.jooq.Schema
    public final Stream<UniqueKey<?>> primaryKeyStream() {
        return schema().primaryKeyStream();
    }

    @Override // org.jooq.Schema
    public final Stream<UniqueKey<?>> uniqueKeyStream() {
        return schema().uniqueKeyStream();
    }

    @Override // org.jooq.Schema
    public final Stream<ForeignKey<?, ?>> foreignKeyStream() {
        return schema().foreignKeyStream();
    }

    @Override // org.jooq.Schema
    public final Stream<Index> indexStream() {
        return schema().indexStream();
    }

    @Override // org.jooq.Schema
    public final Stream<UDT<?>> udtStream() {
        return schema().udtStream();
    }

    @Override // org.jooq.Schema
    public final Stream<Domain<?>> domainStream() {
        return schema().domainStream();
    }

    @Override // org.jooq.Schema
    public final Stream<Sequence<?>> sequenceStream() {
        return schema().sequenceStream();
    }
}
