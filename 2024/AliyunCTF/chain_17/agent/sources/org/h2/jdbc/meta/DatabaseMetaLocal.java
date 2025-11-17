package org.h2.jdbc.meta;

import cn.hutool.core.text.StrPool;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.dml.Help;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.constraint.ConstraintReferential;
import org.h2.constraint.ConstraintUnique;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Mode;
import org.h2.engine.Right;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.condition.CompareLike;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.result.SortOrder;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.UserDefinedFunction;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableSynonym;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueToObjectConverter2;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/meta/DatabaseMetaLocal.class */
public final class DatabaseMetaLocal extends DatabaseMetaLocalBase {
    private static final Value YES = ValueVarchar.get("YES");
    private static final Value NO = ValueVarchar.get("NO");
    private static final ValueSmallint BEST_ROW_SESSION = ValueSmallint.get(2);
    private static final ValueSmallint BEST_ROW_NOT_PSEUDO = ValueSmallint.get(1);
    private static final ValueInteger COLUMN_NO_NULLS = ValueInteger.get(0);
    private static final ValueSmallint COLUMN_NO_NULLS_SMALL = ValueSmallint.get(0);
    private static final ValueInteger COLUMN_NULLABLE = ValueInteger.get(1);
    private static final ValueSmallint COLUMN_NULLABLE_UNKNOWN_SMALL = ValueSmallint.get(2);
    private static final ValueSmallint IMPORTED_KEY_CASCADE = ValueSmallint.get(0);
    private static final ValueSmallint IMPORTED_KEY_RESTRICT = ValueSmallint.get(1);
    private static final ValueSmallint IMPORTED_KEY_DEFAULT = ValueSmallint.get(4);
    private static final ValueSmallint IMPORTED_KEY_SET_NULL = ValueSmallint.get(2);
    private static final ValueSmallint IMPORTED_KEY_NOT_DEFERRABLE = ValueSmallint.get(7);
    private static final ValueSmallint PROCEDURE_COLUMN_IN = ValueSmallint.get(1);
    private static final ValueSmallint PROCEDURE_COLUMN_RETURN = ValueSmallint.get(5);
    private static final ValueSmallint PROCEDURE_NO_RESULT = ValueSmallint.get(1);
    private static final ValueSmallint PROCEDURE_RETURNS_RESULT = ValueSmallint.get(2);
    private static final ValueSmallint TABLE_INDEX_HASHED = ValueSmallint.get(2);
    private static final ValueSmallint TABLE_INDEX_OTHER = ValueSmallint.get(3);
    private static final String[] TABLE_TYPES = {"BASE TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "SYNONYM", "VIEW"};
    private static final ValueSmallint TYPE_NULLABLE = ValueSmallint.get(1);
    private static final ValueSmallint TYPE_SEARCHABLE = ValueSmallint.get(3);
    private static final Value NO_USAGE_RESTRICTIONS = ValueVarchar.get("NO_USAGE_RESTRICTIONS");
    private final SessionLocal session;

    public DatabaseMetaLocal(SessionLocal sessionLocal) {
        this.session = sessionLocal;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public final DefaultNullOrdering defaultNullOrdering() {
        return this.session.getDatabase().getDefaultNullOrdering();
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getSQLKeywords() {
        StringBuilder append = new StringBuilder(103).append("CURRENT_CATALOG,CURRENT_SCHEMA,GROUPS,IF,ILIKE,KEY,");
        Mode mode = this.session.getMode();
        if (mode.limit) {
            append.append("LIMIT,");
        }
        if (mode.minusIsExcept) {
            append.append("MINUS,");
        }
        append.append("OFFSET,QUALIFY,REGEXP,ROWNUM,");
        if (mode.topInSelect || mode.topInDML) {
            append.append("TOP,");
        }
        return append.append(Column.ROWID).toString();
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
        checkClosed();
        StringBuilder sb = new StringBuilder();
        try {
            ResultSet table = Help.getTable();
            while (table.next()) {
                if (table.getString(1).trim().equals(str)) {
                    if (sb.length() != 0) {
                        sb.append(',');
                    }
                    String trim = table.getString(2).trim();
                    int indexOf = trim.indexOf(32);
                    if (indexOf >= 0) {
                        StringUtils.trimSubstring(sb, trim, 0, indexOf);
                    } else {
                        sb.append(trim);
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public String getSearchStringEscape() {
        return this.session.getDatabase().getSettings().defaultEscape;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getProcedures(String str, String str2, String str3) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("PROCEDURE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PROCEDURE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PROCEDURE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("RESERVED1", TypeInfo.TYPE_NULL);
        simpleResult.addColumn("RESERVED2", TypeInfo.TYPE_NULL);
        simpleResult.addColumn("RESERVED3", TypeInfo.TYPE_NULL);
        simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PROCEDURE_TYPE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Value string = getString(this.session.getDatabase().getShortName());
        CompareLike like = getLike(str3);
        for (Schema schema : getSchemasForPattern(str2)) {
            Value string2 = getString(schema.getName());
            for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
                String name = userDefinedFunction.getName();
                if (like == null || like.test(name)) {
                    Value string3 = getString(name);
                    if (userDefinedFunction instanceof FunctionAlias) {
                        try {
                            FunctionAlias.JavaMethod[] javaMethods = ((FunctionAlias) userDefinedFunction).getJavaMethods();
                            for (int i = 0; i < javaMethods.length; i++) {
                                TypeInfo dataType = javaMethods[i].getDataType();
                                getProceduresAdd(simpleResult, string, string2, string3, userDefinedFunction.getComment(), (dataType == null || dataType.getValueType() != 0) ? PROCEDURE_RETURNS_RESULT : PROCEDURE_NO_RESULT, getString(name + '_' + (i + 1)));
                            }
                        } catch (DbException e) {
                        }
                    } else {
                        getProceduresAdd(simpleResult, string, string2, string3, userDefinedFunction.getComment(), PROCEDURE_RETURNS_RESULT, string3);
                    }
                }
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{1, 2, 8}));
        return simpleResult;
    }

    private void getProceduresAdd(SimpleResult simpleResult, Value value, Value value2, Value value3, String str, ValueSmallint valueSmallint, Value value4) {
        simpleResult.addRow(value, value2, value3, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, getString(str), valueSmallint, value4);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getProcedureColumns(String str, String str2, String str3, String str4) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("PROCEDURE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PROCEDURE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PROCEDURE_NAME", TypeInfo.TYPE_VARCHAR);
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
        simpleResult.addColumn("COLUMN_DEF", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Value string = getString(this.session.getDatabase().getShortName());
        CompareLike like = getLike(str3);
        for (Schema schema : getSchemasForPattern(str2)) {
            Value string2 = getString(schema.getName());
            for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
                if (userDefinedFunction instanceof FunctionAlias) {
                    String name = userDefinedFunction.getName();
                    if (like == null || like.test(name)) {
                        Value string3 = getString(name);
                        try {
                            FunctionAlias.JavaMethod[] javaMethods = ((FunctionAlias) userDefinedFunction).getJavaMethods();
                            int length = javaMethods.length;
                            for (int i = 0; i < length; i++) {
                                FunctionAlias.JavaMethod javaMethod = javaMethods[i];
                                Value string4 = getString(name + '_' + (i + 1));
                                TypeInfo dataType = javaMethod.getDataType();
                                if (dataType != null && dataType.getValueType() != 0) {
                                    getProcedureColumnAdd(simpleResult, string, string2, string3, string4, dataType, javaMethod.getClass().isPrimitive(), 0);
                                }
                                Class<?>[] columnClasses = javaMethod.getColumnClasses();
                                int i2 = 1;
                                int length2 = columnClasses.length;
                                for (int i3 = javaMethod.hasConnectionParam() ? 1 : 0; i3 < length2; i3++) {
                                    Class<?> cls = columnClasses[i3];
                                    getProcedureColumnAdd(simpleResult, string, string2, string3, string4, ValueToObjectConverter2.classToType(cls), cls.isPrimitive(), i2);
                                    i2++;
                                }
                            }
                        } catch (DbException e) {
                        }
                    }
                }
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{1, 2, 19}));
        return simpleResult;
    }

    private void getProcedureColumnAdd(SimpleResult simpleResult, Value value, Value value2, Value value3, Value value4, TypeInfo typeInfo, boolean z, int i) {
        int valueType = typeInfo.getValueType();
        DataType dataType = DataType.getDataType(valueType);
        ValueInteger valueInteger = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
        Value[] valueArr = new Value[20];
        valueArr[0] = value;
        valueArr[1] = value2;
        valueArr[2] = value3;
        valueArr[3] = getString(i == 0 ? "RESULT" : "P" + i);
        valueArr[4] = i == 0 ? PROCEDURE_COLUMN_RETURN : PROCEDURE_COLUMN_IN;
        valueArr[5] = ValueInteger.get(DataType.convertTypeToSQLType(typeInfo));
        valueArr[6] = getDataTypeName(typeInfo);
        valueArr[7] = valueInteger;
        valueArr[8] = valueInteger;
        valueArr[9] = dataType.supportsScale ? ValueSmallint.get(MathUtils.convertIntToShort(dataType.defaultScale)) : ValueNull.INSTANCE;
        valueArr[10] = getRadix(valueType, true);
        valueArr[11] = z ? COLUMN_NO_NULLS_SMALL : COLUMN_NULLABLE_UNKNOWN_SMALL;
        valueArr[12] = ValueNull.INSTANCE;
        valueArr[13] = ValueNull.INSTANCE;
        valueArr[14] = ValueNull.INSTANCE;
        valueArr[15] = ValueNull.INSTANCE;
        valueArr[16] = (DataType.isBinaryStringType(valueType) || DataType.isCharacterStringType(valueType)) ? valueInteger : ValueNull.INSTANCE;
        valueArr[17] = ValueInteger.get(i);
        valueArr[18] = ValueVarchar.EMPTY;
        valueArr[19] = value4;
        simpleResult.addRow(valueArr);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTables(String str, String str2, String str3, String[] strArr) {
        HashSet<String> hashSet;
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_TYPE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SELF_REFERENCING_COL_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("REF_GENERATION", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Value string = getString(this.session.getDatabase().getShortName());
        if (strArr != null) {
            hashSet = new HashSet<>(8);
            for (String str4 : strArr) {
                int binarySearch = Arrays.binarySearch(TABLE_TYPES, str4);
                if (binarySearch >= 0) {
                    hashSet.add(TABLE_TYPES[binarySearch]);
                } else if (str4.equals("TABLE")) {
                    hashSet.add("BASE TABLE");
                }
            }
            if (hashSet.isEmpty()) {
                return simpleResult;
            }
        } else {
            hashSet = null;
        }
        for (Schema schema : getSchemasForPattern(str2)) {
            Value string2 = getString(schema.getName());
            for (SchemaObject schemaObject : getTablesForPattern(schema, str3)) {
                Value string3 = getString(schemaObject.getName());
                if (schemaObject instanceof Table) {
                    Table table = (Table) schemaObject;
                    if (!table.isHidden()) {
                        getTablesAdd(simpleResult, string, string2, string3, table, false, hashSet);
                    }
                } else {
                    getTablesAdd(simpleResult, string, string2, string3, ((TableSynonym) schemaObject).getSynonymFor(), true, hashSet);
                }
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{3, 1, 2}));
        return simpleResult;
    }

    private void getTablesAdd(SimpleResult simpleResult, Value value, Value value2, Value value3, Table table, boolean z, HashSet<String> hashSet) {
        String sQLTableType = z ? "SYNONYM" : table.getSQLTableType();
        if (hashSet != null && !hashSet.contains(sQLTableType)) {
            return;
        }
        simpleResult.addRow(value, value2, value3, getString(sQLTableType), getString(table.getComment()), ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getSchemas() {
        return getSchemas(null, null);
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getCatalogs() {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addRow(getString(this.session.getDatabase().getShortName()));
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTableTypes() {
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_TYPE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addRow(getString("BASE TABLE"));
        simpleResult.addRow(getString("GLOBAL TEMPORARY"));
        simpleResult.addRow(getString("LOCAL TEMPORARY"));
        simpleResult.addRow(getString("SYNONYM"));
        simpleResult.addRow(getString("VIEW"));
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getColumns(String str, String str2, String str3, String str4) {
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("BUFFER_LENGTH", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_DEF", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SCOPE_CATALOG", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SCOPE_SCHEMA", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SCOPE_TABLE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("IS_AUTOINCREMENT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("IS_GENERATEDCOLUMN", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Value string = getString(this.session.getDatabase().getShortName());
        CompareLike like = getLike(str4);
        for (Schema schema : getSchemasForPattern(str2)) {
            Value string2 = getString(schema.getName());
            for (SchemaObject schemaObject : getTablesForPattern(schema, str3)) {
                Value string3 = getString(schemaObject.getName());
                if (schemaObject instanceof Table) {
                    Table table = (Table) schemaObject;
                    if (!table.isHidden()) {
                        getColumnsAdd(simpleResult, string, string2, string3, table, like);
                    }
                } else {
                    getColumnsAdd(simpleResult, string, string2, string3, ((TableSynonym) schemaObject).getSynonymFor(), like);
                }
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{1, 2, 16}));
        return simpleResult;
    }

    private void getColumnsAdd(SimpleResult simpleResult, Value value, Value value2, Value value3, Table table, CompareLike compareLike) {
        int i = 0;
        for (Column column : table.getColumns()) {
            if (column.getVisible()) {
                i++;
                String name = column.getName();
                if (compareLike == null || compareLike.test(name)) {
                    TypeInfo type = column.getType();
                    ValueInteger valueInteger = ValueInteger.get(MathUtils.convertLongToInt(type.getPrecision()));
                    boolean isNullable = column.isNullable();
                    boolean isGenerated = column.isGenerated();
                    Value[] valueArr = new Value[24];
                    valueArr[0] = value;
                    valueArr[1] = value2;
                    valueArr[2] = value3;
                    valueArr[3] = getString(name);
                    valueArr[4] = ValueInteger.get(DataType.convertTypeToSQLType(type));
                    valueArr[5] = getDataTypeName(type);
                    valueArr[6] = valueInteger;
                    valueArr[7] = ValueNull.INSTANCE;
                    valueArr[8] = ValueInteger.get(type.getScale());
                    valueArr[9] = getRadix(type.getValueType(), false);
                    valueArr[10] = isNullable ? COLUMN_NULLABLE : COLUMN_NO_NULLS;
                    valueArr[11] = getString(column.getComment());
                    valueArr[12] = isGenerated ? ValueNull.INSTANCE : getString(column.getDefaultSQL());
                    valueArr[13] = ValueNull.INSTANCE;
                    valueArr[14] = ValueNull.INSTANCE;
                    valueArr[15] = valueInteger;
                    valueArr[16] = ValueInteger.get(i);
                    valueArr[17] = isNullable ? YES : NO;
                    valueArr[18] = ValueNull.INSTANCE;
                    valueArr[19] = ValueNull.INSTANCE;
                    valueArr[20] = ValueNull.INSTANCE;
                    valueArr[21] = ValueNull.INSTANCE;
                    valueArr[22] = column.isIdentity() ? YES : NO;
                    valueArr[23] = isGenerated ? YES : NO;
                    simpleResult.addRow(valueArr);
                }
            }
        }
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getColumnPrivileges(String str, String str2, String str3, String str4) {
        if (str3 == null) {
            throw DbException.getInvalidValueException("table", null);
        }
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("GRANTOR", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("GRANTEE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PRIVILEGE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("IS_GRANTABLE", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Database database = this.session.getDatabase();
        Value string = getString(database.getShortName());
        CompareLike like = getLike(str4);
        Iterator<Right> it = database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            DbObject grantedObject = next.getGrantedObject();
            if (grantedObject instanceof Table) {
                Table table = (Table) grantedObject;
                if (!table.isHidden()) {
                    String name = table.getName();
                    if (database.equalsIdentifiers(str3, name)) {
                        Schema schema = table.getSchema();
                        if (checkSchema(str2, schema)) {
                            addPrivileges(simpleResult, string, schema.getName(), name, next.getGrantee(), next.getRightMask(), like, table.getColumns());
                        }
                    }
                }
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{3, 6}));
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTablePrivileges(String str, String str2, String str3) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("GRANTOR", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("GRANTEE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PRIVILEGE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("IS_GRANTABLE", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Database database = this.session.getDatabase();
        Value string = getString(database.getShortName());
        CompareLike like = getLike(str2);
        CompareLike like2 = getLike(str3);
        Iterator<Right> it = database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            DbObject grantedObject = next.getGrantedObject();
            if (grantedObject instanceof Table) {
                Table table = (Table) grantedObject;
                if (!table.isHidden()) {
                    String name = table.getName();
                    if (like2 == null || like2.test(name)) {
                        Schema schema = table.getSchema();
                        String name2 = schema.getName();
                        if (str2 != null) {
                            if (str2.isEmpty()) {
                                if (schema != database.getMainSchema()) {
                                }
                            } else if (!like.test(name2)) {
                            }
                        }
                        addPrivileges(simpleResult, string, name2, name, next.getGrantee(), next.getRightMask(), null, null);
                    }
                }
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{1, 2, 5}));
        return simpleResult;
    }

    private void addPrivileges(SimpleResult simpleResult, Value value, String str, String str2, DbObject dbObject, int i, CompareLike compareLike, Column[] columnArr) {
        Value string = getString(str);
        Value string2 = getString(str2);
        Value string3 = getString(dbObject.getName());
        boolean z = dbObject.getType() == 2 && ((User) dbObject).isAdmin();
        if ((i & 1) != 0) {
            addPrivilege(simpleResult, value, string, string2, string3, "SELECT", z, compareLike, columnArr);
        }
        if ((i & 4) != 0) {
            addPrivilege(simpleResult, value, string, string2, string3, "INSERT", z, compareLike, columnArr);
        }
        if ((i & 8) != 0) {
            addPrivilege(simpleResult, value, string, string2, string3, "UPDATE", z, compareLike, columnArr);
        }
        if ((i & 2) != 0) {
            addPrivilege(simpleResult, value, string, string2, string3, "DELETE", z, compareLike, columnArr);
        }
    }

    private void addPrivilege(SimpleResult simpleResult, Value value, Value value2, Value value3, Value value4, String str, boolean z, CompareLike compareLike, Column[] columnArr) {
        if (columnArr == null) {
            Value[] valueArr = new Value[7];
            valueArr[0] = value;
            valueArr[1] = value2;
            valueArr[2] = value3;
            valueArr[3] = ValueNull.INSTANCE;
            valueArr[4] = value4;
            valueArr[5] = getString(str);
            valueArr[6] = z ? YES : NO;
            simpleResult.addRow(valueArr);
            return;
        }
        for (Column column : columnArr) {
            String name = column.getName();
            if (compareLike == null || compareLike.test(name)) {
                Value[] valueArr2 = new Value[8];
                valueArr2[0] = value;
                valueArr2[1] = value2;
                valueArr2[2] = value3;
                valueArr2[3] = getString(name);
                valueArr2[4] = ValueNull.INSTANCE;
                valueArr2[5] = value4;
                valueArr2[6] = getString(str);
                valueArr2[7] = z ? YES : NO;
                simpleResult.addRow(valueArr2);
            }
        }
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getBestRowIdentifier(String str, String str2, String str3, int i, boolean z) {
        ArrayList<Constraint> constraints;
        if (str3 == null) {
            throw DbException.getInvalidValueException("table", null);
        }
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
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Iterator<Schema> it = getSchemas(str2).iterator();
        while (it.hasNext()) {
            Table findTableOrView = it.next().findTableOrView(this.session, str3);
            if (findTableOrView != null && !findTableOrView.isHidden() && (constraints = findTableOrView.getConstraints()) != null) {
                Iterator<Constraint> it2 = constraints.iterator();
                while (it2.hasNext()) {
                    Constraint next = it2.next();
                    if (next.getConstraintType() == Constraint.Type.PRIMARY_KEY) {
                        for (IndexColumn indexColumn : ((ConstraintUnique) next).getColumns()) {
                            Column column = indexColumn.column;
                            TypeInfo type = column.getType();
                            DataType dataType = DataType.getDataType(type.getValueType());
                            Value[] valueArr = new Value[8];
                            valueArr[0] = BEST_ROW_SESSION;
                            valueArr[1] = getString(column.getName());
                            valueArr[2] = ValueInteger.get(DataType.convertTypeToSQLType(type));
                            valueArr[3] = getDataTypeName(type);
                            valueArr[4] = ValueInteger.get(MathUtils.convertLongToInt(type.getPrecision()));
                            valueArr[5] = ValueNull.INSTANCE;
                            valueArr[6] = dataType.supportsScale ? ValueSmallint.get(MathUtils.convertIntToShort(type.getScale())) : ValueNull.INSTANCE;
                            valueArr[7] = BEST_ROW_NOT_PSEUDO;
                            simpleResult.addRow(valueArr);
                        }
                    }
                }
            }
        }
        return simpleResult;
    }

    private Value getDataTypeName(TypeInfo typeInfo) {
        return getString(typeInfo.getDeclaredTypeName());
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getPrimaryKeys(String str, String str2, String str3) {
        ArrayList<Constraint> constraints;
        if (str3 == null) {
            throw DbException.getInvalidValueException("table", null);
        }
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("KEY_SEQ", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("PK_NAME", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Value string = getString(this.session.getDatabase().getShortName());
        for (Schema schema : getSchemas(str2)) {
            Table findTableOrView = schema.findTableOrView(this.session, str3);
            if (findTableOrView != null && !findTableOrView.isHidden() && (constraints = findTableOrView.getConstraints()) != null) {
                Iterator<Constraint> it = constraints.iterator();
                while (it.hasNext()) {
                    Constraint next = it.next();
                    if (next.getConstraintType() == Constraint.Type.PRIMARY_KEY) {
                        Value string2 = getString(schema.getName());
                        Value string3 = getString(findTableOrView.getName());
                        Value string4 = getString(next.getName());
                        IndexColumn[] columns = ((ConstraintUnique) next).getColumns();
                        int i = 0;
                        int length = columns.length;
                        while (i < length) {
                            i++;
                            simpleResult.addRow(string, string2, string3, getString(columns[i].column.getName()), ValueSmallint.get((short) i), string4);
                        }
                    }
                }
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{3}));
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getImportedKeys(String str, String str2, String str3) {
        ArrayList<Constraint> constraints;
        ConstraintReferential constraintReferential;
        Table table;
        if (str3 == null) {
            throw DbException.getInvalidValueException("table", null);
        }
        SimpleResult initCrossReferenceResult = initCrossReferenceResult();
        if (!checkCatalogName(str)) {
            return initCrossReferenceResult;
        }
        Value string = getString(this.session.getDatabase().getShortName());
        Iterator<Schema> it = getSchemas(str2).iterator();
        while (it.hasNext()) {
            Table findTableOrView = it.next().findTableOrView(this.session, str3);
            if (findTableOrView != null && !findTableOrView.isHidden() && (constraints = findTableOrView.getConstraints()) != null) {
                Iterator<Constraint> it2 = constraints.iterator();
                while (it2.hasNext()) {
                    Constraint next = it2.next();
                    if (next.getConstraintType() == Constraint.Type.REFERENTIAL && (table = (constraintReferential = (ConstraintReferential) next).getTable()) == findTableOrView) {
                        Table refTable = constraintReferential.getRefTable();
                        addCrossReferenceResult(initCrossReferenceResult, string, refTable.getSchema().getName(), refTable, table.getSchema().getName(), table, constraintReferential);
                    }
                }
            }
        }
        initCrossReferenceResult.sortRows(new SortOrder(this.session, new int[]{1, 2, 8}));
        return initCrossReferenceResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getExportedKeys(String str, String str2, String str3) {
        ArrayList<Constraint> constraints;
        ConstraintReferential constraintReferential;
        Table refTable;
        if (str3 == null) {
            throw DbException.getInvalidValueException("table", null);
        }
        SimpleResult initCrossReferenceResult = initCrossReferenceResult();
        if (!checkCatalogName(str)) {
            return initCrossReferenceResult;
        }
        Value string = getString(this.session.getDatabase().getShortName());
        Iterator<Schema> it = getSchemas(str2).iterator();
        while (it.hasNext()) {
            Table findTableOrView = it.next().findTableOrView(this.session, str3);
            if (findTableOrView != null && !findTableOrView.isHidden() && (constraints = findTableOrView.getConstraints()) != null) {
                Iterator<Constraint> it2 = constraints.iterator();
                while (it2.hasNext()) {
                    Constraint next = it2.next();
                    if (next.getConstraintType() == Constraint.Type.REFERENTIAL && (refTable = (constraintReferential = (ConstraintReferential) next).getRefTable()) == findTableOrView) {
                        Table table = constraintReferential.getTable();
                        addCrossReferenceResult(initCrossReferenceResult, string, refTable.getSchema().getName(), refTable, table.getSchema().getName(), table, constraintReferential);
                    }
                }
            }
        }
        initCrossReferenceResult.sortRows(new SortOrder(this.session, new int[]{5, 6, 8}));
        return initCrossReferenceResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getCrossReference(String str, String str2, String str3, String str4, String str5, String str6) {
        ArrayList<Constraint> constraints;
        ConstraintReferential constraintReferential;
        Table table;
        if (str3 == null) {
            throw DbException.getInvalidValueException("primaryTable", null);
        }
        if (str6 == null) {
            throw DbException.getInvalidValueException("foreignTable", null);
        }
        SimpleResult initCrossReferenceResult = initCrossReferenceResult();
        if (!checkCatalogName(str) || !checkCatalogName(str4)) {
            return initCrossReferenceResult;
        }
        Database database = this.session.getDatabase();
        Value string = getString(database.getShortName());
        Iterator<Schema> it = getSchemas(str5).iterator();
        while (it.hasNext()) {
            Table findTableOrView = it.next().findTableOrView(this.session, str6);
            if (findTableOrView != null && !findTableOrView.isHidden() && (constraints = findTableOrView.getConstraints()) != null) {
                Iterator<Constraint> it2 = constraints.iterator();
                while (it2.hasNext()) {
                    Constraint next = it2.next();
                    if (next.getConstraintType() == Constraint.Type.REFERENTIAL && (table = (constraintReferential = (ConstraintReferential) next).getTable()) == findTableOrView) {
                        Table refTable = constraintReferential.getRefTable();
                        if (database.equalsIdentifiers(refTable.getName(), str3)) {
                            Schema schema = refTable.getSchema();
                            if (checkSchema(str2, schema)) {
                                addCrossReferenceResult(initCrossReferenceResult, string, schema.getName(), refTable, table.getSchema().getName(), table, constraintReferential);
                            }
                        }
                    }
                }
            }
        }
        initCrossReferenceResult.sortRows(new SortOrder(this.session, new int[]{5, 6, 8}));
        return initCrossReferenceResult;
    }

    private SimpleResult initCrossReferenceResult() {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("PKTABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PKTABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PKTABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PKCOLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FKTABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FKTABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FKTABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("FKCOLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("KEY_SEQ", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("UPDATE_RULE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("DELETE_RULE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("FK_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("PK_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("DEFERRABILITY", TypeInfo.TYPE_SMALLINT);
        return simpleResult;
    }

    private void addCrossReferenceResult(SimpleResult simpleResult, Value value, String str, Table table, String str2, Table table2, ConstraintReferential constraintReferential) {
        Value string = getString(str);
        Value string2 = getString(table.getName());
        Value string3 = getString(str2);
        Value string4 = getString(table2.getName());
        IndexColumn[] refColumns = constraintReferential.getRefColumns();
        IndexColumn[] columns = constraintReferential.getColumns();
        ValueSmallint refAction = getRefAction(constraintReferential.getUpdateAction());
        ValueSmallint refAction2 = getRefAction(constraintReferential.getDeleteAction());
        Value string5 = getString(constraintReferential.getName());
        Value string6 = getString(constraintReferential.getReferencedConstraint().getName());
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            simpleResult.addRow(value, string, string2, getString(refColumns[i].column.getName()), value, string3, string4, getString(columns[i].column.getName()), ValueSmallint.get((short) (i + 1)), refAction, refAction2, string5, string6, IMPORTED_KEY_NOT_DEFERRABLE);
        }
    }

    private static ValueSmallint getRefAction(ConstraintActionType constraintActionType) {
        switch (constraintActionType) {
            case CASCADE:
                return IMPORTED_KEY_CASCADE;
            case RESTRICT:
                return IMPORTED_KEY_RESTRICT;
            case SET_DEFAULT:
                return IMPORTED_KEY_DEFAULT;
            case SET_NULL:
                return IMPORTED_KEY_SET_NULL;
            default:
                throw DbException.getInternalError("action=" + constraintActionType);
        }
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getTypeInfo() {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("PRECISION", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("LITERAL_PREFIX", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("LITERAL_SUFFIX", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("CREATE_PARAMS", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("CASE_SENSITIVE", TypeInfo.TYPE_BOOLEAN);
        simpleResult.addColumn("SEARCHABLE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("UNSIGNED_ATTRIBUTE", TypeInfo.TYPE_BOOLEAN);
        simpleResult.addColumn("FIXED_PREC_SCALE", TypeInfo.TYPE_BOOLEAN);
        simpleResult.addColumn("AUTO_INCREMENT", TypeInfo.TYPE_BOOLEAN);
        simpleResult.addColumn("LOCAL_TYPE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("MINIMUM_SCALE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("MAXIMUM_SCALE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
        for (int i = 1; i < 42; i++) {
            DataType dataType = DataType.getDataType(i);
            Value string = getString(Value.getTypeName(dataType.type));
            Value[] valueArr = new Value[18];
            valueArr[0] = string;
            valueArr[1] = ValueInteger.get(dataType.sqlType);
            valueArr[2] = ValueInteger.get(MathUtils.convertLongToInt(dataType.maxPrecision));
            valueArr[3] = getString(dataType.prefix);
            valueArr[4] = getString(dataType.suffix);
            valueArr[5] = getString(dataType.params);
            valueArr[6] = TYPE_NULLABLE;
            valueArr[7] = ValueBoolean.get(dataType.caseSensitive);
            valueArr[8] = TYPE_SEARCHABLE;
            valueArr[9] = ValueBoolean.FALSE;
            valueArr[10] = ValueBoolean.get(dataType.type == 13);
            valueArr[11] = ValueBoolean.get(DataType.isNumericType(i));
            valueArr[12] = string;
            valueArr[13] = ValueSmallint.get(MathUtils.convertIntToShort(dataType.minScale));
            valueArr[14] = ValueSmallint.get(MathUtils.convertIntToShort(dataType.maxScale));
            valueArr[15] = ValueNull.INSTANCE;
            valueArr[16] = ValueNull.INSTANCE;
            valueArr[17] = getRadix(dataType.type, false);
            simpleResult.addRow(valueArr);
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{1}));
        return simpleResult;
    }

    private static Value getRadix(int i, boolean z) {
        if (DataType.isNumericType(i)) {
            int i2 = (i == 13 || i == 16) ? 10 : 2;
            return z ? ValueSmallint.get((short) i2) : ValueInteger.get(i2);
        }
        return ValueNull.INSTANCE;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getIndexInfo(String str, String str2, String str3, boolean z, boolean z2) {
        if (str3 == null) {
            throw DbException.getInvalidValueException("table", null);
        }
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("NON_UNIQUE", TypeInfo.TYPE_BOOLEAN);
        simpleResult.addColumn("INDEX_QUALIFIER", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("INDEX_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TYPE", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT);
        simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("ASC_OR_DESC", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("CARDINALITY", TypeInfo.TYPE_BIGINT);
        simpleResult.addColumn("PAGES", TypeInfo.TYPE_BIGINT);
        simpleResult.addColumn("FILTER_CONDITION", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        Database database = this.session.getDatabase();
        Value string = getString(database.getShortName());
        for (Schema schema : getSchemas(str2)) {
            Table findTableOrView = schema.findTableOrView(this.session, str3);
            if (findTableOrView != null && !findTableOrView.isHidden()) {
                getIndexInfo(string, getString(schema.getName()), findTableOrView, z, z2, simpleResult, database);
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{3, 6, 5, 7}));
        return simpleResult;
    }

    private void getIndexInfo(Value value, Value value2, Table table, boolean z, boolean z2, SimpleResult simpleResult, Database database) {
        long rowCount;
        ArrayList<Index> indexes = table.getIndexes();
        if (indexes != null) {
            Iterator<Index> it = indexes.iterator();
            while (it.hasNext()) {
                Index next = it.next();
                if (next.getCreateSQL() != null) {
                    int uniqueColumnCount = next.getUniqueColumnCount();
                    if (!z || uniqueColumnCount != 0) {
                        Value string = getString(table.getName());
                        Value string2 = getString(next.getName());
                        IndexColumn[] indexColumns = next.getIndexColumns();
                        ValueSmallint valueSmallint = next.getIndexType().isHash() ? TABLE_INDEX_HASHED : TABLE_INDEX_OTHER;
                        int i = 0;
                        int length = indexColumns.length;
                        while (i < length) {
                            IndexColumn indexColumn = indexColumns[i];
                            boolean z3 = i >= uniqueColumnCount;
                            if (!z || !z3) {
                                Value[] valueArr = new Value[13];
                                valueArr[0] = value;
                                valueArr[1] = value2;
                                valueArr[2] = string;
                                valueArr[3] = ValueBoolean.get(z3);
                                valueArr[4] = value;
                                valueArr[5] = string2;
                                valueArr[6] = valueSmallint;
                                valueArr[7] = ValueSmallint.get((short) (i + 1));
                                valueArr[8] = getString(indexColumn.column.getName());
                                valueArr[9] = getString((indexColumn.sortType & 1) != 0 ? "D" : "A");
                                if (z2) {
                                    rowCount = next.getRowCountApproximation(this.session);
                                } else {
                                    rowCount = next.getRowCount(this.session);
                                }
                                valueArr[10] = ValueBigint.get(rowCount);
                                valueArr[11] = ValueBigint.get(next.getDiskSpaceUsed() / database.getPageSize());
                                valueArr[12] = ValueNull.INSTANCE;
                                simpleResult.addRow(valueArr);
                                i++;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getSchemas(String str, String str2) {
        checkClosed();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("TABLE_CATALOG", TypeInfo.TYPE_VARCHAR);
        if (!checkCatalogName(str)) {
            return simpleResult;
        }
        CompareLike like = getLike(str2);
        Collection<Schema> allSchemas = this.session.getDatabase().getAllSchemas();
        Value string = getString(this.session.getDatabase().getShortName());
        if (like == null) {
            Iterator<Schema> it = allSchemas.iterator();
            while (it.hasNext()) {
                simpleResult.addRow(getString(it.next().getName()), string);
            }
        } else {
            for (Schema schema : allSchemas) {
                if (like.test(schema.getName())) {
                    simpleResult.addRow(getString(schema.getName()), string);
                }
            }
        }
        simpleResult.sortRows(new SortOrder(this.session, new int[]{0}));
        return simpleResult;
    }

    @Override // org.h2.jdbc.meta.DatabaseMeta
    public ResultInterface getPseudoColumns(String str, String str2, String str3, String str4) {
        SimpleResult pseudoColumnsResult = getPseudoColumnsResult();
        if (!checkCatalogName(str)) {
            return pseudoColumnsResult;
        }
        Value string = getString(this.session.getDatabase().getShortName());
        CompareLike like = getLike(str4);
        for (Schema schema : getSchemasForPattern(str2)) {
            Value string2 = getString(schema.getName());
            for (SchemaObject schemaObject : getTablesForPattern(schema, str3)) {
                Value string3 = getString(schemaObject.getName());
                if (schemaObject instanceof Table) {
                    Table table = (Table) schemaObject;
                    if (!table.isHidden()) {
                        getPseudoColumnsAdd(pseudoColumnsResult, string, string2, string3, table, like);
                    }
                } else {
                    getPseudoColumnsAdd(pseudoColumnsResult, string, string2, string3, ((TableSynonym) schemaObject).getSynonymFor(), like);
                }
            }
        }
        pseudoColumnsResult.sortRows(new SortOrder(this.session, new int[]{1, 2, 3}));
        return pseudoColumnsResult;
    }

    private void getPseudoColumnsAdd(SimpleResult simpleResult, Value value, Value value2, Value value3, Table table, CompareLike compareLike) {
        Column rowIdColumn = table.getRowIdColumn();
        if (rowIdColumn != null) {
            getPseudoColumnsAdd(simpleResult, value, value2, value3, compareLike, rowIdColumn);
        }
        for (Column column : table.getColumns()) {
            if (!column.getVisible()) {
                getPseudoColumnsAdd(simpleResult, value, value2, value3, compareLike, column);
            }
        }
    }

    private void getPseudoColumnsAdd(SimpleResult simpleResult, Value value, Value value2, Value value3, CompareLike compareLike, Column column) {
        String name = column.getName();
        if (compareLike != null && !compareLike.test(name)) {
            return;
        }
        TypeInfo type = column.getType();
        ValueInteger valueInteger = ValueInteger.get(MathUtils.convertLongToInt(type.getPrecision()));
        Value[] valueArr = new Value[12];
        valueArr[0] = value;
        valueArr[1] = value2;
        valueArr[2] = value3;
        valueArr[3] = getString(name);
        valueArr[4] = ValueInteger.get(DataType.convertTypeToSQLType(type));
        valueArr[5] = valueInteger;
        valueArr[6] = ValueInteger.get(type.getScale());
        valueArr[7] = getRadix(type.getValueType(), false);
        valueArr[8] = NO_USAGE_RESTRICTIONS;
        valueArr[9] = getString(column.getComment());
        valueArr[10] = valueInteger;
        valueArr[11] = column.isNullable() ? YES : NO;
        simpleResult.addRow(valueArr);
    }

    @Override // org.h2.jdbc.meta.DatabaseMetaLocalBase
    void checkClosed() {
        if (this.session.isClosed()) {
            throw DbException.get(ErrorCode.DATABASE_CALLED_AT_SHUTDOWN);
        }
    }

    Value getString(String str) {
        return str != null ? ValueVarchar.get(str, this.session) : ValueNull.INSTANCE;
    }

    private boolean checkCatalogName(String str) {
        if (str != null && !str.isEmpty()) {
            Database database = this.session.getDatabase();
            return database.equalsIdentifiers(str, database.getShortName());
        }
        return true;
    }

    private Collection<Schema> getSchemas(String str) {
        Database database = this.session.getDatabase();
        if (str == null) {
            return database.getAllSchemas();
        }
        if (str.isEmpty()) {
            return Collections.singleton(database.getMainSchema());
        }
        Schema findSchema = database.findSchema(str);
        if (findSchema != null) {
            return Collections.singleton(findSchema);
        }
        return Collections.emptySet();
    }

    private Collection<Schema> getSchemasForPattern(String str) {
        Database database = this.session.getDatabase();
        if (str == null) {
            return database.getAllSchemas();
        }
        if (str.isEmpty()) {
            return Collections.singleton(database.getMainSchema());
        }
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        CompareLike like = getLike(str);
        for (Schema schema : database.getAllSchemas()) {
            if (like.test(schema.getName())) {
                newSmallArrayList.add(schema);
            }
        }
        return newSmallArrayList;
    }

    private Collection<? extends SchemaObject> getTablesForPattern(Schema schema, String str) {
        Collection<Table> allTablesAndViews = schema.getAllTablesAndViews(this.session);
        Collection<TableSynonym> allSynonyms = schema.getAllSynonyms();
        if (str == null) {
            if (allTablesAndViews.isEmpty()) {
                return allSynonyms;
            }
            if (allSynonyms.isEmpty()) {
                return allTablesAndViews;
            }
            ArrayList arrayList = new ArrayList(allTablesAndViews.size() + allSynonyms.size());
            arrayList.addAll(allTablesAndViews);
            arrayList.addAll(allSynonyms);
            return arrayList;
        }
        if (allTablesAndViews.isEmpty() && allSynonyms.isEmpty()) {
            return Collections.emptySet();
        }
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        CompareLike like = getLike(str);
        for (Table table : allTablesAndViews) {
            if (like.test(table.getName())) {
                newSmallArrayList.add(table);
            }
        }
        for (TableSynonym tableSynonym : allSynonyms) {
            if (like.test(tableSynonym.getName())) {
                newSmallArrayList.add(tableSynonym);
            }
        }
        return newSmallArrayList;
    }

    private boolean checkSchema(String str, Schema schema) {
        if (str == null) {
            return true;
        }
        if (str.isEmpty()) {
            return schema == this.session.getDatabase().getMainSchema();
        }
        return this.session.getDatabase().equalsIdentifiers(str, schema.getName());
    }

    private CompareLike getLike(String str) {
        if (str == null) {
            return null;
        }
        CompareLike compareLike = new CompareLike(this.session.getDatabase().getCompareMode(), StrPool.BACKSLASH, null, false, false, null, null, CompareLike.LikeType.LIKE);
        compareLike.initPattern(str, '\\');
        return compareLike;
    }
}
