package org.h2.table;

import cn.hutool.core.text.StrPool;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.h2.api.IntervalQualifier;
import org.h2.command.Command;
import org.h2.command.ParserBase;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintDomain;
import org.h2.constraint.ConstraintReferential;
import org.h2.constraint.ConstraintUnique;
import org.h2.engine.Constants;
import org.h2.engine.DbObject;
import org.h2.engine.NullsDistinct;
import org.h2.engine.QueryStatisticsData;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.Setting;
import org.h2.engine.User;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.index.MetaIndex;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Constant;
import org.h2.schema.Domain;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.schema.UserDefinedFunction;
import org.h2.store.InDoubtTransaction;
import org.h2.util.DateTimeUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.util.geometry.EWKTUtils;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfoEnum;
import org.h2.value.ExtTypeInfoGeometry;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueToObjectConverter2;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/InformationSchemaTable.class */
public final class InformationSchemaTable extends MetaTable {
    private static final String CHARACTER_SET_NAME = "Unicode";
    private static final int INFORMATION_SCHEMA_CATALOG_NAME = 0;
    private static final int CHECK_CONSTRAINTS = 1;
    private static final int COLLATIONS = 2;
    private static final int COLUMNS = 3;
    private static final int COLUMN_PRIVILEGES = 4;
    private static final int CONSTRAINT_COLUMN_USAGE = 5;
    private static final int DOMAINS = 6;
    private static final int DOMAIN_CONSTRAINTS = 7;
    private static final int ELEMENT_TYPES = 8;
    private static final int FIELDS = 9;
    private static final int KEY_COLUMN_USAGE = 10;
    private static final int PARAMETERS = 11;
    private static final int REFERENTIAL_CONSTRAINTS = 12;
    private static final int ROUTINES = 13;
    private static final int SCHEMATA = 14;
    private static final int SEQUENCES = 15;
    private static final int TABLES = 16;
    private static final int TABLE_CONSTRAINTS = 17;
    private static final int TABLE_PRIVILEGES = 18;
    private static final int TRIGGERS = 19;
    private static final int VIEWS = 20;
    private static final int CONSTANTS = 21;
    private static final int ENUM_VALUES = 22;
    private static final int INDEXES = 23;
    private static final int INDEX_COLUMNS = 24;
    private static final int IN_DOUBT = 25;
    private static final int LOCKS = 26;
    private static final int QUERY_STATISTICS = 27;
    private static final int RIGHTS = 28;
    private static final int ROLES = 29;
    private static final int SESSIONS = 30;
    private static final int SESSION_STATE = 31;
    private static final int SETTINGS = 32;
    private static final int SYNONYMS = 33;
    private static final int USERS = 34;
    public static final int META_TABLE_TYPE_COUNT = 35;
    private final boolean isView;

