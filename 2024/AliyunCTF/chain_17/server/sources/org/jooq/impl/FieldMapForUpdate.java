package org.jooq.impl;

import java.util.Map;
import java.util.Set;
import org.jooq.CaseConditionStep;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldMapForUpdate.class */
public final class FieldMapForUpdate extends AbstractQueryPartMap<FieldOrRow, FieldOrRowOrSelect> {
    static final Set<SQLDialect> CASTS_NEEDED = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> NO_SUPPORT_QUALIFY = SQLDialect.supportedBy(SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> EMULATE_RVE_SET_QUERY = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);
    static final Set<SQLDialect> NO_SUPPORT_RVE_SET = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);
    static final Set<SQLDialect> NO_SUPPORT_RVE_SET_IN_MERGE = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);
    static final Set<SQLDialect> REQUIRE_RVE_ROW_CLAUSE = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    final Table<?> table;
    final SetClause setClause;
    final Clause assignmentClause;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldMapForUpdate$SetClause.class */
    public enum SetClause {
        UPDATE,
        INSERT,
        MERGE
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldMapForUpdate(FieldMapForUpdate um, SetClause setClause) {
        this(um.table, setClause, um.assignmentClause);
        putAll(um);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldMapForUpdate(Table<?> table, SetClause setClause, Clause assignmentClause) {
        this.table = table;
        this.setClause = setClause;
        this.assignmentClause = assignmentClause;
    }

    @Override // org.jooq.impl.AbstractQueryPartMap, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (size() > 0) {
            String separator = "";
            boolean supportsQualify = !NO_SUPPORT_QUALIFY.contains(ctx.dialect()) && ctx.qualify();
            RenderContext.CastMode previous = ctx.castMode();
            if (!CASTS_NEEDED.contains(ctx.dialect())) {
                ctx.castMode(RenderContext.CastMode.NEVER);
            }
            for (Map.Entry<FieldOrRow, FieldOrRowOrSelect> entry : Tools.flattenEntrySet(entrySet(), true)) {
                FieldOrRow key = entry.getKey();
                FieldOrRowOrSelect value = entry.getValue();
                separator = acceptAssignmentClause(ctx, supportsQualify, key, value, separator);
            }
            if (!CASTS_NEEDED.contains(ctx.dialect())) {
                ctx.castMode(previous);
                return;
            }
            return;
        }
        ctx.sql("[ no fields are updated ]");
    }

    /* JADX WARN: Type inference failed for: r0v44, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v89, types: [org.jooq.Context] */
    private final String acceptAssignmentClause(Context<?> ctx, boolean supportsQualify, FieldOrRow key, FieldOrRowOrSelect value, String separator) {
        if (!"".equals(separator)) {
            ctx.sql(separator).formatSeparator();
        }
        if (this.assignmentClause != null) {
            ctx.start(this.assignmentClause);
        }
        if (key instanceof Row) {
            Row multiRow = (Row) key;
            Row multiValue = value instanceof Row ? (Row) value : null;
            Select<?> multiSelect = value instanceof Select ? (Select) value : null;
            if (multiValue != null && (NO_SUPPORT_RVE_SET.contains(ctx.dialect()) || (NO_SUPPORT_RVE_SET_IN_MERGE.contains(ctx.dialect()) && this.setClause == SetClause.MERGE))) {
                FieldMapForUpdate map = new FieldMapForUpdate(DSL.table(new Record[0]), this.setClause, null);
                for (int i = 0; i < multiRow.size(); i++) {
                    Field<?> k = multiRow.field(i);
                    Field<?> v = multiValue.field(i);
                    map.put((FieldOrRow) k, (FieldOrRowOrSelect) FieldMapsForInsert.patchDefault(ctx, Tools.field(v, k), k));
                }
                ctx.visit(map);
            } else if (multiSelect != null && (EMULATE_RVE_SET_QUERY.contains(ctx.dialect()) || (NO_SUPPORT_RVE_SET_IN_MERGE.contains(ctx.dialect()) && this.setClause == SetClause.MERGE))) {
                Row row = removeReadonly(ctx, multiRow);
                int size = row.size();
                if (size == 1) {
                    acceptStoreAssignment(ctx, false, row.field(0));
                    visitSubquery(ctx, multiSelect);
                } else {
                    for (int i2 = 0; i2 < size; i2++) {
                        FieldMapForUpdate mu = new FieldMapForUpdate(this.table, this.setClause, null);
                        separator = mu.acceptAssignmentClause(ctx, supportsQualify, row.field(i2), new ProjectSingleScalarSubquery(multiSelect, i2), separator);
                    }
                }
            } else {
                acceptStoreAssignment(ctx, false, removeReadonly(ctx, multiRow));
                if (multiValue != null) {
                    if (REQUIRE_RVE_ROW_CLAUSE.contains(ctx.dialect())) {
                        ctx.visit(Keywords.K_ROW).sql(" ");
                    }
                    ctx.visit(removeReadonly(ctx, multiRow, multiValue));
                } else if (multiSelect != null) {
                    visitSubquery(ctx, multiSelect);
                }
            }
        } else {
            acceptStoreAssignment(ctx, supportsQualify, key);
            Condition condition = (Condition) ctx.data(Tools.SimpleDataKey.DATA_ON_DUPLICATE_KEY_WHERE);
            if (condition != null) {
                ctx.visit(DSL.when(condition, (Field) value).else_((CaseConditionStep) key));
            } else {
                ctx.visit(FieldMapsForInsert.patchDefault(ctx, (Field) value, (Field) key));
            }
        }
        if (this.assignmentClause != null) {
            ctx.end(this.assignmentClause);
            return ",";
        }
        return ",";
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private static final void acceptStoreAssignment(Context<?> ctx, boolean qualify, QueryPart target) {
        ctx.qualify(qualify, c1 -> {
            c1.data(Tools.BooleanDataKey.DATA_STORE_ASSIGNMENT, true, c2 -> {
                c2.visit(target);
            });
        }).sql(" = ");
    }

    private static final void visitSubquery(Context<?> ctx, Select<?> select) {
        Tools.visitSubquery(ctx, select);
    }

    static final Row removeReadonly(Context<?> ctx, Row row) {
        return removeReadonly(ctx, row, row);
    }

    static final Row removeReadonly(Context<?> ctx, Row checkRow, Row removeRow) {
        return removeRow;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void set(Map<?, ?> map) {
        map.forEach((k, v) -> {
            if (k instanceof Row) {
                Row r = (Row) k;
                put((FieldOrRow) r, (FieldOrRowOrSelect) v);
            } else {
                Field<?> field = Tools.tableField(this.table, k);
                put((FieldOrRow) field, Tools.field(v, field));
            }
        });
    }

    @Override // org.jooq.impl.AbstractQueryPartMap, java.util.Map
    public FieldOrRowOrSelect put(FieldOrRow key, FieldOrRowOrSelect value) {
        if ((key instanceof NoField) || (value instanceof NoField)) {
            return null;
        }
        return (FieldOrRowOrSelect) super.put((FieldMapForUpdate) key, (FieldOrRow) value);
    }

    @Override // org.jooq.impl.AbstractQueryPartMap
    final java.util.function.Function<? super Map<FieldOrRow, FieldOrRowOrSelect>, ? extends AbstractQueryPartMap<FieldOrRow, FieldOrRowOrSelect>> $construct() {
        return m -> {
            FieldMapForUpdate r = new FieldMapForUpdate(this.table, this.setClause, this.assignmentClause);
            r.putAll(m);
            return r;
        };
    }
}
