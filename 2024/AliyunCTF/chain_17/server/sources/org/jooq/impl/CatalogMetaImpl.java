package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Meta;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Schema;
import org.jooq.Table;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CatalogMetaImpl.class */
final class CatalogMetaImpl extends AbstractMeta {
    private final Catalog[] catalogs;

    private CatalogMetaImpl(Configuration configuration, Catalog[] catalogs) {
        super(configuration);
        this.catalogs = catalogs;
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Catalog> getCatalogs0() {
        return Arrays.asList(this.catalogs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Meta filterCatalogs(Configuration configuration, Catalog[] catalogs) {
        return filterCatalogs0(configuration, catalogs, new LinkedHashSet(Arrays.asList(catalogs)));
    }

    static final Meta filterCatalogs(Configuration configuration, Set<Catalog> catalogs) {
        return filterCatalogs0(configuration, (Catalog[]) catalogs.toArray(Tools.EMPTY_CATALOG), catalogs);
    }

    private static final Meta filterCatalogs0(Configuration configuration, Catalog[] array, Set<Catalog> set) {
        CatalogMetaImpl catalogMetaImpl = new CatalogMetaImpl(configuration, array);
        Objects.requireNonNull(set);
        return catalogMetaImpl.filterCatalogs((v1) -> {
            return r1.contains(v1);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Meta filterSchemas(Configuration configuration, Schema[] schemas) {
        return filterSchemas(configuration, new LinkedHashSet(Arrays.asList(schemas)));
    }

    static final Meta filterSchemas(Configuration configuration, Set<Schema> schemas) {
        Map<Name, Catalog> c = new LinkedHashMap<>();
        Map<Name, List<Schema>> mapping = new LinkedHashMap<>();
        for (Schema schema : schemas) {
            mapping.computeIfAbsent(AbstractNamed.nameOrDefault(schema.getCatalog()), k -> {
                return new ArrayList();
            }).add(schema);
        }
        Iterator<Schema> it = schemas.iterator();
        while (it.hasNext()) {
            c.computeIfAbsent(AbstractNamed.nameOrDefault(it.next().getCatalog()), k2 -> {
                return new CatalogImpl(k2) { // from class: org.jooq.impl.CatalogMetaImpl.1
                    @Override // org.jooq.impl.CatalogImpl, org.jooq.Catalog
                    public List<Schema> getSchemas() {
                        return (List) mapping.get(getQualifiedName());
                    }
                };
            });
        }
        Meta filterCatalogs = filterCatalogs(configuration, new LinkedHashSet(c.values()));
        Objects.requireNonNull(schemas);
        return filterCatalogs.filterSchemas((v1) -> {
            return r1.contains(v1);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Meta filterTables(Configuration configuration, Table<?>[] tables) {
        return filterTables(configuration, new LinkedHashSet(Arrays.asList(tables)));
    }

    static final Meta filterTables(Configuration configuration, Set<Table<?>> tables) {
        Map<Name, Schema> s = new LinkedHashMap<>();
        Map<Name, List<Table<?>>> mapping = new LinkedHashMap<>();
        for (Table<?> table : tables) {
            mapping.computeIfAbsent(AbstractNamed.nameOrDefault(table.getCatalog()).append(AbstractNamed.nameOrDefault(table.getSchema())), k -> {
                return new ArrayList();
            }).add(table);
        }
        for (Table<?> table2 : tables) {
            s.computeIfAbsent(AbstractNamed.nameOrDefault(table2.getCatalog()).append(AbstractNamed.nameOrDefault(table2.getSchema())), k2 -> {
                return new SchemaImpl(k2, table2.getCatalog()) { // from class: org.jooq.impl.CatalogMetaImpl.2
                    @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
                    public List<Table<?>> getTables() {
                        return (List) mapping.get(getQualifiedName());
                    }
                };
            });
        }
        Meta filterSchemas = filterSchemas(configuration, new LinkedHashSet(s.values()));
        Objects.requireNonNull(tables);
        return filterSchemas.filterTables((v1) -> {
            return r1.contains(v1);
        }).filterSequences(none()).filterDomains(none());
    }

    static final <Q extends QueryPart> Predicate<Q> none() {
        return t -> {
            return false;
        };
    }
}