    public InformationSchemaTable(Schema schema, int i, int i2) {
        super(schema, i, i2);
        Column[] columnArr;
        String str = null;
        boolean z = true;
        switch (i2) {
            case 0:
                setMetaTableName("INFORMATION_SCHEMA_CATALOG_NAME");
                z = false;
                columnArr = new Column[]{column("CATALOG_NAME")};
                break;
            case 1:
                setMetaTableName("CHECK_CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CHECK_CLAUSE")};
                str = "CONSTRAINT_NAME";
                break;
            case 2:
                setMetaTableName("COLLATIONS");
                columnArr = new Column[]{column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("PAD_ATTRIBUTE"), column("LANGUAGE_TAG")};
                break;
            case 3:
                setMetaTableName("COLUMNS");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("COLUMN_DEFAULT"), column("IS_NULLABLE"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("IS_IDENTITY"), column("IDENTITY_GENERATION"), column("IDENTITY_START", TypeInfo.TYPE_BIGINT), column("IDENTITY_INCREMENT", TypeInfo.TYPE_BIGINT), column("IDENTITY_MAXIMUM", TypeInfo.TYPE_BIGINT), column("IDENTITY_MINIMUM", TypeInfo.TYPE_BIGINT), column("IDENTITY_CYCLE"), column("IS_GENERATED"), column("GENERATION_EXPRESSION"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), column("IDENTITY_BASE", TypeInfo.TYPE_BIGINT), column("IDENTITY_CACHE", TypeInfo.TYPE_BIGINT), column("COLUMN_ON_UPDATE"), column("IS_VISIBLE", TypeInfo.TYPE_BOOLEAN), column("DEFAULT_ON_NULL", TypeInfo.TYPE_BOOLEAN), column("SELECTIVITY", TypeInfo.TYPE_INTEGER), column("REMARKS")};
                str = "TABLE_NAME";
                break;
            case 4:
                setMetaTableName("COLUMN_PRIVILEGES");
                columnArr = new Column[]{column("GRANTOR"), column("GRANTEE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("PRIVILEGE_TYPE"), column("IS_GRANTABLE")};
                str = "TABLE_NAME";
                break;
            case 5:
                setMetaTableName("CONSTRAINT_COLUMN_USAGE");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME")};
                str = "TABLE_NAME";
                break;
            case 6:
                setMetaTableName("DOMAINS");
                columnArr = new Column[]{column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("DOMAIN_DEFAULT"), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), column("DOMAIN_ON_UPDATE"), column("PARENT_DOMAIN_CATALOG"), column("PARENT_DOMAIN_SCHEMA"), column("PARENT_DOMAIN_NAME"), column("REMARKS")};
                str = "DOMAIN_NAME";
                break;
            case 7:
                setMetaTableName("DOMAIN_CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("IS_DEFERRABLE"), column("INITIALLY_DEFERRED"), column("REMARKS")};
                str = "DOMAIN_NAME";
                break;
            case 8:
                setMetaTableName("ELEMENT_TYPES");
                columnArr = new Column[]{column("OBJECT_CATALOG"), column("OBJECT_SCHEMA"), column("OBJECT_NAME"), column("OBJECT_TYPE"), column("COLLECTION_TYPE_IDENTIFIER"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER)};
                break;
            case 9:
                setMetaTableName("FIELDS");
                columnArr = new Column[]{column("OBJECT_CATALOG"), column("OBJECT_SCHEMA"), column("OBJECT_NAME"), column("OBJECT_TYPE"), column("ROW_IDENTIFIER"), column("FIELD_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER)};
                break;
            case 10:
                setMetaTableName("KEY_COLUMN_USAGE");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("POSITION_IN_UNIQUE_CONSTRAINT", TypeInfo.TYPE_INTEGER)};
                str = "TABLE_NAME";
                break;
            case 11:
                setMetaTableName("PARAMETERS");
                columnArr = new Column[]{column("SPECIFIC_CATALOG"), column("SPECIFIC_SCHEMA"), column("SPECIFIC_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("PARAMETER_MODE"), column("IS_RESULT"), column("AS_LOCATOR"), column("PARAMETER_NAME"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("PARAMETER_DEFAULT"), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER)};
                break;
            case 12:
                setMetaTableName("REFERENTIAL_CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("UNIQUE_CONSTRAINT_CATALOG"), column("UNIQUE_CONSTRAINT_SCHEMA"), column("UNIQUE_CONSTRAINT_NAME"), column("MATCH_OPTION"), column("UPDATE_RULE"), column("DELETE_RULE")};
                str = "CONSTRAINT_NAME";
                break;
            case 13:
                setMetaTableName("ROUTINES");
                columnArr = new Column[]{column("SPECIFIC_CATALOG"), column("SPECIFIC_SCHEMA"), column("SPECIFIC_NAME"), column("ROUTINE_CATALOG"), column("ROUTINE_SCHEMA"), column("ROUTINE_NAME"), column("ROUTINE_TYPE"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("ROUTINE_BODY"), column("ROUTINE_DEFINITION"), column("EXTERNAL_NAME"), column("EXTERNAL_LANGUAGE"), column("PARAMETER_STYLE"), column("IS_DETERMINISTIC"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), column("REMARKS")};
                break;
            case 14:
                setMetaTableName("SCHEMATA");
                columnArr = new Column[]{column("CATALOG_NAME"), column("SCHEMA_NAME"), column("SCHEMA_OWNER"), column("DEFAULT_CHARACTER_SET_CATALOG"), column("DEFAULT_CHARACTER_SET_SCHEMA"), column("DEFAULT_CHARACTER_SET_NAME"), column("SQL_PATH"), column("DEFAULT_COLLATION_NAME"), column("REMARKS")};
                break;
            case 15:
                setMetaTableName("SEQUENCES");
                columnArr = new Column[]{column("SEQUENCE_CATALOG"), column("SEQUENCE_SCHEMA"), column("SEQUENCE_NAME"), column("DATA_TYPE"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("START_VALUE", TypeInfo.TYPE_BIGINT), column("MINIMUM_VALUE", TypeInfo.TYPE_BIGINT), column("MAXIMUM_VALUE", TypeInfo.TYPE_BIGINT), column("INCREMENT", TypeInfo.TYPE_BIGINT), column("CYCLE_OPTION"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("BASE_VALUE", TypeInfo.TYPE_BIGINT), column("CACHE", TypeInfo.TYPE_BIGINT), column("REMARKS")};
                str = "SEQUENCE_NAME";
                break;
            case 16:
                setMetaTableName("TABLES");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("TABLE_TYPE"), column("IS_INSERTABLE_INTO"), column("COMMIT_ACTION"), column("STORAGE_TYPE"), column("REMARKS"), column("LAST_MODIFICATION", TypeInfo.TYPE_BIGINT), column("TABLE_CLASS"), column("ROW_COUNT_ESTIMATE", TypeInfo.TYPE_BIGINT)};
                str = "TABLE_NAME";
                break;
            case 17:
                setMetaTableName("TABLE_CONSTRAINTS");
                columnArr = new Column[]{column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CONSTRAINT_TYPE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("IS_DEFERRABLE"), column("INITIALLY_DEFERRED"), column("ENFORCED"), column("NULLS_DISTINCT"), column("INDEX_CATALOG"), column("INDEX_SCHEMA"), column("INDEX_NAME"), column("REMARKS")};
                str = "TABLE_NAME";
                break;
            case 18:
                setMetaTableName("TABLE_PRIVILEGES");
                columnArr = new Column[]{column("GRANTOR"), column("GRANTEE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("PRIVILEGE_TYPE"), column("IS_GRANTABLE"), column("WITH_HIERARCHY")};
                str = "TABLE_NAME";
                break;
            case 19:
                setMetaTableName("TRIGGERS");
                columnArr = new Column[]{column("TRIGGER_CATALOG"), column("TRIGGER_SCHEMA"), column("TRIGGER_NAME"), column("EVENT_MANIPULATION"), column("EVENT_OBJECT_CATALOG"), column("EVENT_OBJECT_SCHEMA"), column("EVENT_OBJECT_TABLE"), column("ACTION_ORIENTATION"), column("ACTION_TIMING"), column("IS_ROLLBACK", TypeInfo.TYPE_BOOLEAN), column("JAVA_CLASS"), column("QUEUE_SIZE", TypeInfo.TYPE_INTEGER), column("NO_WAIT", TypeInfo.TYPE_BOOLEAN), column("REMARKS")};
                str = "EVENT_OBJECT_TABLE";
                break;
            case 20:
                setMetaTableName("VIEWS");
                columnArr = new Column[]{column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("VIEW_DEFINITION"), column("CHECK_OPTION"), column("IS_UPDATABLE"), column("INSERTABLE_INTO"), column("IS_TRIGGER_UPDATABLE"), column("IS_TRIGGER_DELETABLE"), column("IS_TRIGGER_INSERTABLE_INTO"), column("STATUS"), column("REMARKS")};
                str = "TABLE_NAME";
                break;
            case 21:
                setMetaTableName("CONSTANTS");
                z = false;
                columnArr = new Column[]{column("CONSTANT_CATALOG"), column("CONSTANT_SCHEMA"), column("CONSTANT_NAME"), column("VALUE_DEFINITION"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), column("REMARKS")};
                str = "CONSTANT_NAME";
                break;
            case 22:
                setMetaTableName("ENUM_VALUES");
                z = false;
                columnArr = new Column[]{column("OBJECT_CATALOG"), column("OBJECT_SCHEMA"), column("OBJECT_NAME"), column("OBJECT_TYPE"), column("ENUM_IDENTIFIER"), column("VALUE_NAME"), column("VALUE_ORDINAL")};
                break;
            case 23:
                setMetaTableName("INDEXES");
                z = false;
                columnArr = new Column[]{column("INDEX_CATALOG"), column("INDEX_SCHEMA"), column("INDEX_NAME"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("INDEX_TYPE_NAME"), column("NULLS_DISTINCT"), column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), column("REMARKS"), column("INDEX_CLASS")};
                str = "TABLE_NAME";
                break;
            case 24:
                setMetaTableName("INDEX_COLUMNS");
                z = false;
                columnArr = new Column[]{column("INDEX_CATALOG"), column("INDEX_SCHEMA"), column("INDEX_NAME"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("ORDERING_SPECIFICATION"), column("NULL_ORDERING"), column("IS_UNIQUE", TypeInfo.TYPE_BOOLEAN)};
                str = "TABLE_NAME";
                break;
            case 25:
                setMetaTableName("IN_DOUBT");
                z = false;
                columnArr = new Column[]{column("TRANSACTION_NAME"), column("TRANSACTION_STATE")};
                break;
            case 26:
                setMetaTableName("LOCKS");
                z = false;
                columnArr = new Column[]{column("TABLE_SCHEMA"), column("TABLE_NAME"), column("SESSION_ID", TypeInfo.TYPE_INTEGER), column("LOCK_TYPE")};
                break;
            case 27:
                setMetaTableName("QUERY_STATISTICS");
                z = false;
                columnArr = new Column[]{column("SQL_STATEMENT"), column("EXECUTION_COUNT", TypeInfo.TYPE_INTEGER), column("MIN_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("MAX_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("CUMULATIVE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("AVERAGE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("STD_DEV_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("MIN_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("MAX_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("CUMULATIVE_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("AVERAGE_ROW_COUNT", TypeInfo.TYPE_DOUBLE), column("STD_DEV_ROW_COUNT", TypeInfo.TYPE_DOUBLE)};
                break;
            case 28:
                setMetaTableName("RIGHTS");
                z = false;
                columnArr = new Column[]{column("GRANTEE"), column("GRANTEETYPE"), column("GRANTEDROLE"), column("RIGHTS"), column("TABLE_SCHEMA"), column("TABLE_NAME")};
                str = "TABLE_NAME";
                break;
            case 29:
                setMetaTableName("ROLES");
                z = false;
                columnArr = new Column[]{column("ROLE_NAME"), column("REMARKS")};
                break;
            case 30:
                setMetaTableName("SESSIONS");
                z = false;
                columnArr = new Column[]{column("SESSION_ID", TypeInfo.TYPE_INTEGER), column("USER_NAME"), column("SERVER"), column("CLIENT_ADDR"), column("CLIENT_INFO"), column("SESSION_START", TypeInfo.TYPE_TIMESTAMP_TZ), column("ISOLATION_LEVEL"), column("EXECUTING_STATEMENT"), column("EXECUTING_STATEMENT_START", TypeInfo.TYPE_TIMESTAMP_TZ), column("CONTAINS_UNCOMMITTED", TypeInfo.TYPE_BOOLEAN), column("SESSION_STATE"), column("BLOCKER_ID", TypeInfo.TYPE_INTEGER), column("SLEEP_SINCE", TypeInfo.TYPE_TIMESTAMP_TZ)};
                break;
            case 31:
                setMetaTableName("SESSION_STATE");
                z = false;
                columnArr = new Column[]{column("STATE_KEY"), column("STATE_COMMAND")};
                break;
            case 32:
                setMetaTableName("SETTINGS");
                z = false;
                columnArr = new Column[]{column("SETTING_NAME"), column("SETTING_VALUE")};
                break;
            case 33:
                setMetaTableName("SYNONYMS");
                z = false;
                columnArr = new Column[]{column("SYNONYM_CATALOG"), column("SYNONYM_SCHEMA"), column("SYNONYM_NAME"), column("SYNONYM_FOR"), column("SYNONYM_FOR_SCHEMA"), column("TYPE_NAME"), column("STATUS"), column("REMARKS")};
                str = "SYNONYM_NAME";
                break;
            case 34:
                setMetaTableName("USERS");
                z = false;
                columnArr = new Column[]{column("USER_NAME"), column("IS_ADMIN", TypeInfo.TYPE_BOOLEAN), column("REMARKS")};
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
        this.isView = z;
    }

    @Override // org.h2.table.MetaTable
    public ArrayList<Row> generateRows(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        Value value = null;
        Value value2 = null;
        if (this.indexColumn >= 0) {
            if (searchRow != null) {
                value = searchRow.getValue(this.indexColumn);
            }
            if (searchRow2 != null) {
                value2 = searchRow2.getValue(this.indexColumn);
            }
        }
        ArrayList<Row> newSmallArrayList = Utils.newSmallArrayList();
        String shortName = this.database.getShortName();
        switch (this.type) {
            case 0:
                informationSchemaCatalogName(sessionLocal, newSmallArrayList, shortName);
                break;
            case 1:
                checkConstraints(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 2:
                collations(sessionLocal, newSmallArrayList, shortName);
                break;
            case 3:
                columns(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 4:
                columnPrivileges(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 5:
                constraintColumnUsage(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 6:
                domains(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 7:
                domainConstraints(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 8:
                elementTypesFields(sessionLocal, newSmallArrayList, shortName, 8);
                break;
            case 9:
                elementTypesFields(sessionLocal, newSmallArrayList, shortName, 9);
                break;
            case 10:
                keyColumnUsage(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 11:
                parameters(sessionLocal, newSmallArrayList, shortName);
                break;
            case 12:
                referentialConstraints(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 13:
                routines(sessionLocal, newSmallArrayList, shortName);
                break;
            case 14:
                schemata(sessionLocal, newSmallArrayList, shortName);
                break;
            case 15:
                sequences(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 16:
                tables(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 17:
                tableConstraints(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 18:
                tablePrivileges(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 19:
                triggers(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 20:
                views(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 21:
                constants(sessionLocal, value, value2, newSmallArrayList, shortName);
                break;
            case 22:
                elementTypesFields(sessionLocal, newSmallArrayList, shortName, 22);
                break;
            case 23:
                indexes(sessionLocal, value, value2, newSmallArrayList, shortName, false);
                break;
            case 24:
                indexes(sessionLocal, value, value2, newSmallArrayList, shortName, true);
                break;
            case 25:
                inDoubt(sessionLocal, newSmallArrayList);
                break;
            case 26:
                locks(sessionLocal, newSmallArrayList);
                break;
            case 27:
                queryStatistics(sessionLocal, newSmallArrayList);
                break;
            case 28:
                rights(sessionLocal, value, value2, newSmallArrayList);
                break;
            case 29:
                roles(sessionLocal, newSmallArrayList);
                break;
            case 30:
                sessions(sessionLocal, newSmallArrayList);
                break;
            case 31:
                sessionState(sessionLocal, newSmallArrayList);
                break;
            case 32:
                settings(sessionLocal, newSmallArrayList);
                break;
            case 33:
                synonyms(sessionLocal, newSmallArrayList, shortName);
                break;
            case 34:
                users(sessionLocal, newSmallArrayList);
                break;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
        return newSmallArrayList;
    }

    private void informationSchemaCatalogName(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str) {
        add(sessionLocal, arrayList, str);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0094 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0030 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void checkConstraints(org.h2.engine.SessionLocal r8, org.h2.value.Value r9, org.h2.value.Value r10, java.util.ArrayList<org.h2.result.Row> r11, java.lang.String r12) {
        /*
            r7 = this;
            r0 = r7
            org.h2.engine.Database r0 = r0.database
            java.util.Collection r0 = r0.getAllSchemas()
            java.util.Iterator r0 = r0.iterator()
            r13 = r0
        Le:
            r0 = r13
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto La7
            r0 = r13
            java.lang.Object r0 = r0.next()
            org.h2.schema.Schema r0 = (org.h2.schema.Schema) r0
            r14 = r0
            r0 = r14
            java.util.Collection r0 = r0.getAllConstraints()
            java.util.Iterator r0 = r0.iterator()
            r15 = r0
        L30:
            r0 = r15
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto La4
            r0 = r15
            java.lang.Object r0 = r0.next()
            org.h2.constraint.Constraint r0 = (org.h2.constraint.Constraint) r0
            r16 = r0
            r0 = r16
            org.h2.constraint.Constraint$Type r0 = r0.getConstraintType()
            r17 = r0
            r0 = r17
            org.h2.constraint.Constraint$Type r1 = org.h2.constraint.Constraint.Type.CHECK
            if (r0 != r1) goto L73
            r0 = r16
            org.h2.constraint.ConstraintCheck r0 = (org.h2.constraint.ConstraintCheck) r0
            r18 = r0
            r0 = r18
            org.h2.table.Table r0 = r0.getTable()
            r19 = r0
            r0 = r7
            r1 = r19
            r2 = r8
            boolean r0 = r0.hideTable(r1, r2)
            if (r0 == 0) goto L70
            goto L30
        L70:
            goto L7e
        L73:
            r0 = r17
            org.h2.constraint.Constraint$Type r1 = org.h2.constraint.Constraint.Type.DOMAIN
            if (r0 == r1) goto L7e
            goto L30
        L7e:
            r0 = r16
            java.lang.String r0 = r0.getName()
            r18 = r0
            r0 = r7
            r1 = r8
            r2 = r18
            r3 = r9
            r4 = r10
            boolean r0 = r0.checkIndex(r1, r2, r3, r4)
            if (r0 != 0) goto L94
            goto L30
        L94:
            r0 = r7
            r1 = r8
            r2 = r11
            r3 = r12
            r4 = r16
            r5 = r18
            r0.checkConstraints(r1, r2, r3, r4, r5)
            goto L30
        La4:
            goto Le
        La7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.table.InformationSchemaTable.checkConstraints(org.h2.engine.SessionLocal, org.h2.value.Value, org.h2.value.Value, java.util.ArrayList, java.lang.String):void");
    }

    private void checkConstraints(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Constraint constraint, String str2) {
        add(sessionLocal, arrayList, str, constraint.getSchema().getName(), str2, constraint.getExpression().getSQL(0, 2));
    }

    private void collations(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str) {
        String name = this.database.getMainSchema().getName();
        collations(sessionLocal, arrayList, str, name, CompareMode.OFF, null);
        for (Locale locale : CompareMode.getCollationLocales(false)) {
            collations(sessionLocal, arrayList, str, name, CompareMode.getName(locale), locale.toLanguageTag());
        }
    }

    private void collations(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, String str4) {
        if ("und".equals(str4)) {
            str4 = null;
        }
        add(sessionLocal, arrayList, str, str2, str3, "NO PAD", str4);
    }

    private void columns(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        String name = this.database.getMainSchema().getName();
        String name2 = this.database.getCompareMode().getName();
        if (value != null && value.equals(value2)) {
            String string = value.getString();
            if (string == null) {
                return;
            }
            Iterator<Schema> it = this.database.getAllSchemas().iterator();
            while (it.hasNext()) {
                Table tableOrViewByName = it.next().getTableOrViewByName(sessionLocal, string);
                if (tableOrViewByName != null) {
                    columns(sessionLocal, arrayList, str, name, name2, tableOrViewByName, tableOrViewByName.getName());
                }
            }
            Table findLocalTempTable = sessionLocal.findLocalTempTable(string);
            if (findLocalTempTable != null) {
                columns(sessionLocal, arrayList, str, name, name2, findLocalTempTable, findLocalTempTable.getName());
                return;
            }
            return;
        }
        Iterator<Schema> it2 = this.database.getAllSchemas().iterator();
        while (it2.hasNext()) {
            for (Table table : it2.next().getAllTablesAndViews(sessionLocal)) {
                String name3 = table.getName();
                if (checkIndex(sessionLocal, name3, value, value2)) {
                    columns(sessionLocal, arrayList, str, name, name2, table, name3);
                }
            }
        }
        for (Table table2 : sessionLocal.getLocalTempTables()) {
            String name4 = table2.getName();
            if (checkIndex(sessionLocal, name4, value, value2)) {
                columns(sessionLocal, arrayList, str, name, name2, table2, name4);
            }
        }
    }

    private void columns(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, Table table, String str4) {
        if (hideTable(table, sessionLocal)) {
            return;
        }
        Column[] columns = table.getColumns();
        int i = 0;
        int length = columns.length;
        while (i < length) {
            Column column = columns[i];
            i++;
            columns(sessionLocal, arrayList, str, str2, str3, table, str4, column, i);
        }
    }

    private void columns(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, Table table, String str4, Column column, int i) {
        String str5;
        Object obj;
        String str6;
        String str7;
        String defaultSQL;
        Object obj2;
        String str8;
        Object obj3;
        String str9;
        String str10;
        ValueBigint valueBigint;
        ValueBigint valueBigint2;
        ValueBigint valueBigint3;
        ValueBigint valueBigint4;
        ValueBigint valueBigint5;
        ValueBigint valueBigint6;
        DataTypeInformation valueOf = DataTypeInformation.valueOf(column.getType());
        if (valueOf.hasCharsetAndCollation) {
            str7 = str;
            str6 = str2;
            obj = CHARACTER_SET_NAME;
            str5 = str3;
        } else {
            str5 = null;
            obj = null;
            str6 = null;
            str7 = null;
        }
        Domain domain = column.getDomain();
        String str11 = null;
        String str12 = null;
        String str13 = null;
        if (domain != null) {
            str11 = str;
            str12 = domain.getSchema().getName();
            str13 = domain.getName();
        }
        Sequence sequence = column.getSequence();
        if (sequence != null) {
            defaultSQL = null;
            obj2 = "NEVER";
            str8 = null;
            obj3 = "YES";
            str10 = column.isGeneratedAlways() ? "ALWAYS" : "BY DEFAULT";
            valueBigint6 = ValueBigint.get(sequence.getStartValue());
            valueBigint5 = ValueBigint.get(sequence.getIncrement());
            valueBigint4 = ValueBigint.get(sequence.getMaxValue());
            valueBigint3 = ValueBigint.get(sequence.getMinValue());
            Sequence.Cycle cycle = sequence.getCycle();
            str9 = cycle.isCycle() ? "YES" : "NO";
            valueBigint2 = cycle != Sequence.Cycle.EXHAUSTED ? ValueBigint.get(sequence.getBaseValue()) : null;
            valueBigint = ValueBigint.get(sequence.getCacheSize());
        } else {
            if (column.isGenerated()) {
                defaultSQL = null;
                obj2 = "ALWAYS";
                str8 = column.getDefaultSQL();
            } else {
                defaultSQL = column.getDefaultSQL();
                obj2 = "NEVER";
                str8 = null;
            }
            obj3 = "NO";
            str9 = null;
            str10 = null;
            valueBigint = null;
            valueBigint2 = null;
            valueBigint3 = null;
            valueBigint4 = null;
            valueBigint5 = null;
            valueBigint6 = null;
        }
        Object[] objArr = new Object[48];
        objArr[0] = str;
        objArr[1] = table.getSchema().getName();
        objArr[2] = str4;
        objArr[3] = column.getName();
        objArr[4] = ValueInteger.get(i);
        objArr[5] = defaultSQL;
        objArr[6] = column.isNullable() ? "YES" : "NO";
        objArr[7] = identifier(valueOf.dataType);
        objArr[8] = valueOf.characterPrecision;
        objArr[9] = valueOf.characterPrecision;
        objArr[10] = valueOf.numericPrecision;
        objArr[11] = valueOf.numericPrecisionRadix;
        objArr[12] = valueOf.numericScale;
        objArr[13] = valueOf.datetimePrecision;
        objArr[14] = valueOf.intervalType;
        objArr[15] = valueOf.intervalPrecision;
        objArr[16] = str7;
        objArr[17] = str6;
        objArr[18] = obj;
        objArr[19] = str7;
        objArr[20] = str6;
        objArr[21] = str5;
        objArr[22] = str11;
        objArr[23] = str12;
        objArr[24] = str13;
        objArr[25] = valueOf.maximumCardinality;
        objArr[26] = Integer.toString(i);
        objArr[27] = obj3;
        objArr[28] = str10;
        objArr[29] = valueBigint6;
        objArr[30] = valueBigint5;
        objArr[31] = valueBigint4;
        objArr[32] = valueBigint3;
        objArr[33] = str9;
        objArr[34] = obj2;
        objArr[35] = str8;
        objArr[36] = valueOf.declaredDataType;
        objArr[37] = valueOf.declaredNumericPrecision;
        objArr[38] = valueOf.declaredNumericScale;
        objArr[39] = valueOf.geometryType;
        objArr[40] = valueOf.geometrySrid;
        objArr[41] = valueBigint2;
        objArr[42] = valueBigint;
        objArr[43] = column.getOnUpdateSQL();
        objArr[44] = ValueBoolean.get(column.getVisible());
        objArr[45] = ValueBoolean.get(column.isDefaultOnNull());
        objArr[46] = ValueInteger.get(column.getSelectivity());
        objArr[47] = column.getComment();
        add(sessionLocal, arrayList, objArr);
    }

    private void columnPrivileges(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Right> it = this.database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            DbObject grantedObject = next.getGrantedObject();
            if (grantedObject instanceof Table) {
                Table table = (Table) grantedObject;
                if (!hideTable(table, sessionLocal) && checkIndex(sessionLocal, table.getName(), value, value2)) {
                    DbObject grantee = next.getGrantee();
                    int rightMask = next.getRightMask();
                    for (Column column : table.getColumns()) {
                        addPrivileges(sessionLocal, arrayList, grantee, str, table, column.getName(), rightMask);
                    }
                }
            }
        }
    }

    private void constraintColumnUsage(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            Iterator<Constraint> it2 = it.next().getAllConstraints().iterator();
            while (it2.hasNext()) {
                constraintColumnUsage(sessionLocal, value, value2, arrayList, str, it2.next());
            }
        }
    }

    private void constraintColumnUsage(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str, Constraint constraint) {
        switch (constraint.getConstraintType()) {
            case CHECK:
            case DOMAIN:
                HashSet hashSet = new HashSet();
                constraint.getExpression().isEverything(ExpressionVisitor.getColumnsVisitor(hashSet, null));
                Iterator it = hashSet.iterator();
                while (it.hasNext()) {
                    Column column = (Column) it.next();
                    Table table = column.getTable();
                    if (checkIndex(sessionLocal, table.getName(), value, value2) && !hideTable(table, sessionLocal)) {
                        addConstraintColumnUsage(sessionLocal, arrayList, str, constraint, column);
                    }
                }
                return;
            case REFERENTIAL:
                Table refTable = constraint.getRefTable();
                if (checkIndex(sessionLocal, refTable.getName(), value, value2) && !hideTable(refTable, sessionLocal)) {
                    Iterator<Column> it2 = constraint.getReferencedColumns(refTable).iterator();
                    while (it2.hasNext()) {
                        addConstraintColumnUsage(sessionLocal, arrayList, str, constraint, it2.next());
                    }
                    break;
                }
                break;
            case PRIMARY_KEY:
            case UNIQUE:
                break;
            default:
                return;
        }
        Table table2 = constraint.getTable();
        if (checkIndex(sessionLocal, table2.getName(), value, value2) && !hideTable(table2, sessionLocal)) {
            Iterator<Column> it3 = constraint.getReferencedColumns(table2).iterator();
            while (it3.hasNext()) {
                addConstraintColumnUsage(sessionLocal, arrayList, str, constraint, it3.next());
            }
        }
    }

    private void domains(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        String name = this.database.getMainSchema().getName();
        String name2 = this.database.getCompareMode().getName();
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Domain domain : it.next().getAllDomains()) {
                String name3 = domain.getName();
                if (checkIndex(sessionLocal, name3, value, value2)) {
                    domains(sessionLocal, arrayList, str, name, name2, domain, name3);
                }
            }
        }
    }

    private void domains(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, Domain domain, String str4) {
        String str5;
        Object obj;
        String str6;
        String str7;
        Domain domain2 = domain.getDomain();
        DataTypeInformation valueOf = DataTypeInformation.valueOf(domain.getDataType());
        if (valueOf.hasCharsetAndCollation) {
            str7 = str;
            str6 = str2;
            obj = CHARACTER_SET_NAME;
            str5 = str3;
        } else {
            str5 = null;
            obj = null;
            str6 = null;
            str7 = null;
        }
        Object[] objArr = new Object[31];
        objArr[0] = str;
        objArr[1] = domain.getSchema().getName();
        objArr[2] = str4;
        objArr[3] = valueOf.dataType;
        objArr[4] = valueOf.characterPrecision;
        objArr[5] = valueOf.characterPrecision;
        objArr[6] = str7;
        objArr[7] = str6;
        objArr[8] = obj;
        objArr[9] = str7;
        objArr[10] = str6;
        objArr[11] = str5;
        objArr[12] = valueOf.numericPrecision;
        objArr[13] = valueOf.numericPrecisionRadix;
        objArr[14] = valueOf.numericScale;
        objArr[15] = valueOf.datetimePrecision;
        objArr[16] = valueOf.intervalType;
        objArr[17] = valueOf.intervalPrecision;
        objArr[18] = domain.getDefaultSQL();
        objArr[19] = valueOf.maximumCardinality;
        objArr[20] = "TYPE";
        objArr[21] = valueOf.declaredDataType;
        objArr[22] = valueOf.declaredNumericPrecision;
        objArr[23] = valueOf.declaredNumericScale;
        objArr[24] = valueOf.geometryType;
        objArr[25] = valueOf.geometrySrid;
        objArr[26] = domain.getOnUpdateSQL();
        objArr[27] = domain2 != null ? str : null;
        objArr[28] = domain2 != null ? domain2.getSchema().getName() : null;
        objArr[29] = domain2 != null ? domain2.getName() : null;
        objArr[30] = domain.getComment();
        add(sessionLocal, arrayList, objArr);
    }

    private void domainConstraints(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Constraint constraint : it.next().getAllConstraints()) {
                if (constraint.getConstraintType() == Constraint.Type.DOMAIN) {
                    ConstraintDomain constraintDomain = (ConstraintDomain) constraint;
                    Domain domain = constraintDomain.getDomain();
                    String name = domain.getName();
                    if (checkIndex(sessionLocal, name, value, value2)) {
                        domainConstraints(sessionLocal, arrayList, str, constraintDomain, domain, name);
                    }
                }
            }
        }
    }

    private void domainConstraints(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, ConstraintDomain constraintDomain, Domain domain, String str2) {
        add(sessionLocal, arrayList, str, constraintDomain.getSchema().getName(), constraintDomain.getName(), str, domain.getSchema().getName(), str2, "NO", "NO", constraintDomain.getComment());
    }

    private void elementTypesFields(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, int i) {
        String name = this.database.getMainSchema().getName();
        String name2 = this.database.getCompareMode().getName();
        for (Schema schema : this.database.getAllSchemas()) {
            String name3 = schema.getName();
            Iterator<Table> it = schema.getAllTablesAndViews(sessionLocal).iterator();
            while (it.hasNext()) {
                elementTypesFieldsForTable(sessionLocal, arrayList, str, i, name, name2, name3, it.next());
            }
            for (Domain domain : schema.getAllDomains()) {
                elementTypesFieldsRow(sessionLocal, arrayList, str, i, name, name2, name3, domain.getName(), "DOMAIN", "TYPE", domain.getDataType());
            }
            for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
                if (userDefinedFunction instanceof FunctionAlias) {
                    String name4 = userDefinedFunction.getName();
                    try {
                        FunctionAlias.JavaMethod[] javaMethods = ((FunctionAlias) userDefinedFunction).getJavaMethods();
                        for (int i2 = 0; i2 < javaMethods.length; i2++) {
                            FunctionAlias.JavaMethod javaMethod = javaMethods[i2];
                            TypeInfo dataType = javaMethod.getDataType();
                            String str2 = name4 + '_' + (i2 + 1);
                            if (dataType != null && dataType.getValueType() != 0) {
                                elementTypesFieldsRow(sessionLocal, arrayList, str, i, name, name2, name3, str2, "ROUTINE", "RESULT", dataType);
                            }
                            Class<?>[] columnClasses = javaMethod.getColumnClasses();
                            int i3 = 1;
                            int length = columnClasses.length;
                            for (int i4 = javaMethod.hasConnectionParam() ? 1 : 0; i4 < length; i4++) {
                                elementTypesFieldsRow(sessionLocal, arrayList, str, i, name, name2, name3, str2, "ROUTINE", Integer.toString(i3), ValueToObjectConverter2.classToType(columnClasses[i4]));
                                i3++;
                            }
                        }
                    } catch (DbException e) {
                    }
                }
            }
            for (Constant constant : schema.getAllConstants()) {
                elementTypesFieldsRow(sessionLocal, arrayList, str, i, name, name2, name3, constant.getName(), "CONSTANT", "TYPE", constant.getValue().getType());
            }
        }
        for (Table table : sessionLocal.getLocalTempTables()) {
            elementTypesFieldsForTable(sessionLocal, arrayList, str, i, name, name2, table.getSchema().getName(), table);
        }
    }

    private void elementTypesFieldsForTable(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, int i, String str2, String str3, String str4, Table table) {
        if (hideTable(table, sessionLocal)) {
            return;
        }
        String name = table.getName();
        Column[] columns = table.getColumns();
        for (int i2 = 0; i2 < columns.length; i2++) {
            elementTypesFieldsRow(sessionLocal, arrayList, str, i, str2, str3, str4, name, "TABLE", Integer.toString(i2 + 1), columns[i2].getType());
        }
    }

    private void elementTypesFieldsRow(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, TypeInfo typeInfo) {
        switch (typeInfo.getValueType()) {
            case 36:
                if (i == 22) {
                    enumValues(sessionLocal, arrayList, str, str4, str5, str6, str7, typeInfo);
                    return;
                }
                return;
            case 40:
                TypeInfo typeInfo2 = (TypeInfo) typeInfo.getExtTypeInfo();
                String str8 = str7 + '_';
                if (i == 8) {
                    elementTypes(sessionLocal, arrayList, str, str2, str3, str4, str5, str6, str7, str8, typeInfo2);
                }
                elementTypesFieldsRow(sessionLocal, arrayList, str, i, str2, str3, str4, str5, str6, str8, typeInfo2);
                return;
            case 41:
                int i2 = 0;
                for (Map.Entry<String, TypeInfo> entry : ((ExtTypeInfoRow) typeInfo.getExtTypeInfo()).getFields()) {
                    TypeInfo value = entry.getValue();
                    String key = entry.getKey();
                    i2++;
                    String str9 = str7 + '_' + i2;
                    if (i == 9) {
                        fields(sessionLocal, arrayList, str, str2, str3, str4, str5, str6, str7, key, i2, str9, value);
                    }
                    elementTypesFieldsRow(sessionLocal, arrayList, str, i, str2, str3, str4, str5, str6, str9, value);
                }
                return;
            default:
                return;
        }
    }

    private void elementTypes(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, TypeInfo typeInfo) {
        String str9;
        Object obj;
        String str10;
        String str11;
        DataTypeInformation valueOf = DataTypeInformation.valueOf(typeInfo);
        if (valueOf.hasCharsetAndCollation) {
            str11 = str;
            str10 = str2;
            obj = CHARACTER_SET_NAME;
            str9 = str3;
        } else {
            str9 = null;
            obj = null;
            str10 = null;
            str11 = null;
        }
        add(sessionLocal, arrayList, str, str4, str5, str6, str7, valueOf.dataType, valueOf.characterPrecision, valueOf.characterPrecision, str11, str10, obj, str11, str10, str9, valueOf.numericPrecision, valueOf.numericPrecisionRadix, valueOf.numericScale, valueOf.datetimePrecision, valueOf.intervalType, valueOf.intervalPrecision, valueOf.maximumCardinality, str8, valueOf.declaredDataType, valueOf.declaredNumericPrecision, valueOf.declaredNumericScale, valueOf.geometryType, valueOf.geometrySrid);
    }

    private void fields(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, String str9, TypeInfo typeInfo) {
        String str10;
        Object obj;
        String str11;
        String str12;
        DataTypeInformation valueOf = DataTypeInformation.valueOf(typeInfo);
        if (valueOf.hasCharsetAndCollation) {
            str12 = str;
            str11 = str2;
            obj = CHARACTER_SET_NAME;
            str10 = str3;
        } else {
            str10 = null;
            obj = null;
            str11 = null;
            str12 = null;
        }
        add(sessionLocal, arrayList, str, str4, str5, str6, str7, str8, ValueInteger.get(i), valueOf.dataType, valueOf.characterPrecision, valueOf.characterPrecision, str12, str11, obj, str12, str11, str10, valueOf.numericPrecision, valueOf.numericPrecisionRadix, valueOf.numericScale, valueOf.datetimePrecision, valueOf.intervalType, valueOf.intervalPrecision, valueOf.maximumCardinality, str9, valueOf.declaredDataType, valueOf.declaredNumericPrecision, valueOf.declaredNumericScale, valueOf.geometryType, valueOf.geometrySrid);
    }

    private void keyColumnUsage(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Constraint constraint : it.next().getAllConstraints()) {
                Constraint.Type constraintType = constraint.getConstraintType();
                IndexColumn[] indexColumnArr = null;
                if (constraintType == Constraint.Type.UNIQUE || constraintType == Constraint.Type.PRIMARY_KEY) {
                    indexColumnArr = ((ConstraintUnique) constraint).getColumns();
                } else if (constraintType == Constraint.Type.REFERENTIAL) {
                    indexColumnArr = ((ConstraintReferential) constraint).getColumns();
                }
                if (indexColumnArr != null) {
                    Table table = constraint.getTable();
                    if (!hideTable(table, sessionLocal)) {
                        String name = table.getName();
                        if (checkIndex(sessionLocal, name, value, value2)) {
                            keyColumnUsage(sessionLocal, arrayList, str, constraint, constraintType, indexColumnArr, table, name);
                        }
                    }
                }
            }
        }
    }

    private void keyColumnUsage(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Constraint constraint, Constraint.Type type, IndexColumn[] indexColumnArr, Table table, String str2) {
        ConstraintUnique constraintUnique;
        if (type == Constraint.Type.REFERENTIAL) {
            constraintUnique = constraint.getReferencedConstraint();
        } else {
            constraintUnique = null;
        }
        for (int i = 0; i < indexColumnArr.length; i++) {
            IndexColumn indexColumn = indexColumnArr[i];
            ValueInteger valueInteger = ValueInteger.get(i + 1);
            ValueInteger valueInteger2 = null;
            if (constraintUnique != null) {
                Column column = ((ConstraintReferential) constraint).getRefColumns()[i].column;
                IndexColumn[] columns = constraintUnique.getColumns();
                int i2 = 0;
                while (true) {
                    if (i2 >= columns.length) {
                        break;
                    }
                    if (!columns[i2].column.equals(column)) {
                        i2++;
                    } else {
                        valueInteger2 = ValueInteger.get(i2 + 1);
                        break;
                    }
                }
            }
            add(sessionLocal, arrayList, str, constraint.getSchema().getName(), constraint.getName(), str, table.getSchema().getName(), str2, indexColumn.columnName, valueInteger, valueInteger2);
        }
    }

    private void parameters(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str) {
        String name = this.database.getMainSchema().getName();
        String name2 = this.database.getCompareMode().getName();
        for (Schema schema : this.database.getAllSchemas()) {
            for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
                if (userDefinedFunction instanceof FunctionAlias) {
                    try {
                        FunctionAlias.JavaMethod[] javaMethods = ((FunctionAlias) userDefinedFunction).getJavaMethods();
                        for (int i = 0; i < javaMethods.length; i++) {
                            FunctionAlias.JavaMethod javaMethod = javaMethods[i];
                            Class<?>[] columnClasses = javaMethod.getColumnClasses();
                            int i2 = 1;
                            int length = columnClasses.length;
                            for (int i3 = javaMethod.hasConnectionParam() ? 1 : 0; i3 < length; i3++) {
                                parameters(sessionLocal, arrayList, str, name, name2, schema.getName(), userDefinedFunction.getName() + '_' + (i + 1), ValueToObjectConverter2.classToType(columnClasses[i3]), i2);
                                i2++;
                            }
                        }
                    } catch (DbException e) {
                    }
                }
            }
        }
    }

    private void parameters(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, String str4, String str5, TypeInfo typeInfo, int i) {
        String str6;
        Object obj;
        String str7;
        String str8;
        DataTypeInformation valueOf = DataTypeInformation.valueOf(typeInfo);
        if (valueOf.hasCharsetAndCollation) {
            str8 = str;
            str7 = str2;
            obj = CHARACTER_SET_NAME;
            str6 = str3;
        } else {
            str6 = null;
            obj = null;
            str7 = null;
            str8 = null;
        }
        Object[] objArr = new Object[31];
        objArr[0] = str;
        objArr[1] = str4;
        objArr[2] = str5;
        objArr[3] = ValueInteger.get(i);
        objArr[4] = "IN";
        objArr[5] = "NO";
        objArr[6] = DataType.isLargeObject(typeInfo.getValueType()) ? "YES" : "NO";
        objArr[7] = "P" + i;
        objArr[8] = identifier(valueOf.dataType);
        objArr[9] = valueOf.characterPrecision;
        objArr[10] = valueOf.characterPrecision;
        objArr[11] = str8;
        objArr[12] = str7;
        objArr[13] = obj;
        objArr[14] = str8;
        objArr[15] = str7;
        objArr[16] = str6;
        objArr[17] = valueOf.numericPrecision;
        objArr[18] = valueOf.numericPrecisionRadix;
        objArr[19] = valueOf.numericScale;
        objArr[20] = valueOf.datetimePrecision;
        objArr[21] = valueOf.intervalType;
        objArr[22] = valueOf.intervalPrecision;
        objArr[23] = valueOf.maximumCardinality;
        objArr[24] = Integer.toString(i);
        objArr[25] = valueOf.declaredDataType;
        objArr[26] = valueOf.declaredNumericPrecision;
        objArr[27] = valueOf.declaredNumericScale;
        objArr[28] = null;
        objArr[29] = valueOf.geometryType;
        objArr[30] = valueOf.geometrySrid;
        add(sessionLocal, arrayList, objArr);
    }

    private void referentialConstraints(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Constraint constraint : it.next().getAllConstraints()) {
                if (constraint.getConstraintType() == Constraint.Type.REFERENTIAL && !hideTable(constraint.getTable(), sessionLocal)) {
                    String name = constraint.getName();
                    if (checkIndex(sessionLocal, name, value, value2)) {
                        referentialConstraints(sessionLocal, arrayList, str, (ConstraintReferential) constraint, name);
                    }
                }
            }
        }
    }

    private void referentialConstraints(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, ConstraintReferential constraintReferential, String str2) {
        ConstraintUnique referencedConstraint = constraintReferential.getReferencedConstraint();
        add(sessionLocal, arrayList, str, constraintReferential.getSchema().getName(), str2, str, referencedConstraint.getSchema().getName(), referencedConstraint.getName(), "NONE", constraintReferential.getUpdateAction().getSqlName(), constraintReferential.getDeleteAction().getSqlName());
    }

    private void routines(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str) {
        String str2;
        boolean isAdmin = sessionLocal.getUser().isAdmin();
        String name = this.database.getMainSchema().getName();
        String name2 = this.database.getCompareMode().getName();
        for (Schema schema : this.database.getAllSchemas()) {
            String name3 = schema.getName();
            for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
                String name4 = userDefinedFunction.getName();
                if (userDefinedFunction instanceof FunctionAlias) {
                    FunctionAlias functionAlias = (FunctionAlias) userDefinedFunction;
                    try {
                        FunctionAlias.JavaMethod[] javaMethods = functionAlias.getJavaMethods();
                        for (int i = 0; i < javaMethods.length; i++) {
                            TypeInfo dataType = javaMethods[i].getDataType();
                            if (dataType != null && dataType.getValueType() == 0) {
                                str2 = "PROCEDURE";
                                dataType = null;
                            } else {
                                str2 = "FUNCTION";
                            }
                            String javaClassName = functionAlias.getJavaClassName();
                            routines(sessionLocal, arrayList, str, name, name2, name3, name4, name4 + '_' + (i + 1), str2, isAdmin ? functionAlias.getSource() : null, javaClassName != null ? javaClassName + '.' + functionAlias.getJavaMethodName() : null, dataType, functionAlias.isDeterministic(), functionAlias.getComment());
                        }
                    } catch (DbException e) {
                    }
                } else {
                    routines(sessionLocal, arrayList, str, name, name2, name3, name4, name4, "AGGREGATE", null, userDefinedFunction.getJavaClassName(), TypeInfo.TYPE_NULL, false, userDefinedFunction.getComment());
                }
            }
        }
    }

    private void routines(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, TypeInfo typeInfo, boolean z, String str10) {
        String str11;
        Object obj;
        String str12;
        String str13;
        DataTypeInformation valueOf = typeInfo != null ? DataTypeInformation.valueOf(typeInfo) : DataTypeInformation.NULL;
        if (valueOf.hasCharsetAndCollation) {
            str13 = str;
            str12 = str2;
            obj = CHARACTER_SET_NAME;
            str11 = str3;
        } else {
            str11 = null;
            obj = null;
            str12 = null;
            str13 = null;
        }
        Object[] objArr = new Object[36];
        objArr[0] = str;
        objArr[1] = str4;
        objArr[2] = str6;
        objArr[3] = str;
        objArr[4] = str4;
        objArr[5] = str5;
        objArr[6] = str7;
        objArr[7] = identifier(valueOf.dataType);
        objArr[8] = valueOf.characterPrecision;
        objArr[9] = valueOf.characterPrecision;
        objArr[10] = str13;
        objArr[11] = str12;
        objArr[12] = obj;
        objArr[13] = str13;
        objArr[14] = str12;
        objArr[15] = str11;
        objArr[16] = valueOf.numericPrecision;
        objArr[17] = valueOf.numericPrecisionRadix;
        objArr[18] = valueOf.numericScale;
        objArr[19] = valueOf.datetimePrecision;
        objArr[20] = valueOf.intervalType;
        objArr[21] = valueOf.intervalPrecision;
        objArr[22] = valueOf.maximumCardinality;
        objArr[23] = "RESULT";
        objArr[24] = "EXTERNAL";
        objArr[25] = str8;
        objArr[26] = str9;
        objArr[27] = "JAVA";
        objArr[28] = "GENERAL";
        objArr[29] = z ? "YES" : "NO";
        objArr[30] = valueOf.declaredDataType;
        objArr[31] = valueOf.declaredNumericPrecision;
        objArr[32] = valueOf.declaredNumericScale;
        objArr[33] = valueOf.geometryType;
        objArr[34] = valueOf.geometrySrid;
        objArr[35] = str10;
        add(sessionLocal, arrayList, objArr);
    }

    private void schemata(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str) {
        String name = this.database.getMainSchema().getName();
        String name2 = this.database.getCompareMode().getName();
        for (Schema schema : this.database.getAllSchemas()) {
            add(sessionLocal, arrayList, str, schema.getName(), identifier(schema.getOwner().getName()), str, name, CHARACTER_SET_NAME, null, name2, schema.getComment());
        }
    }

    private void sequences(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Sequence sequence : it.next().getAllSequences()) {
                if (!sequence.getBelongsToTable()) {
                    String name = sequence.getName();
                    if (checkIndex(sessionLocal, name, value, value2)) {
                        sequences(sessionLocal, arrayList, str, sequence, name);
                    }
                }
            }
        }
    }

    private void sequences(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Sequence sequence, String str2) {
        DataTypeInformation valueOf = DataTypeInformation.valueOf(sequence.getDataType());
        Sequence.Cycle cycle = sequence.getCycle();
        Object[] objArr = new Object[18];
        objArr[0] = str;
        objArr[1] = sequence.getSchema().getName();
        objArr[2] = str2;
        objArr[3] = valueOf.dataType;
        objArr[4] = ValueInteger.get(sequence.getEffectivePrecision());
        objArr[5] = valueOf.numericPrecisionRadix;
        objArr[6] = valueOf.numericScale;
        objArr[7] = ValueBigint.get(sequence.getStartValue());
        objArr[8] = ValueBigint.get(sequence.getMinValue());
        objArr[9] = ValueBigint.get(sequence.getMaxValue());
        objArr[10] = ValueBigint.get(sequence.getIncrement());
        objArr[11] = cycle.isCycle() ? "YES" : "NO";
        objArr[12] = valueOf.declaredDataType;
        objArr[13] = valueOf.declaredNumericPrecision;
        objArr[14] = valueOf.declaredNumericScale;
        objArr[15] = cycle != Sequence.Cycle.EXHAUSTED ? ValueBigint.get(sequence.getBaseValue()) : null;
        objArr[16] = ValueBigint.get(sequence.getCacheSize());
        objArr[17] = sequence.getComment();
        add(sessionLocal, arrayList, objArr);
    }

    private void tables(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Table table : it.next().getAllTablesAndViews(sessionLocal)) {
                String name = table.getName();
                if (checkIndex(sessionLocal, name, value, value2)) {
                    tables(sessionLocal, arrayList, str, table, name);
                }
            }
        }
        for (Table table2 : sessionLocal.getLocalTempTables()) {
            String name2 = table2.getName();
            if (checkIndex(sessionLocal, name2, value, value2)) {
                tables(sessionLocal, arrayList, str, table2, name2);
            }
        }
    }

    private void tables(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Table table, String str2) {
        String str3;
        String str4;
        if (hideTable(table, sessionLocal)) {
            return;
        }
        if (table.isTemporary()) {
            str3 = table.getOnCommitTruncate() ? "DELETE" : table.getOnCommitDrop() ? "DROP" : "PRESERVE";
            str4 = table.isGlobalTemporary() ? "GLOBAL TEMPORARY" : "LOCAL TEMPORARY";
        } else {
            str3 = null;
            switch (table.getTableType()) {
                case TABLE_LINK:
                    str4 = "TABLE LINK";
                    break;
                case EXTERNAL_TABLE_ENGINE:
                    str4 = "EXTERNAL";
                    break;
                default:
                    str4 = table.isPersistIndexes() ? "CACHED" : "MEMORY";
                    break;
            }
        }
        long maxDataModificationId = table.getMaxDataModificationId();
        Object[] objArr = new Object[11];
        objArr[0] = str;
        objArr[1] = table.getSchema().getName();
        objArr[2] = str2;
        objArr[3] = table.getSQLTableType();
        objArr[4] = table.isInsertable() ? "YES" : "NO";
        objArr[5] = str3;
        objArr[6] = str4;
        objArr[7] = table.getComment();
        objArr[8] = maxDataModificationId != Long.MAX_VALUE ? ValueBigint.get(maxDataModificationId) : null;
        objArr[9] = table.getClass().getName();
        objArr[10] = ValueBigint.get(table.getRowCountApproximation(sessionLocal));
        add(sessionLocal, arrayList, objArr);
    }

    private void tableConstraints(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Constraint constraint : it.next().getAllConstraints()) {
                Constraint.Type constraintType = constraint.getConstraintType();
                if (constraintType != Constraint.Type.DOMAIN) {
                    Table table = constraint.getTable();
                    if (!hideTable(table, sessionLocal)) {
                        String name = table.getName();
                        if (checkIndex(sessionLocal, name, value, value2)) {
                            tableConstraints(sessionLocal, arrayList, str, constraint, constraintType, table, name);
                        }
                    }
                }
            }
        }
    }

    private void tableConstraints(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Constraint constraint, Constraint.Type type, Table table, String str2) {
        boolean z;
        Index index = constraint.getIndex();
        if (type != Constraint.Type.REFERENTIAL) {
            z = true;
        } else {
            z = this.database.getReferentialIntegrity() && table.getCheckForeignKeyConstraints() && constraint.getRefTable().getCheckForeignKeyConstraints();
        }
        Object[] objArr = new Object[15];
        objArr[0] = str;
        objArr[1] = constraint.getSchema().getName();
        objArr[2] = constraint.getName();
        objArr[3] = type.getSqlName();
        objArr[4] = str;
        objArr[5] = table.getSchema().getName();
        objArr[6] = str2;
        objArr[7] = "NO";
        objArr[8] = "NO";
        objArr[9] = z ? "YES" : "NO";
        objArr[10] = type == Constraint.Type.UNIQUE ? nullsDistinctToString(((ConstraintUnique) constraint).getNullsDistinct()) : null;
        objArr[11] = index != null ? str : null;
        objArr[12] = index != null ? index.getSchema().getName() : null;
        objArr[13] = index != null ? index.getName() : null;
        objArr[14] = constraint.getComment();
        add(sessionLocal, arrayList, objArr);
    }

    private void tablePrivileges(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Right> it = this.database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            DbObject grantedObject = next.getGrantedObject();
            if (grantedObject instanceof Table) {
                Table table = (Table) grantedObject;
                if (!hideTable(table, sessionLocal) && checkIndex(sessionLocal, table.getName(), value, value2)) {
                    addPrivileges(sessionLocal, arrayList, next.getGrantee(), str, table, null, next.getRightMask());
                }
            }
        }
    }

    private void triggers(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (TriggerObject triggerObject : it.next().getAllTriggers()) {
                Table table = triggerObject.getTable();
                String name = table.getName();
                if (checkIndex(sessionLocal, name, value, value2)) {
                    int typeMask = triggerObject.getTypeMask();
                    if ((typeMask & 1) != 0) {
                        triggers(sessionLocal, arrayList, str, triggerObject, "INSERT", table, name);
                    }
                    if ((typeMask & 2) != 0) {
                        triggers(sessionLocal, arrayList, str, triggerObject, "UPDATE", table, name);
                    }
                    if ((typeMask & 4) != 0) {
                        triggers(sessionLocal, arrayList, str, triggerObject, "DELETE", table, name);
                    }
                    if ((typeMask & 8) != 0) {
                        triggers(sessionLocal, arrayList, str, triggerObject, "SELECT", table, name);
                    }
                }
            }
        }
    }

    private void triggers(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, TriggerObject triggerObject, String str2, Table table, String str3) {
        Object[] objArr = new Object[14];
        objArr[0] = str;
        objArr[1] = triggerObject.getSchema().getName();
        objArr[2] = triggerObject.getName();
        objArr[3] = str2;
        objArr[4] = str;
        objArr[5] = table.getSchema().getName();
        objArr[6] = str3;
        objArr[7] = triggerObject.isRowBased() ? "ROW" : "STATEMENT";
        objArr[8] = triggerObject.isInsteadOf() ? "INSTEAD OF" : triggerObject.isBefore() ? "BEFORE" : "AFTER";
        objArr[9] = ValueBoolean.get(triggerObject.isOnRollback());
        objArr[10] = triggerObject.getTriggerClassName();
        objArr[11] = ValueInteger.get(triggerObject.getQueueSize());
        objArr[12] = ValueBoolean.get(triggerObject.isNoWait());
        objArr[13] = triggerObject.getComment();
        add(sessionLocal, arrayList, objArr);
    }

    private void views(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Table table : it.next().getAllTablesAndViews(sessionLocal)) {
                if (table.isView()) {
                    String name = table.getName();
                    if (checkIndex(sessionLocal, name, value, value2)) {
                        views(sessionLocal, arrayList, str, table, name);
                    }
                }
            }
        }
        for (Table table2 : sessionLocal.getLocalTempTables()) {
            if (table2.isView()) {
                String name2 = table2.getName();
                if (checkIndex(sessionLocal, name2, value, value2)) {
                    views(sessionLocal, arrayList, str, table2, name2);
                }
            }
        }
    }

    private void views(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Table table, String str2) {
        String str3;
        Object obj = "VALID";
        if (table instanceof TableView) {
            TableView tableView = (TableView) table;
            str3 = tableView.getQuerySQL();
            if (tableView.isInvalid()) {
                obj = "INVALID";
            }
        } else {
            str3 = null;
        }
        int i = 0;
        ArrayList<TriggerObject> triggers = table.getTriggers();
        if (triggers != null) {
            Iterator<TriggerObject> it = triggers.iterator();
            while (it.hasNext()) {
                TriggerObject next = it.next();
                if (next.isInsteadOf()) {
                    i |= next.getTypeMask();
                }
            }
        }
        Object[] objArr = new Object[12];
        objArr[0] = str;
        objArr[1] = table.getSchema().getName();
        objArr[2] = str2;
        objArr[3] = str3;
        objArr[4] = "NONE";
        objArr[5] = "NO";
        objArr[6] = "NO";
        objArr[7] = (i & 2) != 0 ? "YES" : "NO";
        objArr[8] = (i & 4) != 0 ? "YES" : "NO";
        objArr[9] = (i & 1) != 0 ? "YES" : "NO";
        objArr[10] = obj;
        objArr[11] = table.getComment();
        add(sessionLocal, arrayList, objArr);
    }

    private void constants(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str) {
        String name = this.database.getMainSchema().getName();
        String name2 = this.database.getCompareMode().getName();
        Iterator<Schema> it = this.database.getAllSchemas().iterator();
        while (it.hasNext()) {
            for (Constant constant : it.next().getAllConstants()) {
                String name3 = constant.getName();
                if (checkIndex(sessionLocal, name3, value, value2)) {
                    constants(sessionLocal, arrayList, str, name, name2, constant, name3);
                }
            }
        }
    }

    private void constants(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, Constant constant, String str4) {
        String str5;
        Object obj;
        String str6;
        String str7;
        ValueExpression value = constant.getValue();
        DataTypeInformation valueOf = DataTypeInformation.valueOf(value.getType());
        if (valueOf.hasCharsetAndCollation) {
            str7 = str;
            str6 = str2;
            obj = CHARACTER_SET_NAME;
            str5 = str3;
        } else {
            str5 = null;
            obj = null;
            str6 = null;
            str7 = null;
        }
        add(sessionLocal, arrayList, str, constant.getSchema().getName(), str4, value.getSQL(0), valueOf.dataType, valueOf.characterPrecision, valueOf.characterPrecision, str7, str6, obj, str7, str6, str5, valueOf.numericPrecision, valueOf.numericPrecisionRadix, valueOf.numericScale, valueOf.datetimePrecision, valueOf.intervalType, valueOf.intervalPrecision, valueOf.maximumCardinality, "TYPE", valueOf.declaredDataType, valueOf.declaredNumericPrecision, valueOf.declaredNumericScale, valueOf.geometryType, valueOf.geometrySrid, constant.getComment());
    }

    private void enumValues(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, String str2, String str3, String str4, String str5, TypeInfo typeInfo) {
        ExtTypeInfoEnum extTypeInfoEnum = (ExtTypeInfoEnum) typeInfo.getExtTypeInfo();
        if (extTypeInfoEnum == null) {
            return;
        }
        int i = 0;
        int i2 = sessionLocal.zeroBasedEnums() ? 0 : 1;
        int count = extTypeInfoEnum.getCount();
        while (i < count) {
            add(sessionLocal, arrayList, str, str2, str3, str4, str5, extTypeInfoEnum.getEnumerator(i), ValueInteger.get(i2));
            i++;
            i2++;
        }
    }

    private void indexes(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList, String str, boolean z) {
        if (value != null && value.equals(value2)) {
            String string = value.getString();
            if (string == null) {
                return;
            }
            Iterator<Schema> it = this.database.getAllSchemas().iterator();
            while (it.hasNext()) {
                Table tableOrViewByName = it.next().getTableOrViewByName(sessionLocal, string);
                if (tableOrViewByName != null) {
                    indexes(sessionLocal, arrayList, str, z, tableOrViewByName, tableOrViewByName.getName());
                }
            }
            Table findLocalTempTable = sessionLocal.findLocalTempTable(string);
            if (findLocalTempTable != null) {
                indexes(sessionLocal, arrayList, str, z, findLocalTempTable, findLocalTempTable.getName());
                return;
            }
            return;
        }
        Iterator<Schema> it2 = this.database.getAllSchemas().iterator();
        while (it2.hasNext()) {
            for (Table table : it2.next().getAllTablesAndViews(sessionLocal)) {
                String name = table.getName();
                if (checkIndex(sessionLocal, name, value, value2)) {
                    indexes(sessionLocal, arrayList, str, z, table, name);
                }
            }
        }
        for (Table table2 : sessionLocal.getLocalTempTables()) {
            String name2 = table2.getName();
            if (checkIndex(sessionLocal, name2, value, value2)) {
                indexes(sessionLocal, arrayList, str, z, table2, name2);
            }
        }
    }

    private void indexes(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, boolean z, Table table, String str2) {
        ArrayList<Index> indexes;
        if (hideTable(table, sessionLocal) || (indexes = table.getIndexes()) == null) {
            return;
        }
        Iterator<Index> it = indexes.iterator();
        while (it.hasNext()) {
            Index next = it.next();
            if (next.getCreateSQL() != null) {
                if (z) {
                    indexColumns(sessionLocal, arrayList, str, table, str2, next);
                } else {
                    indexes(sessionLocal, arrayList, str, table, str2, next);
                }
            }
        }
    }

    private void indexes(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Table table, String str2, Index index) {
        IndexType indexType = index.getIndexType();
        add(sessionLocal, arrayList, str, index.getSchema().getName(), index.getName(), str, table.getSchema().getName(), str2, indexType.getSQL(false), nullsDistinctToString(indexType.getNullsDistinct()), ValueBoolean.get(indexType.getBelongsToConstraint()), index.getComment(), index.getClass().getName());
    }

    private void indexColumns(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str, Table table, String str2, Index index) {
        IndexColumn[] indexColumns = index.getIndexColumns();
        int uniqueColumnCount = index.getUniqueColumnCount();
        int i = 0;
        int length = indexColumns.length;
        while (i < length) {
            IndexColumn indexColumn = indexColumns[i];
            int i2 = indexColumn.sortType;
            Object[] objArr = new Object[11];
            objArr[0] = str;
            objArr[1] = index.getSchema().getName();
            objArr[2] = index.getName();
            objArr[3] = str;
            objArr[4] = table.getSchema().getName();
            objArr[5] = str2;
            objArr[6] = indexColumn.column.getName();
            i++;
            objArr[7] = ValueInteger.get(i);
            objArr[8] = (i2 & 1) == 0 ? "ASC" : "DESC";
            objArr[9] = (i2 & 2) != 0 ? "FIRST" : (i2 & 4) != 0 ? "LAST" : null;
            objArr[10] = ValueBoolean.get(i <= uniqueColumnCount);
            add(sessionLocal, arrayList, objArr);
        }
    }

    private void inDoubt(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        if (sessionLocal.getUser().isAdmin()) {
            Iterator<InDoubtTransaction> it = this.database.getInDoubtTransactions().iterator();
            while (it.hasNext()) {
                InDoubtTransaction next = it.next();
                add(sessionLocal, arrayList, next.getTransactionName(), next.getStateDescription());
            }
        }
    }

    private void locks(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        if (sessionLocal.getUser().isAdmin()) {
            for (SessionLocal sessionLocal2 : this.database.getSessions(false)) {
                locks(sessionLocal, arrayList, sessionLocal2);
            }
            return;
        }
        locks(sessionLocal, arrayList, sessionLocal);
    }

    private void locks(SessionLocal sessionLocal, ArrayList<Row> arrayList, SessionLocal sessionLocal2) {
        for (Table table : sessionLocal2.getLocks()) {
            Object[] objArr = new Object[4];
            objArr[0] = table.getSchema().getName();
            objArr[1] = table.getName();
            objArr[2] = ValueInteger.get(sessionLocal2.getId());
            objArr[3] = table.isLockedExclusivelyBy(sessionLocal2) ? "WRITE" : "READ";
            add(sessionLocal, arrayList, objArr);
        }
    }

    private void queryStatistics(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        QueryStatisticsData queryStatisticsData = this.database.getQueryStatisticsData();
        if (queryStatisticsData != null) {
            for (QueryStatisticsData.QueryEntry queryEntry : queryStatisticsData.getQueries()) {
                add(sessionLocal, arrayList, queryEntry.sqlStatement, ValueInteger.get(queryEntry.count), ValueDouble.get(queryEntry.executionTimeMinNanos / 1000000.0d), ValueDouble.get(queryEntry.executionTimeMaxNanos / 1000000.0d), ValueDouble.get(queryEntry.executionTimeCumulativeNanos / 1000000.0d), ValueDouble.get(queryEntry.executionTimeMeanNanos / 1000000.0d), ValueDouble.get(queryEntry.getExecutionTimeStandardDeviation() / 1000000.0d), ValueBigint.get(queryEntry.rowCountMin), ValueBigint.get(queryEntry.rowCountMax), ValueBigint.get(queryEntry.rowCountCumulative), ValueDouble.get(queryEntry.rowCountMean), ValueDouble.get(queryEntry.getRowCountStandardDeviation()));
            }
        }
    }

    private void rights(SessionLocal sessionLocal, Value value, Value value2, ArrayList<Row> arrayList) {
        if (!sessionLocal.getUser().isAdmin()) {
            return;
        }
        Iterator<Right> it = this.database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            Role grantedRole = next.getGrantedRole();
            DbObject grantee = next.getGrantee();
            String str = grantee.getType() == 2 ? "USER" : "ROLE";
            if (grantedRole == null) {
                DbObject grantedObject = next.getGrantedObject();
                Schema schema = null;
                Table table = null;
                if (grantedObject != null) {
                    if (grantedObject instanceof Schema) {
                        schema = (Schema) grantedObject;
                    } else if (grantedObject instanceof Table) {
                        table = (Table) grantedObject;
                        schema = table.getSchema();
                    }
                }
                String name = table != null ? table.getName() : "";
                String name2 = schema != null ? schema.getName() : "";
                if (checkIndex(sessionLocal, name, value, value2)) {
                    add(sessionLocal, arrayList, identifier(grantee.getName()), str, null, next.getRights(), name2, name);
                }
            } else {
                add(sessionLocal, arrayList, identifier(grantee.getName()), str, identifier(grantedRole.getName()), null, null, null);
            }
        }
    }

    private void roles(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        boolean isAdmin = sessionLocal.getUser().isAdmin();
        for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) {
            if (rightOwner instanceof Role) {
                Role role = (Role) rightOwner;
                if (isAdmin || sessionLocal.getUser().isRoleGranted(role)) {
                    add(sessionLocal, arrayList, identifier(role.getName()), role.getComment());
                }
            }
        }
    }

    private void sessions(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        if (sessionLocal.getUser().isAdmin()) {
            for (SessionLocal sessionLocal2 : this.database.getSessions(false)) {
                sessions(sessionLocal, arrayList, sessionLocal2);
            }
            return;
        }
        sessions(sessionLocal, arrayList, sessionLocal);
    }

    private void sessions(SessionLocal sessionLocal, ArrayList<Row> arrayList, SessionLocal sessionLocal2) {
        NetworkConnectionInfo networkConnectionInfo = sessionLocal2.getNetworkConnectionInfo();
        Command currentCommand = sessionLocal2.getCurrentCommand();
        int blockingSessionId = sessionLocal2.getBlockingSessionId();
        User user = sessionLocal2.getUser();
        if (user == null) {
            return;
        }
        Object[] objArr = new Object[13];
        objArr[0] = ValueInteger.get(sessionLocal2.getId());
        objArr[1] = user.getName();
        objArr[2] = networkConnectionInfo == null ? null : networkConnectionInfo.getServer();
        objArr[3] = networkConnectionInfo == null ? null : networkConnectionInfo.getClient();
        objArr[4] = networkConnectionInfo == null ? null : networkConnectionInfo.getClientInfo();
        objArr[5] = sessionLocal2.getSessionStart();
        objArr[6] = sessionLocal2.getIsolationLevel().getSQL();
        objArr[7] = currentCommand == null ? null : currentCommand.toString();
        objArr[8] = currentCommand == null ? null : sessionLocal2.getCommandStartOrEnd();
        objArr[9] = ValueBoolean.get(sessionLocal2.hasPendingTransaction());
        objArr[10] = String.valueOf(sessionLocal2.getState());
        objArr[11] = blockingSessionId == 0 ? null : ValueInteger.get(blockingSessionId);
        objArr[12] = sessionLocal2.getState() == SessionLocal.State.SLEEP ? sessionLocal2.getCommandStartOrEnd() : null;
        add(sessionLocal, arrayList, objArr);
    }

    private void sessionState(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        for (String str : sessionLocal.getVariableNames()) {
            Value variable = sessionLocal.getVariable(str);
            StringBuilder append = new StringBuilder().append("SET @").append(str).append(' ');
            variable.getSQL(append, 0);
            add(sessionLocal, arrayList, StrPool.AT + str, append.toString());
        }
        for (Table table : sessionLocal.getLocalTempTables()) {
            add(sessionLocal, arrayList, "TABLE " + table.getName(), table.getCreateSQL());
        }
        String[] schemaSearchPath = sessionLocal.getSchemaSearchPath();
        if (schemaSearchPath != null && schemaSearchPath.length > 0) {
            StringBuilder sb = new StringBuilder("SET SCHEMA_SEARCH_PATH ");
            int length = schemaSearchPath.length;
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                StringUtils.quoteIdentifier(sb, schemaSearchPath[i]);
            }
            add(sessionLocal, arrayList, "SCHEMA_SEARCH_PATH", sb.toString());
        }
        String currentSchemaName = sessionLocal.getCurrentSchemaName();
        if (currentSchemaName != null) {
            add(sessionLocal, arrayList, "SCHEMA", StringUtils.quoteIdentifier(new StringBuilder("SET SCHEMA "), currentSchemaName).toString());
        }
        TimeZoneProvider currentTimeZone = sessionLocal.currentTimeZone();
        if (!currentTimeZone.equals(DateTimeUtils.getTimeZone())) {
            add(sessionLocal, arrayList, "TIME ZONE", StringUtils.quoteStringSQL(new StringBuilder("SET TIME ZONE "), currentTimeZone.getId()).toString());
        }
    }

    private void settings(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        for (Setting setting : this.database.getAllSettings()) {
            String stringValue = setting.getStringValue();
            if (stringValue == null) {
                stringValue = Integer.toString(setting.getIntValue());
            }
            add(sessionLocal, arrayList, identifier(setting.getName()), stringValue);
        }
        add(sessionLocal, arrayList, "info.BUILD_ID", "224");
        add(sessionLocal, arrayList, "info.VERSION_MAJOR", "2");
        add(sessionLocal, arrayList, "info.VERSION_MINOR", "2");
        add(sessionLocal, arrayList, "info.VERSION", Constants.FULL_VERSION);
        if (sessionLocal.getUser().isAdmin()) {
            for (String str : new String[]{"java.runtime.version", "java.vm.name", "java.vendor", "os.name", "os.arch", "os.version", "sun.os.patch.level", "file.separator", "path.separator", "line.separator", "user.country", "user.language", "user.variant", "file.encoding"}) {
                add(sessionLocal, arrayList, "property." + str, Utils.getProperty(str, ""));
            }
        }
        add(sessionLocal, arrayList, "DEFAULT_NULL_ORDERING", this.database.getDefaultNullOrdering().name());
        Object[] objArr = new Object[2];
        objArr[0] = "EXCLUSIVE";
        objArr[1] = this.database.getExclusiveSession() == null ? "FALSE" : Constants.CLUSTERING_ENABLED;
        add(sessionLocal, arrayList, objArr);
        add(sessionLocal, arrayList, "MODE", this.database.getMode().getName());
        add(sessionLocal, arrayList, "QUERY_TIMEOUT", Integer.toString(sessionLocal.getQueryTimeout()));
        add(sessionLocal, arrayList, "TIME ZONE", sessionLocal.currentTimeZone().getId());
        Object[] objArr2 = new Object[2];
        objArr2[0] = "TRUNCATE_LARGE_LENGTH";
        objArr2[1] = sessionLocal.isTruncateLargeLength() ? Constants.CLUSTERING_ENABLED : "FALSE";
        add(sessionLocal, arrayList, objArr2);
        Object[] objArr3 = new Object[2];
        objArr3[0] = "VARIABLE_BINARY";
        objArr3[1] = sessionLocal.isVariableBinary() ? Constants.CLUSTERING_ENABLED : "FALSE";
        add(sessionLocal, arrayList, objArr3);
        Object[] objArr4 = new Object[2];
        objArr4[0] = "OLD_INFORMATION_SCHEMA";
        objArr4[1] = sessionLocal.isOldInformationSchema() ? Constants.CLUSTERING_ENABLED : "FALSE";
        add(sessionLocal, arrayList, objArr4);
        BitSet nonKeywords = sessionLocal.getNonKeywords();
        if (nonKeywords != null) {
            add(sessionLocal, arrayList, "NON_KEYWORDS", ParserBase.formatNonKeywords(nonKeywords));
        }
        add(sessionLocal, arrayList, "RETENTION_TIME", Integer.toString(this.database.getRetentionTime()));
        add(sessionLocal, arrayList, "WRITE_DELAY", Integer.toString(this.database.getWriteDelay()));
        for (Map.Entry<String, String> entry : this.database.getSettings().getSortedSettings()) {
            add(sessionLocal, arrayList, entry.getKey(), entry.getValue());
        }
        this.database.getStore().getMvStore().populateInfo((str2, str3) -> {
            add(sessionLocal, arrayList, str2, str3);
        });
    }

    private void synonyms(SessionLocal sessionLocal, ArrayList<Row> arrayList, String str) {
        Iterator<TableSynonym> it = this.database.getAllSynonyms().iterator();
        while (it.hasNext()) {
            TableSynonym next = it.next();
            add(sessionLocal, arrayList, str, next.getSchema().getName(), next.getName(), next.getSynonymForName(), next.getSynonymForSchema().getName(), "SYNONYM", "VALID", next.getComment());
        }
    }

    private void users(SessionLocal sessionLocal, ArrayList<Row> arrayList) {
        User user = sessionLocal.getUser();
        if (user.isAdmin()) {
            for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) {
                if (rightOwner instanceof User) {
                    users(sessionLocal, arrayList, (User) rightOwner);
                }
            }
            return;
        }
        users(sessionLocal, arrayList, user);
    }

    private void users(SessionLocal sessionLocal, ArrayList<Row> arrayList, User user) {
        add(sessionLocal, arrayList, identifier(user.getName()), ValueBoolean.get(user.isAdmin()), user.getComment());
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
            add(sessionLocal, arrayList, null, identifier(dbObject.getName()), str, table.getSchema().getName(), table.getName(), str3, obj, "NO");
        } else {
            add(sessionLocal, arrayList, null, identifier(dbObject.getName()), str, table.getSchema().getName(), table.getName(), str2, str3, obj);
        }
    }

    private static String nullsDistinctToString(NullsDistinct nullsDistinct) {
        if (nullsDistinct != null) {
            switch (nullsDistinct) {
                case DISTINCT:
                    return "YES";
                case ALL_DISTINCT:
                    return "ALL";
                case NOT_DISTINCT:
                    return "NO";
                default:
                    return null;
            }
        }
        return null;
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        switch (this.type) {
            case 15:
            case 25:
            case 26:
            case 30:
            case 31:
            case 32:
                return Long.MAX_VALUE;
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 27:
            case 28:
            case 29:
            default:
                return this.database.getModificationDataId();
        }
    }

    @Override // org.h2.table.Table
    public boolean isView() {
        return this.isView;
    }

    @Override // org.h2.table.MetaTable, org.h2.table.Table
    public long getRowCount(SessionLocal sessionLocal) {
        return getRowCount(sessionLocal, false);
    }

    @Override // org.h2.table.MetaTable, org.h2.table.Table
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return getRowCount(sessionLocal, true);
    }

    private long getRowCount(SessionLocal sessionLocal, boolean z) {
        switch (this.type) {
            case 0:
                return 1L;
            case 2:
                if (CompareMode.getCollationLocales(z) != null) {
                    return r0.length + 1;
                }
                break;
            case 14:
                return sessionLocal.getDatabase().getAllSchemas().size();
            case 25:
                if (sessionLocal.getUser().isAdmin()) {
                    return sessionLocal.getDatabase().getInDoubtTransactions().size();
                }
                return 0L;
            case 29:
                if (sessionLocal.getUser().isAdmin()) {
                    long j = 0;
                    Iterator<RightOwner> it = sessionLocal.getDatabase().getAllUsersAndRoles().iterator();
                    while (it.hasNext()) {
                        if (it.next() instanceof Role) {
                            j++;
                        }
                    }
                    return j;
                }
                break;
            case 30:
                if (sessionLocal.getUser().isAdmin()) {
                    return sessionLocal.getDatabase().getSessionCount();
                }
                return 1L;
            case 34:
                if (sessionLocal.getUser().isAdmin()) {
                    long j2 = 0;
                    Iterator<RightOwner> it2 = sessionLocal.getDatabase().getAllUsersAndRoles().iterator();
                    while (it2.hasNext()) {
                        if (it2.next() instanceof User) {
                            j2++;
                        }
                    }
                    return j2;
                }
                return 1L;
        }
        if (z) {
            return 1000L;
        }
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.table.MetaTable, org.h2.table.Table
    public boolean canGetRowCount(SessionLocal sessionLocal) {
        switch (this.type) {
            case 0:
            case 2:
            case 14:
            case 25:
            case 30:
            case 34:
                return true;
            case 29:
                if (sessionLocal.getUser().isAdmin()) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/InformationSchemaTable$DataTypeInformation.class */
    public static final class DataTypeInformation {
        static final DataTypeInformation NULL = new DataTypeInformation(null, null, null, null, null, null, null, null, null, false, null, null, null, null, null);
        final String dataType;
        final Value characterPrecision;
        final Value numericPrecision;
        final Value numericPrecisionRadix;
        final Value numericScale;
        final Value datetimePrecision;
        final Value intervalPrecision;
        final Value intervalType;
        final Value maximumCardinality;
        final boolean hasCharsetAndCollation;
        final String declaredDataType;
        final Value declaredNumericPrecision;
        final Value declaredNumericScale;
        final String geometryType;
        final Value geometrySrid;

        static DataTypeInformation valueOf(TypeInfo typeInfo) {
            int valueType = typeInfo.getValueType();
            String typeName = Value.getTypeName(valueType);
            ValueBigint valueBigint = null;
            ValueInteger valueInteger = null;
            ValueInteger valueInteger2 = null;
            ValueInteger valueInteger3 = null;
            ValueInteger valueInteger4 = null;
            ValueInteger valueInteger5 = null;
            ValueInteger valueInteger6 = null;
            String str = null;
            boolean z = false;
            String str2 = null;
            ValueInteger valueInteger7 = null;
            ValueInteger valueInteger8 = null;
            String str3 = null;
            ValueInteger valueInteger9 = null;
            switch (valueType) {
                case 1:
                case 2:
                case 3:
                case 4:
                    z = true;
                case 5:
                case 6:
                case 7:
                case 35:
                case 38:
                    valueBigint = ValueBigint.get(typeInfo.getPrecision());
                    break;
                case 9:
                case 10:
                case 11:
                case 12:
                    valueInteger = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
                    valueInteger2 = ValueInteger.get(0);
                    valueInteger3 = ValueInteger.get(2);
                    str2 = typeName;
                    break;
                case 13:
                    valueInteger = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
                    valueInteger2 = ValueInteger.get(typeInfo.getScale());
                    valueInteger3 = ValueInteger.get(10);
                    str2 = typeInfo.getExtTypeInfo() != null ? "DECIMAL" : "NUMERIC";
                    if (typeInfo.getDeclaredPrecision() >= 0) {
                        valueInteger7 = valueInteger;
                    }
                    if (typeInfo.getDeclaredScale() >= 0) {
                        valueInteger8 = valueInteger2;
                        break;
                    }
                    break;
                case 14:
                case 15:
                    valueInteger = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
                    valueInteger3 = ValueInteger.get(2);
                    long declaredPrecision = typeInfo.getDeclaredPrecision();
                    if (declaredPrecision >= 0) {
                        str2 = "FLOAT";
                        if (declaredPrecision > 0) {
                            valueInteger7 = ValueInteger.get((int) declaredPrecision);
                            break;
                        }
                    } else {
                        str2 = typeName;
                        break;
                    }
                    break;
                case 16:
                    valueInteger = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
                    valueInteger3 = ValueInteger.get(10);
                    str2 = typeName;
                    if (typeInfo.getDeclaredPrecision() >= 0) {
                        valueInteger7 = valueInteger;
                        break;
                    }
                    break;
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                    str = IntervalQualifier.valueOf(valueType - 22).toString();
                    typeName = "INTERVAL";
                    valueInteger5 = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                    valueInteger4 = ValueInteger.get(typeInfo.getScale());
                    break;
                case 37:
                    ExtTypeInfoGeometry extTypeInfoGeometry = (ExtTypeInfoGeometry) typeInfo.getExtTypeInfo();
                    if (extTypeInfoGeometry != null) {
                        int type = extTypeInfoGeometry.getType();
                        if (type != 0) {
                            str3 = EWKTUtils.formatGeometryTypeAndDimensionSystem(new StringBuilder(), type).toString();
                        }
                        Integer srid = extTypeInfoGeometry.getSrid();
                        if (srid != null) {
                            valueInteger9 = ValueInteger.get(srid.intValue());
                            break;
                        }
                    }
                    break;
                case 40:
                    valueInteger6 = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
                    break;
            }
            return new DataTypeInformation(typeName, valueBigint, valueInteger, valueInteger3, valueInteger2, valueInteger4, valueInteger5, str != null ? ValueVarchar.get(str) : ValueNull.INSTANCE, valueInteger6, z, str2, valueInteger7, valueInteger8, str3, valueInteger9);
        }

        private DataTypeInformation(String str, Value value, Value value2, Value value3, Value value4, Value value5, Value value6, Value value7, Value value8, boolean z, String str2, Value value9, Value value10, String str3, Value value11) {
            this.dataType = str;
            this.characterPrecision = value;
            this.numericPrecision = value2;
            this.numericPrecisionRadix = value3;
            this.numericScale = value4;
            this.datetimePrecision = value5;
            this.intervalPrecision = value6;
            this.intervalType = value7;
            this.maximumCardinality = value8;
            this.hasCharsetAndCollation = z;
            this.declaredDataType = str2;
            this.declaredNumericPrecision = value9;
            this.declaredNumericScale = value10;
            this.geometryType = str3;
            this.geometrySrid = value11;
        }
    }
}
