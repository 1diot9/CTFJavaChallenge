package org.h2.mode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringJoiner;
import org.h2.api.ErrorCode;
import org.h2.command.Parser;
import org.h2.engine.Constants;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.expression.function.CurrentGeneralValueSpecification;
import org.h2.expression.function.RandFunction;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.server.pg.PgServer;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/FunctionsPostgreSQL.class */
public final class FunctionsPostgreSQL extends ModeFunction {
    private static final int CURRENT_DATABASE = 3001;
    private static final int CURRTID2 = 3002;
    private static final int FORMAT_TYPE = 3003;
    private static final int HAS_DATABASE_PRIVILEGE = 3004;
    private static final int HAS_SCHEMA_PRIVILEGE = 3005;
    private static final int HAS_TABLE_PRIVILEGE = 3006;
    private static final int LASTVAL = 3007;
    private static final int VERSION = 3008;
    private static final int OBJ_DESCRIPTION = 3009;
    private static final int PG_ENCODING_TO_CHAR = 3010;
    private static final int PG_GET_EXPR = 3011;
    private static final int PG_GET_INDEXDEF = 3012;
    private static final int PG_GET_USERBYID = 3013;
    private static final int PG_POSTMASTER_START_TIME = 3014;
    private static final int PG_RELATION_SIZE = 3015;
    private static final int PG_TABLE_IS_VISIBLE = 3016;
    private static final int SET_CONFIG = 3017;
    private static final int ARRAY_TO_STRING = 3018;
    private static final int PG_STAT_GET_NUMSCANS = 3019;
    private static final int TO_DATE = 3020;
    private static final int TO_TIMESTAMP = 3021;
    private static final int GEN_RANDOM_UUID = 3022;
    private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>(32);

    static {
        FUNCTIONS.put("CURRENT_DATABASE", new FunctionInfo("CURRENT_DATABASE", CURRENT_DATABASE, 0, 2, true, false));
        FUNCTIONS.put("CURRTID2", new FunctionInfo("CURRTID2", CURRTID2, 2, 11, true, false));
        FUNCTIONS.put("FORMAT_TYPE", new FunctionInfo("FORMAT_TYPE", FORMAT_TYPE, 2, 2, false, true));
        FUNCTIONS.put("HAS_DATABASE_PRIVILEGE", new FunctionInfo("HAS_DATABASE_PRIVILEGE", HAS_DATABASE_PRIVILEGE, -1, 8, true, false));
        FUNCTIONS.put("HAS_SCHEMA_PRIVILEGE", new FunctionInfo("HAS_SCHEMA_PRIVILEGE", HAS_SCHEMA_PRIVILEGE, -1, 8, true, false));
        FUNCTIONS.put("HAS_TABLE_PRIVILEGE", new FunctionInfo("HAS_TABLE_PRIVILEGE", HAS_TABLE_PRIVILEGE, -1, 8, true, false));
        FUNCTIONS.put("LASTVAL", new FunctionInfo("LASTVAL", LASTVAL, 0, 12, true, false));
        FUNCTIONS.put("VERSION", new FunctionInfo("VERSION", VERSION, 0, 2, true, false));
        FUNCTIONS.put("OBJ_DESCRIPTION", new FunctionInfo("OBJ_DESCRIPTION", OBJ_DESCRIPTION, -1, 2, true, false));
        FUNCTIONS.put("PG_ENCODING_TO_CHAR", new FunctionInfo("PG_ENCODING_TO_CHAR", PG_ENCODING_TO_CHAR, 1, 2, true, true));
        FUNCTIONS.put("PG_GET_EXPR", new FunctionInfo("PG_GET_EXPR", PG_GET_EXPR, -1, 2, true, true));
        FUNCTIONS.put("PG_GET_INDEXDEF", new FunctionInfo("PG_GET_INDEXDEF", PG_GET_INDEXDEF, -1, 2, true, false));
        FUNCTIONS.put("PG_GET_USERBYID", new FunctionInfo("PG_GET_USERBYID", PG_GET_USERBYID, 1, 2, true, false));
        FUNCTIONS.put("PG_POSTMASTER_START_TIME", new FunctionInfo("PG_POSTMASTER_START_TIME", PG_POSTMASTER_START_TIME, 0, 21, true, false));
        FUNCTIONS.put("PG_RELATION_SIZE", new FunctionInfo("PG_RELATION_SIZE", PG_RELATION_SIZE, -1, 12, true, false));
        FUNCTIONS.put("PG_TABLE_IS_VISIBLE", new FunctionInfo("PG_TABLE_IS_VISIBLE", PG_TABLE_IS_VISIBLE, 1, 8, true, false));
        FUNCTIONS.put("SET_CONFIG", new FunctionInfo("SET_CONFIG", SET_CONFIG, 3, 2, true, false));
        FUNCTIONS.put("ARRAY_TO_STRING", new FunctionInfo("ARRAY_TO_STRING", ARRAY_TO_STRING, -1, 2, false, true));
        FUNCTIONS.put("PG_STAT_GET_NUMSCANS", new FunctionInfo("PG_STAT_GET_NUMSCANS", PG_STAT_GET_NUMSCANS, 1, 11, true, true));
        FUNCTIONS.put("TO_DATE", new FunctionInfo("TO_DATE", TO_DATE, 2, 17, true, true));
        FUNCTIONS.put("TO_TIMESTAMP", new FunctionInfo("TO_TIMESTAMP", TO_TIMESTAMP, 2, 21, true, true));
        FUNCTIONS.put("GEN_RANDOM_UUID", new FunctionInfo("GEN_RANDOM_UUID", GEN_RANDOM_UUID, 0, 39, true, false));
    }

