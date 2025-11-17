package org.h2.table;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.engine.NullsDistinct;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.index.LinkedIndex;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcResultSet;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableLink.class */
public class TableLink extends Table {
    private static final int MAX_RETRY = 2;
    private static final long ROW_COUNT_APPROXIMATION = 100000;
    private final String originalSchema;
    private String driver;
    private String url;
    private String user;
    private String password;
    private String originalTable;
    private String qualifiedTableName;
    private TableLinkConnection conn;
    private HashMap<String, PreparedStatement> preparedMap;
    private final ArrayList<Index> indexes;
    private final boolean emitUpdates;
    private LinkedIndex linkedIndex;
    private DbException connectException;
    private boolean storesLowerCase;
    private boolean storesMixedCase;
    private boolean storesMixedCaseQuoted;
    private boolean supportsMixedCaseIdentifiers;
    private String identifierQuoteString;
    private boolean globalTemporary;
    private boolean readOnly;
    private final boolean targetsMySql;
    private int fetchSize;
    private boolean autocommit;

    public TableLink(Schema schema, int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, boolean z, boolean z2) {
        super(schema, i, str, false, true);
        this.preparedMap = new HashMap<>();
        this.indexes = Utils.newSmallArrayList();
        this.fetchSize = 0;
        this.autocommit = true;
        this.driver = str2;
        this.url = str3;
        this.user = str4;
        this.password = str5;
        this.originalSchema = str6;
        this.originalTable = str7;
        this.emitUpdates = z;
        this.targetsMySql = isMySqlUrl(this.url);
        try {
            connect();
        } catch (DbException e) {
            if (!z2) {
                throw e;
            }
            Column[] columnArr = new Column[0];
            setColumns(columnArr);
            this.linkedIndex = new LinkedIndex(this, i, IndexColumn.wrap(columnArr), 0, IndexType.createNonUnique(false));
            this.indexes.add(this.linkedIndex);
        }
    }

