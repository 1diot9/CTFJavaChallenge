package org.jooq.impl;

import java.util.Set;
import java.util.function.Predicate;
import org.jooq.Configuration;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.conf.Transformation;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Transformations.class */
public final class Transformations {
    static final Set<SQLDialect> NO_SUPPORT_IN_LIMIT = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    static final Set<SQLDialect> SUPPORT_MISSING_TABLE_REFERENCES = SQLDialect.supportedBy(new SQLDialect[0]);
    static final Set<SQLDialect> EMULATE_QUALIFY = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> EMULATE_ROWNUM = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> EMULATE_GROUP_BY_COLUMN_INDEX = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE);
    static final Set<SQLDialect> NO_SUPPORT_CTE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY);

    Transformations() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final SelectQueryImpl<?> subqueryWithLimit(QueryPart source) {
        SelectQueryImpl<?> s = Tools.selectQueryImpl(source);
        if (s == null || !s.getLimit().isApplicable()) {
            return null;
        }
        return s;
    }

    static final boolean transformInConditionSubqueryWithLimitToDerivedTable(Configuration configuration) {
        return transform(configuration, "Settings.transformInConditionSubqueryWithLimitToDerivedTable", configuration.settings().getTransformInConditionSubqueryWithLimitToDerivedTable(), c -> {
            return NO_SUPPORT_IN_LIMIT.contains(c.dialect());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean transformInlineCTE(Configuration configuration) {
        return transform(configuration, "Settings.transformInlineCTE", configuration.settings().getTransformInlineCTE(), c -> {
            return NO_SUPPORT_CTE.contains(c.dialect());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean transformQualify(Configuration configuration) {
        return transform(configuration, "Settings.transformQualify", configuration.settings().getTransformQualify(), c -> {
            return EMULATE_QUALIFY.contains(c.dialect());
        });
    }

    static final boolean transformRownum(Configuration configuration) {
        return transform(configuration, "Settings.transformRownum", configuration.settings().getTransformRownum(), c -> {
            return EMULATE_ROWNUM.contains(c.dialect());
        });
    }

    static final boolean transformAppendMissingTableReferences(Configuration configuration) {
        return transform(configuration, "Settings.transformAppendMissingTableReferences", configuration.settings().getParseAppendMissingTableReferences(), c -> {
            return SUPPORT_MISSING_TABLE_REFERENCES.contains(c.settings().getParseDialect());
        });
    }

    static final boolean transformGroupByColumnIndex(Configuration configuration) {
        return transform(configuration, "Settings.transformGroupByColumnIndex", configuration.settings().getTransformGroupByColumnIndex(), c -> {
            return EMULATE_GROUP_BY_COLUMN_INDEX.contains(c.dialect());
        });
    }

    static final boolean transform(Configuration configuration, String label, Transformation transformation, Predicate<? super Configuration> whenNeeded) {
        boolean result;
        switch ((Transformation) StringUtils.defaultIfNull(transformation, Transformation.WHEN_NEEDED)) {
            case NEVER:
                result = false;
                break;
            case ALWAYS:
                result = true;
                break;
            case WHEN_NEEDED:
                result = whenNeeded.test(configuration);
                break;
            default:
                throw new IllegalStateException("Transformation configuration not supported: " + String.valueOf(transformation));
        }
        return result && configuration.requireCommercial(() -> {
            return "SQL transformation " + label + " required. SQL transformations are a commercial only feature. Please consider upgrading to the jOOQ Professional Edition or jOOQ Enterprise Edition.";
        });
    }
}
