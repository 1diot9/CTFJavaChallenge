package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.DDLExportConfiguration;
import org.jooq.Queries;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.exception.DataAccessException;
import org.jooq.util.xml.jaxb.InformationSchema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableMetaImpl.class */
final class TableMetaImpl extends AbstractMeta {
    private final Table<?>[] tables;

    TableMetaImpl(Configuration configuration, Table<?>[] tables) {
        super(configuration);
        this.tables = tables;
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Catalog> getCatalogs0() {
        Set<Catalog> result = new LinkedHashSet<>();
        for (Table<?> table : this.tables) {
            if (table.getSchema() != null && table.getSchema().getCatalog() != null) {
                result.add(table.getSchema().getCatalog());
            }
        }
        return new ArrayList(result);
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Schema> getSchemas0() {
        Set<Schema> result = new LinkedHashSet<>();
        for (Table<?> table : this.tables) {
            if (table.getSchema() != null) {
                result.add(table.getSchema());
            }
        }
        return new ArrayList(result);
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Table<?>> getTables0() {
        return Collections.unmodifiableList(Arrays.asList(this.tables));
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<UniqueKey<?>> getPrimaryKeys0() {
        List<UniqueKey<?>> result = new ArrayList<>();
        for (Table<?> table : this.tables) {
            if (table.getPrimaryKey() != null) {
                result.add(table.getPrimaryKey());
            }
        }
        return result;
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public Queries ddl(DDLExportConfiguration exportConfiguration) throws DataAccessException {
        return new DDL(dsl(), exportConfiguration).queries(this.tables);
    }

    @Override // org.jooq.impl.AbstractMeta, org.jooq.Meta
    public InformationSchema informationSchema() throws DataAccessException {
        return InformationSchemaExport.exportTables(configuration(), getTables());
    }
}
