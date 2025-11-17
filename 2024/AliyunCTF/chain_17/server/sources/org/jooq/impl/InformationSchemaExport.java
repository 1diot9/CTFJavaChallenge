package org.jooq.impl;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Key;
import org.jooq.Param;
import org.jooq.Qualified;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.jooq.util.xml.jaxb.CheckConstraint;
import org.jooq.util.xml.jaxb.Column;
import org.jooq.util.xml.jaxb.DomainConstraint;
import org.jooq.util.xml.jaxb.IndexColumnUsage;
import org.jooq.util.xml.jaxb.InformationSchema;
import org.jooq.util.xml.jaxb.KeyColumnUsage;
import org.jooq.util.xml.jaxb.ReferentialConstraint;
import org.jooq.util.xml.jaxb.TableConstraint;
import org.jooq.util.xml.jaxb.TableConstraintType;
import org.jooq.util.xml.jaxb.TableType;
import org.jooq.util.xml.jaxb.View;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InformationSchemaExport.class */
final class InformationSchemaExport {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static final InformationSchema exportTables(Configuration configuration, List<Table<?>> tables) {
        InformationSchema result = new InformationSchema();
        Set<Catalog> includedCatalogs = new LinkedHashSet<>();
        Set<Schema> includedSchemas = new LinkedHashSet<>();
        Set<Table<?>> includedTables = new LinkedHashSet<>(tables);
        for (Table<?> t : tables) {
            if (t.getSchema() != null) {
                includedSchemas.add(t.getSchema());
            }
        }
        for (Schema s : includedSchemas) {
            if (s.getCatalog() != null) {
                includedCatalogs.add(s.getCatalog());
            }
        }
        for (Catalog c : includedCatalogs) {
            exportCatalog0(result, c);
        }
        Iterator<Schema> it = includedSchemas.iterator();
        while (it.hasNext()) {
            exportSchema0(result, it.next());
        }
        Iterator<Table<?>> it2 = tables.iterator();
        while (it2.hasNext()) {
            exportTable0(configuration, result, it2.next(), includedTables);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final InformationSchema exportSchemas(Configuration configuration, List<Schema> schemas) {
        InformationSchema result = new InformationSchema();
        Set<Catalog> includedCatalogs = new LinkedHashSet<>();
        Set<Table<?>> includedTables = new LinkedHashSet<>();
        for (Schema s : schemas) {
            if (s.getCatalog() != null) {
                includedCatalogs.add(s.getCatalog());
            }
            includedTables.addAll(s.getTables());
        }
        for (Catalog c : includedCatalogs) {
            exportCatalog0(result, c);
        }
        for (Schema s2 : schemas) {
            exportSchema0(result, s2);
            for (Domain<?> d : s2.getDomains()) {
                exportDomain0(configuration, result, d);
            }
            for (Table<?> t : s2.getTables()) {
                exportTable0(configuration, result, t, includedTables);
            }
            for (Sequence<?> q : s2.getSequences()) {
                exportSequence0(configuration, result, q);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final InformationSchema exportCatalogs(Configuration configuration, List<Catalog> catalogs) {
        InformationSchema result = new InformationSchema();
        Set<Table<?>> includedTables = new LinkedHashSet<>();
        Iterator<Catalog> it = catalogs.iterator();
        while (it.hasNext()) {
            Iterator<Schema> it2 = it.next().getSchemas().iterator();
            while (it2.hasNext()) {
                includedTables.addAll(it2.next().getTables());
            }
        }
        for (Catalog c : catalogs) {
            exportCatalog0(result, c);
            for (Schema s : c.getSchemas()) {
                exportSchema0(result, s);
                for (Domain<?> d : s.getDomains()) {
                    exportDomain0(configuration, result, d);
                }
                for (Table<?> t : s.getTables()) {
                    exportTable0(configuration, result, t, includedTables);
                }
                for (Sequence<?> q : s.getSequences()) {
                    exportSequence0(configuration, result, q);
                }
            }
        }
        return result;
    }

    private static final void exportDomain0(Configuration configuration, InformationSchema result, Domain<?> d) {
        org.jooq.util.xml.jaxb.Domain id = new org.jooq.util.xml.jaxb.Domain();
        String catalogName = catalogName(d);
        String schemaName = schemaName(d);
        String domainName = d.getName();
        if (!StringUtils.isBlank(catalogName)) {
            id.setDomainCatalog(catalogName);
        }
        if (!StringUtils.isBlank(schemaName)) {
            id.setDomainSchema(schemaName);
        }
        id.setDomainName(domainName);
        id.setDataType(d.getDataType().getTypeName(configuration));
        if (d.getDataType().lengthDefined()) {
            id.setCharacterMaximumLength(Integer.valueOf(d.getDataType().length()));
        }
        if (d.getDataType().precisionDefined()) {
            id.setNumericPrecision(Integer.valueOf(d.getDataType().precision()));
        }
        if (d.getDataType().scaleDefined()) {
            id.setNumericScale(Integer.valueOf(d.getDataType().scale()));
        }
        result.getDomains().add(id);
        for (Check<?> c : d.getChecks()) {
            DomainConstraint idc = new DomainConstraint();
            CheckConstraint icc = new CheckConstraint();
            if (!StringUtils.isBlank(catalogName)) {
                idc.setDomainCatalog(catalogName);
                idc.setConstraintCatalog(catalogName);
                icc.setConstraintCatalog(catalogName);
            }
            if (!StringUtils.isBlank(schemaName)) {
                idc.setDomainSchema(schemaName);
                idc.setConstraintSchema(schemaName);
                icc.setConstraintSchema(schemaName);
            }
            idc.setDomainName(domainName);
            idc.setConstraintName(c.getName());
            icc.setConstraintName(c.getName());
            icc.setCheckClause(configuration.dsl().render(c.condition()));
            result.getDomainConstraints().add(idc);
            result.getCheckConstraints().add(icc);
        }
    }

    private static final void exportSequence0(Configuration configuration, InformationSchema result, Sequence<?> q) {
        Object field;
        Object field2;
        Object field3;
        Object field4;
        Object field5;
        org.jooq.util.xml.jaxb.Sequence iq = new org.jooq.util.xml.jaxb.Sequence();
        String catalogName = catalogName(q);
        String schemaName = schemaName(q);
        if (!StringUtils.isBlank(catalogName)) {
            iq.setSequenceCatalog(catalogName);
        }
        if (!StringUtils.isBlank(schemaName)) {
            iq.setSequenceSchema(schemaName);
        }
        iq.setSequenceName(q.getName());
        iq.setDataType(q.getDataType().getTypeName(configuration));
        if (q.getDataType().lengthDefined()) {
            iq.setCharacterMaximumLength(Integer.valueOf(q.getDataType().length()));
        }
        if (q.getDataType().precisionDefined()) {
            iq.setNumericPrecision(Integer.valueOf(q.getDataType().precision()));
        }
        if (q.getDataType().scaleDefined()) {
            iq.setNumericScale(Integer.valueOf(q.getDataType().scale()));
        }
        if (q.getStartWith() != null) {
            Field<?> startWith = q.getStartWith();
            if (startWith instanceof Param) {
                Param<?> p = (Param) startWith;
                field5 = p.getValue();
            } else {
                field5 = q.getStartWith().toString();
            }
            iq.setStartValue((BigInteger) Convert.convert(field5, BigInteger.class));
        }
        if (q.getIncrementBy() != null) {
            Field<?> incrementBy = q.getIncrementBy();
            if (incrementBy instanceof Param) {
                Param<?> p2 = (Param) incrementBy;
                field4 = p2.getValue();
            } else {
                field4 = q.getIncrementBy().toString();
            }
            iq.setIncrement((BigInteger) Convert.convert(field4, BigInteger.class));
        }
        if (q.getMinvalue() != null) {
            Field<?> minvalue = q.getMinvalue();
            if (minvalue instanceof Param) {
                Param<?> p3 = (Param) minvalue;
                field3 = p3.getValue();
            } else {
                field3 = q.getMinvalue().toString();
            }
            iq.setMinimumValue((BigInteger) Convert.convert(field3, BigInteger.class));
        }
        if (q.getMaxvalue() != null) {
            Field<?> maxvalue = q.getMaxvalue();
            if (maxvalue instanceof Param) {
                Param<?> p4 = (Param) maxvalue;
                field2 = p4.getValue();
            } else {
                field2 = q.getMaxvalue().toString();
            }
            iq.setMaximumValue((BigInteger) Convert.convert(field2, BigInteger.class));
        }
        iq.setCycleOption(Boolean.valueOf(q.getCycle()));
        if (q.getCache() != null) {
            Field<?> cache = q.getCache();
            if (cache instanceof Param) {
                Param<?> p5 = (Param) cache;
                field = p5.getValue();
            } else {
                field = q.getCache().toString();
            }
            iq.setCache((BigInteger) Convert.convert(field, BigInteger.class));
        }
        result.getSequences().add(iq);
    }

    private static final void exportCatalog0(InformationSchema result, Catalog c) {
        org.jooq.util.xml.jaxb.Catalog ic = new org.jooq.util.xml.jaxb.Catalog();
        if (!StringUtils.isBlank(c.getName())) {
            ic.setCatalogName(c.getName());
            ic.setComment(c.getComment());
            result.getCatalogs().add(ic);
        }
    }

    private static final void exportSchema0(InformationSchema result, Schema s) {
        org.jooq.util.xml.jaxb.Schema is = new org.jooq.util.xml.jaxb.Schema();
        String catalogName = catalogName(s);
        if (!StringUtils.isBlank(catalogName)) {
            is.setCatalogName(catalogName);
        }
        if (!StringUtils.isBlank(s.getName())) {
            is.setSchemaName(s.getName());
            is.setComment(s.getComment());
            result.getSchemata().add(is);
        }
    }

    private static final void exportTable0(Configuration configuration, InformationSchema result, Table<?> t, Set<Table<?>> includedTables) {
        org.jooq.util.xml.jaxb.Table it = new org.jooq.util.xml.jaxb.Table();
        String catalogName = catalogName(t);
        String schemaName = schemaName(t);
        if (!StringUtils.isBlank(catalogName)) {
            it.setTableCatalog(catalogName);
        }
        if (!StringUtils.isBlank(schemaName)) {
            it.setTableSchema(schemaName);
        }
        switch (t.getOptions().type()) {
            case MATERIALIZED_VIEW:
                it.setTableType(TableType.MATERIALIZED_VIEW);
                break;
            case VIEW:
                it.setTableType(TableType.VIEW);
                break;
            case TEMPORARY:
                it.setTableType(TableType.GLOBAL_TEMPORARY);
                break;
            case FUNCTION:
            case TABLE:
            case EXPRESSION:
            case UNKNOWN:
            default:
                it.setTableType(TableType.BASE_TABLE);
                break;
        }
        it.setTableName(t.getName());
        it.setComment(t.getComment());
        result.getTables().add(it);
        if (t.getOptions().type() == TableOptions.TableType.VIEW) {
            View iv = new View();
            if (!StringUtils.isBlank(catalogName)) {
                iv.setTableCatalog(catalogName);
            }
            if (!StringUtils.isBlank(schemaName)) {
                iv.setTableSchema(schemaName);
            }
            iv.setTableName(t.getName());
            iv.setViewDefinition(t.getOptions().source());
            result.getViews().add(iv);
        }
        Field<?>[] fields = t.fields();
        for (int i = 0; i < fields.length; i++) {
            Field<?> f = fields[i];
            DataType<?> type = f.getDataType();
            Column ic = new Column();
            if (!StringUtils.isBlank(catalogName)) {
                ic.setTableCatalog(catalogName);
            }
            if (!StringUtils.isBlank(schemaName)) {
                ic.setTableSchema(schemaName);
            }
            ic.setTableName(t.getName());
            ic.setColumnName(f.getName());
            ic.setComment(f.getComment());
            ic.setDataType(type.getTypeName(configuration));
            if (type.lengthDefined()) {
                ic.setCharacterMaximumLength(Integer.valueOf(type.length()));
            }
            if (type.precisionDefined()) {
                ic.setNumericPrecision(Integer.valueOf(type.precision()));
            }
            if (type.scaleDefined()) {
                ic.setNumericScale(Integer.valueOf(type.scale()));
            }
            ic.setColumnDefault(DSL.using(configuration).render(type.defaultValue()));
            ic.setIsNullable(Boolean.valueOf(type.nullable()));
            ic.setOrdinalPosition(Integer.valueOf(i + 1));
            ic.setReadonly(Boolean.valueOf(type.readonly()));
            if (type.computed()) {
                ic.setIsGenerated(Boolean.valueOf(type.computed()));
                ic.setGenerationExpression(DSL.using(configuration).render(type.generatedAlwaysAs()));
                ic.setGenerationOption(type.generationOption() == QOM.GenerationOption.VIRTUAL ? "VIRTUAL" : type.generationOption() == QOM.GenerationOption.STORED ? "STORED" : null);
            }
            result.getColumns().add(ic);
        }
        for (UniqueKey<?> key : t.getKeys()) {
            exportKey0(result, t, key, key.isPrimary() ? TableConstraintType.PRIMARY_KEY : TableConstraintType.UNIQUE);
        }
        for (ForeignKey<?, ?> fk : t.getReferences()) {
            if (includedTables.contains(fk.getKey().getTable())) {
                exportKey0(result, t, fk, TableConstraintType.FOREIGN_KEY);
            }
        }
        for (Check<?> chk : t.getChecks()) {
            if (includedTables.contains(chk.getTable())) {
                exportCheck0(configuration, result, t, chk);
            }
        }
        for (Index index : t.getIndexes()) {
            exportIndex0(result, t, index);
        }
    }

    private static final void exportCheck0(Configuration configuration, InformationSchema result, Table<?> t, Check<?> chk) {
        exportTableConstraint(result, t, chk.getName(), TableConstraintType.CHECK);
        CheckConstraint c = new CheckConstraint();
        String catalogName = catalogName(t);
        String schemaName = schemaName(t);
        if (!StringUtils.isBlank(catalogName)) {
            c.setConstraintCatalog(catalogName);
        }
        if (!StringUtils.isBlank(schemaName)) {
            c.setConstraintSchema(schemaName);
        }
        c.setConstraintName(chk.getName());
        c.setCheckClause(configuration.dsl().render(chk.condition()));
        result.getCheckConstraints().add(c);
    }

    private static final void exportIndex0(InformationSchema result, Table<?> t, Index index) {
        org.jooq.util.xml.jaxb.Index i = new org.jooq.util.xml.jaxb.Index();
        String catalogName = catalogName(t);
        String schemaName = schemaName(t);
        if (!StringUtils.isBlank(catalogName)) {
            i.withIndexCatalog(catalogName).withTableCatalog(catalogName);
        }
        if (!StringUtils.isBlank(schemaName)) {
            i.withIndexSchema(schemaName).withTableSchema(schemaName);
        }
        i.setIndexName(index.getName());
        i.setTableName(t.getName());
        i.setIsUnique(Boolean.valueOf(index.getUnique()));
        result.getIndexes().add(i);
        int position = 1;
        for (SortField<?> sortField : index.getFields()) {
            IndexColumnUsage ic = new IndexColumnUsage();
            if (!StringUtils.isBlank(catalogName)) {
                ic.withIndexCatalog(catalogName).withTableCatalog(catalogName);
            }
            if (!StringUtils.isBlank(schemaName)) {
                ic.withIndexSchema(schemaName).withTableSchema(schemaName);
            }
            ic.setIndexName(index.getName());
            ic.setTableName(t.getName());
            int i2 = position;
            position++;
            ic.setOrdinalPosition(i2);
            ic.setColumnName(sortField.getName());
            ic.setIsDescending(Boolean.valueOf(sortField.getOrder() == SortOrder.DESC));
            result.getIndexColumnUsages().add(ic);
        }
    }

    private static final void exportKey0(InformationSchema result, Table<?> t, Key<?> key, TableConstraintType constraintType) {
        exportTableConstraint(result, t, key.getName(), constraintType);
        String catalogName = catalogName(t);
        String schemaName = schemaName(t);
        int i = 0;
        for (Field<?> f : key.getFields()) {
            KeyColumnUsage kc = new KeyColumnUsage();
            if (!StringUtils.isBlank(catalogName)) {
                kc.setConstraintCatalog(catalogName);
                kc.setTableCatalog(catalogName);
            }
            if (!StringUtils.isBlank(schemaName)) {
                kc.setConstraintSchema(schemaName);
                kc.setTableSchema(schemaName);
            }
            kc.setColumnName(f.getName());
            kc.setTableName(t.getName());
            i++;
            kc.setOrdinalPosition(i);
            kc.setConstraintName(key.getName());
            result.getKeyColumnUsages().add(kc);
        }
        if (constraintType == TableConstraintType.FOREIGN_KEY) {
            ReferentialConstraint rc = new ReferentialConstraint();
            UniqueKey<?> uk = ((ForeignKey) key).getKey();
            String ukCatalogName = catalogName(uk.getTable());
            String ukSchemaName = schemaName(uk.getTable());
            if (!StringUtils.isBlank(catalogName)) {
                rc.setConstraintCatalog(catalogName);
            }
            if (!StringUtils.isBlank(ukCatalogName)) {
                rc.setUniqueConstraintCatalog(ukCatalogName);
            }
            if (!StringUtils.isBlank(schemaName)) {
                rc.setConstraintSchema(schemaName);
            }
            if (!StringUtils.isBlank(ukSchemaName)) {
                rc.setUniqueConstraintSchema(ukSchemaName);
            }
            rc.setConstraintName(key.getName());
            rc.setUniqueConstraintName(uk.getName());
            result.getReferentialConstraints().add(rc);
        }
    }

    private static final void exportTableConstraint(InformationSchema result, Table<?> t, String constraintName, TableConstraintType constraintType) {
        TableConstraint tc = new TableConstraint();
        String catalogName = catalogName(t);
        String schemaName = schemaName(t);
        tc.setConstraintName(constraintName);
        tc.setConstraintType(constraintType);
        if (!StringUtils.isBlank(catalogName)) {
            tc.withConstraintCatalog(catalogName).withTableCatalog(catalogName);
        }
        if (!StringUtils.isBlank(schemaName)) {
            tc.withConstraintSchema(schemaName).withTableSchema(schemaName);
        }
        tc.setTableName(t.getName());
        result.getTableConstraints().add(tc);
    }

    private static final String catalogName(Schema s) {
        if (s.getCatalog() == null) {
            return null;
        }
        return s.getCatalog().getName();
    }

    private static final String catalogName(Qualified q) {
        if (q.getCatalog() == null) {
            return null;
        }
        return q.getCatalog().getName();
    }

    private static final String schemaName(Qualified q) {
        if (q.getSchema() == null) {
            return null;
        }
        return q.getSchema().getName();
    }

    private InformationSchemaExport() {
    }
}
