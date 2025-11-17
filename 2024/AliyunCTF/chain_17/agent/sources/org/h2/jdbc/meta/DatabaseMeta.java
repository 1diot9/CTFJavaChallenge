package org.h2.jdbc.meta;

import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/meta/DatabaseMeta.class */
public abstract class DatabaseMeta {
    public abstract DefaultNullOrdering defaultNullOrdering();

    public abstract String getDatabaseProductVersion();

    public abstract String getSQLKeywords();

    public abstract String getNumericFunctions();

    public abstract String getStringFunctions();

    public abstract String getSystemFunctions();

    public abstract String getTimeDateFunctions();

    public abstract String getSearchStringEscape();

    public abstract ResultInterface getProcedures(String str, String str2, String str3);

    public abstract ResultInterface getProcedureColumns(String str, String str2, String str3, String str4);

    public abstract ResultInterface getTables(String str, String str2, String str3, String[] strArr);

    public abstract ResultInterface getSchemas();

    public abstract ResultInterface getCatalogs();

    public abstract ResultInterface getTableTypes();

    public abstract ResultInterface getColumns(String str, String str2, String str3, String str4);

    public abstract ResultInterface getColumnPrivileges(String str, String str2, String str3, String str4);

    public abstract ResultInterface getTablePrivileges(String str, String str2, String str3);

    public abstract ResultInterface getBestRowIdentifier(String str, String str2, String str3, int i, boolean z);

    public abstract ResultInterface getVersionColumns(String str, String str2, String str3);

    public abstract ResultInterface getPrimaryKeys(String str, String str2, String str3);

    public abstract ResultInterface getImportedKeys(String str, String str2, String str3);

    public abstract ResultInterface getExportedKeys(String str, String str2, String str3);

    public abstract ResultInterface getCrossReference(String str, String str2, String str3, String str4, String str5, String str6);

    public abstract ResultInterface getTypeInfo();

    public abstract ResultInterface getIndexInfo(String str, String str2, String str3, boolean z, boolean z2);

    public abstract ResultInterface getUDTs(String str, String str2, String str3, int[] iArr);

    public abstract ResultInterface getSuperTypes(String str, String str2, String str3);

    public abstract ResultInterface getSuperTables(String str, String str2, String str3);

    public abstract ResultInterface getAttributes(String str, String str2, String str3, String str4);

    public abstract int getDatabaseMajorVersion();

    public abstract int getDatabaseMinorVersion();

    public abstract ResultInterface getSchemas(String str, String str2);

    public abstract ResultInterface getFunctions(String str, String str2, String str3);

    public abstract ResultInterface getFunctionColumns(String str, String str2, String str3, String str4);

    public abstract ResultInterface getPseudoColumns(String str, String str2, String str3, String str4);
}
