package org.h2.jdbc.meta;

import java.io.IOException;
import java.util.ArrayList;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionRemote;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.result.ResultRemote;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/meta/DatabaseMetaRemote.class */
public class DatabaseMetaRemote extends DatabaseMeta {
    static final int DEFAULT_NULL_ORDERING = 0;
    static final int GET_DATABASE_PRODUCT_VERSION = 1;
    static final int GET_SQL_KEYWORDS = 2;
    static final int GET_NUMERIC_FUNCTIONS = 3;
    static final int GET_STRING_FUNCTIONS = 4;
    static final int GET_SYSTEM_FUNCTIONS = 5;
    static final int GET_TIME_DATE_FUNCTIONS = 6;
    static final int GET_SEARCH_STRING_ESCAPE = 7;
    static final int GET_PROCEDURES_3 = 8;
    static final int GET_PROCEDURE_COLUMNS_4 = 9;
    static final int GET_TABLES_4 = 10;
    static final int GET_SCHEMAS = 11;
    static final int GET_CATALOGS = 12;
    static final int GET_TABLE_TYPES = 13;
    static final int GET_COLUMNS_4 = 14;
    static final int GET_COLUMN_PRIVILEGES_4 = 15;
    static final int GET_TABLE_PRIVILEGES_3 = 16;
    static final int GET_BEST_ROW_IDENTIFIER_5 = 17;
    static final int GET_VERSION_COLUMNS_3 = 18;
    static final int GET_PRIMARY_KEYS_3 = 19;
    static final int GET_IMPORTED_KEYS_3 = 20;
    static final int GET_EXPORTED_KEYS_3 = 21;
    static final int GET_CROSS_REFERENCE_6 = 22;
    static final int GET_TYPE_INFO = 23;
    static final int GET_INDEX_INFO_5 = 24;
    static final int GET_UDTS_4 = 25;
    static final int GET_SUPER_TYPES_3 = 26;
    static final int GET_SUPER_TABLES_3 = 27;
    static final int GET_ATTRIBUTES_4 = 28;
    static final int GET_DATABASE_MAJOR_VERSION = 29;
    static final int GET_DATABASE_MINOR_VERSION = 30;
    static final int GET_SCHEMAS_2 = 31;
    static final int GET_FUNCTIONS_3 = 32;
    static final int GET_FUNCTION_COLUMNS_4 = 33;
    static final int GET_PSEUDO_COLUMNS_4 = 34;
    private final SessionRemote session;
    private final ArrayList<Transfer> transferList;

