package org.jooq.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Meta;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UDT;
import org.jooq.UDTRecord;
import org.jooq.UniqueKey;
import org.jooq.exception.DataAccessException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Snapshot.class */
public final class Snapshot extends AbstractMeta {
    private Meta delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Snapshot(Meta meta) {
        super(meta.configuration());
        this.delegate = meta;
        getCatalogs();
        this.delegate = null;
        resolveReferences();
    }

    private final void resolveReferences() {
        for (Catalog catalog : getCatalogs()) {
            ((SnapshotCatalog) catalog).resolveReferences();
        }
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Catalog> getCatalogs0() throws DataAccessException {
        return Tools.map(this.delegate.getCatalogs(), x$0 -> {
            return new SnapshotCatalog(x$0);
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Snapshot$SnapshotCatalog.class */
    public class SnapshotCatalog extends CatalogImpl {
        private final List<SnapshotSchema> schemas;

        SnapshotCatalog(Catalog catalog) {
            super(catalog.getQualifiedName(), catalog.getCommentPart());
            this.schemas = Tools.map(catalog.getSchemas(), s -> {
                return new SnapshotSchema(this, s);
            });
        }

        private final void resolveReferences() {
            for (SnapshotSchema schema : this.schemas) {
                schema.resolveReferences();
            }
        }

        @Override // org.jooq.impl.CatalogImpl, org.jooq.Catalog
        public final List<Schema> getSchemas() {
            return Collections.unmodifiableList(this.schemas);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Snapshot$SnapshotSchema.class */
    public class SnapshotSchema extends SchemaImpl {
        private final List<SnapshotDomain<?>> domains;
        private final List<SnapshotTable<?>> tables;
        private final List<SnapshotSequence<?>> sequences;
        private final List<SnapshotUDT<?>> udts;

        SnapshotSchema(SnapshotCatalog catalog, Schema schema) {
            super(schema.getQualifiedName(), catalog, schema.getCommentPart());
            this.domains = Tools.map(schema.getDomains(), d -> {
                return new SnapshotDomain(this, d);
            });
            this.tables = Tools.map(schema.getTables(), t -> {
                return new SnapshotTable(this, t);
            });
            this.sequences = Tools.map(schema.getSequences(), s -> {
                return new SnapshotSequence(this, s);
            });
            this.udts = Tools.map(schema.getUDTs(), u -> {
                return new SnapshotUDT(this, u);
            });
        }

        final void resolveReferences() {
            for (SnapshotTable<?> table : this.tables) {
                table.resolveReferences();
            }
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<Domain<?>> getDomains() {
            return Collections.unmodifiableList(this.domains);
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<Table<?>> getTables() {
            return Collections.unmodifiableList(this.tables);
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<Sequence<?>> getSequences() {
            return Collections.unmodifiableList(this.sequences);
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<UDT<?>> getUDTs() {
            return Collections.unmodifiableList(this.udts);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Snapshot$SnapshotDomain.class */
    private class SnapshotDomain<T> extends DomainImpl<T> {
        SnapshotDomain(SnapshotSchema schema, Domain<T> domain) {
            super(schema, domain.getQualifiedName(), domain.getDataType(), (Check[]) domain.getChecks().toArray(Tools.EMPTY_CHECK));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Snapshot$SnapshotTable.class */
    public class SnapshotTable<R extends Record> extends TableImpl<R> {
        private final List<Index> indexes;
        private final List<UniqueKey<R>> uniqueKeys;
        private UniqueKey<R> primaryKey;
        private final List<ForeignKey<R, ?>> foreignKeys;
        private final List<Check<R>> checks;

        SnapshotTable(SnapshotSchema schema, Table<R> table) {
            super(table.getQualifiedName(), schema, null, null, table.getCommentPart(), table.getOptions());
            for (Field<?> field : table.fields()) {
                createField(field.getUnqualifiedName(), field.getDataType(), this, field.getComment());
            }
            this.indexes = Tools.map(table.getIndexes(), index -> {
                return new IndexImpl(index.getQualifiedName(), this, (OrderField[]) Tools.map(index.getFields(), field2 -> {
                    Field<?> f = field(field2.getName());
                    return f != null ? f.sort(field2.getOrder()) : field2;
                }, x$0 -> {
                    return new SortField[x$0];
                }), index.getWhere(), index.getUnique());
            });
            this.uniqueKeys = Tools.map(table.getUniqueKeys(), uk -> {
                return Internal.createUniqueKey(this, uk.getQualifiedName(), fields((TableField[]) uk.getFieldsArray()), uk.enforced());
            });
            UniqueKey<R> pk = table.getPrimaryKey();
            if (pk != null) {
                this.primaryKey = Internal.createUniqueKey(this, pk.getQualifiedName(), fields((TableField[]) pk.getFieldsArray()), pk.enforced());
            }
            this.foreignKeys = new ArrayList(table.getReferences());
            this.checks = new ArrayList(table.getChecks());
        }

        @Deprecated
        private final TableField<R, ?>[] fields(TableField<R, ?>[] tableFields) {
            return (TableField[]) Tools.map(tableFields, f -> {
                return (TableField) field(f.getName());
            }, x$0 -> {
                return new TableField[x$0];
            });
        }

        final void resolveReferences() {
            for (int i = 0; i < this.foreignKeys.size(); i++) {
                ForeignKey<R, ?> fk = this.foreignKeys.get(i);
                UniqueKey<?> uk = Snapshot.this.lookupUniqueKey(fk);
                if (uk == null) {
                    this.foreignKeys.remove(i);
                } else {
                    this.foreignKeys.set(i, AbstractMeta.copyFK(this, uk, fk));
                }
            }
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<Index> getIndexes() {
            return Collections.unmodifiableList(this.indexes);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<UniqueKey<R>> getUniqueKeys() {
            return Collections.unmodifiableList(this.uniqueKeys);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final UniqueKey<R> getPrimaryKey() {
            return this.primaryKey;
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<ForeignKey<R, ?>> getReferences() {
            return Collections.unmodifiableList(this.foreignKeys);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<Check<R>> getChecks() {
            return Collections.unmodifiableList(this.checks);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Snapshot$SnapshotSequence.class */
    private class SnapshotSequence<T extends Number> extends SequenceImpl<T> {
        SnapshotSequence(SnapshotSchema schema, Sequence<T> sequence) {
            super(sequence.getQualifiedName(), schema, sequence.getDataType(), false, sequence.getStartWith(), sequence.getIncrementBy(), sequence.getMinvalue(), sequence.getMaxvalue(), sequence.getCycle(), sequence.getCache());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Snapshot$SnapshotUDT.class */
    private class SnapshotUDT<R extends UDTRecord<R>> extends UDTImpl<R> {
        SnapshotUDT(SnapshotSchema schema, UDT<R> udt) {
            super(udt.getUnqualifiedName(), schema, udt.getPackage(), udt.isSynthetic());
        }
    }
}