    public static FunctionsPostgreSQL getFunction(String str) {
        FunctionInfo functionInfo = FUNCTIONS.get(str);
        if (functionInfo != null) {
            return new FunctionsPostgreSQL(functionInfo);
        }
        return null;
    }

    private FunctionsPostgreSQL(FunctionInfo functionInfo) {
        super(functionInfo);
    }

    @Override // org.h2.mode.ModeFunction
    protected void checkParameterCount(int i) {
        int i2;
        int i3;
        switch (this.info.type) {
            case HAS_DATABASE_PRIVILEGE /* 3004 */:
            case HAS_SCHEMA_PRIVILEGE /* 3005 */:
            case HAS_TABLE_PRIVILEGE /* 3006 */:
                i2 = 2;
                i3 = 3;
                break;
            case LASTVAL /* 3007 */:
            case VERSION /* 3008 */:
            case PG_ENCODING_TO_CHAR /* 3010 */:
            case PG_GET_USERBYID /* 3013 */:
            case PG_POSTMASTER_START_TIME /* 3014 */:
            case PG_TABLE_IS_VISIBLE /* 3016 */:
            case SET_CONFIG /* 3017 */:
            default:
                throw DbException.getInternalError("type=" + this.info.type);
            case OBJ_DESCRIPTION /* 3009 */:
            case PG_RELATION_SIZE /* 3015 */:
                i2 = 1;
                i3 = 2;
                break;
            case PG_GET_EXPR /* 3011 */:
            case ARRAY_TO_STRING /* 3018 */:
                i2 = 2;
                i3 = 3;
                break;
            case PG_GET_INDEXDEF /* 3012 */:
                if (i != 1 && i != 3) {
                    throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, this.info.name, "1, 3");
                }
                return;
        }
        if (i < i2 || i > i3) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, this.info.name, i2 + ".." + i3);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        switch (this.info.type) {
            case CURRENT_DATABASE /* 3001 */:
                return new CurrentGeneralValueSpecification(0).optimize(sessionLocal);
            case GEN_RANDOM_UUID /* 3022 */:
                return new RandFunction(null, 2).optimize(sessionLocal);
            default:
                boolean optimizeArguments = optimizeArguments(sessionLocal);
                this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
                if (optimizeArguments) {
                    return ValueExpression.get(getValue(sessionLocal));
                }
                return this;
        }
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value timestampTz;
        Value[] argumentsValues = getArgumentsValues(sessionLocal, this.args);
        if (argumentsValues == null) {
            return ValueNull.INSTANCE;
        }
        Value nullOrValue = getNullOrValue(sessionLocal, this.args, argumentsValues, 0);
        Value nullOrValue2 = getNullOrValue(sessionLocal, this.args, argumentsValues, 1);
        Value nullOrValue3 = getNullOrValue(sessionLocal, this.args, argumentsValues, 2);
        switch (this.info.type) {
            case CURRTID2 /* 3002 */:
                timestampTz = ValueInteger.get(1);
                break;
            case FORMAT_TYPE /* 3003 */:
                timestampTz = nullOrValue != ValueNull.INSTANCE ? ValueVarchar.get(PgServer.formatType(nullOrValue.getInt())) : ValueNull.INSTANCE;
                break;
            case HAS_DATABASE_PRIVILEGE /* 3004 */:
            case HAS_SCHEMA_PRIVILEGE /* 3005 */:
            case HAS_TABLE_PRIVILEGE /* 3006 */:
            case PG_TABLE_IS_VISIBLE /* 3016 */:
                timestampTz = ValueBoolean.TRUE;
                break;
            case LASTVAL /* 3007 */:
                Value lastIdentity = sessionLocal.getLastIdentity();
                if (lastIdentity == ValueNull.INSTANCE) {
                    throw DbException.get(ErrorCode.CURRENT_SEQUENCE_VALUE_IS_NOT_DEFINED_IN_SESSION_1, "lastval()");
                }
                timestampTz = lastIdentity.convertToBigint(null);
                break;
            case VERSION /* 3008 */:
                timestampTz = ValueVarchar.get("PostgreSQL 8.2.23 server protocol using H2 " + Constants.FULL_VERSION);
                break;
            case OBJ_DESCRIPTION /* 3009 */:
                timestampTz = ValueNull.INSTANCE;
                break;
            case PG_ENCODING_TO_CHAR /* 3010 */:
                timestampTz = ValueVarchar.get(encodingToChar(nullOrValue.getInt()));
                break;
            case PG_GET_EXPR /* 3011 */:
                timestampTz = ValueNull.INSTANCE;
                break;
            case PG_GET_INDEXDEF /* 3012 */:
                timestampTz = getIndexdef(sessionLocal, nullOrValue.getInt(), nullOrValue2, nullOrValue3);
                break;
            case PG_GET_USERBYID /* 3013 */:
                timestampTz = ValueVarchar.get(getUserbyid(sessionLocal, nullOrValue.getInt()));
                break;
            case PG_POSTMASTER_START_TIME /* 3014 */:
                timestampTz = sessionLocal.getDatabase().getSystemSession().getSessionStart();
                break;
            case PG_RELATION_SIZE /* 3015 */:
                timestampTz = relationSize(sessionLocal, nullOrValue);
                break;
            case SET_CONFIG /* 3017 */:
                timestampTz = nullOrValue2.convertTo(2);
                break;
            case ARRAY_TO_STRING /* 3018 */:
                if (nullOrValue == ValueNull.INSTANCE || nullOrValue2 == ValueNull.INSTANCE) {
                    timestampTz = ValueNull.INSTANCE;
                    break;
                } else {
                    StringJoiner stringJoiner = new StringJoiner(nullOrValue2.getString());
                    if (nullOrValue.getValueType() != 40) {
                        throw DbException.getInvalidValueException("ARRAY_TO_STRING array", nullOrValue);
                    }
                    String str = null;
                    if (nullOrValue3 != null) {
                        str = nullOrValue3.getString();
                    }
                    for (Value value : ((ValueArray) nullOrValue).getList()) {
                        if (value != ValueNull.INSTANCE) {
                            stringJoiner.add(value.getString());
                        } else if (str != null) {
                            stringJoiner.add(str);
                        }
                    }
                    timestampTz = ValueVarchar.get(stringJoiner.toString());
                    break;
                }
                break;
            case PG_STAT_GET_NUMSCANS /* 3019 */:
                timestampTz = ValueInteger.get(0);
                break;
            case TO_DATE /* 3020 */:
                timestampTz = ToDateParser.toDate(sessionLocal, nullOrValue.getString(), nullOrValue2.getString()).convertToDate(sessionLocal);
                break;
            case TO_TIMESTAMP /* 3021 */:
                timestampTz = ToDateParser.toTimestampTz(sessionLocal, nullOrValue.getString(), nullOrValue2.getString());
                break;
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
        return timestampTz;
    }

    private static String encodingToChar(int i) {
        switch (i) {
            case 0:
                return "SQL_ASCII";
            case 6:
                return "UTF8";
            case 8:
                return "LATIN1";
            default:
                return i < 40 ? "UTF8" : "";
        }
    }

    private static Value getIndexdef(SessionLocal sessionLocal, int i, Value value, Value value2) {
        int i2;
        Iterator<Schema> it = sessionLocal.getDatabase().getAllSchemasNoMeta().iterator();
        while (it.hasNext()) {
            Iterator<Index> it2 = it.next().getAllIndexes().iterator();
            while (true) {
                if (it2.hasNext()) {
                    Index next = it2.next();
                    if (next.getId() == i) {
                        if (next.getTable().isHidden()) {
                            continue;
                        } else {
                            if (value == null || (i2 = value.getInt()) == 0) {
                                return ValueVarchar.get(next.getCreateSQL());
                            }
                            if (i2 >= 1) {
                                Column[] columns = next.getColumns();
                                if (i2 <= columns.length) {
                                    return ValueVarchar.get(columns[i2 - 1].getName());
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return ValueNull.INSTANCE;
    }

    private static String getUserbyid(SessionLocal sessionLocal, int i) {
        String name;
        User user = sessionLocal.getUser();
        if (user.getId() == i) {
            name = user.getName();
        } else {
            if (user.isAdmin()) {
                for (RightOwner rightOwner : sessionLocal.getDatabase().getAllUsersAndRoles()) {
                    if (rightOwner.getId() == i) {
                        name = rightOwner.getName();
                    }
                }
            }
            return "unknown (OID=" + i + ')';
        }
        if (sessionLocal.getDatabase().getSettings().databaseToLower) {
            name = StringUtils.toLowerEnglish(name);
        }
        return name;
    }

    private static Value relationSize(SessionLocal sessionLocal, Value value) {
        if (value.getValueType() == 11) {
            int i = value.getInt();
            Iterator<Schema> it = sessionLocal.getDatabase().getAllSchemasNoMeta().iterator();
            while (it.hasNext()) {
                Iterator<Table> it2 = it.next().getAllTablesAndViews(sessionLocal).iterator();
                while (it2.hasNext() && i != it2.next().getId()) {
                }
            }
            return ValueNull.INSTANCE;
        }
        return ValueBigint.get(new Parser(sessionLocal).parseTableName(value.getString()).getDiskSpaceUsed());
    }
}
