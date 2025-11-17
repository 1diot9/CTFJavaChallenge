package org.jooq.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.DDLExportConfiguration;
import org.jooq.Domain;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Meta;
import org.jooq.MigrationConfiguration;
import org.jooq.Name;
import org.jooq.Named;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.util.xml.jaxb.InformationSchema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractMeta.class */
abstract class AbstractMeta extends AbstractScope implements Meta, Serializable {
    private Map<Name, Catalog> cachedCatalogs;
    private Map<Name, Schema> cachedQualifiedSchemas;
    private Map<Name, Table<?>> cachedQualifiedTables;
    private Map<Name, Domain<?>> cachedQualifiedDomains;
    private Map<Name, Sequence<?>> cachedQualifiedSequences;
    private Map<Name, UniqueKey<?>> cachedQualifiedPrimaryKeys;
    private Map<Name, UniqueKey<?>> cachedQualifiedUniqueKeys;
    private Map<Name, ForeignKey<?, ?>> cachedQualifiedForeignKeys;
    private Map<Name, Index> cachedQualifiedIndexes;
    private Map<Name, List<Schema>> cachedUnqualifiedSchemas;
    private Map<Name, List<Table<?>>> cachedUnqualifiedTables;
    private Map<Name, List<Domain<?>>> cachedUnqualifiedDomains;
    private Map<Name, List<Sequence<?>>> cachedUnqualifiedSequences;
    private Map<Name, List<UniqueKey<?>>> cachedUnqualifiedPrimaryKeys;
    private Map<Name, List<UniqueKey<?>>> cachedUnqualifiedUniqueKeys;
    private Map<Name, List<ForeignKey<?, ?>>> cachedUnqualifiedForeignKeys;
    private Map<Name, List<Index>> cachedUnqualifiedIndexes;

    abstract List<Catalog> getCatalogs0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractMeta(Configuration configuration) {
        super(configuration);
    }

