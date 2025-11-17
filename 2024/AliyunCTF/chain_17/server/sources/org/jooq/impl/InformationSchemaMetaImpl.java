package org.jooq.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.SortField;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.jooq.util.xml.jaxb.CheckConstraint;
import org.jooq.util.xml.jaxb.Column;
import org.jooq.util.xml.jaxb.Domain;
import org.jooq.util.xml.jaxb.DomainConstraint;
import org.jooq.util.xml.jaxb.Index;
import org.jooq.util.xml.jaxb.IndexColumnUsage;
import org.jooq.util.xml.jaxb.InformationSchema;
import org.jooq.util.xml.jaxb.KeyColumnUsage;
import org.jooq.util.xml.jaxb.ReferentialConstraint;
import org.jooq.util.xml.jaxb.Table;
import org.jooq.util.xml.jaxb.TableConstraint;
import org.jooq.util.xml.jaxb.TableConstraintType;
import org.jooq.util.xml.jaxb.View;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InformationSchemaMetaImpl.class */
final class InformationSchemaMetaImpl extends AbstractMeta {
    private final List<Catalog> catalogs;
    private final Map<Name, Catalog> catalogsByName;
    private final List<Schema> schemas;
    private final Map<Name, Schema> schemasByName;
    private final Map<Catalog, List<Schema>> schemasPerCatalog;
    private final List<InformationSchemaTable> tables;
    private final Map<Name, InformationSchemaTable> tablesByName;
    private final Map<Schema, List<InformationSchemaTable>> tablesPerSchema;
    private final List<InformationSchemaDomain<?>> domains;
    private final Map<Name, InformationSchemaDomain<?>> domainsByName;
    private final Map<Schema, List<InformationSchemaDomain<?>>> domainsPerSchema;
    private final List<Sequence<?>> sequences;
    private final Map<Schema, List<Sequence<?>>> sequencesPerSchema;
    private final List<UniqueKeyImpl<Record>> primaryKeys;
    private final Map<Name, UniqueKeyImpl<Record>> keysByName;
    private final Map<Name, Name> referentialKeys;
    private final Map<Name, IndexImpl> indexesByName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InformationSchemaMetaImpl(Configuration configuration, InformationSchema source) {
        super(configuration);
        this.catalogs = new ArrayList();
        this.catalogsByName = new HashMap();
        this.schemas = new ArrayList();
        this.schemasByName = new HashMap();
        this.schemasPerCatalog = new HashMap();
        this.tables = new ArrayList();
        this.tablesByName = new HashMap();
        this.tablesPerSchema = new HashMap();
        this.domains = new ArrayList();
        this.domainsByName = new HashMap();
        this.domainsPerSchema = new HashMap();
        this.sequences = new ArrayList();
        this.sequencesPerSchema = new HashMap();
        this.primaryKeys = new ArrayList();
        this.keysByName = new HashMap();
        this.referentialKeys = new HashMap();
        this.indexesByName = new HashMap();
        init(source);
    }