    public DatabaseMetaRemote(SessionRemote sessionRemote, ArrayList<Transfer> arrayList) {
        this.session = sessionRemote;
        this.transferList = arrayList;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public DefaultNullOrdering defaultNullOrdering() {
        ResultInterface executeQuery = executeQuery(0, new Value[0]);
        executeQuery.next();
        return DefaultNullOrdering.valueOf(executeQuery.currentRow()[0].getInt());
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getDatabaseProductVersion() {
        ResultInterface executeQuery = executeQuery(1, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getString();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getSQLKeywords() {
        ResultInterface executeQuery = executeQuery(2, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getString();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getNumericFunctions() {
        ResultInterface executeQuery = executeQuery(3, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getString();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getStringFunctions() {
        ResultInterface executeQuery = executeQuery(4, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getString();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getSystemFunctions() {
        ResultInterface executeQuery = executeQuery(5, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getString();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getTimeDateFunctions() {
        ResultInterface executeQuery = executeQuery(6, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getString();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getSearchStringEscape() {
        ResultInterface executeQuery = executeQuery(7, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getString();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getProcedures(String str, String str2, String str3) {
        return executeQuery(8, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getProcedureColumns(String str, String str2, String str3, String str4) {
        return executeQuery(9, getString(str), getString(str2), getString(str3), getString(str4));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTables(String str, String str2, String str3, String[] strArr) {
        return executeQuery(10, getString(str), getString(str2), getString(str3), getStringArray(strArr));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getSchemas() {
        return executeQuery(11, new Value[0]);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getCatalogs() {
        return executeQuery(12, new Value[0]);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTableTypes() {
        return executeQuery(13, new Value[0]);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getColumns(String str, String str2, String str3, String str4) {
        return executeQuery(14, getString(str), getString(str2), getString(str3), getString(str4));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getColumnPrivileges(String str, String str2, String str3, String str4) {
        return executeQuery(15, getString(str), getString(str2), getString(str3), getString(str4));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTablePrivileges(String str, String str2, String str3) {
        return executeQuery(16, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getBestRowIdentifier(String str, String str2, String str3, int i, boolean z) {
        return executeQuery(17, getString(str), getString(str2), getString(str3), ValueInteger.get(i), ValueBoolean.get(z));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getVersionColumns(String str, String str2, String str3) {
        return executeQuery(18, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getPrimaryKeys(String str, String str2, String str3) {
        return executeQuery(19, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getImportedKeys(String str, String str2, String str3) {
        return executeQuery(20, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getExportedKeys(String str, String str2, String str3) {
        return executeQuery(21, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getCrossReference(String str, String str2, String str3, String str4, String str5, String str6) {
        return executeQuery(22, getString(str), getString(str2), getString(str3), getString(str4), getString(str5), getString(str6));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTypeInfo() {
        return executeQuery(23, new Value[0]);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getIndexInfo(String str, String str2, String str3, boolean z, boolean z2) {
        return executeQuery(24, getString(str), getString(str2), getString(str3), ValueBoolean.get(z), ValueBoolean.get(z2));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getUDTs(String str, String str2, String str3, int[] iArr) {
        return executeQuery(25, getString(str), getString(str2), getString(str3), getIntArray(iArr));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getSuperTypes(String str, String str2, String str3) {
        return executeQuery(26, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getSuperTables(String str, String str2, String str3) {
        return executeQuery(27, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getAttributes(String str, String str2, String str3, String str4) {
        return executeQuery(28, getString(str), getString(str2), getString(str3), getString(str4));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public int getDatabaseMajorVersion() {
        ResultInterface executeQuery = executeQuery(29, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getInt();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public int getDatabaseMinorVersion() {
        ResultInterface executeQuery = executeQuery(30, new Value[0]);
        executeQuery.next();
        return executeQuery.currentRow()[0].getInt();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getSchemas(String str, String str2) {
        return executeQuery(31, getString(str), getString(str2));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getFunctions(String str, String str2, String str3) {
        return executeQuery(32, getString(str), getString(str2), getString(str3));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getFunctionColumns(String str, String str2, String str3, String str4) {
        return executeQuery(33, getString(str), getString(str2), getString(str3), getString(str4));
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getPseudoColumns(String str, String str2, String str3, String str4) {
        return executeQuery(34, getString(str), getString(str2), getString(str3), getString(str4));
    }

    private ResultInterface executeQuery(int i, Value... valueArr) {
        if (this.session.isClosed()) {
            throw DbException.get(ErrorCode.DATABASE_CALLED_AT_SHUTDOWN);
        }
        this.session.lock();
        try {
            int nextId = this.session.getNextId();
            int i2 = 0;
            for (int i3 = 0; i3 < this.transferList.size(); i3 = (i3 - 1) + 1) {
                Transfer transfer = this.transferList.get(i3);
                try {
                    this.session.traceOperation("GET_META", nextId);
                    transfer.writeInt(19).writeInt(i).writeInt(valueArr.length);
                    for (Value value : valueArr) {
                        transfer.writeValue(value);
                    }
                    this.session.done(transfer);
                    return new ResultRemote(this.session, transfer, nextId, transfer.readInt(), Integer.MAX_VALUE);
                } catch (IOException e) {
                    i2++;
                    this.session.removeServer(e, i3, i2);
                }
            }
            this.session.unlock();
            return null;
        } finally {
            this.session.unlock();
        }
    }

    private Value getIntArray(int[] iArr) {
        if (iArr == null) {
            return ValueNull.INSTANCE;
        }
        int length = iArr.length;
        Value[] valueArr = new Value[length];
        for (int i = 0; i < length; i++) {
            valueArr[i] = ValueInteger.get(iArr[i]);
        }
        return ValueArray.get(TypeInfo.TYPE_INTEGER, valueArr, this.session);
    }

    private Value getStringArray(String[] strArr) {
        if (strArr == null) {
            return ValueNull.INSTANCE;
        }
        int length = strArr.length;
        Value[] valueArr = new Value[length];
        for (int i = 0; i < length; i++) {
            valueArr[i] = getString(strArr[i]);
        }
        return ValueArray.get(TypeInfo.TYPE_VARCHAR, valueArr, this.session);
    }

    private Value getString(String str) {
        return str != null ? ValueVarchar.get(str, this.session) : ValueNull.INSTANCE;
    }
}
