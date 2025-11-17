package org.h2.table;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.index.MetaIndex;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/InformationSchemaTableLegacy.class */
public final class InformationSchemaTableLegacy extends MetaTable {
    private static final String CHARACTER_SET_NAME = "Unicode";
    private static final int TABLES = 0;
    private static final int COLUMNS = 1;
    private static final int INDEXES = 2;
    private static final int TABLE_TYPES = 3;
    private static final int TYPE_INFO = 4;
    private static final int CATALOGS = 5;
    private static final int SETTINGS = 6;
    private static final int HELP = 7;
    private static final int SEQUENCES = 8;
    private static final int USERS = 9;
    private static final int ROLES = 10;
    private static final int RIGHTS = 11;
    private static final int FUNCTION_ALIASES = 12;
    private static final int SCHEMATA = 13;
    private static final int TABLE_PRIVILEGES = 14;
    private static final int COLUMN_PRIVILEGES = 15;
    private static final int COLLATIONS = 16;
    private static final int VIEWS = 17;
    private static final int IN_DOUBT = 18;
    private static final int CROSS_REFERENCES = 19;
    private static final int FUNCTION_COLUMNS = 20;
    private static final int CONSTRAINTS = 21;
    private static final int CONSTANTS = 22;
    private static final int DOMAINS = 23;
    private static final int TRIGGERS = 24;
    private static final int SESSIONS = 25;
    private static final int LOCKS = 26;
    private static final int SESSION_STATE = 27;
    private static final int QUERY_STATISTICS = 28;
    private static final int SYNONYMS = 29;
    private static final int TABLE_CONSTRAINTS = 30;
    private static final int DOMAIN_CONSTRAINTS = 31;
    private static final int KEY_COLUMN_USAGE = 32;
    private static final int REFERENTIAL_CONSTRAINTS = 33;
    private static final int CHECK_CONSTRAINTS = 34;
    private static final int CONSTRAINT_COLUMN_USAGE = 35;
    public static final int META_TABLE_TYPE_COUNT = 36;