    private void connect() {
        this.connectException = null;
        int i = 0;
        while (true) {
            try {
                this.conn = this.database.getLinkConnection(this.driver, this.url, this.user, this.password);
                this.conn.setAutoCommit(this.autocommit);
                synchronized (this.conn) {
                    try {
                        readMetaData();
                    } catch (Exception e) {
                        this.conn.close(true);
                        this.conn = null;
                        throw DbException.convert(e);
                    }
                }
                return;
            } catch (DbException e2) {
                if (i < 2) {
                    i++;
                } else {
                    this.connectException = e2;
                    throw e2;
                }
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Type inference failed for: r0v134, types: [java.sql.ResultSet, java.sql.Statement] */
    private void readMetaData() throws SQLException {
        DatabaseMetaData metaData = this.conn.getConnection().getMetaData();
        this.storesLowerCase = metaData.storesLowerCaseIdentifiers();
        this.storesMixedCase = metaData.storesMixedCaseIdentifiers();
        this.storesMixedCaseQuoted = metaData.storesMixedCaseQuotedIdentifiers();
        this.supportsMixedCaseIdentifiers = metaData.supportsMixedCaseIdentifiers();
        this.identifierQuoteString = metaData.getIdentifierQuoteString();
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        HashMap<String, Column> hashMap = new HashMap<>();
        String str = null;
        boolean startsWith = this.originalTable.startsWith("(");
        if (!startsWith) {
            ResultSet tables = metaData.getTables(null, this.originalSchema, this.originalTable, null);
            Throwable th = null;
            try {
                try {
                    if (tables.next() && tables.next()) {
                        throw DbException.get(ErrorCode.SCHEMA_NAME_MUST_MATCH, this.originalTable);
                    }
                    if (tables != null) {
                        if (0 != 0) {
                            try {
                                tables.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            tables.close();
                        }
                    }
                    ?? columns = metaData.getColumns(null, this.originalSchema, this.originalTable, null);
                    Throwable th3 = null;
                    int i = 0;
                    String str2 = null;
                    while (columns.next()) {
                        try {
                            try {
                                String string = columns.getString("TABLE_CAT");
                                if (str2 == null) {
                                    str2 = string;
                                }
                                String string2 = columns.getString("TABLE_SCHEM");
                                if (str == null) {
                                    str = string2;
                                }
                                if (!Objects.equals(str2, string) || !Objects.equals(str, string2)) {
                                    hashMap.clear();
                                    newSmallArrayList.clear();
                                    break;
                                }
                                String convertColumnName = convertColumnName(columns.getString("COLUMN_NAME"));
                                int i2 = columns.getInt("DATA_TYPE");
                                int i3 = i;
                                i++;
                                Column column = new Column(convertColumnName, TypeInfo.getTypeInfo(DataType.convertSQLTypeToValueType(i2, columns.getString("TYPE_NAME")), convertPrecision(i2, columns.getInt("COLUMN_SIZE")), convertScale(i2, columns.getInt("DECIMAL_DIGITS")), null), this, i3);
                                newSmallArrayList.add(column);
                                hashMap.put(convertColumnName, column);
                            } catch (Throwable th4) {
                                if (columns != 0) {
                                    if (th3 != null) {
                                        try {
                                            columns.close();
                                        } catch (Throwable th5) {
                                            th3.addSuppressed(th5);
                                        }
                                    } else {
                                        columns.close();
                                    }
                                }
                                throw th4;
                            }
                        } catch (Throwable th6) {
                            th3 = th6;
                            throw th6;
                        }
                    }
                    if (columns != 0) {
                        if (0 != 0) {
                            try {
                                columns.close();
                            } catch (Throwable th7) {
                                th3.addSuppressed(th7);
                            }
                        } else {
                            columns.close();
                        }
                    }
                } catch (Throwable th8) {
                    if (tables != null) {
                        if (th != null) {
                            try {
                                tables.close();
                            } catch (Throwable th9) {
                                th.addSuppressed(th9);
                            }
                        } else {
                            tables.close();
                        }
                    }
                    throw th8;
                }
            } catch (Throwable th10) {
                th = th10;
                throw th10;
            }
        }
        if (this.originalTable.indexOf(46) >= 0 || StringUtils.isNullOrEmpty(str)) {
            this.qualifiedTableName = this.originalTable;
        } else {
            this.qualifiedTableName = str + '.' + this.originalTable;
        }
        try {
            try {
                Statement createStatement = this.conn.getConnection().createStatement();
                Throwable th11 = null;
                ResultSet executeQuery = createStatement.executeQuery("SELECT * FROM " + this.qualifiedTableName + " T WHERE 1=0");
                Throwable th12 = null;
                try {
                    if (executeQuery instanceof JdbcResultSet) {
                        ResultInterface result = ((JdbcResultSet) executeQuery).getResult();
                        newSmallArrayList.clear();
                        hashMap.clear();
                        int i4 = 0;
                        int visibleColumnCount = result.getVisibleColumnCount();
                        while (i4 < visibleColumnCount) {
                            String columnName = result.getColumnName(i4);
                            TypeInfo columnType = result.getColumnType(i4);
                            i4++;
                            Column column2 = new Column(columnName, columnType, this, i4);
                            newSmallArrayList.add(column2);
                            hashMap.put(columnName, column2);
                        }
                    } else if (newSmallArrayList.isEmpty()) {
                        ResultSetMetaData metaData2 = executeQuery.getMetaData();
                        int i5 = 0;
                        int columnCount = metaData2.getColumnCount();
                        while (i5 < columnCount) {
                            String convertColumnName2 = convertColumnName(metaData2.getColumnName(i5 + 1));
                            int columnType2 = metaData2.getColumnType(i5 + 1);
                            TypeInfo typeInfo = TypeInfo.getTypeInfo(DataType.getValueTypeFromResultSet(metaData2, i5 + 1), convertPrecision(columnType2, metaData2.getPrecision(i5 + 1)), convertScale(columnType2, metaData2.getScale(i5 + 1)), null);
                            int i6 = i5;
                            i5++;
                            Column column3 = new Column(convertColumnName2, typeInfo, this, i6);
                            newSmallArrayList.add(column3);
                            hashMap.put(convertColumnName2, column3);
                        }
                    }
                    if (executeQuery != null) {
                        if (0 != 0) {
                            try {
                                executeQuery.close();
                            } catch (Throwable th13) {
                                th12.addSuppressed(th13);
                            }
                        } else {
                            executeQuery.close();
                        }
                    }
                    if (createStatement != null) {
                        if (0 != 0) {
                            try {
                                createStatement.close();
                            } catch (Throwable th14) {
                                th11.addSuppressed(th14);
                            }
                        } else {
                            createStatement.close();
                        }
                    }
                    Column[] columnArr = (Column[]) newSmallArrayList.toArray(new Column[0]);
                    setColumns(columnArr);
                    this.linkedIndex = new LinkedIndex(this, getId(), IndexColumn.wrap(columnArr), 0, IndexType.createNonUnique(false));
                    this.indexes.add(this.linkedIndex);
                    if (startsWith) {
                        return;
                    }
                    readIndexes(metaData, hashMap);
                } catch (Throwable th15) {
                    if (executeQuery != null) {
                        if (0 != 0) {
                            try {
                                executeQuery.close();
                            } catch (Throwable th16) {
                                th12.addSuppressed(th16);
                            }
                        } else {
                            executeQuery.close();
                        }
                    }
                    throw th15;
                }
            } finally {
            }
        } catch (Exception e) {
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, e, this.originalTable + '(' + e + ')');
        }
    }

    private void readIndexes(DatabaseMetaData databaseMetaData, HashMap<String, Column> hashMap) {
        String str = null;
        try {
            ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(null, this.originalSchema, this.originalTable);
            Throwable th = null;
            try {
                try {
                    if (primaryKeys.next()) {
                        str = readPrimaryKey(primaryKeys, hashMap);
                    }
                    if (primaryKeys != null) {
                        if (0 != 0) {
                            try {
                                primaryKeys.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            primaryKeys.close();
                        }
                    }
                } finally {
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Exception e) {
        }
        try {
            ResultSet indexInfo = databaseMetaData.getIndexInfo(null, this.originalSchema, this.originalTable, false, true);
            Throwable th4 = null;
            try {
                try {
                    readIndexes(indexInfo, hashMap, str);
                    if (indexInfo != null) {
                        if (0 != 0) {
                            try {
                                indexInfo.close();
                            } catch (Throwable th5) {
                                th4.addSuppressed(th5);
                            }
                        } else {
                            indexInfo.close();
                        }
                    }
                } finally {
                }
            } catch (Throwable th6) {
                th4 = th6;
                throw th6;
            }
        } catch (Exception e2) {
        }
    }

    private String readPrimaryKey(ResultSet resultSet, HashMap<String, Column> hashMap) throws SQLException {
        String str = null;
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        do {
            int i = resultSet.getInt("KEY_SEQ");
            if (StringUtils.isNullOrEmpty(str)) {
                str = resultSet.getString("PK_NAME");
            }
            while (newSmallArrayList.size() < i) {
                newSmallArrayList.add(null);
            }
            Column column = hashMap.get(convertColumnName(resultSet.getString("COLUMN_NAME")));
            if (i == 0) {
                newSmallArrayList.add(column);
            } else {
                newSmallArrayList.set(i - 1, column);
            }
        } while (resultSet.next());
        addIndex(newSmallArrayList, newSmallArrayList.size(), IndexType.createPrimaryKey(false, false));
        return str;
    }

    private void readIndexes(ResultSet resultSet, HashMap<String, Column> hashMap, String str) throws SQLException {
        IndexType createNonUnique;
        String str2 = null;
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        int i = 0;
        IndexType indexType = null;
        while (resultSet.next()) {
            if (resultSet.getShort("TYPE") != 0) {
                String string = resultSet.getString("INDEX_NAME");
                if (str == null || !str.equals(string)) {
                    if (str2 != null && !str2.equals(string)) {
                        addIndex(newSmallArrayList, i, indexType);
                        i = 0;
                        str2 = null;
                    }
                    if (str2 == null) {
                        str2 = string;
                        newSmallArrayList.clear();
                    }
                    if (!resultSet.getBoolean("NON_UNIQUE")) {
                        i++;
                    }
                    if (i > 0) {
                        createNonUnique = IndexType.createUnique(false, false, i, NullsDistinct.NOT_DISTINCT);
                    } else {
                        createNonUnique = IndexType.createNonUnique(false);
                    }
                    indexType = createNonUnique;
                    newSmallArrayList.add(hashMap.get(convertColumnName(resultSet.getString("COLUMN_NAME"))));
                }
            }
        }
        if (str2 != null) {
            addIndex(newSmallArrayList, i, indexType);
        }
    }

    private static long convertPrecision(int i, long j) {
        switch (i) {
            case 2:
            case 3:
                if (j == 0) {
                    j = 65535;
                    break;
                }
                break;
            case 91:
                j = Math.max(10L, j);
                break;
            case 92:
                j = Math.max(18L, j);
                break;
            case 93:
                j = Math.max(29L, j);
                break;
        }
        return j;
    }

    private static int convertScale(int i, int i2) {
        switch (i) {
            case 2:
            case 3:
                if (i2 < 0) {
                    i2 = 32767;
                    break;
                }
                break;
        }
        return i2;
    }

    private String convertColumnName(String str) {
        if (this.targetsMySql) {
            str = StringUtils.toUpperEnglish(str);
        } else if ((this.storesMixedCase || this.storesLowerCase) && str.equals(StringUtils.toLowerEnglish(str))) {
            str = StringUtils.toUpperEnglish(str);
        } else if (this.storesMixedCase && !this.supportsMixedCaseIdentifiers) {
            str = StringUtils.toUpperEnglish(str);
        } else if (this.storesMixedCase && this.storesMixedCaseQuoted) {
            str = StringUtils.toUpperEnglish(str);
        }
        return str;
    }

    private void addIndex(List<Column> list, int i, IndexType indexType) {
        int indexOf = list.indexOf(null);
        if (indexOf == 0) {
            this.trace.info("Omitting linked index - no recognized columns.");
            return;
        }
        if (indexOf > 0) {
            this.trace.info("Unrecognized columns in linked index. Registering the index against the leading {0} recognized columns of {1} total columns.", Integer.valueOf(indexOf), Integer.valueOf(list.size()));
            list = list.subList(0, indexOf);
        }
        this.indexes.add(new LinkedIndex(this, 0, IndexColumn.wrap((Column[]) list.toArray(new Column[0])), i, indexType));
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        return getSQL(new StringBuilder("DROP TABLE IF EXISTS "), 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sb = new StringBuilder("CREATE FORCE ");
        if (isTemporary()) {
            if (this.globalTemporary) {
                sb.append("GLOBAL ");
            } else {
                sb.append("LOCAL ");
            }
            sb.append("TEMPORARY ");
        }
        sb.append("LINKED TABLE ");
        getSQL(sb, 0);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        sb.append('(');
        StringUtils.quoteStringSQL(sb, this.driver).append(", ");
        StringUtils.quoteStringSQL(sb, this.url).append(", ");
        StringUtils.quoteStringSQL(sb, this.user).append(", ");
        StringUtils.quoteStringSQL(sb, this.password).append(", ");
        StringUtils.quoteStringSQL(sb, this.originalTable).append(')');
        if (this.emitUpdates) {
            sb.append(" EMIT UPDATES");
        }
        if (this.readOnly) {
            sb.append(" READONLY");
        }
        if (this.fetchSize != 0) {
            sb.append(" FETCH_SIZE ").append(this.fetchSize);
        }
        if (!this.autocommit) {
            sb.append(" AUTOCOMMIT OFF");
        }
        sb.append(" /*").append(DbException.HIDE_SQL).append("*/");
        return sb.toString();
    }

    @Override // org.h2.table.Table
    public Index addIndex(SessionLocal sessionLocal, String str, int i, IndexColumn[] indexColumnArr, int i2, IndexType indexType, boolean z, String str2) {
        throw DbException.getUnsupportedException("LINK");
    }

    @Override // org.h2.table.Table
    public Index getScanIndex(SessionLocal sessionLocal) {
        return this.linkedIndex;
    }

    @Override // org.h2.table.Table
    public boolean isInsertable() {
        return !this.readOnly;
    }

    private void checkReadOnly() {
        if (this.readOnly) {
            throw DbException.get(ErrorCode.DATABASE_IS_READ_ONLY);
        }
    }

    @Override // org.h2.table.Table
    public void removeRow(SessionLocal sessionLocal, Row row) {
        checkReadOnly();
        getScanIndex(sessionLocal).remove(sessionLocal, row);
    }

    @Override // org.h2.table.Table
    public void addRow(SessionLocal sessionLocal, Row row) {
        checkReadOnly();
        getScanIndex(sessionLocal).add(sessionLocal, row);
    }

    @Override // org.h2.table.Table
    public void close(SessionLocal sessionLocal) {
        if (this.conn != null) {
            try {
                this.conn.close(false);
            } finally {
                this.conn = null;
            }
        }
    }

    @Override // org.h2.table.Table
    public synchronized long getRowCount(SessionLocal sessionLocal) {
        String str = "SELECT COUNT(*) FROM " + this.qualifiedTableName + " T";
        try {
            PreparedStatement execute = execute(str, null, false, sessionLocal);
            ResultSet resultSet = execute.getResultSet();
            resultSet.next();
            long j = resultSet.getLong(1);
            resultSet.close();
            reusePreparedStatement(execute, str);
            return j;
        } catch (Exception e) {
            throw wrapException(str, e);
        }
    }

    public static DbException wrapException(String str, Exception exc) {
        SQLException sQLException = DbException.toSQLException(exc);
        return DbException.get(ErrorCode.ERROR_ACCESSING_LINKED_TABLE_2, sQLException, str, sQLException.toString());
    }

    public String getQualifiedTable() {
        return this.qualifiedTableName;
    }

    public PreparedStatement execute(String str, ArrayList<Value> arrayList, boolean z, SessionLocal sessionLocal) {
        if (this.conn == null) {
            throw this.connectException;
        }
        int i = 0;
        while (true) {
            try {
                synchronized (this.conn) {
                    PreparedStatement remove = this.preparedMap.remove(str);
                    if (remove == null) {
                        remove = this.conn.getConnection().prepareStatement(str);
                        if (this.fetchSize != 0) {
                            remove.setFetchSize(this.fetchSize);
                        }
                    }
                    if (this.trace.isDebugEnabled()) {
                        StringBuilder append = new StringBuilder(getName()).append(":\n").append(str);
                        if (arrayList != null && !arrayList.isEmpty()) {
                            append.append(" {");
                            int i2 = 0;
                            int size = arrayList.size();
                            while (i2 < size) {
                                Value value = arrayList.get(i2);
                                if (i2 > 0) {
                                    append.append(", ");
                                }
                                i2++;
                                append.append(i2).append(": ");
                                value.getSQL(append, 0);
                            }
                            append.append('}');
                        }
                        append.append(';');
                        this.trace.debug(append.toString());
                    }
                    if (arrayList != null) {
                        JdbcConnection createConnection = sessionLocal.createConnection(false);
                        int size2 = arrayList.size();
                        for (int i3 = 0; i3 < size2; i3++) {
                            JdbcUtils.set(remove, i3 + 1, arrayList.get(i3), createConnection);
                        }
                    }
                    remove.execute();
                    if (z) {
                        reusePreparedStatement(remove, str);
                        return null;
                    }
                    return remove;
                }
            } catch (SQLException e) {
                if (i >= 2) {
                    throw DbException.convert(e);
                }
                this.conn.close(true);
                connect();
                i++;
            }
        }
    }

    @Override // org.h2.table.Table
    public void checkSupportAlter() {
        throw DbException.getUnsupportedException("LINK");
    }

    @Override // org.h2.table.Table
    public long truncate(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("LINK");
    }

    @Override // org.h2.table.Table
    public boolean canGetRowCount(SessionLocal sessionLocal) {
        return true;
    }

    @Override // org.h2.table.Table
    public boolean canDrop() {
        return true;
    }

    @Override // org.h2.table.Table
    public TableType getTableType() {
        return TableType.TABLE_LINK;
    }

    @Override // org.h2.table.Table, org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        super.removeChildrenAndResources(sessionLocal);
        close(sessionLocal);
        this.database.removeMeta(sessionLocal, getId());
        this.driver = null;
        this.originalTable = null;
        this.password = null;
        this.user = null;
        this.url = null;
        this.preparedMap = null;
        invalidate();
    }

    public boolean isOracle() {
        return this.url.startsWith("jdbc:oracle:");
    }

    private static boolean isMySqlUrl(String str) {
        return str.startsWith("jdbc:mysql:") || str.startsWith("jdbc:mariadb:");
    }

    @Override // org.h2.table.Table
    public ArrayList<Index> getIndexes() {
        return this.indexes;
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        return Long.MAX_VALUE;
    }

    @Override // org.h2.table.Table
    public void updateRows(Prepared prepared, SessionLocal sessionLocal, LocalResult localResult) {
        checkReadOnly();
        if (this.emitUpdates) {
            while (localResult.next()) {
                prepared.checkCanceled();
                Row currentRowForTable = localResult.currentRowForTable();
                localResult.next();
                this.linkedIndex.update(currentRowForTable, localResult.currentRowForTable(), sessionLocal);
            }
            return;
        }
        super.updateRows(prepared, sessionLocal, localResult);
    }

    public void setGlobalTemporary(boolean z) {
        this.globalTemporary = z;
    }

    public void setReadOnly(boolean z) {
        this.readOnly = z;
    }

    @Override // org.h2.table.Table
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return ROW_COUNT_APPROXIMATION;
    }

    public void reusePreparedStatement(PreparedStatement preparedStatement, String str) {
        synchronized (this.conn) {
            this.preparedMap.put(str, preparedStatement);
        }
    }

    @Override // org.h2.table.Table
    public boolean isDeterministic() {
        return false;
    }

    @Override // org.h2.table.Table
    public void checkWritingAllowed() {
    }

    @Override // org.h2.table.Table
    public void convertInsertRow(SessionLocal sessionLocal, Row row, Boolean bool) {
        convertRow(sessionLocal, row);
    }

    @Override // org.h2.table.Table
    public void convertUpdateRow(SessionLocal sessionLocal, Row row, boolean z) {
        convertRow(sessionLocal, row);
    }

    private void convertRow(SessionLocal sessionLocal, Row row) {
        Value validateConvertUpdateSequence;
        for (int i = 0; i < this.columns.length; i++) {
            Value value = row.getValue(i);
            if (value != null && (validateConvertUpdateSequence = this.columns[i].validateConvertUpdateSequence(sessionLocal, value, row)) != value) {
                row.setValue(i, validateConvertUpdateSequence);
            }
        }
    }

    public void setFetchSize(int i) {
        this.fetchSize = i;
    }

    public void setAutoCommit(boolean z) {
        this.autocommit = z;
    }

    public boolean getAutocommit() {
        return this.autocommit;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public String getIdentifierQuoteString() {
        return this.identifierQuoteString;
    }
}
