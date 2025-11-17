package org.jooq.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Meta;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UDT;
import org.jooq.UniqueKey;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FilteredMeta.class */
public final class FilteredMeta extends AbstractMeta {
    private final AbstractMeta meta;
    private final Predicate<? super Catalog> catalogFilter;
    private final Predicate<? super Schema> schemaFilter;
    private final Predicate<? super Table<?>> tableFilter;
    private final Predicate<? super Domain<?>> domainFilter;
    private final Predicate<? super Sequence<?>> sequenceFilter;
    private final Predicate<? super UniqueKey<?>> primaryKeyFilter;
    private final Predicate<? super UniqueKey<?>> uniqueKeyFilter;
    private final Predicate<? super ForeignKey<?, ?>> foreignKeyFilter;
    private final Predicate<? super Index> indexFilter;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilteredMeta(AbstractMeta meta, Predicate<? super Catalog> catalogFilter, Predicate<? super Schema> schemaFilter, Predicate<? super Table<?>> tableFilter, Predicate<? super Domain<?>> domainFilter, Predicate<? super Sequence<?>> sequenceFilter, Predicate<? super UniqueKey<?>> primaryKeyFilter, Predicate<? super UniqueKey<?>> uniqueKeyFilter, Predicate<? super ForeignKey<?, ?>> foreignKeyFilter, Predicate<? super Index> indexFilter) {
        super(meta.configuration());
        this.meta = meta;
        this.catalogFilter = catalogFilter;
        this.schemaFilter = schemaFilter;
        this.tableFilter = tableFilter;
        this.domainFilter = domainFilter;
        this.sequenceFilter = sequenceFilter;
        this.primaryKeyFilter = primaryKeyFilter;
        this.uniqueKeyFilter = uniqueKeyFilter;
        this.foreignKeyFilter = foreignKeyFilter;
        this.indexFilter = indexFilter;
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Catalog> getCatalogs0() {
        List<Catalog> result = new ArrayList<>();
        for (Catalog c : this.meta.getCatalogs()) {
            if (this.catalogFilter == null || this.catalogFilter.test(c)) {
                result.add(new FilteredCatalog(c));
            }
        }
        return result;
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterCatalogs(Predicate<? super Catalog> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter != null ? new And<>(this.catalogFilter, filter) : filter, this.schemaFilter, this.tableFilter, this.domainFilter, this.sequenceFilter, this.primaryKeyFilter, this.uniqueKeyFilter, this.foreignKeyFilter, this.indexFilter);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterSchemas(Predicate<? super Schema> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter, this.schemaFilter != null ? new And<>(this.schemaFilter, filter) : filter, this.tableFilter, this.domainFilter, this.sequenceFilter, this.primaryKeyFilter, this.uniqueKeyFilter, this.foreignKeyFilter, this.indexFilter);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterTables(Predicate<? super Table<?>> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter, this.schemaFilter, this.tableFilter != null ? new And<>(this.tableFilter, filter) : filter, this.domainFilter, this.sequenceFilter, this.primaryKeyFilter, this.uniqueKeyFilter, this.foreignKeyFilter, this.indexFilter);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterDomains(Predicate<? super Domain<?>> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter, this.schemaFilter, this.tableFilter, this.domainFilter != null ? new And<>(this.domainFilter, filter) : filter, this.sequenceFilter, this.primaryKeyFilter, this.uniqueKeyFilter, this.foreignKeyFilter, this.indexFilter);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterSequences(Predicate<? super Sequence<?>> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter, this.schemaFilter, this.tableFilter, this.domainFilter, this.sequenceFilter != null ? new And<>(this.sequenceFilter, filter) : filter, this.primaryKeyFilter, this.uniqueKeyFilter, this.foreignKeyFilter, this.indexFilter);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterPrimaryKeys(Predicate<? super UniqueKey<?>> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter, this.schemaFilter, this.tableFilter, this.domainFilter, this.sequenceFilter, this.primaryKeyFilter != null ? new And<>(this.primaryKeyFilter, filter) : filter, this.uniqueKeyFilter, this.foreignKeyFilter, this.indexFilter);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterUniqueKeys(Predicate<? super UniqueKey<?>> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter, this.schemaFilter, this.tableFilter, this.domainFilter, this.sequenceFilter, this.primaryKeyFilter, this.uniqueKeyFilter != null ? new And<>(this.uniqueKeyFilter, filter) : filter, this.foreignKeyFilter, this.indexFilter);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterForeignKeys(Predicate<? super ForeignKey<?, ?>> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter, this.schemaFilter, this.tableFilter, this.domainFilter, this.sequenceFilter, this.primaryKeyFilter, this.uniqueKeyFilter, this.foreignKeyFilter != null ? new And<>(this.foreignKeyFilter, filter) : filter, this.indexFilter);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public final Meta filterIndexes(Predicate<? super Index> filter) {
        return new FilteredMeta(this.meta, this.catalogFilter, this.schemaFilter, this.tableFilter, this.domainFilter, this.sequenceFilter, this.primaryKeyFilter, this.uniqueKeyFilter, this.foreignKeyFilter, this.indexFilter != null ? new And<>(this.indexFilter, filter) : filter);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FilteredMeta$And.class */
    private static class And<Q extends QueryPart> implements Predicate<Q> {
        private final Predicate<? super Q> p1;
        private final Predicate<? super Q> p2;

        And(Predicate<? super Q> p1, Predicate<? super Q> p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override // java.util.function.Predicate
        public final boolean test(Q q) {
            return this.p1.test(q) && this.p2.test(q);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FilteredMeta$FilteredCatalog.class */
    private class FilteredCatalog extends CatalogImpl {
        private final Catalog delegate;
        private transient List<Schema> schemas;

        private FilteredCatalog(Catalog delegate) {
            super(delegate.getQualifiedName(), delegate.getCommentPart());
            this.delegate = delegate;
        }

        @Override // org.jooq.impl.CatalogImpl, org.jooq.Catalog
        public final List<Schema> getSchemas() {
            if (this.schemas == null) {
                this.schemas = new ArrayList();
                for (Schema s : this.delegate.getSchemas()) {
                    if (FilteredMeta.this.schemaFilter == null || FilteredMeta.this.schemaFilter.test(s)) {
                        this.schemas.add(new FilteredSchema(this, s));
                    }
                }
            }
            return Collections.unmodifiableList(this.schemas);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FilteredMeta$FilteredSchema.class */
    private class FilteredSchema extends SchemaImpl {
        private final Schema delegate;
        private transient List<Domain<?>> domains;
        private transient List<Table<?>> tables;
        private transient List<Sequence<?>> sequences;

        private FilteredSchema(FilteredCatalog catalog, Schema delegate) {
            super(delegate.getQualifiedName(), catalog, delegate.getCommentPart());
            this.delegate = delegate;
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<Domain<?>> getDomains() {
            if (this.domains == null) {
                this.domains = new ArrayList();
                for (Domain<?> d : this.delegate.getDomains()) {
                    if (FilteredMeta.this.domainFilter == null || FilteredMeta.this.domainFilter.test(d)) {
                        this.domains.add(d);
                    }
                }
            }
            return Collections.unmodifiableList(this.domains);
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<Table<?>> getTables() {
            if (this.tables == null) {
                this.tables = new ArrayList();
                for (Table<?> t : this.delegate.getTables()) {
                    if (FilteredMeta.this.tableFilter == null || FilteredMeta.this.tableFilter.test(t)) {
                        this.tables.add(new FilteredTable(this, t));
                    }
                }
            }
            return Collections.unmodifiableList(this.tables);
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<Sequence<?>> getSequences() {
            if (this.sequences == null) {
                this.sequences = new ArrayList();
                for (Sequence<?> t : this.delegate.getSequences()) {
                    if (FilteredMeta.this.sequenceFilter == null || FilteredMeta.this.sequenceFilter.test(t)) {
                        this.sequences.add(t);
                    }
                }
            }
            return Collections.unmodifiableList(this.sequences);
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<UDT<?>> getUDTs() {
            return this.delegate.getUDTs();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FilteredMeta$FilteredTable.class */
    private class FilteredTable<R extends Record> extends TableImpl<R> {
        private final Table<R> delegate;
        private transient List<Index> indexes;
        private transient UniqueKey<R> primaryKey;
        private transient List<UniqueKey<R>> uniqueKeys;
        private transient List<ForeignKey<R, ?>> references;

        private FilteredTable(FilteredSchema schema, Table<R> delegate) {
            super(delegate.getQualifiedName(), schema, null, null, delegate.getCommentPart(), delegate.getOptions());
            this.delegate = delegate;
            for (Field<?> field : delegate.fields()) {
                createField(field.getQualifiedName(), field.getDataType(), this, field.getComment());
            }
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<Index> getIndexes() {
            if (this.indexes == null) {
                this.indexes = new ArrayList();
                for (Index index : this.delegate.getIndexes()) {
                    if (FilteredMeta.this.indexFilter == null || FilteredMeta.this.indexFilter.test(index)) {
                        this.indexes.add(index);
                    }
                }
            }
            return Collections.unmodifiableList(this.indexes);
        }

        private final void initKeys() {
            if (this.uniqueKeys == null) {
                this.uniqueKeys = new ArrayList();
                for (UniqueKey<R> uk : this.delegate.getUniqueKeys()) {
                    if (FilteredMeta.this.uniqueKeyFilter == null || FilteredMeta.this.uniqueKeyFilter.test(uk)) {
                        this.uniqueKeys.add(key(uk));
                    }
                }
                UniqueKey<R> pk = this.delegate.getPrimaryKey();
                if (pk != null) {
                    if (FilteredMeta.this.primaryKeyFilter == null || FilteredMeta.this.primaryKeyFilter.test(pk)) {
                        this.primaryKey = key(pk);
                    }
                }
            }
        }

        private final UniqueKey<R> key(UniqueKey<R> key) {
            return Internal.createUniqueKey(this, key.getName(), (TableField[]) Tools.map(key.getFieldsArray(), f -> {
                return (TableField) field(f);
            }, x$0 -> {
                return new TableField[x$0];
            }), key.enforced());
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final UniqueKey<R> getPrimaryKey() {
            initKeys();
            return this.primaryKey;
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<UniqueKey<R>> getUniqueKeys() {
            initKeys();
            return Collections.unmodifiableList(this.uniqueKeys);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<ForeignKey<R, ?>> getReferences() {
            if (this.references == null) {
                this.references = new ArrayList();
                for (ForeignKey<R, ?> fk : this.delegate.getReferences()) {
                    if (FilteredMeta.this.foreignKeyFilter == null || FilteredMeta.this.foreignKeyFilter.test(fk)) {
                        UniqueKey<?> uk = FilteredMeta.this.lookupUniqueKey(fk);
                        if (uk != null && (!uk.isPrimary() || FilteredMeta.this.primaryKeyFilter == null || FilteredMeta.this.primaryKeyFilter.test(uk))) {
                            if (uk.isPrimary() || FilteredMeta.this.uniqueKeyFilter == null || FilteredMeta.this.uniqueKeyFilter.test(uk)) {
                                this.references.add(AbstractMeta.copyFK(this, uk, fk));
                            }
                        }
                    }
                }
            }
            return Collections.unmodifiableList(this.references);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<Check<R>> getChecks() {
            return this.delegate.getChecks();
        }
    }
}
