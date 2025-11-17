package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.jooq.Check;
import org.jooq.Comment;
import org.jooq.Constraint;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.CreateDomainAsStep;
import org.jooq.CreateDomainConstraintStep;
import org.jooq.CreateDomainDefaultStep;
import org.jooq.CreateIndexIncludeStep;
import org.jooq.CreateIndexStep;
import org.jooq.CreateSequenceFlagsStep;
import org.jooq.CreateTableElementListStep;
import org.jooq.CreateTableOnCommitStep;
import org.jooq.CreateViewAsStep;
import org.jooq.DDLExportConfiguration;
import org.jooq.DDLFlag;
import org.jooq.DSLContext;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Key;
import org.jooq.Meta;
import org.jooq.Named;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DDL.class */
public final class DDL {
    private final DSLContext ctx;
    private final DDLExportConfiguration configuration;
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) DDL.class);
    static final Pattern P_CREATE_VIEW = Pattern.compile("^(?i:\\bcreate\\b.*?\\bview\\b.*?\\bas\\b\\s+)(.*)$");

    /* JADX INFO: Access modifiers changed from: package-private */
    public DDL(DSLContext ctx, DDLExportConfiguration configuration) {
        this.ctx = ctx;
        this.configuration = configuration;
    }

    final List<Query> createTableOrView(Table<?> table, Collection<? extends Constraint> constraints) {
        CreateTableElementListStep createTable;
        CreateViewAsStep<Record> createView;
        boolean temporary = table.getTableType() == TableOptions.TableType.TEMPORARY;
        boolean materialized = table.getTableType() == TableOptions.TableType.MATERIALIZED_VIEW;
        boolean view = table.getTableType().isView();
        TableOptions.OnCommit onCommit = table.getOptions().onCommit();
        if (view) {
            List<Query> result = new ArrayList<>();
            if (materialized) {
                if (this.configuration.createMaterializedViewIfNotExists()) {
                    createView = this.ctx.createMaterializedViewIfNotExists(table, table.fields());
                } else if (this.configuration.createOrReplaceMaterializedView()) {
                    createView = this.ctx.createOrReplaceMaterializedView(table, table.fields());
                } else {
                    createView = this.ctx.createMaterializedView(table, table.fields());
                }
            } else if (this.configuration.createViewIfNotExists()) {
                createView = this.ctx.createViewIfNotExists(table, table.fields());
            } else if (this.configuration.createOrReplaceView()) {
                createView = this.ctx.createOrReplaceView(table, table.fields());
            } else {
                createView = this.ctx.createView(table, table.fields());
            }
            result.add(applyAs(createView, table.getOptions()));
            if (!constraints.isEmpty() && this.configuration.includeConstraintsOnViews()) {
                result.addAll(alterTableAddConstraints(table));
            }
            return result;
        }
        if (this.configuration.createTableIfNotExists()) {
            if (temporary) {
                createTable = this.ctx.createTemporaryTableIfNotExists(table);
            } else {
                createTable = this.ctx.createTableIfNotExists(table);
            }
        } else if (temporary) {
            createTable = this.ctx.createTemporaryTable(table);
        } else {
            createTable = this.ctx.createTable(table);
        }
        CreateTableOnCommitStep s0 = createTable.columns(sortIf(Tools.map(table.fields(), f -> {
            return f.comment("");
        }), !this.configuration.respectColumnOrder())).constraints(constraints);
        if (!temporary || onCommit == null) {
            return Arrays.asList(s0);
        }
        switch (onCommit) {
            case DELETE_ROWS:
                return Arrays.asList(s0.onCommitDeleteRows());
            case PRESERVE_ROWS:
                return Arrays.asList(s0.onCommitPreserveRows());
            case DROP:
                return Arrays.asList(s0.onCommitDrop());
            default:
                throw new IllegalStateException("Unsupported flag: " + String.valueOf(onCommit));
        }
    }

    private final Query applyAs(CreateViewAsStep q, TableOptions options) {
        if (options.select() != null) {
            return q.as(options.select());
        }
        if (StringUtils.isBlank(options.source())) {
            return q.as("");
        }
        try {
            if (!Boolean.FALSE.equals(this.ctx.settings().isParseMetaViewSources())) {
                Query[] queries = this.ctx.parser().parse(options.source()).queries();
                if (queries.length > 0) {
                    Query query = queries[0];
                    if (query instanceof CreateViewImpl) {
                        CreateViewImpl<?> cv = (CreateViewImpl) query;
                        return q.as(cv.$query());
                    }
                    Query query2 = queries[0];
                    if (query2 instanceof Select) {
                        Select<?> s = (Select) query2;
                        return q.as(s);
                    }
                    return q.as("");
                }
                return q.as("");
            }
        } catch (ParserException e) {
            log.info((Object) ("Cannot parse view source (to skip parsing, use Settings.parseMetaViewSources): " + options.source()), (Throwable) e);
        }
        return applyAsPlainSQL(q, options);
    }

    private final Query applyAsPlainSQL(CreateViewAsStep q, TableOptions options) {
        if (options.source().toLowerCase().startsWith("create")) {
            return q.as(P_CREATE_VIEW.matcher(options.source()).replaceFirst("$1"));
        }
        return q.as(options.source());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v50, types: [org.jooq.CreateSequenceFlagsStep] */
    /* JADX WARN: Type inference failed for: r0v54, types: [org.jooq.CreateSequenceFlagsStep] */
    /* JADX WARN: Type inference failed for: r0v56, types: [org.jooq.CreateSequenceFlagsStep] */
    /* JADX WARN: Type inference failed for: r0v58, types: [org.jooq.CreateSequenceFlagsStep] */
    /* JADX WARN: Type inference failed for: r5v0, types: [org.jooq.CreateSequenceFlagsStep] */
    /* JADX WARN: Type inference failed for: r5v1 */
    /* JADX WARN: Type inference failed for: r5v10 */
    /* JADX WARN: Type inference failed for: r5v14 */
    /* JADX WARN: Type inference failed for: r5v15 */
    /* JADX WARN: Type inference failed for: r5v16 */
    /* JADX WARN: Type inference failed for: r5v17 */
    /* JADX WARN: Type inference failed for: r5v18 */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v29 */
    /* JADX WARN: Type inference failed for: r5v3 */
    /* JADX WARN: Type inference failed for: r5v30 */
    /* JADX WARN: Type inference failed for: r5v31 */
    /* JADX WARN: Type inference failed for: r5v32 */
    /* JADX WARN: Type inference failed for: r5v33 */
    /* JADX WARN: Type inference failed for: r5v4 */
    /* JADX WARN: Type inference failed for: r5v5 */
    /* JADX WARN: Type inference failed for: r5v6 */
    /* JADX WARN: Type inference failed for: r5v7 */
    /* JADX WARN: Type inference failed for: r5v8 */
    /* JADX WARN: Type inference failed for: r5v9 */
    public final Query createSequence(Sequence<?> sequence) {
        CreateSequenceFlagsStep createSequence;
        ?? startWith;
        ?? incrementBy;
        ?? noMinvalue;
        ?? noMaxvalue;
        ?? noCycle;
        CreateSequenceFlagsStep noCache;
        if (this.configuration.createSequenceIfNotExists()) {
            createSequence = this.ctx.createSequenceIfNotExists(sequence);
        } else {
            createSequence = this.ctx.createSequence(sequence);
        }
        ?? r5 = createSequence;
        if (sequence.getStartWith() != null) {
            startWith = r5.startWith(sequence.getStartWith());
        } else {
            startWith = r5;
            if (this.configuration.defaultSequenceFlags()) {
                startWith = r5.startWith(1);
            }
        }
        if (sequence.getIncrementBy() != null) {
            incrementBy = (startWith == true ? 1 : 0).incrementBy(sequence.getIncrementBy());
        } else {
            incrementBy = startWith;
            if (this.configuration.defaultSequenceFlags()) {
                incrementBy = (startWith == true ? 1 : 0).incrementBy((Number) 1);
            }
        }
        if (sequence.getMinvalue() != null) {
            noMinvalue = (incrementBy == true ? 1 : 0).minvalue(sequence.getMinvalue());
        } else {
            noMinvalue = incrementBy;
            if (this.configuration.defaultSequenceFlags()) {
                noMinvalue = (incrementBy == true ? 1 : 0).noMinvalue();
            }
        }
        if (sequence.getMaxvalue() != null) {
            noMaxvalue = (noMinvalue == true ? 1 : 0).maxvalue(sequence.getMaxvalue());
        } else {
            noMaxvalue = noMinvalue;
            if (this.configuration.defaultSequenceFlags()) {
                noMaxvalue = (noMinvalue == true ? 1 : 0).noMaxvalue();
            }
        }
        if (sequence.getCycle()) {
            noCycle = (noMaxvalue == true ? 1 : 0).cycle();
        } else {
            noCycle = noMaxvalue;
            if (this.configuration.defaultSequenceFlags()) {
                noCycle = (noMaxvalue == true ? 1 : 0).noCycle();
            }
        }
        if (sequence.getCache() != null) {
            noCache = (noCycle == true ? 1 : 0).cache(sequence.getCache());
        } else {
            noCache = noCycle;
            if (this.configuration.defaultSequenceFlags()) {
                noCache = (noCycle == true ? 1 : 0).noCache();
            }
        }
        return noCache;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Query createDomain(Domain<?> domain) {
        CreateDomainAsStep createDomain;
        CreateDomainConstraintStep createDomainConstraintStep;
        if (this.configuration.createDomainIfNotExists()) {
            createDomain = this.ctx.createDomainIfNotExists(domain);
        } else {
            createDomain = this.ctx.createDomain(domain);
        }
        CreateDomainAsStep s1 = createDomain;
        CreateDomainDefaultStep s2 = s1.as(domain.getDataType());
        if (domain.getDataType().defaulted()) {
            createDomainConstraintStep = s2.default_((Field) domain.getDataType().default_());
        } else {
            createDomainConstraintStep = s2;
        }
        CreateDomainConstraintStep s3 = createDomainConstraintStep;
        if (domain.getChecks().isEmpty()) {
            return s3;
        }
        return s3.constraints(Tools.map(domain.getChecks(), c -> {
            return c.constraint();
        }));
    }

    final List<Query> createTableOrView(Table<?> table) {
        return createTableOrView(table, constraints(table));
    }

    final List<Query> createIndex(Table<?> table) {
        List<Query> result = new ArrayList<>();
        if (this.configuration.flags().contains(DDLFlag.INDEX)) {
            for (Index i : sortIf(table.getIndexes(), !this.configuration.respectIndexOrder())) {
                result.add(createIndex(i));
            }
        }
        return result;
    }

    final Query createIndex(Index i) {
        CreateIndexStep createIndex;
        if (this.configuration.createIndexIfNotExists()) {
            if (i.getUnique()) {
                createIndex = this.ctx.createUniqueIndexIfNotExists(i);
            } else {
                createIndex = this.ctx.createIndexIfNotExists(i);
            }
        } else if (i.getUnique()) {
            createIndex = this.ctx.createUniqueIndex(i);
        } else {
            createIndex = this.ctx.createIndex(i);
        }
        CreateIndexIncludeStep s1 = createIndex.on(i.getTable(), i.getFields());
        return i.getWhere() != null ? s1.where(i.getWhere()) : s1;
    }

    final List<Query> alterTableAddConstraints(Table<?> table) {
        return alterTableAddConstraints(table, constraints(table));
    }

    final List<Query> alterTableAddConstraints(Table<?> table, List<Constraint> constraints) {
        return Tools.map(constraints, c -> {
            return this.ctx.alterTable((Table<?>) table).add(c);
        });
    }

    final List<Constraint> constraints(Table<?> table) {
        List<Constraint> result = new ArrayList<>();
        result.addAll(primaryKeys(table));
        result.addAll(uniqueKeys(table));
        result.addAll(foreignKeys(table));
        result.addAll(checks(table));
        return result;
    }

    final List<Constraint> primaryKeys(Table<?> table) {
        List<Constraint> result = new ArrayList<>();
        if (this.configuration.flags().contains(DDLFlag.PRIMARY_KEY) && (table.getTableType() != TableOptions.TableType.VIEW || this.configuration.includeConstraintsOnViews())) {
            for (UniqueKey<?> key : table.getKeys()) {
                if (key.isPrimary()) {
                    result.add(enforced(DSL.constraint(key.getUnqualifiedName()).primaryKey(key.getFieldsArray()), key.enforced()));
                }
            }
        }
        return result;
    }

    final List<Constraint> uniqueKeys(Table<?> table) {
        List<Constraint> result = new ArrayList<>();
        if (this.configuration.flags().contains(DDLFlag.UNIQUE) && (table.getTableType() != TableOptions.TableType.VIEW || this.configuration.includeConstraintsOnViews())) {
            for (UniqueKey<?> key : sortKeysIf(table.getKeys(), !this.configuration.respectConstraintOrder())) {
                if (!key.isPrimary()) {
                    result.add(enforced(DSL.constraint(key.getUnqualifiedName()).unique(key.getFieldsArray()), key.enforced()));
                }
            }
        }
        return result;
    }

    final List<Constraint> foreignKeys(Table<?> table) {
        List<Constraint> result = new ArrayList<>();
        if (this.configuration.flags().contains(DDLFlag.FOREIGN_KEY) && (table.getTableType() != TableOptions.TableType.VIEW || this.configuration.includeConstraintsOnViews())) {
            for (ForeignKey<?, ?> key : sortKeysIf(table.getReferences(), !this.configuration.respectConstraintOrder())) {
                result.add(enforced(DSL.constraint(key.getUnqualifiedName()).foreignKey(key.getFieldsArray()).references(key.getKey().getTable(), key.getKeyFieldsArray()), key.enforced()));
            }
        }
        return result;
    }

    final List<Constraint> checks(Table<?> table) {
        List<Constraint> result = new ArrayList<>();
        if (this.configuration.flags().contains(DDLFlag.CHECK) && (table.getTableType() != TableOptions.TableType.VIEW || this.configuration.includeConstraintsOnViews())) {
            for (Check<?> check : sortIf(table.getChecks(), !this.configuration.respectConstraintOrder())) {
                result.add(enforced(DSL.constraint(check.getUnqualifiedName()).check(check.condition()), check.enforced()));
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Queries queries(Table<?>... tables) {
        List<Query> queries = new ArrayList<>();
        for (Table<?> table : tables) {
            if (this.configuration.flags().contains(DDLFlag.TABLE)) {
                queries.addAll(createTableOrView(table));
            } else {
                queries.addAll(alterTableAddConstraints(table));
            }
            queries.addAll(createIndex(table));
            queries.addAll(commentOn(table));
        }
        return this.ctx.queries(queries);
    }

    final List<Query> commentOn(Table<?> table) {
        List<Query> result = new ArrayList<>();
        if (this.configuration.flags().contains(DDLFlag.COMMENT)) {
            Comment tComment = table.getCommentPart();
            if (!StringUtils.isEmpty(tComment.getComment())) {
                if (table.getTableType() == TableOptions.TableType.MATERIALIZED_VIEW) {
                    result.add(this.ctx.commentOnMaterializedView(table).is(tComment));
                } else if (table.getTableType() == TableOptions.TableType.VIEW) {
                    result.add(this.ctx.commentOnView(table).is(tComment));
                } else {
                    result.add(this.ctx.commentOnTable(table).is(tComment));
                }
            }
            for (Field<?> field : sortIf(Arrays.asList(table.fields()), !this.configuration.respectColumnOrder())) {
                Comment fComment = field.getCommentPart();
                if (!StringUtils.isEmpty(fComment.getComment())) {
                    result.add(this.ctx.commentOnColumn(field).is(fComment));
                }
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Queries queries(Meta meta) {
        List<Query> queries = new ArrayList<>();
        List<Schema> schemas = sortIf(meta.getSchemas(), !this.configuration.respectSchemaOrder());
        for (Schema schema : schemas) {
            if (this.configuration.flags().contains(DDLFlag.SCHEMA) && !schema.getUnqualifiedName().empty()) {
                if (this.configuration.createSchemaIfNotExists()) {
                    queries.add(this.ctx.createSchemaIfNotExists(schema.getUnqualifiedName()));
                } else {
                    queries.add(this.ctx.createSchema(schema.getUnqualifiedName()));
                }
            }
        }
        Set<Table<?>> tablesWithInlineConstraints = new HashSet<>();
        if (this.configuration.flags().contains(DDLFlag.TABLE)) {
            for (Schema schema2 : schemas) {
                for (Table<?> table : sortTablesIf(schema2.getTables(), !this.configuration.respectTableOrder())) {
                    List<Constraint> constraints = new ArrayList<>();
                    if (!hasConstraintsUsingIndexes(table)) {
                        tablesWithInlineConstraints.add(table);
                        constraints.addAll(primaryKeys(table));
                        constraints.addAll(uniqueKeys(table));
                        constraints.addAll(checks(table));
                    }
                    queries.addAll(createTableOrView(table, constraints));
                }
            }
        }
        if (this.configuration.flags().contains(DDLFlag.INDEX)) {
            for (Schema schema3 : schemas) {
                Iterator it = sortIf(schema3.getTables(), !this.configuration.respectTableOrder()).iterator();
                while (it.hasNext()) {
                    queries.addAll(createIndex((Table<?>) it.next()));
                }
            }
        }
        for (Schema schema4 : schemas) {
            if (this.configuration.flags().contains(DDLFlag.PRIMARY_KEY)) {
                for (Table<?> table2 : sortIf(schema4.getTables(), !this.configuration.respectTableOrder())) {
                    if (!tablesWithInlineConstraints.contains(table2)) {
                        for (Constraint constraint : sortIf(primaryKeys(table2), !this.configuration.respectConstraintOrder())) {
                            queries.add(this.ctx.alterTable(table2).add(constraint));
                        }
                    }
                }
            }
            if (this.configuration.flags().contains(DDLFlag.UNIQUE)) {
                for (Table<?> table3 : sortIf(schema4.getTables(), !this.configuration.respectTableOrder())) {
                    if (!tablesWithInlineConstraints.contains(table3)) {
                        for (Constraint constraint2 : sortIf(uniqueKeys(table3), !this.configuration.respectConstraintOrder())) {
                            queries.add(this.ctx.alterTable(table3).add(constraint2));
                        }
                    }
                }
            }
            if (this.configuration.flags().contains(DDLFlag.CHECK)) {
                for (Table<?> table4 : sortIf(schema4.getTables(), !this.configuration.respectTableOrder())) {
                    if (!tablesWithInlineConstraints.contains(table4)) {
                        for (Constraint constraint3 : sortIf(checks(table4), !this.configuration.respectConstraintOrder())) {
                            queries.add(this.ctx.alterTable(table4).add(constraint3));
                        }
                    }
                }
            }
        }
        if (this.configuration.flags().contains(DDLFlag.FOREIGN_KEY)) {
            for (Schema schema5 : schemas) {
                for (Table<?> table5 : sortIf(schema5.getTables(), !this.configuration.respectTableOrder())) {
                    for (Constraint constraint4 : foreignKeys(table5)) {
                        queries.add(this.ctx.alterTable(table5).add(constraint4));
                    }
                }
            }
        }
        if (this.configuration.flags().contains(DDLFlag.DOMAIN)) {
            for (Schema schema6 : schemas) {
                for (Domain<?> domain : sortIf(schema6.getDomains(), !this.configuration.respectDomainOrder())) {
                    queries.add(createDomain(domain));
                }
            }
        }
        if (this.configuration.flags().contains(DDLFlag.SEQUENCE)) {
            for (Schema schema7 : schemas) {
                for (Sequence<?> sequence : sortIf(schema7.getSequences(), !this.configuration.respectSequenceOrder())) {
                    queries.add(createSequence(sequence));
                }
            }
        }
        if (this.configuration.flags().contains(DDLFlag.COMMENT)) {
            for (Schema schema8 : schemas) {
                Iterator it2 = sortIf(schema8.getTables(), !this.configuration.respectTableOrder()).iterator();
                while (it2.hasNext()) {
                    queries.addAll(commentOn((Table) it2.next()));
                }
            }
        }
        return this.ctx.queries(queries);
    }

    private final <R extends Record> boolean hasConstraintsUsingIndexes(Table<R> table) {
        return false;
    }

    private final <K extends Key<?>> List<K> sortKeysIf(List<K> input, boolean sort) {
        if (sort) {
            List<K> result = new ArrayList<>(input);
            result.sort(Comparators.KEY_COMP);
            result.sort(Comparators.NAMED_COMP);
            return result;
        }
        return input;
    }

    private final <N extends Named> List<N> sortIf(List<N> input, boolean sort) {
        if (sort) {
            List<N> result = new ArrayList<>(input);
            result.sort(Comparators.NAMED_COMP);
            return result;
        }
        return input;
    }

    private final <T extends Table<?>> List<T> sortTablesIf(List<T> input, boolean sort) {
        if (sort) {
            List<T> result = new ArrayList<>(input);
            result.sort(Comparators.NAMED_COMP);
            result.sort(Comparators.TABLE_VIEW_COMP);
            return result;
        }
        return input;
    }

    private final Constraint enforced(ConstraintEnforcementStep check, boolean enforced) {
        return enforced ? check : check.notEnforced();
    }
}
