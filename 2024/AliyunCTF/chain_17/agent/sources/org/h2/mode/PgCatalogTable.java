package org.h2.mode;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.constraint.Constraint;
import org.h2.engine.Constants;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Schema;
import org.h2.schema.TriggerObject;
import org.h2.server.pg.PgServer;
import org.h2.table.Column;
import org.h2.table.MetaTable;
import org.h2.table.Table;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueSmallint;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/PgCatalogTable.class */
public final class PgCatalogTable extends MetaTable {
    private static final int PG_AM = 0;
    private static final int PG_ATTRDEF = 1;
    private static final int PG_ATTRIBUTE = 2;
    private static final int PG_AUTHID = 3;
    private static final int PG_CLASS = 4;
    private static final int PG_CONSTRAINT = 5;
    private static final int PG_DATABASE = 6;
    private static final int PG_DESCRIPTION = 7;
    private static final int PG_GROUP = 8;
    private static final int PG_INDEX = 9;
    private static final int PG_INHERITS = 10;
    private static final int PG_NAMESPACE = 11;
    private static final int PG_PROC = 12;
    private static final int PG_ROLES = 13;
    private static final int PG_SETTINGS = 14;
    private static final int PG_TABLESPACE = 15;
    private static final int PG_TRIGGER = 16;
    private static final int PG_TYPE = 17;
    private static final int PG_USER = 18;
    public static final int META_TABLE_TYPE_COUNT = 19;
    private static final Object[][] PG_EXTRA_TYPES = {new Object[]{18, "char", 1, 0}, new Object[]{19, "name", 64, 18}, new Object[]{22, "int2vector", -1, 21}, new Object[]{24, "regproc", 4, 0}, new Object[]{Integer.valueOf(PgServer.PG_TYPE_INT2_ARRAY), "_int2", -1, 21}, new Object[]{1007, "_int4", -1, 23}, new Object[]{Integer.valueOf(PgServer.PG_TYPE_VARCHAR_ARRAY), "_varchar", -1, Integer.valueOf(PgServer.PG_TYPE_VARCHAR)}, new Object[]{2205, "regclass", 4, 0}};

