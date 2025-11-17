package org.h2.jdbc.meta;

import cn.hutool.core.text.StrPool;
import java.util.ArrayList;
import java.util.Arrays;
import org.h2.api.ErrorCode;
import org.h2.command.CommandInterface;
import org.h2.engine.Constants;
import org.h2.engine.Session;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/meta/DatabaseMetaLegacy.class */
public final class DatabaseMetaLegacy extends DatabaseMetaLocalBase {
    private static final Value PERCENT = ValueVarchar.get(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
    private static final Value BACKSLASH = ValueVarchar.get(StrPool.BACKSLASH);
    private static final Value YES = ValueVarchar.get("YES");
    private static final Value NO = ValueVarchar.get("NO");
    private static final Value SCHEMA_MAIN = ValueVarchar.get("PUBLIC");
    private final Session session;

    public DatabaseMetaLegacy(Session session) {
        this.session = session;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final DefaultNullOrdering defaultNullOrdering() {
        return DefaultNullOrdering.LOW;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getSQLKeywords() {
        return "CURRENT_CATALOG,CURRENT_SCHEMA,GROUPS,IF,ILIKE,INTERSECTS,KEY,LIMIT,MINUS,OFFSET,QUALIFY,REGEXP,ROWNUM,SYSDATE,SYSTIME,SYSTIMESTAMP,TODAY,TOP,_ROWID_";
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getNumericFunctions() {
        return getFunctions("Functions (Numeric)");
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getStringFunctions() {
        return getFunctions("Functions (String)");
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getSystemFunctions() {
        return getFunctions("Functions (System)");
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getTimeDateFunctions() {
        return getFunctions("Functions (Time and Date)");
    }

    private String getFunctions(String str) {
        ResultInterface executeQuery = executeQuery("SELECT TOPIC FROM INFORMATION_SCHEMA.HELP WHERE SECTION = ?", getString(str));
        StringBuilder sb = new StringBuilder();
        while (executeQuery.next()) {
            for (String str2 : StringUtils.arraySplit(executeQuery.currentRow()[0].getString().trim(), ',', true)) {
                if (sb.length() != 0) {
                    sb.append(',');
                }
                String trim = str2.trim();
                int indexOf = trim.indexOf(32);
                if (indexOf >= 0) {
                    StringUtils.trimSubstring(sb, trim, 0, indexOf);
                } else {
                    sb.append(trim);
                }
            }
        }
        return sb.toString();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getSearchStringEscape() {
        return StrPool.BACKSLASH;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getProcedures(String str, String str2, String str3) {
        return executeQuery("SELECT ALIAS_CATALOG PROCEDURE_CAT, ALIAS_SCHEMA PROCEDURE_SCHEM, ALIAS_NAME PROCEDURE_NAME, COLUMN_COUNT NUM_INPUT_PARAMS, ZERO() NUM_OUTPUT_PARAMS, ZERO() NUM_RESULT_SETS, REMARKS, RETURNS_RESULT PROCEDURE_TYPE, ALIAS_NAME SPECIFIC_NAME FROM INFORMATION_SCHEMA.FUNCTION_ALIASES WHERE ALIAS_CATALOG LIKE ?1 ESCAPE ?4 AND ALIAS_SCHEMA LIKE ?2 ESCAPE ?4 AND ALIAS_NAME LIKE ?3 ESCAPE ?4 ORDER BY PROCEDURE_SCHEM, PROCEDURE_NAME, NUM_INPUT_PARAMS", getCatalogPattern(str), getSchemaPattern(str2), getPattern(str3), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getProcedureColumns(String str, String str2, String str3, String str4) {
        return executeQuery("SELECT ALIAS_CATALOG PROCEDURE_CAT, ALIAS_SCHEMA PROCEDURE_SCHEM, ALIAS_NAME PROCEDURE_NAME, COLUMN_NAME, COLUMN_TYPE, DATA_TYPE, TYPE_NAME, PRECISION, PRECISION LENGTH, SCALE, RADIX, NULLABLE, REMARKS, COLUMN_DEFAULT COLUMN_DEF, ZERO() SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, ZERO() CHAR_OCTET_LENGTH, POS ORDINAL_POSITION, ?1 IS_NULLABLE, ALIAS_NAME SPECIFIC_NAME FROM INFORMATION_SCHEMA.FUNCTION_COLUMNS WHERE ALIAS_CATALOG LIKE ?2 ESCAPE ?6 AND ALIAS_SCHEMA LIKE ?3 ESCAPE ?6 AND ALIAS_NAME LIKE ?4 ESCAPE ?6 AND COLUMN_NAME LIKE ?5 ESCAPE ?6 ORDER BY PROCEDURE_SCHEM, PROCEDURE_NAME, ORDINAL_POSITION", YES, getCatalogPattern(str), getSchemaPattern(str2), getPattern(str3), getPattern(str4), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTables(String str, String str2, String str3, String[] strArr) {
        int length = strArr != null ? strArr.length : 0;
        boolean z = strArr == null || Arrays.asList(strArr).contains("SYNONYM");
        StringBuilder sb = new StringBuilder(1008);
        if (z) {
            sb.append("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_CAT, TYPE_SCHEM, TYPE_NAME, SELF_REFERENCING_COL_NAME, REF_GENERATION, SQL FROM (SELECT SYNONYM_CATALOG TABLE_CAT, SYNONYM_SCHEMA TABLE_SCHEM, SYNONYM_NAME as TABLE_NAME, TYPE_NAME AS TABLE_TYPE, REMARKS, TYPE_NAME TYPE_CAT, TYPE_NAME TYPE_SCHEM, TYPE_NAME AS TYPE_NAME, TYPE_NAME SELF_REFERENCING_COL_NAME, TYPE_NAME REF_GENERATION, NULL AS SQL FROM INFORMATION_SCHEMA.SYNONYMS WHERE SYNONYM_CATALOG LIKE ?1 ESCAPE ?4 AND SYNONYM_SCHEMA LIKE ?2 ESCAPE ?4 AND SYNONYM_NAME LIKE ?3 ESCAPE ?4 UNION ");
        }
        sb.append("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_NAME TYPE_CAT, TYPE_NAME TYPE_SCHEM, TYPE_NAME, TYPE_NAME SELF_REFERENCING_COL_NAME, TYPE_NAME REF_GENERATION, SQL FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME LIKE ?3 ESCAPE ?4");
        if (length > 0) {
            sb.append(" AND TABLE_TYPE IN(");
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append('?').append(i + 5);
            }
            sb.append(')');
        }
        if (z) {
            sb.append(')');
        }
        Value[] valueArr = new Value[length + 4];
        valueArr[0] = getCatalogPattern(str);
        valueArr[1] = getSchemaPattern(str2);
        valueArr[2] = getPattern(str3);
        valueArr[3] = BACKSLASH;
        for (int i2 = 0; i2 < length; i2++) {
            valueArr[i2 + 4] = getString(strArr[i2]);
        }
        return executeQuery(sb.append(" ORDER BY TABLE_TYPE, TABLE_SCHEM, TABLE_NAME").toString(), valueArr);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getSchemas() {
        return executeQuery("SELECT SCHEMA_NAME TABLE_SCHEM, CATALOG_NAME TABLE_CATALOG FROM INFORMATION_SCHEMA.SCHEMATA ORDER BY SCHEMA_NAME", new Value[0]);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getCatalogs() {
        return executeQuery("SELECT CATALOG_NAME TABLE_CAT FROM INFORMATION_SCHEMA.CATALOGS", new Value[0]);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTableTypes() {
        return executeQuery("SELECT TYPE TABLE_TYPE FROM INFORMATION_SCHEMA.TABLE_TYPES ORDER BY TABLE_TYPE", new Value[0]);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getColumns(String str, String str2, String str3, String str4) {
        return executeQuery("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT, IS_GENERATEDCOLUMN FROM (SELECT s.SYNONYM_CATALOG TABLE_CAT, s.SYNONYM_SCHEMA TABLE_SCHEM, s.SYNONYM_NAME TABLE_NAME, c.COLUMN_NAME, c.DATA_TYPE, c.TYPE_NAME, c.CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, c.CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, c.NUMERIC_SCALE DECIMAL_DIGITS, c.NUMERIC_PRECISION_RADIX NUM_PREC_RADIX, c.NULLABLE, c.REMARKS, c.COLUMN_DEFAULT COLUMN_DEF, c.DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, c.CHARACTER_OCTET_LENGTH CHAR_OCTET_LENGTH, c.ORDINAL_POSITION, c.IS_NULLABLE IS_NULLABLE, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_CATALOG, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_SCHEMA, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_TABLE, c.SOURCE_DATA_TYPE, CASE WHEN c.SEQUENCE_NAME IS NULL THEN CAST(?1 AS VARCHAR) ELSE CAST(?2 AS VARCHAR) END IS_AUTOINCREMENT, CASE WHEN c.IS_COMPUTED THEN CAST(?2 AS VARCHAR) ELSE CAST(?1 AS VARCHAR) END IS_GENERATEDCOLUMN FROM INFORMATION_SCHEMA.COLUMNS c JOIN INFORMATION_SCHEMA.SYNONYMS s ON s.SYNONYM_FOR = c.TABLE_NAME AND s.SYNONYM_FOR_SCHEMA = c.TABLE_SCHEMA WHERE s.SYNONYM_CATALOG LIKE ?3 ESCAPE ?7 AND s.SYNONYM_SCHEMA LIKE ?4 ESCAPE ?7 AND s.SYNONYM_NAME LIKE ?5 ESCAPE ?7 AND c.COLUMN_NAME LIKE ?6 ESCAPE ?7 UNION SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, NUMERIC_SCALE DECIMAL_DIGITS, NUMERIC_PRECISION_RADIX NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEFAULT COLUMN_DEF, DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, CHARACTER_OCTET_LENGTH CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE IS_NULLABLE, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_CATALOG, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_SCHEMA, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_TABLE, SOURCE_DATA_TYPE, CASE WHEN SEQUENCE_NAME IS NULL THEN CAST(?1 AS VARCHAR) ELSE CAST(?2 AS VARCHAR) END IS_AUTOINCREMENT, CASE WHEN IS_COMPUTED THEN CAST(?2 AS VARCHAR) ELSE CAST(?1 AS VARCHAR) END IS_GENERATEDCOLUMN FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG LIKE ?3 ESCAPE ?7 AND TABLE_SCHEMA LIKE ?4 ESCAPE ?7 AND TABLE_NAME LIKE ?5 ESCAPE ?7 AND COLUMN_NAME LIKE ?6 ESCAPE ?7) ORDER BY TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION", NO, YES, getCatalogPattern(str), getSchemaPattern(str2), getPattern(str3), getPattern(str4), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getColumnPrivileges(String str, String str2, String str3, String str4) {
        return executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, GRANTOR, GRANTEE, PRIVILEGE_TYPE PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?5 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?5 AND TABLE_NAME = ?3 AND COLUMN_NAME LIKE ?4 ESCAPE ?5 ORDER BY COLUMN_NAME, PRIVILEGE", getCatalogPattern(str), getSchemaPattern(str2), getString(str3), getPattern(str4), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTablePrivileges(String str, String str2, String str3) {
        return executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, GRANTOR, GRANTEE, PRIVILEGE_TYPE PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.TABLE_PRIVILEGES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME LIKE ?3 ESCAPE ?4 ORDER BY TABLE_SCHEM, TABLE_NAME, PRIVILEGE", getCatalogPattern(str), getSchemaPattern(str2), getPattern(str3), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getBestRowIdentifier(String str, String str2, String str3, int i, boolean z) {
        return executeQuery("SELECT CAST(?1 AS SMALLINT) SCOPE, C.COLUMN_NAME, C.DATA_TYPE, C.TYPE_NAME, C.CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, C.CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, CAST(C.NUMERIC_SCALE AS SMALLINT) DECIMAL_DIGITS, CAST(?2 AS SMALLINT) PSEUDO_COLUMN FROM INFORMATION_SCHEMA.INDEXES I, INFORMATION_SCHEMA.COLUMNS C WHERE C.TABLE_NAME = I.TABLE_NAME AND C.COLUMN_NAME = I.COLUMN_NAME AND C.TABLE_CATALOG LIKE ?3 ESCAPE ?6 AND C.TABLE_SCHEMA LIKE ?4 ESCAPE ?6 AND C.TABLE_NAME = ?5 AND I.PRIMARY_KEY = TRUE ORDER BY SCOPE", ValueInteger.get(2), ValueInteger.get(1), getCatalogPattern(str), getSchemaPattern(str2), getString(str3), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getPrimaryKeys(String str, String str2, String str3) {
        return executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION KEY_SEQ, COALESCE(CONSTRAINT_NAME, INDEX_NAME) PK_NAME FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME = ?3 AND PRIMARY_KEY = TRUE ORDER BY COLUMN_NAME", getCatalogPattern(str), getSchemaPattern(str2), getString(str3), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getImportedKeys(String str, String str2, String str3) {
        return executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE FKTABLE_CATALOG LIKE ?1 ESCAPE ?4 AND FKTABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND FKTABLE_NAME = ?3 ORDER BY PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, FK_NAME, KEY_SEQ", getCatalogPattern(str), getSchemaPattern(str2), getString(str3), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getExportedKeys(String str, String str2, String str3) {
        return executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE PKTABLE_CATALOG LIKE ?1 ESCAPE ?4 AND PKTABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND PKTABLE_NAME = ?3 ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, FK_NAME, KEY_SEQ", getCatalogPattern(str), getSchemaPattern(str2), getString(str3), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getCrossReference(String str, String str2, String str3, String str4, String str5, String str6) {
        return executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE PKTABLE_CATALOG LIKE ?1 ESCAPE ?7 AND PKTABLE_SCHEMA LIKE ?2 ESCAPE ?7 AND PKTABLE_NAME = ?3 AND FKTABLE_CATALOG LIKE ?4 ESCAPE ?7 AND FKTABLE_SCHEMA LIKE ?5 ESCAPE ?7 AND FKTABLE_NAME = ?6 ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, FK_NAME, KEY_SEQ", getCatalogPattern(str), getSchemaPattern(str2), getString(str3), getCatalogPattern(str4), getSchemaPattern(str5), getString(str6), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTypeInfo() {
        return executeQuery("SELECT TYPE_NAME, DATA_TYPE, PRECISION, PREFIX LITERAL_PREFIX, SUFFIX LITERAL_SUFFIX, PARAMS CREATE_PARAMS, NULLABLE, CASE_SENSITIVE, SEARCHABLE, FALSE UNSIGNED_ATTRIBUTE, FALSE FIXED_PREC_SCALE, AUTO_INCREMENT, TYPE_NAME LOCAL_TYPE_NAME, MINIMUM_SCALE, MAXIMUM_SCALE, DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, RADIX NUM_PREC_RADIX FROM INFORMATION_SCHEMA.TYPE_INFO ORDER BY DATA_TYPE, POS", new Value[0]);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getIndexInfo(String str, String str2, String str3, boolean z, boolean z2) {
        return executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, NON_UNIQUE, TABLE_CATALOG INDEX_QUALIFIER, INDEX_NAME, INDEX_TYPE TYPE, ORDINAL_POSITION, COLUMN_NAME, ASC_OR_DESC, CARDINALITY, PAGES, FILTER_CONDITION, SORT_TYPE FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND (" + (z ? "NON_UNIQUE=FALSE" : Constants.CLUSTERING_ENABLED) + ") AND TABLE_NAME = ?3 ORDER BY NON_UNIQUE, TYPE, TABLE_SCHEM, INDEX_NAME, ORDINAL_POSITION", getCatalogPattern(str), getSchemaPattern(str2), getString(str3), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getSchemas(String str, String str2) {
        return executeQuery("SELECT SCHEMA_NAME TABLE_SCHEM, CATALOG_NAME TABLE_CATALOG FROM INFORMATION_SCHEMA.SCHEMATA WHERE CATALOG_NAME LIKE ?1 ESCAPE ?3 AND SCHEMA_NAME LIKE ?2 ESCAPE ?3 ORDER BY SCHEMA_NAME", getCatalogPattern(str), getSchemaPattern(str2), BACKSLASH);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getPseudoColumns(String str, String str2, String str3, String str4) {
        return getPseudoColumnsResult();
    }

    private ResultInterface executeQuery(String str, Value... valueArr) {
        checkClosed();
        this.session.lock();
        try {
            CommandInterface prepareCommand = this.session.prepareCommand(str, Integer.MAX_VALUE);
            int length = valueArr.length;
            if (length > 0) {
                ArrayList<? extends ParameterInterface> parameters = prepareCommand.getParameters();
                for (int i = 0; i < length; i++) {
                    parameters.get(i).setValue(valueArr[i], true);
                }
            }
            ResultInterface executeQuery = prepareCommand.executeQuery(0L, false);
            prepareCommand.close();
            this.session.unlock();
            return executeQuery;
        } catch (Throwable th) {
            this.session.unlock();
            throw th;
        }
    }

    @Override // org.h2.jdbc.meta.DatabaseMetaLocalBase
    void checkClosed() {
        if (this.session.isClosed()) {
            throw DbException.get(ErrorCode.DATABASE_CALLED_AT_SHUTDOWN);
        }
    }

    private Value getString(String str) {
        return str != null ? ValueVarchar.get(str, this.session) : ValueNull.INSTANCE;
    }

    private Value getPattern(String str) {
        return str == null ? PERCENT : getString(str);
    }

    private Value getSchemaPattern(String str) {
        return str == null ? PERCENT : str.isEmpty() ? SCHEMA_MAIN : getString(str);
    }

    private Value getCatalogPattern(String str) {
        return (str == null || str.isEmpty()) ? PERCENT : getString(str);
    }
}
