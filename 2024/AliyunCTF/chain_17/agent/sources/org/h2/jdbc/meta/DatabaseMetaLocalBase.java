package org.h2.jdbc.meta;

import org.h2.engine.Constants;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/meta/DatabaseMetaLocalBase.class */
abstract class DatabaseMetaLocalBase extends DatabaseMeta {
    abstract void checkClosed();

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final String getDatabaseProductVersion() {
        return Constants.FULL_VERSION;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final ResultInterface getVersionColumns(String str, String str2, String str3) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("SCOPE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("BUFFER_LENGTH", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("PSEUDO_COLUMN", TypeInfo.TYPE_SMALLINT);
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final ResultInterface getUDTs(String str, String str2, String str3, int[] iArr) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("CLASS_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("BASE_TYPE", TypeInfo.TYPE_SMALLINT);
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final ResultInterface getSuperTypes(String str, String str2, String str3) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SUPERTYPE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SUPERTYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SUPERTYPE_NAME", TypeInfo.TYPE_VARCHAR);
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final ResultInterface getSuperTables(String str, String str2, String str3) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SUPERTABLE_NAME", TypeInfo.TYPE_VARCHAR);
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final ResultInterface getAttributes(String str, String str2, String str3, String str4) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("ATTR_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("ATTR_TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("ATTR_SIZE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("ATTR_DEF", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SCOPE_CATALOG", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SCOPE_SCHEMA", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SCOPE_TABLE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT);
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final int getDatabaseMajorVersion() {
        return 2;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final int getDatabaseMinorVersion() {
        return 2;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final ResultInterface getFunctions(String str, String str2, String str3) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("FUNCTION_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FUNCTION_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FUNCTION_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FUNCTION_TYPE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final ResultInterface getFunctionColumns(String str, String str2, String str3, String str4) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("FUNCTION_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FUNCTION_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FUNCTION_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_TYPE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PRECISION", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("LENGTH", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("SCALE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("RADIX", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
        return simpleResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SimpleResult getPseudoColumnsResult() {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("COLUMN_USAGE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
        return simpleResult;
    }
}