    public PgCatalogTable(Schema schema, int i, int i2) {
        super(schema, i, i2);
        Column[] columnArr;
        switch (i2) {
            case 0:
                setMetaTableName("PG_AM");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("AMNAME", TypeInfo.TYPE_VARCHAR)};
                break;
            case 1:
                setMetaTableName("PG_ATTRDEF");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("ADSRC", TypeInfo.TYPE_INTEGER), column("ADRELID", TypeInfo.TYPE_INTEGER), column("ADNUM", TypeInfo.TYPE_INTEGER), column("ADBIN", TypeInfo.TYPE_VARCHAR)};
                break;
            case 2:
                setMetaTableName("PG_ATTRIBUTE");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("ATTRELID", TypeInfo.TYPE_INTEGER), column("ATTNAME", TypeInfo.TYPE_VARCHAR), column("ATTTYPID", TypeInfo.TYPE_INTEGER), column("ATTLEN", TypeInfo.TYPE_INTEGER), column("ATTNUM", TypeInfo.TYPE_INTEGER), column("ATTTYPMOD", TypeInfo.TYPE_INTEGER), column("ATTNOTNULL", TypeInfo.TYPE_BOOLEAN), column("ATTISDROPPED", TypeInfo.TYPE_BOOLEAN), column("ATTHASDEF", TypeInfo.TYPE_BOOLEAN)};
                break;
            case 3:
                setMetaTableName("PG_AUTHID");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("ROLNAME", TypeInfo.TYPE_VARCHAR), column("ROLSUPER", TypeInfo.TYPE_BOOLEAN), column("ROLINHERIT", TypeInfo.TYPE_BOOLEAN), column("ROLCREATEROLE", TypeInfo.TYPE_BOOLEAN), column("ROLCREATEDB", TypeInfo.TYPE_BOOLEAN), column("ROLCATUPDATE", TypeInfo.TYPE_BOOLEAN), column("ROLCANLOGIN", TypeInfo.TYPE_BOOLEAN), column("ROLCONNLIMIT", TypeInfo.TYPE_BOOLEAN), column("ROLPASSWORD", TypeInfo.TYPE_BOOLEAN), column("ROLVALIDUNTIL", TypeInfo.TYPE_TIMESTAMP_TZ), column("ROLCONFIG", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_VARCHAR))};
                break;
            case 4:
                setMetaTableName("PG_CLASS");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("RELNAME", TypeInfo.TYPE_VARCHAR), column("RELNAMESPACE", TypeInfo.TYPE_INTEGER), column("RELKIND", TypeInfo.TYPE_CHAR), column("RELAM", TypeInfo.TYPE_INTEGER), column("RELTUPLES", TypeInfo.TYPE_DOUBLE), column("RELTABLESPACE", TypeInfo.TYPE_INTEGER), column("RELPAGES", TypeInfo.TYPE_INTEGER), column("RELHASINDEX", TypeInfo.TYPE_BOOLEAN), column("RELHASRULES", TypeInfo.TYPE_BOOLEAN), column("RELHASOIDS", TypeInfo.TYPE_BOOLEAN), column("RELCHECKS", TypeInfo.TYPE_SMALLINT), column("RELTRIGGERS", TypeInfo.TYPE_INTEGER)};
                break;
            case 5:
                setMetaTableName("PG_CONSTRAINT");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("CONNAME", TypeInfo.TYPE_VARCHAR), column("CONTYPE", TypeInfo.TYPE_VARCHAR), column("CONRELID", TypeInfo.TYPE_INTEGER), column("CONFRELID", TypeInfo.TYPE_INTEGER), column("CONKEY", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_SMALLINT))};
                break;
            case 6:
                setMetaTableName("PG_DATABASE");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("DATNAME", TypeInfo.TYPE_VARCHAR), column("ENCODING", TypeInfo.TYPE_INTEGER), column("DATLASTSYSOID", TypeInfo.TYPE_INTEGER), column("DATALLOWCONN", TypeInfo.TYPE_BOOLEAN), column("DATCONFIG", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_VARCHAR)), column("DATACL", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_VARCHAR)), column("DATDBA", TypeInfo.TYPE_INTEGER), column("DATTABLESPACE", TypeInfo.TYPE_INTEGER)};
                break;
            case 7:
                setMetaTableName("PG_DESCRIPTION");
                columnArr = new Column[]{column("OBJOID", TypeInfo.TYPE_INTEGER), column("OBJSUBID", TypeInfo.TYPE_INTEGER), column("CLASSOID", TypeInfo.TYPE_INTEGER), column("DESCRIPTION", TypeInfo.TYPE_VARCHAR)};
                break;
            case 8:
                setMetaTableName("PG_GROUP");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("GRONAME", TypeInfo.TYPE_VARCHAR)};
                break;
            case 9:
                setMetaTableName("PG_INDEX");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("INDEXRELID", TypeInfo.TYPE_INTEGER), column("INDRELID", TypeInfo.TYPE_INTEGER), column("INDISCLUSTERED", TypeInfo.TYPE_BOOLEAN), column("INDISUNIQUE", TypeInfo.TYPE_BOOLEAN), column("INDISPRIMARY", TypeInfo.TYPE_BOOLEAN), column("INDEXPRS", TypeInfo.TYPE_VARCHAR), column("INDKEY", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_INTEGER)), column("INDPRED", TypeInfo.TYPE_VARCHAR)};
                break;
            case 10:
                setMetaTableName("PG_INHERITS");
                columnArr = new Column[]{column("INHRELID", TypeInfo.TYPE_INTEGER), column("INHPARENT", TypeInfo.TYPE_INTEGER), column("INHSEQNO", TypeInfo.TYPE_INTEGER)};
                break;
            case 11:
                setMetaTableName("PG_NAMESPACE");
                columnArr = new Column[]{column("ID", TypeInfo.TYPE_INTEGER), column("NSPNAME", TypeInfo.TYPE_VARCHAR)};
                break;
            case 12:
                setMetaTableName("PG_PROC");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("PRONAME", TypeInfo.TYPE_VARCHAR), column("PRORETTYPE", TypeInfo.TYPE_INTEGER), column("PROARGTYPES", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_INTEGER)), column("PRONAMESPACE", TypeInfo.TYPE_INTEGER)};
                break;
            case 13:
                setMetaTableName("PG_ROLES");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("ROLNAME", TypeInfo.TYPE_VARCHAR), column("ROLSUPER", TypeInfo.TYPE_CHAR), column("ROLCREATEROLE", TypeInfo.TYPE_CHAR), column("ROLCREATEDB", TypeInfo.TYPE_CHAR)};
                break;
            case 14:
                setMetaTableName("PG_SETTINGS");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("NAME", TypeInfo.TYPE_VARCHAR), column("SETTING", TypeInfo.TYPE_VARCHAR)};
                break;
            case 15:
                setMetaTableName("PG_TABLESPACE");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("SPCNAME", TypeInfo.TYPE_VARCHAR), column("SPCLOCATION", TypeInfo.TYPE_VARCHAR), column("SPCOWNER", TypeInfo.TYPE_INTEGER), column("SPCACL", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_VARCHAR))};
                break;
            case 16:
                setMetaTableName("PG_TRIGGER");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("TGCONSTRRELID", TypeInfo.TYPE_INTEGER), column("TGFOID", TypeInfo.TYPE_INTEGER), column("TGARGS", TypeInfo.TYPE_INTEGER), column("TGNARGS", TypeInfo.TYPE_INTEGER), column("TGDEFERRABLE", TypeInfo.TYPE_BOOLEAN), column("TGINITDEFERRED", TypeInfo.TYPE_BOOLEAN), column("TGCONSTRNAME", TypeInfo.TYPE_VARCHAR), column("TGRELID", TypeInfo.TYPE_INTEGER)};
                break;
            case 17:
                setMetaTableName("PG_TYPE");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("TYPNAME", TypeInfo.TYPE_VARCHAR), column("TYPNAMESPACE", TypeInfo.TYPE_INTEGER), column("TYPLEN", TypeInfo.TYPE_INTEGER), column("TYPTYPE", TypeInfo.TYPE_VARCHAR), column("TYPDELIM", TypeInfo.TYPE_VARCHAR), column("TYPRELID", TypeInfo.TYPE_INTEGER), column("TYPELEM", TypeInfo.TYPE_INTEGER), column("TYPBASETYPE", TypeInfo.TYPE_INTEGER), column("TYPTYPMOD", TypeInfo.TYPE_INTEGER), column("TYPNOTNULL", TypeInfo.TYPE_BOOLEAN), column("TYPINPUT", TypeInfo.TYPE_VARCHAR)};
                break;
            case 18:
                setMetaTableName("PG_USER");
                columnArr = new Column[]{column("OID", TypeInfo.TYPE_INTEGER), column("USENAME", TypeInfo.TYPE_VARCHAR), column("USECREATEDB", TypeInfo.TYPE_BOOLEAN), column("USESUPER", TypeInfo.TYPE_BOOLEAN)};
                break;
            default:
                throw DbException.getInternalError("type=" + i2);
        }
        setColumns(columnArr);
        this.indexColumn = -1;
        this.metaIndex = null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.h2.table.MetaTable
    public ArrayList<Row> generateRows(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        int convertType;
        int id;
        ArrayList<Row> newSmallArrayList = Utils.newSmallArrayList();
        Object shortName = this.database.getShortName();
        boolean isAdmin = sessionLocal.getUser().isAdmin();
        switch (this.type) {
            case 0:
                Object[] objArr = {"btree", "hash"};
                int length = objArr.length;
                for (int i = 0; i < length; i++) {
                    add(sessionLocal, newSmallArrayList, ValueInteger.get(i), objArr[i]);
                }
                break;
            case 1:
            case 3:
            case 8:
            case 9:
            case 10:
            case 12:
            case 16:
                break;
            case 2:
                Iterator<Schema> it = this.database.getAllSchemas().iterator();
                while (it.hasNext()) {
                    for (Table table : it.next().getAllTablesAndViews(sessionLocal)) {
                        if (!hideTable(table, sessionLocal)) {
                            pgAttribute(sessionLocal, newSmallArrayList, table);
                        }
                    }
                }
                for (Table table2 : sessionLocal.getLocalTempTables()) {
                    if (!hideTable(table2, sessionLocal)) {
                        pgAttribute(sessionLocal, newSmallArrayList, table2);
                    }
                }
                break;
            case 4:
                Iterator<Schema> it2 = this.database.getAllSchemas().iterator();
                while (it2.hasNext()) {
                    for (Table table3 : it2.next().getAllTablesAndViews(sessionLocal)) {
                        if (!hideTable(table3, sessionLocal)) {
                            pgClass(sessionLocal, newSmallArrayList, table3);
                        }
                    }
                }
                for (Table table4 : sessionLocal.getLocalTempTables()) {
                    if (!hideTable(table4, sessionLocal)) {
                        pgClass(sessionLocal, newSmallArrayList, table4);
                    }
                }
                break;
            case 5:
                pgConstraint(sessionLocal, newSmallArrayList);
                break;
            case 6:
                int i2 = Integer.MAX_VALUE;
                for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) {
                    if ((rightOwner instanceof User) && ((User) rightOwner).isAdmin() && (id = rightOwner.getId()) < i2) {
                        i2 = id;
                    }
                }
                add(sessionLocal, newSmallArrayList, ValueInteger.get(100001), shortName, ValueInteger.get(6), ValueInteger.get(100000), ValueBoolean.TRUE, null, null, ValueInteger.get(i2), ValueInteger.get(0));
                break;
            case 7:
                add(sessionLocal, newSmallArrayList, ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(-1), shortName);
                break;
            case 11:
                for (Schema schema : this.database.getAllSchemas()) {
                    add(sessionLocal, newSmallArrayList, ValueInteger.get(schema.getId()), schema.getName());
                }
                break;
            case 13:
                for (RightOwner rightOwner2 : this.database.getAllUsersAndRoles()) {
                    if (isAdmin || sessionLocal.getUser() == rightOwner2) {
                        Object obj = ((rightOwner2 instanceof User) && ((User) rightOwner2).isAdmin()) ? "t" : "f";
                        add(sessionLocal, newSmallArrayList, ValueInteger.get(rightOwner2.getId()), identifier(rightOwner2.getName()), obj, obj, obj);
                    }
                }
                break;
            case 14:
                String[] strArr = {new String[]{"autovacuum", CustomBooleanEditor.VALUE_ON}, new String[]{"stats_start_collector", CustomBooleanEditor.VALUE_ON}, new String[]{"stats_row_level", CustomBooleanEditor.VALUE_ON}};
                int length2 = strArr.length;
                for (int i3 = 0; i3 < length2; i3++) {
                    Object[] objArr2 = strArr[i3];
                    add(sessionLocal, newSmallArrayList, ValueInteger.get(i3), objArr2[0], objArr2[1]);
                }
                break;
            case 15:
                add(sessionLocal, newSmallArrayList, ValueInteger.get(0), "main", CoreConstants.NA, ValueInteger.get(0), null);
                break;
            case 17:
                HashSet hashSet = new HashSet();
                for (int i4 = 1; i4 < 42; i4++) {
                    DataType dataType = DataType.getDataType(i4);
                    if (dataType.type != 40 && (convertType = PgServer.convertType(TypeInfo.getTypeInfo(dataType.type))) != 705 && hashSet.add(Integer.valueOf(convertType))) {
                        add(sessionLocal, newSmallArrayList, ValueInteger.get(convertType), Value.getTypeName(dataType.type), ValueInteger.get(Constants.PG_CATALOG_SCHEMA_ID), ValueInteger.get(-1), "b", ",", ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(-1), ValueBoolean.FALSE, null);
                    }
                }
                for (Object[] objArr3 : PG_EXTRA_TYPES) {
                    add(sessionLocal, newSmallArrayList, ValueInteger.get(((Integer) objArr3[0]).intValue()), objArr3[1], ValueInteger.get(Constants.PG_CATALOG_SCHEMA_ID), ValueInteger.get(((Integer) objArr3[2]).intValue()), "b", ",", ValueInteger.get(0), ValueInteger.get(((Integer) objArr3[3]).intValue()), ValueInteger.get(0), ValueInteger.get(-1), ValueBoolean.FALSE, null);
                }
                break;
            case 18:
                for (RightOwner rightOwner3 : this.database.getAllUsersAndRoles()) {
                    if (rightOwner3 instanceof User) {
                        User user = (User) rightOwner3;
                        if (isAdmin || sessionLocal.getUser() == user) {
                            Object obj2 = ValueBoolean.get(user.isAdmin());
                            add(sessionLocal, newSmallArrayList, ValueInteger.get(user.getId()), identifier(user.getName()), obj2, obj2);
                        }
                    }
                }
                break;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
        return newSmallArrayList;
    }

    private void pgAttribute(SessionLocal sessionLocal, ArrayList<Row> arrayList, Table table) {
        Column[] columns = table.getColumns();
        int id = table.getId();
        int i = 0;
        while (i < columns.length) {
            int i2 = i;
            i++;
            addAttribute(sessionLocal, arrayList, (id * 10000) + i, id, columns[i2], i);
        }
        Iterator<Index> it = table.getIndexes().iterator();
        while (it.hasNext()) {
            Index next = it.next();
            if (next.getCreateSQL() != null) {
                Column[] columns2 = next.getColumns();
                int i3 = 0;
                while (i3 < columns2.length) {
                    int i4 = i3;
                    i3++;
                    Column column = columns2[i4];
                    int id2 = next.getId();
                    addAttribute(sessionLocal, arrayList, (1000000 * id2) + (id * 10000) + i3, id2, column, i3);
                }
            }
        }
    }

    private void pgClass(SessionLocal sessionLocal, ArrayList<Row> arrayList, Table table) {
        ArrayList<TriggerObject> triggers = table.getTriggers();
        addClass(sessionLocal, arrayList, table.getId(), table.getName(), table.getSchema().getId(), table.isView() ? "v" : "r", false, triggers != null ? triggers.size() : 0);
        ArrayList<Index> indexes = table.getIndexes();
        if (indexes != null) {
            Iterator<Index> it = indexes.iterator();
            while (it.hasNext()) {
                Index next = it.next();
                if (next.getCreateSQL() != null) {
                    addClass(sessionLocal, arrayList, next.getId(), next.getName(), next.getSchema().getId(), IntegerTokenConverter.CONVERTER_KEY, true, 0);
                }
            }
        }
    }

    private void pgConstraint(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        Iterator<Schema> it = this.database.getAllSchemasNoMeta().iterator();
        while (it.hasNext()) {
            for (Constraint constraint : it.next().getAllConstraints()) {
                Constraint.Type constraintType = constraint.getConstraintType();
                if (constraintType != Constraint.Type.DOMAIN) {
                    Table table = constraint.getTable();
                    if (!hideTable(table, sessionLocal)) {
                        ArrayList arrayList2 = new ArrayList();
                        Iterator<Column> it2 = constraint.getReferencedColumns(table).iterator();
                        while (it2.hasNext()) {
                            arrayList2.add(ValueSmallint.get((short) (it2.next().getColumnId() + 1)));
                        }
                        Table refTable = constraint.getRefTable();
                        Object[] objArr = new Object[6];
                        objArr[0] = ValueInteger.get(constraint.getId());
                        objArr[1] = constraint.getName();
                        objArr[2] = StringUtils.toLowerEnglish(constraintType.getSqlName().substring(0, 1));
                        objArr[3] = ValueInteger.get(table.getId());
                        objArr[4] = ValueInteger.get((refTable == null || refTable == table || hideTable(refTable, sessionLocal)) ? 0 : table.getId());
                        objArr[5] = ValueArray.get(TypeInfo.TYPE_SMALLINT, (Value[]) arrayList2.toArray(Value.EMPTY_VALUES), null);
                        add(sessionLocal, arrayList, objArr);
                    }
                }
            }
        }
    }

    private void addAttribute(SessionLocal sessionLocal, ArrayList<Row> arrayList, int i, int i2, Column column, int i3) {
        long precision = column.getType().getPrecision();
        Object[] objArr = new Object[10];
        objArr[0] = ValueInteger.get(i);
        objArr[1] = ValueInteger.get(i2);
        objArr[2] = column.getName();
        objArr[3] = ValueInteger.get(PgServer.convertType(column.getType()));
        objArr[4] = ValueInteger.get(precision > 255 ? -1 : (int) precision);
        objArr[5] = ValueInteger.get(i3);
        objArr[6] = ValueInteger.get(-1);
        objArr[7] = ValueBoolean.get(!column.isNullable());
        objArr[8] = ValueBoolean.FALSE;
        objArr[9] = ValueBoolean.FALSE;
        add(sessionLocal, arrayList, objArr);
    }

    private void addClass(SessionLocal sessionLocal, ArrayList<Row> arrayList, int i, String str, int i2, String str2, boolean z, int i3) {
        add(sessionLocal, arrayList, ValueInteger.get(i), str, ValueInteger.get(i2), str2, ValueInteger.get(0), ValueDouble.get(0.0d), ValueInteger.get(0), ValueInteger.get(0), ValueBoolean.get(z), ValueBoolean.FALSE, ValueBoolean.FALSE, ValueSmallint.get((short) 0), ValueInteger.get(i3));
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        return this.database.getModificationDataId();
    }
}