    @Override // org.jooq.Meta
    public final Catalog getCatalog(String name) {
        return getCatalog(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final Catalog getCatalog(Name name) {
        initCatalogs();
        return this.cachedCatalogs.get(name);
    }

    @Override // org.jooq.Meta
    public final List<Catalog> getCatalogs() {
        initCatalogs();
        return Collections.unmodifiableList(new ArrayList(this.cachedCatalogs.values()));
    }

    private final void initCatalogs() {
        if (this.cachedCatalogs == null) {
            this.cachedCatalogs = new LinkedHashMap();
            for (Catalog catalog : getCatalogs0()) {
                this.cachedCatalogs.put(catalog.getQualifiedName(), catalog);
            }
        }
    }

    @Override // org.jooq.Meta
    public final List<Schema> getSchemas(String name) {
        return getSchemas(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final List<Schema> getSchemas(Name name) {
        initSchemas();
        return get(name, () -> {
            return getSchemas0().iterator();
        }, this.cachedQualifiedSchemas, this.cachedUnqualifiedSchemas);
    }

    @Override // org.jooq.Meta
    public final List<Schema> getSchemas() {
        initSchemas();
        return Collections.unmodifiableList(new ArrayList(this.cachedQualifiedSchemas.values()));
    }

    private final void initSchemas() {
        if (this.cachedQualifiedSchemas == null) {
            this.cachedQualifiedSchemas = new LinkedHashMap();
            this.cachedUnqualifiedSchemas = new LinkedHashMap();
            get(DSL.name(""), () -> {
                return getSchemas0().iterator();
            }, this.cachedQualifiedSchemas, this.cachedUnqualifiedSchemas);
        }
    }

    List<Schema> getSchemas0() {
        return Tools.flatMap(getCatalogs(), c -> {
            return c.getSchemas();
        });
    }

    @Override // org.jooq.Meta
    public final List<Table<?>> getTables(String name) {
        return getTables(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final List<Table<?>> getTables(Name name) {
        initTables();
        return get(name, () -> {
            return getTables().iterator();
        }, this.cachedQualifiedTables, this.cachedUnqualifiedTables);
    }

    @Override // org.jooq.Meta
    public final List<Table<?>> getTables() {
        initTables();
        return Collections.unmodifiableList(new ArrayList(this.cachedQualifiedTables.values()));
    }

    private final void initTables() {
        if (this.cachedQualifiedTables == null) {
            this.cachedQualifiedTables = new LinkedHashMap();
            this.cachedUnqualifiedTables = new LinkedHashMap();
            get(DSL.name(""), () -> {
                return getTables0().iterator();
            }, this.cachedQualifiedTables, this.cachedUnqualifiedTables);
        }
    }

    List<Table<?>> getTables0() {
        return Tools.flatMap(getSchemas(), s -> {
            return s.getTables();
        });
    }

    @Override // org.jooq.Meta
    public final List<Domain<?>> getDomains(String name) {
        return getDomains(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final List<Domain<?>> getDomains(Name name) {
        initDomains();
        return get(name, () -> {
            return getDomains().iterator();
        }, this.cachedQualifiedDomains, this.cachedUnqualifiedDomains);
    }

    @Override // org.jooq.Meta
    public final List<Domain<?>> getDomains() {
        initDomains();
        return Collections.unmodifiableList(new ArrayList(this.cachedQualifiedDomains.values()));
    }

    private final void initDomains() {
        if (this.cachedQualifiedDomains == null) {
            this.cachedQualifiedDomains = new LinkedHashMap();
            this.cachedUnqualifiedDomains = new LinkedHashMap();
            get(DSL.name(""), () -> {
                return getDomains0().iterator();
            }, this.cachedQualifiedDomains, this.cachedUnqualifiedDomains);
        }
    }

    List<Domain<?>> getDomains0() {
        return Tools.flatMap(getSchemas(), s -> {
            return s.getDomains();
        });
    }

    @Override // org.jooq.Meta
    public final List<Sequence<?>> getSequences(String name) {
        return getSequences(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final List<Sequence<?>> getSequences(Name name) {
        initSequences();
        return get(name, () -> {
            return getSequences().iterator();
        }, this.cachedQualifiedSequences, this.cachedUnqualifiedSequences);
    }

    @Override // org.jooq.Meta
    public final List<Sequence<?>> getSequences() {
        initSequences();
        return Collections.unmodifiableList(new ArrayList(this.cachedQualifiedSequences.values()));
    }

    private final void initSequences() {
        if (this.cachedQualifiedSequences == null) {
            this.cachedQualifiedSequences = new LinkedHashMap();
            this.cachedUnqualifiedSequences = new LinkedHashMap();
            get(DSL.name(""), () -> {
                return getSequences0().iterator();
            }, this.cachedQualifiedSequences, this.cachedUnqualifiedSequences);
        }
    }

    final List<Sequence<?>> getSequences0() {
        return Tools.flatMap(getSchemas(), s -> {
            return s.getSequences();
        });
    }

    @Override // org.jooq.Meta
    public final List<UniqueKey<?>> getPrimaryKeys(String name) {
        return getPrimaryKeys(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final List<UniqueKey<?>> getPrimaryKeys(Name name) {
        initPrimaryKeys();
        return get(name, () -> {
            return getPrimaryKeys().iterator();
        }, this.cachedQualifiedPrimaryKeys, this.cachedUnqualifiedPrimaryKeys);
    }

    @Override // org.jooq.Meta
    public final List<UniqueKey<?>> getPrimaryKeys() {
        initPrimaryKeys();
        return Collections.unmodifiableList(new ArrayList(this.cachedQualifiedPrimaryKeys.values()));
    }

    private final void initPrimaryKeys() {
        if (this.cachedQualifiedPrimaryKeys == null) {
            this.cachedQualifiedPrimaryKeys = new LinkedHashMap();
            this.cachedUnqualifiedPrimaryKeys = new LinkedHashMap();
            get(DSL.name(""), () -> {
                return getPrimaryKeys0().iterator();
            }, this.cachedQualifiedPrimaryKeys, this.cachedUnqualifiedPrimaryKeys);
        }
    }

    List<UniqueKey<?>> getPrimaryKeys0() {
        List<UniqueKey<?>> result = new ArrayList<>();
        for (Table<?> table : getTables()) {
            if (table.getPrimaryKey() != null) {
                result.add(table.getPrimaryKey());
            }
        }
        return result;
    }

    @Override // org.jooq.Meta
    public final List<UniqueKey<?>> getUniqueKeys(String name) {
        return getUniqueKeys(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final List<UniqueKey<?>> getUniqueKeys(Name name) {
        initUniqueKeys();
        return get(name, () -> {
            return getUniqueKeys().iterator();
        }, this.cachedQualifiedUniqueKeys, this.cachedUnqualifiedUniqueKeys);
    }

    @Override // org.jooq.Meta
    public final List<UniqueKey<?>> getUniqueKeys() {
        initUniqueKeys();
        return Collections.unmodifiableList(new ArrayList(this.cachedQualifiedUniqueKeys.values()));
    }

    private final void initUniqueKeys() {
        if (this.cachedQualifiedUniqueKeys == null) {
            this.cachedQualifiedUniqueKeys = new LinkedHashMap();
            this.cachedUnqualifiedUniqueKeys = new LinkedHashMap();
            get(DSL.name(""), () -> {
                return getUniqueKeys0().iterator();
            }, this.cachedQualifiedUniqueKeys, this.cachedUnqualifiedUniqueKeys);
        }
    }

    List<UniqueKey<?>> getUniqueKeys0() {
        return Tools.flatMap(getTables(), t -> {
            return t.getUniqueKeys();
        });
    }

    @Override // org.jooq.Meta
    public final List<ForeignKey<?, ?>> getForeignKeys(String name) {
        return getForeignKeys(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final List<ForeignKey<?, ?>> getForeignKeys(Name name) {
        initForeignKeys();
        return get(name, () -> {
            return getForeignKeys().iterator();
        }, this.cachedQualifiedForeignKeys, this.cachedUnqualifiedForeignKeys);
    }

    @Override // org.jooq.Meta
    public final List<ForeignKey<?, ?>> getForeignKeys() {
        initForeignKeys();
        return Collections.unmodifiableList(new ArrayList(this.cachedQualifiedForeignKeys.values()));
    }

    private final void initForeignKeys() {
        if (this.cachedQualifiedForeignKeys == null) {
            this.cachedQualifiedForeignKeys = new LinkedHashMap();
            this.cachedUnqualifiedForeignKeys = new LinkedHashMap();
            get(DSL.name(""), () -> {
                return getForeignKeys0().iterator();
            }, this.cachedQualifiedForeignKeys, this.cachedUnqualifiedForeignKeys);
        }
    }

    List<ForeignKey<?, ?>> getForeignKeys0() {
        return Tools.flatMap(getTables(), t -> {
            return t.getReferences();
        });
    }

    @Override // org.jooq.Meta
    public final List<Index> getIndexes(String name) {
        return getIndexes(DSL.name(name));
    }

    @Override // org.jooq.Meta
    public final List<Index> getIndexes(Name name) {
        initIndexes();
        return get(name, () -> {
            return getIndexes().iterator();
        }, this.cachedQualifiedIndexes, this.cachedUnqualifiedIndexes);
    }

    @Override // org.jooq.Meta
    public final List<Index> getIndexes() {
        initIndexes();
        return Collections.unmodifiableList(new ArrayList(this.cachedQualifiedIndexes.values()));
    }

    private final void initIndexes() {
        if (this.cachedQualifiedIndexes == null) {
            this.cachedQualifiedIndexes = new LinkedHashMap();
            this.cachedUnqualifiedIndexes = new LinkedHashMap();
            get(DSL.name(""), () -> {
                return getIndexes0().iterator();
            }, this.cachedQualifiedIndexes, this.cachedUnqualifiedIndexes);
        }
    }

    List<Index> getIndexes0() {
        return Tools.flatMap(getTables(), t -> {
            return t.getIndexes();
        });
    }

    private final <T extends Named> List<T> get(Name name, Iterable<T> i, Map<Name, T> qualified, Map<Name, List<T>> unqualified) {
        if (qualified.isEmpty()) {
            for (T object : i) {
                Name q = object.getQualifiedName();
                Name u = object.getUnqualifiedName();
                qualified.put(q, object);
                unqualified.computeIfAbsent(u, n -> {
                    return new ArrayList();
                }).add(object);
            }
        }
        T object2 = qualified.get(name);
        if (object2 != null) {
            return Collections.singletonList(object2);
        }
        List<T> list = unqualified.get(name);
        if (list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    @Override // org.jooq.Meta
    public Meta filterCatalogs(Predicate<? super Catalog> filter) {
        return new FilteredMeta(this, filter, null, null, null, null, null, null, null, null);
    }

    @Override // org.jooq.Meta
    public Meta filterSchemas(Predicate<? super Schema> filter) {
        return new FilteredMeta(this, null, filter, null, null, null, null, null, null, null);
    }

    @Override // org.jooq.Meta
    public Meta filterTables(Predicate<? super Table<?>> filter) {
        return new FilteredMeta(this, null, null, filter, null, null, null, null, null, null);
    }

    @Override // org.jooq.Meta
    public Meta filterDomains(Predicate<? super Domain<?>> filter) {
        return new FilteredMeta(this, null, null, null, filter, null, null, null, null, null);
    }

    @Override // org.jooq.Meta
    public Meta filterSequences(Predicate<? super Sequence<?>> filter) {
        return new FilteredMeta(this, null, null, null, null, filter, null, null, null, null);
    }

    @Override // org.jooq.Meta
    public Meta filterPrimaryKeys(Predicate<? super UniqueKey<?>> filter) {
        return new FilteredMeta(this, null, null, null, null, null, filter, null, null, null);
    }

    @Override // org.jooq.Meta
    public Meta filterUniqueKeys(Predicate<? super UniqueKey<?>> filter) {
        return new FilteredMeta(this, null, null, null, null, null, null, filter, null, null);
    }

    @Override // org.jooq.Meta
    public Meta filterForeignKeys(Predicate<? super ForeignKey<?, ?>> filter) {
        return new FilteredMeta(this, null, null, null, null, null, null, null, filter, null);
    }

    @Override // org.jooq.Meta
    public Meta filterIndexes(Predicate<? super Index> filter) {
        return new FilteredMeta(this, null, null, null, null, null, null, null, null, filter);
    }

    @Override // org.jooq.Meta
    public final Meta snapshot() {
        return new Snapshot(this);
    }

    @Override // org.jooq.Meta
    public final Queries ddl() {
        return ddl(new DDLExportConfiguration());
    }

    @Override // org.jooq.Meta
    public Queries ddl(DDLExportConfiguration exportConfiguration) {
        return new DDL(dsl(), exportConfiguration).queries(this);
    }

    @Override // org.jooq.Meta
    public final Meta apply(String migration) {
        return apply(dsl().parser().parse(migration));
    }

    @Override // org.jooq.Meta
    public final Meta apply(Query... migration) {
        return apply(dsl().queries(migration));
    }

    @Override // org.jooq.Meta
    public final Meta apply(Collection<? extends Query> migration) {
        return apply(dsl().queries(migration));
    }

    @Override // org.jooq.Meta
    public final Meta apply(Queries migration) {
        return dsl().meta(ddl().concat(migration).queries());
    }

    @Override // org.jooq.Meta
    public final Queries migrateTo(Meta other) {
        return migrateTo(other, new MigrationConfiguration());
    }

    @Override // org.jooq.Meta
    public final Queries migrateTo(Meta other, MigrationConfiguration c) {
        return new Diff(configuration(), c, this, other).queries();
    }

    @Override // org.jooq.Meta
    public InformationSchema informationSchema() {
        return InformationSchemaExport.exportCatalogs(configuration(), getCatalogs());
    }

    final Table<?> lookupTable(Table<?> table) {
        Catalog c = table.getCatalog();
        Schema s = table.getSchema();
        Catalog catalog = getCatalog(c == null ? "" : c.getName());
        if (catalog == null) {
            return null;
        }
        Schema schema = catalog.getSchema(s == null ? "" : s.getName());
        if (schema == null) {
            return null;
        }
        return schema.getTable(table.getName());
    }

    final <R extends Record> UniqueKey<R> lookupKey(Table<R> in, UniqueKey<?> uk) {
        Set<?> ukFields = new HashSet<>(uk.getFields());
        return (UniqueKey) Tools.findAny(in.getKeys(), k -> {
            return ukFields.equals(new HashSet(k.getFields()));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final UniqueKey<?> lookupUniqueKey(ForeignKey<?, ?> fk) {
        Table<?> table = lookupTable(fk.getKey().getTable());
        if (table == null) {
            return null;
        }
        return lookupKey(table, fk.getKey());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> ForeignKey<R, ?> copyFK(Table<R> fkTable, UniqueKey<?> uk, ForeignKey<R, ?> oldFk) {
        Table<?> ukTable = uk.getTable();
        return Internal.createForeignKey(fkTable, oldFk.getQualifiedName(), (TableField[]) Tools.map(oldFk.getFieldsArray(), f -> {
            return (TableField) fkTable.field(f);
        }, x$0 -> {
            return new TableField[x$0];
        }), uk, (TableField[]) Tools.map(oldFk.getKeyFieldsArray(), f2 -> {
            return (TableField) ukTable.field(f2);
        }, x$02 -> {
            return new TableField[x$02];
        }), oldFk.enforced());
    }

    public int hashCode() {
        return ddl().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Meta) {
            Meta m = (Meta) obj;
            return ddl().equals(m.ddl());
        }
        return false;
    }

    public String toString() {
        return ddl().toString();
    }
}