    public InformationSchemaTableLegacy(Schema schema, int i, int i2) {
        super(schema, i, i2);
        Column[] columnArr;
        String str = null;
        switch (i2) {
            case 0:
                setMetaTableName("TABLES");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("TABLE_TYPE"), column("STORAGE_TYPE"), column("SQL"), column("REMARKS"), column("LAST_MODIFICATION", TypeInfo.TYPE_BIGINT), column("ID", TypeInfo.TYPE_INTEGER), column("TYPE_NAME"), column("TABLE_CLASS"), column("ROW_COUNT_ESTIMATE", TypeInfo.TYPE_BIGINT)};
                str = "TABLE_NAME";
                break;
            case 1:
                setMetaTableName("COLUMNS");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("COLUMN_DEFAULT"), column("IS_NULLABLE"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_INTEGER), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("CHARACTER_SET_NAME"), column("COLLATION_NAME"), column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("IS_GENERATED"), column("GENERATION_EXPRESSION"), column("TYPE_NAME"), column("NULLABLE", TypeInfo.TYPE_INTEGER), column("IS_COMPUTED", TypeInfo.TYPE_BOOLEAN), column("SELECTIVITY", TypeInfo.TYPE_INTEGER), column("SEQUENCE_NAME"), column("REMARKS"), column("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT), column("COLUMN_TYPE"), column("COLUMN_ON_UPDATE"), column("IS_VISIBLE"), column("CHECK_CONSTRAINT")};
                str = "TABLE_NAME";
                break;
            case 2:
                setMetaTableName("INDEXES");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("NON_UNIQUE", TypeInfo.TYPE_BOOLEAN), column("INDEX_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT), column("COLUMN_NAME"), column("CARDINALITY", TypeInfo.TYPE_INTEGER), column("PRIMARY_KEY", TypeInfo.TYPE_BOOLEAN), column("INDEX_TYPE_NAME"), column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), column("INDEX_TYPE", TypeInfo.TYPE_SMALLINT), column("ASC_OR_DESC"), column("PAGES", TypeInfo.TYPE_INTEGER), column("FILTER_CONDITION"), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER), column("SORT_TYPE", TypeInfo.TYPE_INTEGER), column("CONSTRAINT_NAME"), column("INDEX_CLASS")};
                str = "TABLE_NAME";
                break;
            case 3:
                setMetaTableName("TABLE_TYPES");
                columnArr = new Column[]{column("TYPE")};
                break;
            case 4:
                setMetaTableName("TYPE_INFO");
                columnArr = new Column[]{column("TYPE_NAME"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("PRECISION", TypeInfo.TYPE_INTEGER), column("PREFIX"), column("SUFFIX"), column("PARAMS"), column("AUTO_INCREMENT", TypeInfo.TYPE_BOOLEAN), column("MINIMUM_SCALE", TypeInfo.TYPE_SMALLINT), column("MAXIMUM_SCALE", TypeInfo.TYPE_SMALLINT), column("RADIX", TypeInfo.TYPE_INTEGER), column("POS", TypeInfo.TYPE_INTEGER), column("CASE_SENSITIVE", TypeInfo.TYPE_BOOLEAN), column("NULLABLE", TypeInfo.TYPE_SMALLINT), column("SEARCHABLE", TypeInfo.TYPE_SMALLINT)};
                break;
            case 5:
                setMetaTableName("CATALOGS");
                columnArr = new Column[]{column("CATALOG_NAME")};
                break;
            case 6:
                setMetaTableName("SETTINGS");
                columnArr = new Column[]{column("NAME"), column("VALUE")};
                break;
            case 7:
                setMetaTableName("HELP");
                columnArr = new Column[]{column("ID", TypeInfo.TYPE_INTEGER), column("SECTION"), column("TOPIC"), column("SYNTAX"), column("TEXT")};
                break;
            case 8:
                setMetaTableName("SEQUENCES");
                columnArr = new Column[]{column("SEQUENCE_CATALOG"), column("SEQUENCE_SCHEMA"), column("SEQUENCE_NAME"), column("DATA_TYPE"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("START_VALUE", TypeInfo.TYPE_BIGINT), column("MINIMUM_VALUE", TypeInfo.TYPE_BIGINT), column("MAXIMUM_VALUE", TypeInfo.TYPE_BIGINT), column("INCREMENT", TypeInfo.TYPE_BIGINT), column("CYCLE_OPTION"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("CURRENT_VALUE", TypeInfo.TYPE_BIGINT), column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), column("REMARKS"), column("CACHE", TypeInfo.TYPE_BIGINT), column("ID", TypeInfo.TYPE_INTEGER), column("MIN_VALUE", TypeInfo.TYPE_BIGINT), column("MAX_VALUE", TypeInfo.TYPE_BIGINT), column("IS_CYCLE", TypeInfo.TYPE_BOOLEAN)};
                break;
            case 9:
                setMetaTableName("USERS");
                columnArr = new Column[]{column("NAME"), column("ADMIN"), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER)};
                break;
            case 10:
                setMetaTableName("ROLES");
                columnArr = new Column[]{column("NAME"), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER)};
                break;
            case 11:
                setMetaTableName("RIGHTS");
                columnArr = new Column[]{column("GRANTEE"), column("GRANTEETYPE"), column("GRANTEDROLE"), column("RIGHTS"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("ID", TypeInfo.TYPE_INTEGER)};
                str = "TABLE_NAME";
                break;
            case 12:
                setMetaTableName("FUNCTION_ALIASES");
                columnArr = new Column[]{column("ALIAS_CATALOG"), column("ALIAS_SCHEMA"), column("ALIAS_NAME"), column("JAVA_CLASS"), column("JAVA_METHOD"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("TYPE_NAME"), column("COLUMN_COUNT", TypeInfo.TYPE_INTEGER), column("RETURNS_RESULT", TypeInfo.TYPE_SMALLINT), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER), column("SOURCE")};
                break;
            case 13:
                setMetaTableName("SCHEMATA");
                columnArr = new Column[]{column("CATALOG_NAME"), column("SCHEMA_NAME"), column("SCHEMA_OWNER"), column("DEFAULT_CHARACTER_SET_NAME"), column("DEFAULT_COLLATION_NAME"), column("IS_DEFAULT", TypeInfo.TYPE_BOOLEAN), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER)};
                break;
            case 14:
                setMetaTableName("TABLE_PRIVILEGES");
                columnArr = new Column[]{column("GRANTOR"), column("GRANTEE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("PRIVILEGE_TYPE"), column("IS_GRANTABLE")};
                str = "TABLE_NAME";
                break;
            case 15:
                setMetaTableName("COLUMN_PRIVILEGES");
                columnArr = new Column[]{column("GRANTOR"), column("GRANTEE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("PRIVILEGE_TYPE"), column("IS_GRANTABLE")};
                str = "TABLE_NAME";
                break;
            case 16:
                setMetaTableName("COLLATIONS");
                columnArr = new Column[]{column("NAME"), column("KEY")};
                break;
            case 17:
                setMetaTableName("VIEWS");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("VIEW_DEFINITION"), column("CHECK_OPTION"), column("IS_UPDATABLE"), column("STATUS"), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER)};
                str = "TABLE_NAME";
                break;
            case 18:
                setMetaTableName("IN_DOUBT");
                columnArr = new Column[]{column("TRANSACTION"), column("STATE")};
                break;
            case 19:
                setMetaTableName("CROSS_REFERENCES");
                columnArr = new Column[]{column("PKTABLE_CATALOG"), column("PKTABLE_SCHEMA"), column("PKTABLE_NAME"), column("PKCOLUMN_NAME"), column("FKTABLE_CATALOG"), column("FKTABLE_SCHEMA"), column("FKTABLE_NAME"), column("FKCOLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT), column("UPDATE_RULE", TypeInfo.TYPE_SMALLINT), column("DELETE_RULE", TypeInfo.TYPE_SMALLINT), column("FK_NAME"), column("PK_NAME"), column("DEFERRABILITY", TypeInfo.TYPE_SMALLINT)};
                str = "PKTABLE_NAME";
                break;
            case 20:
                setMetaTableName("FUNCTION_COLUMNS");
                columnArr = new Column[]{column("ALIAS_CATALOG"), column("ALIAS_SCHEMA"), column("ALIAS_NAME"), column("JAVA_CLASS"), column("JAVA_METHOD"), column("COLUMN_COUNT", TypeInfo.TYPE_INTEGER), column("POS", TypeInfo.TYPE_INTEGER), column("COLUMN_NAME"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("TYPE_NAME"), column("PRECISION", TypeInfo.TYPE_INTEGER), column("SCALE", TypeInfo.TYPE_SMALLINT), column("RADIX", TypeInfo.TYPE_SMALLINT), column("NULLABLE", TypeInfo.TYPE_SMALLINT), column("COLUMN_TYPE", TypeInfo.TYPE_SMALLINT), column("REMARKS"), column("COLUMN_DEFAULT")};
                break;
            case 21:
                setMetaTableName("CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CONSTRAINT_TYPE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("UNIQUE_INDEX_NAME"), column("CHECK_EXPRESSION"), column("COLUMN_LIST"), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER)};
                str = "TABLE_NAME";
                break;
            case 22:
                setMetaTableName("CONSTANTS");
                columnArr = new Column[]{column("CONSTANT_CATALOG"), column("CONSTANT_SCHEMA"), column("CONSTANT_NAME"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER)};
                break;
            case 23:
                setMetaTableName("DOMAINS");
                columnArr = new Column[]{column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("DOMAIN_DEFAULT"), column("DOMAIN_ON_UPDATE"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("PRECISION", TypeInfo.TYPE_INTEGER), column("SCALE", TypeInfo.TYPE_INTEGER), column("TYPE_NAME"), column("PARENT_DOMAIN_CATALOG"), column("PARENT_DOMAIN_SCHEMA"), column("PARENT_DOMAIN_NAME"), column("SELECTIVITY", TypeInfo.TYPE_INTEGER), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER), column("COLUMN_DEFAULT"), column("IS_NULLABLE"), column("CHECK_CONSTRAINT")};
                break;
            case 24:
                setMetaTableName("TRIGGERS");
                columnArr = new Column[]{column("TRIGGER_CATALOG"), column("TRIGGER_SCHEMA"), column("TRIGGER_NAME"), column("TRIGGER_TYPE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("BEFORE", TypeInfo.TYPE_BOOLEAN), column("JAVA_CLASS"), column("QUEUE_SIZE", TypeInfo.TYPE_INTEGER), column("NO_WAIT", TypeInfo.TYPE_BOOLEAN), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER)};
                break;
            case 25:
                setMetaTableName("SESSIONS");
                columnArr = new Column[]{column("ID", TypeInfo.TYPE_INTEGER), column("USER_NAME"), column("SERVER"), column("CLIENT_ADDR"), column("CLIENT_INFO"), column("SESSION_START", TypeInfo.TYPE_TIMESTAMP_TZ), column("ISOLATION_LEVEL"), column("STATEMENT"), column("STATEMENT_START", TypeInfo.TYPE_TIMESTAMP_TZ), column("CONTAINS_UNCOMMITTED", TypeInfo.TYPE_BOOLEAN), column("STATE"), column("BLOCKER_ID", TypeInfo.TYPE_INTEGER), column("SLEEP_SINCE", TypeInfo.TYPE_TIMESTAMP_TZ)};
                break;
            case 26:
                setMetaTableName("LOCKS");
                columnArr = new Column[]{column("TABLE_SCHEMA"), column("TABLE_NAME"), column("SESSION_ID", TypeInfo.TYPE_INTEGER), column("LOCK_TYPE")};
                break;
            case 27:
                setMetaTableName("SESSION_STATE");
                columnArr = new Column[]{column("KEY"), column("SQL")};
                break;
            case 28:
                setMetaTableName("QUERY_STATISTICS");
                columnArr = new Column[]{column("SQL_STATEMENT"), column("EXECUTION_COUNT", TypeInfo.TYPE_INTEGER), column("MIN_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("MAX_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("CUMULATIVE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("AVERAGE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("STD_DEV_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("MIN_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("MAX_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("CUMULATIVE_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("AVERAGE_ROW_COUNT", TypeInfo.TYPE_DOUBLE), column("STD_DEV_ROW_COUNT", TypeInfo.TYPE_DOUBLE)};
                break;
            case 29:
                setMetaTableName("SYNONYMS");
                columnArr = new Column[]{column("SYNONYM_CATALOG"), column("SYNONYM_SCHEMA"), column("SYNONYM_NAME"), column("SYNONYM_FOR"), column("SYNONYM_FOR_SCHEMA"), column("TYPE_NAME"), column("STATUS"), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER)};
                str = "SYNONYM_NAME";
                break;
            case 30:
                setMetaTableName("TABLE_CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CONSTRAINT_TYPE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("IS_DEFERRABLE"), column("INITIALLY_DEFERRED"), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER)};
                str = "TABLE_NAME";
                break;
            case 31:
                setMetaTableName("DOMAIN_CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("IS_DEFERRABLE"), column("INITIALLY_DEFERRED"), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER)};
                break;
            case 32:
                setMetaTableName("KEY_COLUMN_USAGE");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("POSITION_IN_UNIQUE_CONSTRAINT", TypeInfo.TYPE_INTEGER), column("INDEX_CATALOG"), column("INDEX_SCHEMA"), column("INDEX_NAME")};
                str = "TABLE_NAME";
                break;
            case 33:
                setMetaTableName("REFERENTIAL_CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("UNIQUE_CONSTRAINT_CATALOG"), column("UNIQUE_CONSTRAINT_SCHEMA"), column("UNIQUE_CONSTRAINT_NAME"), column("MATCH_OPTION"), column("UPDATE_RULE"), column("DELETE_RULE")};
                break;
            case 34:
                setMetaTableName("CHECK_CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CHECK_CLAUSE")};
                break;
            case 35:
                setMetaTableName("CONSTRAINT_COLUMN_USAGE");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME")};
                str = "TABLE_NAME";
                break;
            default:
                throw DbException.getInternalError("type=" + i2);
        }
        setColumns(columnArr);
        if (str == null) {
            this.indexColumn = -1;
            this.metaIndex = null;
        } else {
            this.indexColumn = getColumn(this.database.sysIdentifier(str)).getColumnId();
            this.metaIndex = new MetaIndex(this, IndexColumn.wrap(new Column[]{columnArr[this.indexColumn]}), false);
        }
    }

    private static String replaceNullWithEmpty(String str) {
        return str == null ? "" : str;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: CFG modification limit reached, blocks count: 846
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:64)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:44)
        */
    @Override // org.h2.table.MetaTable
    public java.util.ArrayList<org.h2.result.Row> generateRows(org.h2.engine.SessionLocal r12, org.h2.result.SearchRow r13, org.h2.result.SearchRow r14) {
        /*
            Method dump skipped, instructions count: 9937
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.table.InformationSchemaTableLegacy.generateRows(org.h2.engine.SessionLocal, org.h2.result.SearchRow, org.h2.result.SearchRow):java.util.ArrayList");
    }

    /* renamed from: org.h2.table.InformationSchemaTableLegacy$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/InformationSchemaTableLegacy$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$h2$constraint$Constraint$Type;

        static {
            try {
                $SwitchMap$org$h2$constraint$ConstraintActionType[ConstraintActionType.CASCADE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$h2$constraint$ConstraintActionType[ConstraintActionType.RESTRICT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$h2$constraint$ConstraintActionType[ConstraintActionType.SET_DEFAULT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$h2$constraint$ConstraintActionType[ConstraintActionType.SET_NULL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            $SwitchMap$org$h2$constraint$Constraint$Type = new int[Constraint.Type.values().length];
            try {
                $SwitchMap$org$h2$constraint$Constraint$Type[Constraint.Type.CHECK.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$h2$constraint$Constraint$Type[Constraint.Type.DOMAIN.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$h2$constraint$Constraint$Type[Constraint.Type.REFERENTIAL.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$h2$constraint$Constraint$Type[Constraint.Type.PRIMARY_KEY.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$h2$constraint$Constraint$Type[Constraint.Type.UNIQUE.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    private static short getRefAction(ConstraintActionType constraintActionType) {
        switch (constraintActionType) {
            case CASCADE:
                return (short) 0;
            case RESTRICT:
                return (short) 1;
            case SET_DEFAULT:
                return (short) 4;
            case SET_NULL:
                return (short) 2;
            default:
                throw DbException.getInternalError("action=" + constraintActionType);
        }
    }

    private void addConstraintColumnUsage(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Constraint constraint, Column column) {
        Table table = column.getTable();
        add(sessionLocal, arrayList, str, table.getSchema().getName(), table.getName(), column.getName(), str, constraint.getSchema().getName(), constraint.getName());
    }

    private void addPrivileges(SessionLocal sessionLocal, ArrayList<Row> arrayList, DbObject dbObject, String str, Table table, String str2, int i) {
        if ((i & 1) != 0) {
            addPrivilege(sessionLocal, arrayList, dbObject, str, table, str2, "SELECT");
        }
        if ((i & 4) != 0) {
            addPrivilege(sessionLocal, arrayList, dbObject, str, table, str2, "INSERT");
        }
        if ((i & 8) != 0) {
            addPrivilege(sessionLocal, arrayList, dbObject, str, table, str2, "UPDATE");
        }
        if ((i & 2) != 0) {
            addPrivilege(sessionLocal, arrayList, dbObject, str, table, str2, "DELETE");
        }
    }

    private void addPrivilege(SessionLocal sessionLocal, ArrayList<Row> arrayList, DbObject dbObject, String str, Table table, String str2, String str3) {
        Object obj = "NO";
        if (dbObject.getType() == 2 && ((User) dbObject).isAdmin()) {
            obj = "YES";
        }
        if (str2 == null) {
            add(sessionLocal, arrayList, null, identifier(dbObject.getName()), str, table.getSchema().getName(), table.getName(), str3, obj);
        } else {
            add(sessionLocal, arrayList, null, identifier(dbObject.getName()), str, table.getSchema().getName(), table.getName(), str2, str3, obj);
        }
    }

    private ArrayList<SchemaObject> getAllSchemaObjects(int i) {
        ArrayList<SchemaObject> arrayList = new ArrayList<>();
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            it.next().getAll(i, arrayList);
        }
        return arrayList;
    }

    private ArrayList<Table> getAllTables(SessionLocal sessionLocal) {
        ArrayList<Table> arrayList = new ArrayList<>();
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            arrayList.addAll(it.next().getAllTablesAndViews(sessionLocal));
        }
        arrayList.addAll(sessionLocal.getLocalTempTables());
        return arrayList;
    }

    private ArrayList<Table> getTablesByName(SessionLocal sessionLocal, String str) {
        ArrayList<Table> arrayList = new ArrayList<>(1);
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            Table tableOrViewByName = it.next().getTableOrViewByName(sessionLocal, str);
            if (tableOrViewByName != null) {
                arrayList.add(tableOrViewByName);
            }
        }
        Table findLocalTempTable = sessionLocal.findLocalTempTable(str);
        if (findLocalTempTable != null) {
            arrayList.add(findLocalTempTable);
        }
        return arrayList;
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        switch (this.type) {
            case 6:
            case 8:
            case 18:
            case 25:
            case 26:
            case 27:
                return Long.MAX_VALUE;
            default:
                return this.database.getModificationDataId();
        }
    }
}