    private final void init(InformationSchema meta) {
        Field<?> field;
        QOM.GenerationOption generationOption;
        TableOptions.TableType tableType;
        List<String> errors = new ArrayList<>();
        boolean hasCatalogs = false;
        for (org.jooq.util.xml.jaxb.Catalog xc : meta.getCatalogs()) {
            InformationSchemaCatalog ic = new InformationSchemaCatalog(xc.getCatalogName(), xc.getComment());
            this.catalogs.add(ic);
            this.catalogsByName.put(DSL.name(xc.getCatalogName()), ic);
            hasCatalogs = true;
        }
        for (org.jooq.util.xml.jaxb.Schema xs : meta.getSchemata()) {
            if (!hasCatalogs) {
                InformationSchemaCatalog ic2 = new InformationSchemaCatalog(xs.getCatalogName(), null);
                if (!this.catalogs.contains(ic2)) {
                    this.catalogs.add(ic2);
                    this.catalogsByName.put(DSL.name(xs.getCatalogName()), ic2);
                }
            }
            Name catalogName = DSL.name(xs.getCatalogName());
            Catalog catalog = this.catalogsByName.get(catalogName);
            if (catalog == null) {
                errors.add("Catalog " + String.valueOf(catalogName) + " not defined for schema " + xs.getSchemaName());
            } else {
                InformationSchemaSchema is = new InformationSchemaSchema(xs.getSchemaName(), catalog, xs.getComment());
                this.schemas.add(is);
                this.schemasByName.put(DSL.name(xs.getCatalogName(), xs.getSchemaName()), is);
            }
        }
        for (Domain d : meta.getDomains()) {
            Name schemaName = DSL.name(d.getDomainCatalog(), d.getDomainSchema());
            Schema schema = this.schemasByName.get(schemaName);
            if (schema == null) {
                errors.add("Schema " + String.valueOf(schemaName) + " not defined for domain " + d.getDomainName());
            } else {
                Name domainName = DSL.name(d.getDomainCatalog(), d.getDomainSchema(), d.getDomainName());
                int length = d.getCharacterMaximumLength() == null ? 0 : d.getCharacterMaximumLength().intValue();
                int precision = d.getNumericPrecision() == null ? 0 : d.getNumericPrecision().intValue();
                int scale = d.getNumericScale() == null ? 0 : d.getNumericScale().intValue();
                List<Check<?>> checks = new ArrayList<>();
                for (DomainConstraint dc : meta.getDomainConstraints()) {
                    if (domainName.equals(DSL.name(dc.getDomainCatalog(), dc.getDomainSchema(), dc.getDomainName()))) {
                        Name constraintName = DSL.name(dc.getConstraintCatalog(), dc.getConstraintSchema(), dc.getConstraintName());
                        for (CheckConstraint cc : meta.getCheckConstraints()) {
                            if (constraintName.equals(DSL.name(cc.getConstraintCatalog(), cc.getConstraintSchema(), cc.getConstraintName()))) {
                                checks.add(new CheckImpl<>(null, constraintName, DSL.condition(cc.getCheckClause()), true));
                            }
                        }
                    }
                }
                InformationSchemaDomain<?> id = new InformationSchemaDomain<>(schema, DSL.name(d.getDomainName()), type(d.getDataType(), length, precision, scale, true, false, null, null), (Check[]) checks.toArray(Tools.EMPTY_CHECK));
                this.domains.add(id);
                this.domainsByName.put(domainName, id);
            }
        }
        for (Table xt : meta.getTables()) {
            Name schemaName2 = DSL.name(xt.getTableCatalog(), xt.getTableSchema());
            Schema schema2 = this.schemasByName.get(schemaName2);
            if (schema2 == null) {
                errors.add("Schema " + String.valueOf(schemaName2) + " not defined for table " + xt.getTableName());
            } else {
                switch (xt.getTableType()) {
                    case GLOBAL_TEMPORARY:
                        tableType = TableOptions.TableType.TEMPORARY;
                        break;
                    case VIEW:
                        tableType = TableOptions.TableType.VIEW;
                        break;
                    case BASE_TABLE:
                    default:
                        tableType = TableOptions.TableType.TABLE;
                        break;
                }
                String sql = null;
                if (tableType == TableOptions.TableType.VIEW) {
                    Iterator<View> it = meta.getViews().iterator();
                    while (true) {
                        if (it.hasNext()) {
                            View vt = it.next();
                            if (StringUtils.equals((String) StringUtils.defaultIfNull(xt.getTableCatalog(), ""), (String) StringUtils.defaultIfNull(vt.getTableCatalog(), "")) && StringUtils.equals((String) StringUtils.defaultIfNull(xt.getTableSchema(), ""), (String) StringUtils.defaultIfNull(vt.getTableSchema(), "")) && StringUtils.equals((String) StringUtils.defaultIfNull(xt.getTableName(), ""), (String) StringUtils.defaultIfNull(vt.getTableName(), ""))) {
                                sql = vt.getViewDefinition();
                            }
                        }
                    }
                }
                InformationSchemaTable it2 = new InformationSchemaTable(xt.getTableName(), schema2, xt.getComment(), tableType, sql);
                this.tables.add(it2);
                this.tablesByName.put(DSL.name(xt.getTableCatalog(), xt.getTableSchema(), xt.getTableName()), it2);
            }
        }
        List<Column> columns = new ArrayList<>(meta.getColumns());
        columns.sort((o1, o2) -> {
            Integer p1 = o1.getOrdinalPosition();
            Integer p2 = o2.getOrdinalPosition();
            if (Objects.equals(p1, p2)) {
                return 0;
            }
            if (p1 == null) {
                return -1;
            }
            if (p2 == null) {
                return 1;
            }
            return p1.compareTo(p2);
        });
        for (Column xc2 : columns) {
            String typeName = xc2.getDataType();
            int length2 = xc2.getCharacterMaximumLength() == null ? 0 : xc2.getCharacterMaximumLength().intValue();
            int precision2 = xc2.getNumericPrecision() == null ? 0 : xc2.getNumericPrecision().intValue();
            int scale2 = xc2.getNumericScale() == null ? 0 : xc2.getNumericScale().intValue();
            boolean nullable = !Boolean.FALSE.equals(xc2.isIsNullable());
            boolean readonly = Boolean.TRUE.equals(xc2.isReadonly());
            if (Boolean.TRUE.equals(xc2.isIsGenerated())) {
                field = DSL.field(xc2.getGenerationExpression());
            } else {
                field = null;
            }
            Field<?> generatedAlwaysAs = field;
            if (Boolean.TRUE.equals(xc2.isIsGenerated())) {
                if ("STORED".equalsIgnoreCase(xc2.getGenerationOption())) {
                    generationOption = QOM.GenerationOption.STORED;
                } else if ("VIRTUAL".equalsIgnoreCase(xc2.getGenerationOption())) {
                    generationOption = QOM.GenerationOption.VIRTUAL;
                } else {
                    generationOption = null;
                }
            } else {
                generationOption = null;
            }
            QOM.GenerationOption generationOption2 = generationOption;
            Name tableName = DSL.name(xc2.getTableCatalog(), xc2.getTableSchema(), xc2.getTableName());
            InformationSchemaTable table = this.tablesByName.get(tableName);
            if (table == null) {
                errors.add("Table " + String.valueOf(tableName) + " not defined for column " + xc2.getColumnName());
            } else {
                AbstractTable.createField(DSL.name(xc2.getColumnName()), type(typeName, length2, precision2, scale2, nullable, readonly, generatedAlwaysAs, generationOption2), table, xc2.getComment());
            }
        }
        Map<Name, List<SortField<?>>> columnsByIndex = new HashMap<>();
        List<IndexColumnUsage> indexColumnUsages = new ArrayList<>(meta.getIndexColumnUsages());
        indexColumnUsages.sort(Comparator.comparingInt((v0) -> {
            return v0.getOrdinalPosition();
        }));
        for (IndexColumnUsage ic3 : indexColumnUsages) {
            Name indexName = DSL.name(ic3.getIndexCatalog(), ic3.getIndexSchema(), ic3.getTableName(), ic3.getIndexName());
            List list = (List) columnsByIndex.computeIfAbsent(indexName, k -> {
                return new ArrayList();
            });
            Name tableName2 = DSL.name(ic3.getTableCatalog(), ic3.getTableSchema(), ic3.getTableName());
            InformationSchemaTable table2 = this.tablesByName.get(tableName2);
            if (table2 == null) {
                errors.add("Table " + String.valueOf(tableName2) + " not defined for index " + String.valueOf(indexName));
            } else {
                TableField<Record, ?> field2 = (TableField) table2.field(ic3.getColumnName());
                if (field2 == null) {
                    errors.add("Column " + ic3.getColumnName() + " not defined for table " + String.valueOf(tableName2));
                } else {
                    list.add(Boolean.TRUE.equals(ic3.isIsDescending()) ? field2.desc() : field2.asc());
                }
            }
        }
        for (Index i : meta.getIndexes()) {
            Name tableName3 = DSL.name(i.getTableCatalog(), i.getTableSchema(), i.getTableName());
            Name indexName2 = DSL.name(i.getIndexCatalog(), i.getIndexSchema(), i.getTableName(), i.getIndexName());
            InformationSchemaTable table3 = this.tablesByName.get(tableName3);
            if (table3 == null) {
                errors.add("Table " + String.valueOf(tableName3) + " not defined for index " + String.valueOf(indexName2));
            } else {
                List<SortField<?>> c = columnsByIndex.get(indexName2);
                if (c == null || c.isEmpty()) {
                    errors.add("No columns defined for index " + String.valueOf(indexName2));
                } else {
                    IndexImpl index = (IndexImpl) Internal.createIndex(i.getIndexName(), table3, (OrderField<?>[]) c.toArray(Tools.EMPTY_SORTFIELD), Boolean.TRUE.equals(i.isIsUnique()));
                    table3.indexes.add(index);
                    this.indexesByName.put(indexName2, index);
                }
            }
        }
        Map<Name, List<TableField<Record, ?>>> columnsByConstraint = new HashMap<>();
        List<KeyColumnUsage> keyColumnUsages = new ArrayList<>(meta.getKeyColumnUsages());
        keyColumnUsages.sort(Comparator.comparing((v0) -> {
            return v0.getOrdinalPosition();
        }));
        for (KeyColumnUsage xc3 : keyColumnUsages) {
            Name constraintName2 = DSL.name(xc3.getConstraintCatalog(), xc3.getConstraintSchema(), xc3.getConstraintName());
            List<TableField<Record, ?>> fields = columnsByConstraint.computeIfAbsent(constraintName2, k2 -> {
                return new ArrayList();
            });
            Name tableName4 = DSL.name(xc3.getTableCatalog(), xc3.getTableSchema(), xc3.getTableName());
            InformationSchemaTable table4 = this.tablesByName.get(tableName4);
            if (table4 == null) {
                errors.add("Table " + String.valueOf(tableName4) + " not defined for constraint " + String.valueOf(constraintName2));
            } else {
                TableField<Record, ?> field3 = (TableField) table4.field(xc3.getColumnName());
                if (field3 == null) {
                    errors.add("Column " + xc3.getColumnName() + " not defined for table " + String.valueOf(tableName4));
                } else {
                    fields.add(field3);
                }
            }
        }
        for (TableConstraint xc4 : meta.getTableConstraints()) {
            switch (xc4.getConstraintType()) {
                case PRIMARY_KEY:
                case UNIQUE:
                    Name tableName5 = DSL.name(xc4.getTableCatalog(), xc4.getTableSchema(), xc4.getTableName());
                    Name constraintName3 = DSL.name(xc4.getConstraintCatalog(), xc4.getConstraintSchema(), xc4.getConstraintName());
                    InformationSchemaTable table5 = this.tablesByName.get(tableName5);
                    if (table5 == null) {
                        errors.add("Table " + String.valueOf(tableName5) + " not defined for constraint " + String.valueOf(constraintName3));
                        break;
                    } else {
                        List<TableField<Record, ?>> c2 = columnsByConstraint.get(constraintName3);
                        if (c2 == null || c2.isEmpty()) {
                            errors.add("No columns defined for constraint " + String.valueOf(constraintName3));
                            break;
                        } else {
                            UniqueKeyImpl<Record> key = (UniqueKeyImpl) Internal.createUniqueKey(table5, xc4.getConstraintName(), (TableField[]) c2.toArray(new TableField[0]));
                            if (xc4.getConstraintType() == TableConstraintType.PRIMARY_KEY) {
                                table5.primaryKey = key;
                                this.primaryKeys.add(key);
                            } else {
                                table5.uniqueKeys.add(key);
                            }
                            this.keysByName.put(constraintName3, key);
                            break;
                        }
                    }
                    break;
            }
        }
        for (ReferentialConstraint xr : meta.getReferentialConstraints()) {
            this.referentialKeys.put(DSL.name(xr.getConstraintCatalog(), xr.getConstraintSchema(), xr.getConstraintName()), DSL.name(xr.getUniqueConstraintCatalog(), xr.getUniqueConstraintSchema(), xr.getUniqueConstraintName()));
        }
        for (TableConstraint xc5 : meta.getTableConstraints()) {
            switch (xc5.getConstraintType()) {
                case FOREIGN_KEY:
                    Name tableName6 = DSL.name(xc5.getTableCatalog(), xc5.getTableSchema(), xc5.getTableName());
                    Name constraintName4 = DSL.name(xc5.getConstraintCatalog(), xc5.getConstraintSchema(), xc5.getConstraintName());
                    InformationSchemaTable table6 = this.tablesByName.get(tableName6);
                    if (table6 == null) {
                        errors.add("Table " + String.valueOf(tableName6) + " not defined for constraint " + String.valueOf(constraintName4));
                        break;
                    } else {
                        List<TableField<Record, ?>> c3 = columnsByConstraint.get(constraintName4);
                        if (c3 == null || c3.isEmpty()) {
                            errors.add("No columns defined for constraint " + String.valueOf(constraintName4));
                            break;
                        } else {
                            UniqueKeyImpl<Record> uniqueKey = this.keysByName.get(this.referentialKeys.get(constraintName4));
                            if (uniqueKey == null) {
                                errors.add("No unique key defined for foreign key " + String.valueOf(constraintName4));
                                break;
                            } else {
                                table6.foreignKeys.add(Internal.createForeignKey(uniqueKey, table6, xc5.getConstraintName(), (TableField[]) c3.toArray(new TableField[0])));
                                break;
                            }
                        }
                    }
            }
        }
        for (TableConstraint xc6 : meta.getTableConstraints()) {
            switch (xc6.getConstraintType()) {
                case CHECK:
                    Name tableName7 = DSL.name(xc6.getTableCatalog(), xc6.getTableSchema(), xc6.getTableName());
                    Name constraintName5 = DSL.name(xc6.getConstraintCatalog(), xc6.getConstraintSchema(), xc6.getConstraintName());
                    InformationSchemaTable table7 = this.tablesByName.get(tableName7);
                    if (table7 == null) {
                        errors.add("Table " + String.valueOf(tableName7) + " not defined for constraint " + String.valueOf(constraintName5));
                        break;
                    } else {
                        Iterator<CheckConstraint> it3 = meta.getCheckConstraints().iterator();
                        while (true) {
                            if (it3.hasNext()) {
                                CheckConstraint cc2 = it3.next();
                                if (constraintName5.equals(DSL.name(cc2.getConstraintCatalog(), cc2.getConstraintSchema(), cc2.getConstraintName()))) {
                                    table7.checks.add(new CheckImpl(table7, constraintName5, DSL.condition(cc2.getCheckClause()), true));
                                    break;
                                }
                            } else {
                                errors.add("No check clause found for check constraint " + String.valueOf(constraintName5));
                                break;
                            }
                        }
                    }
            }
        }
        for (org.jooq.util.xml.jaxb.Sequence xs2 : meta.getSequences()) {
            Name schemaName3 = DSL.name(xs2.getSequenceCatalog(), xs2.getSequenceSchema());
            Schema schema3 = this.schemasByName.get(schemaName3);
            if (schema3 == null) {
                errors.add("Schema " + String.valueOf(schemaName3) + " not defined for sequence " + xs2.getSequenceName());
            } else {
                String typeName2 = xs2.getDataType();
                int length3 = xs2.getCharacterMaximumLength() == null ? 0 : xs2.getCharacterMaximumLength().intValue();
                int precision3 = xs2.getNumericPrecision() == null ? 0 : xs2.getNumericPrecision().intValue();
                int scale3 = xs2.getNumericScale() == null ? 0 : xs2.getNumericScale().intValue();
                BigInteger startWith = xs2.getStartValue();
                BigInteger incrementBy = xs2.getIncrement();
                BigInteger minvalue = xs2.getMinimumValue();
                BigInteger maxvalue = xs2.getMaximumValue();
                Boolean cycle = xs2.isCycleOption();
                BigInteger cache = xs2.getCache();
                this.sequences.add(new InformationSchemaSequence(xs2.getSequenceName(), schema3, type(typeName2, length3, precision3, scale3, true, false, null, null), startWith, incrementBy, minvalue, maxvalue, cycle, cache));
            }
        }
        for (Schema s : this.schemas) {
            initLookup(this.schemasPerCatalog, s.getCatalog(), s);
        }
        for (InformationSchemaDomain<?> d2 : this.domains) {
            initLookup(this.domainsPerSchema, d2.getSchema(), d2);
        }
        for (InformationSchemaTable t : this.tables) {
            initLookup(this.tablesPerSchema, t.getSchema(), t);
        }
        for (Sequence<?> s2 : this.sequences) {
            initLookup(this.sequencesPerSchema, s2.getSchema(), s2);
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
    }

    private final <K, V> void initLookup(Map<K, List<V>> lookup, K key, V value) {
        lookup.computeIfAbsent(key, k -> {
            return new ArrayList();
        }).add(value);
    }

    private final DataType<?> type(String typeName, int length, int precision, int scale, boolean nullable, boolean readonly, Field<?> generatedAlwaysAs, QOM.GenerationOption generationOption) {
        DataType<?> type;
        try {
            DataType<?> type2 = DefaultDataType.getDataType(this.configuration.family(), typeName);
            type = type2.nullable(nullable).readonly(readonly);
            if (length != 0) {
                type = type.length(length);
            } else if (precision != 0 || scale != 0) {
                type = type.precision(precision, scale);
            }
            if (generatedAlwaysAs != null) {
                type = type.generatedAlwaysAs(generatedAlwaysAs);
            }
            if (generationOption != null) {
                type = type.generationOption(generationOption);
            }
        } catch (SQLDialectNotSupportedException e) {
            type = SQLDataType.OTHER;
        }
        return type;
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Catalog> getCatalogs0() {
        return this.catalogs;
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Schema> getSchemas0() {
        return this.schemas;
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<org.jooq.Table<?>> getTables0() {
        return this.tables;
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<org.jooq.Domain<?>> getDomains0() {
        return this.domains;
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<UniqueKey<?>> getPrimaryKeys0() {
        return this.primaryKeys;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InformationSchemaMetaImpl$InformationSchemaCatalog.class */
    public final class InformationSchemaCatalog extends CatalogImpl {
        InformationSchemaCatalog(String name, String comment) {
            super(name, comment);
        }

        @Override // org.jooq.impl.CatalogImpl, org.jooq.Catalog
        public final List<Schema> getSchemas() {
            return InformationSchemaMetaImpl.unmodifiableList(InformationSchemaMetaImpl.this.schemasPerCatalog.get(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InformationSchemaMetaImpl$InformationSchemaSchema.class */
    public final class InformationSchemaSchema extends SchemaImpl {
        InformationSchemaSchema(String name, Catalog catalog, String comment) {
            super(name, catalog, comment);
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<org.jooq.Domain<?>> getDomains() {
            return InformationSchemaMetaImpl.unmodifiableList(InformationSchemaMetaImpl.this.domainsPerSchema.get(this));
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<org.jooq.Table<?>> getTables() {
            return InformationSchemaMetaImpl.unmodifiableList(InformationSchemaMetaImpl.this.tablesPerSchema.get(this));
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final List<Sequence<?>> getSequences() {
            return InformationSchemaMetaImpl.unmodifiableList(InformationSchemaMetaImpl.this.sequencesPerSchema.get(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InformationSchemaMetaImpl$InformationSchemaTable.class */
    public static final class InformationSchemaTable extends TableImpl<Record> {
        UniqueKey<Record> primaryKey;
        final List<UniqueKey<Record>> uniqueKeys;
        final List<ForeignKey<Record, Record>> foreignKeys;
        final List<Check<Record>> checks;
        final List<org.jooq.Index> indexes;

        InformationSchemaTable(String name, Schema schema, String comment, TableOptions.TableType tableType, String source) {
            super(DSL.name(name), schema, null, null, DSL.comment(comment), tableType == TableOptions.TableType.VIEW ? TableOptions.view(source) : TableOptions.of(tableType));
            this.uniqueKeys = new ArrayList();
            this.foreignKeys = new ArrayList();
            this.checks = new ArrayList();
            this.indexes = new ArrayList();
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public List<org.jooq.Index> getIndexes() {
            return InformationSchemaMetaImpl.unmodifiableList(this.indexes);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public UniqueKey<Record> getPrimaryKey() {
            return this.primaryKey;
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public List<UniqueKey<Record>> getUniqueKeys() {
            return InformationSchemaMetaImpl.unmodifiableList(this.uniqueKeys);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public List<ForeignKey<Record, ?>> getReferences() {
            return InformationSchemaMetaImpl.unmodifiableList(this.foreignKeys);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public List<Check<Record>> getChecks() {
            return InformationSchemaMetaImpl.unmodifiableList(this.checks);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InformationSchemaMetaImpl$InformationSchemaDomain.class */
    public static final class InformationSchemaDomain<T> extends DomainImpl<T> {
        InformationSchemaDomain(Schema schema, Name name, DataType<T> type, Check<?>[] checks) {
            super(schema, name, type, checks);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InformationSchemaMetaImpl$InformationSchemaSequence.class */
    public static final class InformationSchemaSequence<N extends Number> extends SequenceImpl<N> {
        InformationSchemaSequence(String name, Schema schema, DataType<N> type, Number startWith, Number incrementBy, Number minvalue, Number maxvalue, Boolean cycle, Number cache) {
            super(DSL.name(name), schema, type, false, startWith != null ? Tools.field(startWith, type) : null, incrementBy != null ? Tools.field(incrementBy, type) : null, minvalue != null ? Tools.field(minvalue, type) : null, maxvalue != null ? Tools.field(maxvalue, type) : null, Boolean.TRUE.equals(cycle), cache != null ? Tools.field(cache, type) : null);
        }
    }

    private static final <T> List<T> unmodifiableList(List<? extends T> list) {
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }
}
