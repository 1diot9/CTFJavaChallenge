package org.h2.jdbc.meta;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/meta/DatabaseMetaServer.class */
public final class DatabaseMetaServer {
    public static ResultInterface process(SessionLocal sessionLocal, int i, Value[] valueArr) {
        DatabaseMeta databaseMeta = sessionLocal.getDatabaseMeta();
        switch (i) {
            case 0:
                return result(databaseMeta.defaultNullOrdering().ordinal());
            case 1:
                return result(sessionLocal, databaseMeta.getDatabaseProductVersion());
            case 2:
                return result(sessionLocal, databaseMeta.getSQLKeywords());
            case 3:
                return result(sessionLocal, databaseMeta.getNumericFunctions());
            case 4:
                return result(sessionLocal, databaseMeta.getStringFunctions());
            case 5:
                return result(sessionLocal, databaseMeta.getSystemFunctions());
            case 6:
                return result(sessionLocal, databaseMeta.getTimeDateFunctions());
            case 7:
                return result(sessionLocal, databaseMeta.getSearchStringEscape());
            case 8:
                return databaseMeta.getProcedures(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 9:
                return databaseMeta.getProcedureColumns(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getString());
            case 10:
                return databaseMeta.getTables(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), toStringArray(valueArr[3]));
            case 11:
                return databaseMeta.getSchemas();
            case 12:
                return databaseMeta.getCatalogs();
            case 13:
                return databaseMeta.getTableTypes();
            case 14:
                return databaseMeta.getColumns(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getString());
            case 15:
                return databaseMeta.getColumnPrivileges(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getString());
            case 16:
                return databaseMeta.getTablePrivileges(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 17:
                return databaseMeta.getBestRowIdentifier(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getInt(), valueArr[4].getBoolean());
            case 18:
                return databaseMeta.getVersionColumns(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 19:
                return databaseMeta.getPrimaryKeys(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 20:
                return databaseMeta.getImportedKeys(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 21:
                return databaseMeta.getExportedKeys(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 22:
                return databaseMeta.getCrossReference(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getString(), valueArr[4].getString(), valueArr[5].getString());
            case 23:
                return databaseMeta.getTypeInfo();
            case 24:
                return databaseMeta.getIndexInfo(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getBoolean(), valueArr[4].getBoolean());
            case 25:
                return databaseMeta.getUDTs(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), toIntArray(valueArr[3]));
            case 26:
                return databaseMeta.getSuperTypes(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 27:
                return databaseMeta.getSuperTables(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 28:
                return databaseMeta.getAttributes(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getString());
            case 29:
                return result(databaseMeta.getDatabaseMajorVersion());
            case 30:
                return result(databaseMeta.getDatabaseMinorVersion());
            case 31:
                return databaseMeta.getSchemas(valueArr[0].getString(), valueArr[1].getString());
            case 32:
                return databaseMeta.getFunctions(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString());
            case 33:
                return databaseMeta.getFunctionColumns(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getString());
            case 34:
                return databaseMeta.getPseudoColumns(valueArr[0].getString(), valueArr[1].getString(), valueArr[2].getString(), valueArr[3].getString());
            default:
                throw DbException.getUnsupportedException("META " + i);
        }
    }

    private static String[] toStringArray(Value value) {
        if (value == ValueNull.INSTANCE) {
            return null;
        }
        Value[] list = ((ValueArray) value).getList();
        int length = list.length;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = list[i].getString();
        }
        return strArr;
    }

    private static int[] toIntArray(Value value) {
        if (value == ValueNull.INSTANCE) {
            return null;
        }
        Value[] list = ((ValueArray) value).getList();
        int length = list.length;
        int[] iArr = new int[length];
        for (int i = 0; i < length; i++) {
            iArr[i] = list[i].getInt();
        }
        return iArr;
    }

    private static ResultInterface result(int i) {
        return result(ValueInteger.get(i));
    }

    private static ResultInterface result(SessionLocal sessionLocal, String str) {
        return result(ValueVarchar.get(str, sessionLocal));
    }

    private static ResultInterface result(Value value) {
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("RESULT", value.getType());
        simpleResult.addRow(value);
        return simpleResult;
    }

    private DatabaseMetaServer() {
    }
}
