package org.h2.message;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.h2.api.ErrorCode;
import org.h2.util.StringUtils;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/message/TraceObject.class */
public abstract class TraceObject {
    protected static final int CALLABLE_STATEMENT = 0;
    protected static final int CONNECTION = 1;
    protected static final int DATABASE_META_DATA = 2;
    protected static final int PREPARED_STATEMENT = 3;
    protected static final int RESULT_SET = 4;
    protected static final int RESULT_SET_META_DATA = 5;
    protected static final int SAVEPOINT = 6;
    protected static final int STATEMENT = 8;
    protected static final int BLOB = 9;
    protected static final int CLOB = 10;
    protected static final int PARAMETER_META_DATA = 11;
    protected static final int DATA_SOURCE = 12;
    protected static final int XA_DATA_SOURCE = 13;
    protected static final int XID = 15;
    protected static final int ARRAY = 16;
    protected static final int SQLXML = 17;
    private static final int LAST = 18;
    private static final AtomicIntegerArray ID = new AtomicIntegerArray(18);
    private static final String[] PREFIX = {"call", "conn", "dbMeta", "prep", "rs", "rsMeta", "sp", "ex", "stat", "blob", "clob", "pMeta", "ds", "xads", "xares", "xid", "ar", "sqlxml"};
    private static final SQLException SQL_OOME = DbException.SQL_OOME;
    protected Trace trace;
    private int traceType;
    private int id;

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTrace(Trace trace, int i, int i2) {
        this.trace = trace;
        this.traceType = i;
        this.id = i2;
    }

    public int getTraceId() {
        return this.id;
    }

    public String getTraceObjectName() {
        return PREFIX[this.traceType] + this.id;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int getNextId(int i) {
        return ID.getAndIncrement(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isDebugEnabled() {
        return this.trace.isDebugEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isInfoEnabled() {
        return this.trace.isInfoEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void debugCodeAssign(String str, int i, int i2, String str2) {
        if (this.trace.isDebugEnabled()) {
            this.trace.debugCode(str + ' ' + PREFIX[i] + i2 + " = " + getTraceObjectName() + '.' + str2 + ';');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void debugCodeCall(String str) {
        if (this.trace.isDebugEnabled()) {
            this.trace.debugCode(getTraceObjectName() + '.' + str + "();");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void debugCodeCall(String str, long j) {
        if (this.trace.isDebugEnabled()) {
            this.trace.debugCode(getTraceObjectName() + '.' + str + '(' + j + ");");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void debugCodeCall(String str, String str2) {
        if (this.trace.isDebugEnabled()) {
            this.trace.debugCode(getTraceObjectName() + '.' + str + '(' + quote(str2) + ");");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void debugCode(String str) {
        if (this.trace.isDebugEnabled()) {
            this.trace.debugCode(getTraceObjectName() + '.' + str + ';');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quote(String str) {
        return StringUtils.quoteJavaString(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quoteTime(Time time) {
        if (time == null) {
            return "null";
        }
        return "Time.valueOf(\"" + time.toString() + "\")";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quoteTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "null";
        }
        return "Timestamp.valueOf(\"" + timestamp.toString() + "\")";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quoteDate(Date date) {
        if (date == null) {
            return "null";
        }
        return "Date.valueOf(\"" + date.toString() + "\")";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quoteBigDecimal(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "null";
        }
        return "new BigDecimal(\"" + bigDecimal.toString() + "\")";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quoteBytes(byte[] bArr) {
        if (bArr == null) {
            return "null";
        }
        return StringUtils.convertBytesToHex(new StringBuilder((bArr.length * 2) + 45).append("org.h2.util.StringUtils.convertHexToBytes(\""), bArr).append("\")").toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quoteArray(String[] strArr) {
        return StringUtils.quoteJavaStringArray(strArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quoteIntArray(int[] iArr) {
        return StringUtils.quoteJavaIntArray(iArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String quoteMap(Map<String, Class<?>> map) {
        if (map == null) {
            return "null";
        }
        if (map.size() == 0) {
            return "new Map()";
        }
        return "new Map() /* " + map.toString() + " */";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SQLException logAndConvert(Throwable th) {
        SQLException sQLException = null;
        try {
            sQLException = DbException.toSQLException(th);
            if (this.trace == null) {
                DbException.traceThrowable(sQLException);
            } else {
                int errorCode = sQLException.getErrorCode();
                if (errorCode >= 23000 && errorCode < 24000) {
                    this.trace.info(sQLException, SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);
                } else {
                    this.trace.error(sQLException, SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);
                }
            }
        } catch (Throwable th2) {
            if (0 == 0) {
                try {
                    sQLException = new SQLException("GeneralError", "HY000", ErrorCode.GENERAL_ERROR_1, th);
                } catch (NoClassDefFoundError | OutOfMemoryError e) {
                    return SQL_OOME;
                }
            }
            sQLException.addSuppressed(th2);
        }
        return sQLException;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SQLException unsupported(String str) {
        try {
            throw DbException.getUnsupportedException(str);
        } catch (Exception e) {
            return logAndConvert(e);
        }
    }
}
