package org.h2.command.dml;

import org.apache.tomcat.jni.SSL;
import org.h2.api.ErrorCode;
import org.h2.command.ParserBase;
import org.h2.command.Prepared;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.engine.Setting;
import org.h2.expression.Expression;
import org.h2.expression.TimeZoneOperation;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.security.auth.AuthenticatorFactory;
import org.h2.table.Table;
import org.h2.util.DateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/Set.class */
public class Set extends Prepared {
    private final int type;
    private Expression expression;
    private String stringValue;
    private String[] stringValueList;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !Set.class.desiredAssertionStatus();
    }

    public Set(SessionLocal sessionLocal, int i) {
        super(sessionLocal);
        this.type = i;
    }

    public void setString(String str) {
        this.stringValue = str;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        switch (this.type) {
            case 4:
            case 8:
            case 9:
            case 12:
            case 13:
            case 18:
            case 22:
            case 24:
            case 29:
            case 30:
            case 33:
            case 36:
            case 40:
            case 41:
            case 42:
            case 43:
            case 45:
                return true;
            case 5:
            case 6:
            case 7:
            case 10:
            case 11:
            case 14:
            case 15:
            case 16:
            case 17:
            case 19:
            case 20:
            case 21:
            case 23:
            case 25:
            case 26:
            case 27:
            case 28:
            case 31:
            case 32:
            case 34:
            case 35:
            case 37:
            case 38:
            case 39:
            case 44:
            default:
                return false;
        }
    }

    @Override // org.h2.command.Prepared
    public long update() {
        CompareMode compareMode;
        Database database = getDatabase();
        String typeName = SetTypes.getTypeName(this.type);
        switch (this.type) {
            case 0:
                this.session.getUser().checkAdmin();
                int intValue = getIntValue();
                synchronized (database) {
                    database.setIgnoreCase(intValue == 1);
                    addOrUpdateSetting(typeName, null, intValue);
                }
                break;
            case 1:
                this.session.getUser().checkAdmin();
                int intValue2 = getIntValue();
                if (intValue2 < 0) {
                    throw DbException.getInvalidValueException("MAX_LOG_SIZE", Integer.valueOf(intValue2));
                }
                break;
            case 2:
                Mode mode = Mode.getInstance(this.stringValue);
                if (mode == null) {
                    throw DbException.get(ErrorCode.UNKNOWN_MODE_1, this.stringValue);
                }
                if (database.getMode() != mode) {
                    this.session.getUser().checkAdmin();
                    database.setMode(mode);
                    break;
                }
                break;
            case 3:
            default:
                throw DbException.getInternalError("type=" + this.type);
            case 4:
                int intValue3 = getIntValue();
                if (intValue3 < 0) {
                    throw DbException.getInvalidValueException("LOCK_TIMEOUT", Integer.valueOf(intValue3));
                }
                this.session.setLockTimeout(intValue3);
                break;
            case 5:
                this.session.getUser().checkAdmin();
                int intValue4 = getIntValue();
                if (intValue4 < 0) {
                    throw DbException.getInvalidValueException("DEFAULT_LOCK_TIMEOUT", Integer.valueOf(intValue4));
                }
                synchronized (database) {
                    addOrUpdateSetting(typeName, null, intValue4);
                }
                break;
            case 6:
                this.session.getUser().checkAdmin();
                int intValue5 = getIntValue();
                synchronized (database) {
                    database.setDefaultTableType(intValue5);
                    addOrUpdateSetting(typeName, null, intValue5);
                }
                break;
            case 7:
                this.session.getUser().checkAdmin();
                int intValue6 = getIntValue();
                if (intValue6 < 0) {
                    throw DbException.getInvalidValueException("CACHE_SIZE", Integer.valueOf(intValue6));
                }
                synchronized (database) {
                    database.setCacheSize(intValue6);
                    addOrUpdateSetting(typeName, null, intValue6);
                }
                break;
            case 8:
                this.session.getUser().checkAdmin();
                if (getPersistedObjectId() == 0) {
                    database.getTraceSystem().setLevelSystemOut(getIntValue());
                    break;
                }
                break;
            case 9:
                this.session.getUser().checkAdmin();
                if (getPersistedObjectId() == 0) {
                    database.getTraceSystem().setLevelFile(getIntValue());
                    break;
                }
                break;
            case 10:
                this.session.getUser().checkAdmin();
                int intValue7 = getIntValue();
                if (intValue7 < 0) {
                    throw DbException.getInvalidValueException("TRACE_MAX_FILE_SIZE", Integer.valueOf(intValue7));
                }
                int i = intValue7 * SSL.SSL_OP_SINGLE_DH_USE;
                synchronized (database) {
                    database.getTraceSystem().setMaxFileSize(i);
                    addOrUpdateSetting(typeName, null, intValue7);
                }
                break;
            case 11:
                this.session.getUser().checkAdmin();
                StringBuilder sb = new StringBuilder(this.stringValue);
                if (this.stringValue.equals(CompareMode.OFF)) {
                    compareMode = CompareMode.getInstance(null, 0);
                } else {
                    int intValue8 = getIntValue();
                    sb.append(" STRENGTH ");
                    if (intValue8 == 3) {
                        sb.append("IDENTICAL");
                    } else if (intValue8 == 0) {
                        sb.append("PRIMARY");
                    } else if (intValue8 == 1) {
                        sb.append("SECONDARY");
                    } else if (intValue8 == 2) {
                        sb.append("TERTIARY");
                    }
                    compareMode = CompareMode.getInstance(this.stringValue, intValue8);
                }
                synchronized (database) {
                    if (!database.getCompareMode().equals(compareMode)) {
                        Table firstUserTable = database.getFirstUserTable();
                        if (firstUserTable != null) {
                            throw DbException.get(ErrorCode.COLLATION_CHANGE_WITH_DATA_TABLE_1, firstUserTable.getTraceSQL());
                        }
                        addOrUpdateSetting(typeName, sb.toString(), 0);
                        database.setCompareMode(compareMode);
                        break;
                    } else {
                        break;
                    }
                }
            case 12:
                if (!Constants.CLUSTERING_ENABLED.equals(this.stringValue)) {
                    String quoteStringSQL = StringUtils.quoteStringSQL(this.stringValue);
                    if (!quoteStringSQL.equals(database.getCluster())) {
                        if (!quoteStringSQL.equals(Constants.CLUSTERING_DISABLED)) {
                            this.session.getUser().checkAdmin();
                        }
                        database.setCluster(quoteStringSQL);
                        SessionLocal systemSession = database.getSystemSession();
                        synchronized (systemSession) {
                            synchronized (database) {
                                addOrUpdateSetting(systemSession, typeName, quoteStringSQL, 0);
                                systemSession.commit(true);
                            }
                        }
                        break;
                    }
                }
                break;
            case 13:
                this.session.getUser().checkAdmin();
                int intValue9 = getIntValue();
                if (intValue9 < 0) {
                    throw DbException.getInvalidValueException("WRITE_DELAY", Integer.valueOf(intValue9));
                }
                synchronized (database) {
                    database.setWriteDelay(intValue9);
                    addOrUpdateSetting(typeName, null, intValue9);
                }
                break;
            case 14:
                this.session.getUser().checkAdmin();
                database.setEventListenerClass(this.stringValue);
                break;
            case 15:
                this.session.getUser().checkAdmin();
                int intValue10 = getIntValue();
                if (intValue10 < 0) {
                    throw DbException.getInvalidValueException("MAX_MEMORY_ROWS", Integer.valueOf(intValue10));
                }
                synchronized (database) {
                    database.setMaxMemoryRows(intValue10);
                    addOrUpdateSetting(typeName, null, intValue10);
                }
                break;
            case 16:
                this.session.getUser().checkAdmin();
                int intValue11 = getIntValue();
                synchronized (database) {
                    database.setLockMode(intValue11);
                    addOrUpdateSetting(typeName, null, intValue11);
                }
                break;
            case 17:
                this.session.getUser().checkAdmin();
                int intValue12 = getIntValue();
                if (intValue12 != -1 && intValue12 < 0) {
                    throw DbException.getInvalidValueException("DB_CLOSE_DELAY", Integer.valueOf(intValue12));
                }
                synchronized (database) {
                    database.setCloseDelay(intValue12);
                    addOrUpdateSetting(typeName, null, intValue12);
                }
                break;
                break;
            case 18:
                int intValue13 = getIntValue();
                if (intValue13 < 0) {
                    throw DbException.getInvalidValueException("THROTTLE", Integer.valueOf(intValue13));
                }
                this.session.setThrottle(intValue13);
                break;
            case 19:
                this.session.getUser().checkAdmin();
                int intValue14 = getIntValue();
                if (intValue14 < 0) {
                    throw DbException.getInvalidValueException("MAX_MEMORY_UNDO", Integer.valueOf(intValue14));
                }
                synchronized (database) {
                    addOrUpdateSetting(typeName, null, intValue14);
                }
                break;
            case 20:
                this.session.getUser().checkAdmin();
                int intValue15 = getIntValue();
                if (intValue15 < 0) {
                    throw DbException.getInvalidValueException("MAX_LENGTH_INPLACE_LOB", Integer.valueOf(intValue15));
                }
                synchronized (database) {
                    database.setMaxLengthInplaceLob(intValue15);
                    addOrUpdateSetting(typeName, null, intValue15);
                }
                break;
            case 21:
                this.session.getUser().checkAdmin();
                int intValue16 = getIntValue();
                if (intValue16 < 0 || intValue16 > 2) {
                    throw DbException.getInvalidValueException("ALLOW_LITERALS", Integer.valueOf(intValue16));
                }
                synchronized (database) {
                    database.setAllowLiterals(intValue16);
                    addOrUpdateSetting(typeName, null, intValue16);
                }
                break;
                break;
            case 22:
                this.session.setCurrentSchema(database.getSchema(this.expression.optimize(this.session).getValue(this.session).getString()));
                break;
            case 23:
                this.session.getUser().checkAdmin();
                database.setOptimizeReuseResults(getIntValue() != 0);
                break;
            case 24:
                this.session.setSchemaSearchPath(this.stringValueList);
                break;
            case 25:
                this.session.getUser().checkAdmin();
                int intValue17 = getIntValue();
                if (intValue17 < 0 || intValue17 > 1) {
                    throw DbException.getInvalidValueException("REFERENTIAL_INTEGRITY", Integer.valueOf(intValue17));
                }
                database.setReferentialIntegrity(intValue17 == 1);
                break;
            case 26:
                this.session.getUser().checkAdmin();
                int intValue18 = getIntValue();
                if (intValue18 < 0) {
                    throw DbException.getInvalidValueException("MAX_OPERATION_MEMORY", Integer.valueOf(intValue18));
                }
                database.setMaxOperationMemory(intValue18);
                break;
            case 27:
                this.session.getUser().checkAdmin();
                int intValue19 = getIntValue();
                switch (intValue19) {
                    case 0:
                        if (!database.unsetExclusiveSession(this.session)) {
                            throw DbException.get(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE);
                        }
                        break;
                    case 1:
                        if (!database.setExclusiveSession(this.session, false)) {
                            throw DbException.get(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE);
                        }
                        break;
                    case 2:
                        if (!database.setExclusiveSession(this.session, true)) {
                            throw DbException.get(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE);
                        }
                        break;
                    default:
                        throw DbException.getInvalidValueException("EXCLUSIVE", Integer.valueOf(intValue19));
                }
            case 28:
                this.session.getUser().checkAdmin();
                if (database.isStarting()) {
                    int intValue20 = getIntValue();
                    synchronized (database) {
                        addOrUpdateSetting(typeName, null, intValue20);
                    }
                    break;
                }
                break;
            case 29:
                this.session.setVariable(this.stringValue, this.expression.optimize(this.session).getValue(this.session));
                break;
            case 30:
                int intValue21 = getIntValue();
                if (intValue21 < 0) {
                    throw DbException.getInvalidValueException("QUERY_TIMEOUT", Integer.valueOf(intValue21));
                }
                this.session.setQueryTimeout(intValue21);
                break;
            case 31:
                DbException.getUnsupportedException("MV_STORE + SET REDO_LOG_BINARY");
                break;
            case 32:
                this.session.getUser().checkAdmin();
                synchronized (database) {
                    Table firstUserTable2 = database.getFirstUserTable();
                    if (firstUserTable2 != null) {
                        throw DbException.get(ErrorCode.JAVA_OBJECT_SERIALIZER_CHANGE_WITH_DATA_TABLE, firstUserTable2.getTraceSQL());
                    }
                    database.setJavaObjectSerializerName(this.stringValue);
                    addOrUpdateSetting(typeName, this.stringValue, 0);
                }
                break;
            case 33:
                this.session.getUser().checkAdmin();
                int intValue22 = getIntValue();
                if (intValue22 < 0) {
                    throw DbException.getInvalidValueException("RETENTION_TIME", Integer.valueOf(intValue22));
                }
                synchronized (database) {
                    database.setRetentionTime(intValue22);
                    addOrUpdateSetting(typeName, null, intValue22);
                }
                break;
            case 34:
                this.session.getUser().checkAdmin();
                int intValue23 = getIntValue();
                if (intValue23 < 0 || intValue23 > 1) {
                    throw DbException.getInvalidValueException("QUERY_STATISTICS", Integer.valueOf(intValue23));
                }
                database.setQueryStatistics(intValue23 == 1);
                break;
            case 35:
                this.session.getUser().checkAdmin();
                int intValue24 = getIntValue();
                if (intValue24 < 1) {
                    throw DbException.getInvalidValueException("QUERY_STATISTICS_MAX_ENTRIES", Integer.valueOf(intValue24));
                }
                database.setQueryStatisticsMaxEntries(intValue24);
                break;
            case 36:
                int intValue25 = getIntValue();
                if (intValue25 != 0 && intValue25 != 1) {
                    throw DbException.getInvalidValueException("LAZY_QUERY_EXECUTION", Integer.valueOf(intValue25));
                }
                this.session.setLazyQueryExecution(intValue25 == 1);
                break;
                break;
            case 37:
                this.session.getUser().checkAdmin();
                int intValue26 = getIntValue();
                if (intValue26 != 0 && intValue26 != 1) {
                    throw DbException.getInvalidValueException("BUILTIN_ALIAS_OVERRIDE", Integer.valueOf(intValue26));
                }
                database.setAllowBuiltinAliasOverride(intValue26 == 1);
                break;
            case 38:
                this.session.getUser().checkAdmin();
                boolean booleanValue = this.expression.optimize(this.session).getBooleanValue(this.session);
                try {
                    synchronized (database) {
                        if (booleanValue) {
                            database.setAuthenticator(AuthenticatorFactory.createAuthenticator());
                        } else {
                            database.setAuthenticator(null);
                        }
                        addOrUpdateSetting(typeName, booleanValue ? Constants.CLUSTERING_ENABLED : "FALSE", 0);
                    }
                    break;
                } catch (Exception e) {
                    if (database.isStarting()) {
                        database.getTrace(2).error(e, "{0}: failed to set authenticator during database start ", this.expression.toString());
                        break;
                    } else {
                        throw DbException.convert(e);
                    }
                }
            case 39:
                this.session.getUser().checkAdmin();
                int intValue27 = getIntValue();
                synchronized (database) {
                    database.setIgnoreCatalogs(intValue27 == 1);
                    addOrUpdateSetting(typeName, null, intValue27);
                }
                break;
            case 40:
                String shortName = database.getShortName();
                String string = this.expression.optimize(this.session).getValue(this.session).getString();
                if (string == null || (!database.equalsIdentifiers(shortName, string) && !database.equalsIdentifiers(shortName, string.trim()))) {
                    throw DbException.get(ErrorCode.DATABASE_NOT_FOUND_1, this.stringValue);
                }
                break;
            case 41:
                this.session.setNonKeywords(ParserBase.parseNonKeywords(this.stringValueList));
                break;
            case 42:
                this.session.setTimeZone(this.expression == null ? DateTimeUtils.getTimeZone() : parseTimeZone(this.expression.getValue(this.session)));
                break;
            case 43:
                this.session.setVariableBinary(this.expression.getBooleanValue(this.session));
                break;
            case 44:
                try {
                    DefaultNullOrdering valueOf = DefaultNullOrdering.valueOf(StringUtils.toUpperEnglish(this.stringValue));
                    if (database.getDefaultNullOrdering() != valueOf) {
                        this.session.getUser().checkAdmin();
                        database.setDefaultNullOrdering(valueOf);
                        break;
                    }
                } catch (RuntimeException e2) {
                    throw DbException.getInvalidValueException("DEFAULT_NULL_ORDERING", this.stringValue);
                }
                break;
            case 45:
                this.session.setTruncateLargeLength(this.expression.getBooleanValue(this.session));
                break;
        }
        database.getNextModificationDataId();
        database.getNextModificationMetaId();
        return 0L;
    }

    private static TimeZoneProvider parseTimeZone(Value value) {
        if (DataType.isCharacterStringType(value.getValueType())) {
            try {
                return TimeZoneProvider.ofId(value.getString());
            } catch (RuntimeException e) {
                throw DbException.getInvalidValueException("TIME ZONE", value.getTraceSQL());
            }
        }
        if (value == ValueNull.INSTANCE) {
            throw DbException.getInvalidValueException("TIME ZONE", value);
        }
        return TimeZoneProvider.ofOffset(TimeZoneOperation.parseInterval(value));
    }

    private int getIntValue() {
        this.expression = this.expression.optimize(this.session);
        return this.expression.getValue(this.session).getInt();
    }

    public void setInt(int i) {
        this.expression = ValueExpression.get(ValueInteger.get(i));
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    private void addOrUpdateSetting(String str, String str2, int i) {
        addOrUpdateSetting(this.session, str, str2, i);
    }

    private void addOrUpdateSetting(SessionLocal sessionLocal, String str, String str2, int i) {
        Database database = sessionLocal.getDatabase();
        if (!$assertionsDisabled && !Thread.holdsLock(database)) {
            throw new AssertionError();
        }
        if (database.isReadOnly()) {
            return;
        }
        Setting findSetting = database.findSetting(str);
        boolean z = false;
        if (findSetting == null) {
            z = true;
            findSetting = new Setting(database, getObjectId(), str);
        }
        if (str2 != null) {
            if (!z && findSetting.getStringValue().equals(str2)) {
                return;
            } else {
                findSetting.setStringValue(str2);
            }
        } else if (!z && findSetting.getIntValue() == i) {
            return;
        } else {
            findSetting.setIntValue(i);
        }
        if (z) {
            database.addDatabaseObject(sessionLocal, findSetting);
        } else {
            database.updateMeta(sessionLocal, findSetting);
        }
    }

    @Override // org.h2.command.Prepared
    public boolean needRecompile() {
        return false;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return null;
    }

    public void setStringArray(String[] strArr) {
        this.stringValueList = strArr;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 67;
    }
}
