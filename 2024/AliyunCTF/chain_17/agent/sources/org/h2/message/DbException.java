package org.h2.message;

import cn.hutool.core.text.StrPool;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcException;
import org.h2.jdbc.JdbcSQLDataException;
import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbc.JdbcSQLFeatureNotSupportedException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.h2.jdbc.JdbcSQLInvalidAuthorizationSpecException;
import org.h2.jdbc.JdbcSQLNonTransientConnectionException;
import org.h2.jdbc.JdbcSQLNonTransientException;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.h2.jdbc.JdbcSQLTimeoutException;
import org.h2.jdbc.JdbcSQLTransactionRollbackException;
import org.h2.jdbc.JdbcSQLTransientException;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/message/DbException.class */
public class DbException extends RuntimeException {
    private static final long serialVersionUID = 1;
    public static final String HIDE_SQL = "--hide--";
    private static final Properties MESSAGES = new Properties();
    public static final SQLException SQL_OOME = new SQLException("OutOfMemoryError", "HY000", ErrorCode.OUT_OF_MEMORY, new OutOfMemoryError());
    private static final DbException OOME = new DbException(SQL_OOME);
    private Object source;

    static {
        byte[] resource;
        try {
            byte[] resource2 = Utils.getResource("/org/h2/res/_messages_en.prop");
            if (resource2 != null) {
                MESSAGES.load(new ByteArrayInputStream(resource2));
            }
            String language = Locale.getDefault().getLanguage();
            if (!"en".equals(language) && (resource = Utils.getResource("/org/h2/res/_messages_" + language + ".prop")) != null) {
                for (Map.Entry<Object, Object> entry : SortedProperties.fromLines(new String(resource, StandardCharsets.UTF_8)).entrySet()) {
                    String str = (String) entry.getKey();
                    String str2 = (String) entry.getValue();
                    if (str2 != null && !str2.startsWith("#")) {
                        MESSAGES.put(str, str2 + StrPool.LF + MESSAGES.getProperty(str));
                    }
                }
            }
        } catch (IOException | OutOfMemoryError e) {
            traceThrowable(e);
        }
    }

    private DbException(SQLException sQLException) {
        super(sQLException.getMessage(), sQLException);
    }

    private static String translate(String str, String... strArr) {
        String property = MESSAGES.getProperty(str);
        if (property == null) {
            property = "(Message " + str + " not found)";
        }
        if (strArr != null) {
            for (int i = 0; i < strArr.length; i++) {
                String str2 = strArr[i];
                if (str2 != null && str2.length() > 0) {
                    strArr[i] = quote(str2);
                }
            }
            property = MessageFormat.format(property, strArr);
        }
        return property;
    }

    private static String quote(String str) {
        int length = str.length();
        StringBuilder append = new StringBuilder(length + 2).append('\"');
        int i = 0;
        while (i < length) {
            int codePointAt = str.codePointAt(i);
            i += Character.charCount(codePointAt);
            int type = Character.getType(codePointAt);
            if (type == 0 || (type >= 12 && type <= 19 && codePointAt != 32)) {
                if (codePointAt <= 65535) {
                    StringUtils.appendHex(append.append('\\'), codePointAt, 2);
                } else {
                    StringUtils.appendHex(append.append("\\+"), codePointAt, 3);
                }
            } else {
                if (codePointAt == 34 || codePointAt == 92) {
                    append.append((char) codePointAt);
                }
                append.appendCodePoint(codePointAt);
            }
        }
        return append.append('\"').toString();
    }

    public SQLException getSQLException() {
        return (SQLException) getCause();
    }

    public int getErrorCode() {
        return getSQLException().getErrorCode();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public DbException addSQL(String str) {
        SQLException sQLException = getSQLException();
        if (sQLException instanceof JdbcException) {
            JdbcException jdbcException = (JdbcException) sQLException;
            if (jdbcException.getSQL() == null) {
                jdbcException.setSQL(filterSQL(str));
            }
            return this;
        }
        return new DbException(getJdbcSQLException(sQLException.getMessage(), str, sQLException.getSQLState(), sQLException.getErrorCode(), sQLException, null));
    }

    public static DbException get(int i) {
        return get(i, (String) null);
    }

    public static DbException get(int i, String str) {
        return get(i, str);
    }

    public static DbException get(int i, Throwable th, String... strArr) {
        return new DbException(getJdbcSQLException(i, th, strArr));
    }

    public static DbException get(int i, String... strArr) {
        return new DbException(getJdbcSQLException(i, null, strArr));
    }

    public static DbException fromUser(String str, String str2) {
        return new DbException(getJdbcSQLException(str2, null, str, 0, null, null));
    }

    public static DbException getSyntaxError(String str, int i) {
        return get(ErrorCode.SYNTAX_ERROR_1, StringUtils.addAsterisk(str, i));
    }

    public static DbException getSyntaxError(String str, int i, String str2) {
        return new DbException(getJdbcSQLException(ErrorCode.SYNTAX_ERROR_2, null, StringUtils.addAsterisk(str, i), str2));
    }

    public static DbException getSyntaxError(int i, String str, int i2, String... strArr) {
        String addAsterisk = StringUtils.addAsterisk(str, i2);
        String state = ErrorCode.getState(i);
        return new DbException(getJdbcSQLException(translate(state, strArr), addAsterisk, state, i, null, null));
    }

    public static DbException getUnsupportedException(String str) {
        return get(ErrorCode.FEATURE_NOT_SUPPORTED_1, str);
    }

    public static DbException getInvalidValueException(String str, Object obj) {
        String[] strArr = new String[2];
        strArr[0] = obj == null ? "null" : obj.toString();
        strArr[1] = str;
        return get(ErrorCode.INVALID_VALUE_2, strArr);
    }

    public static DbException getInvalidValueException(Throwable th, String str, Object obj) {
        String[] strArr = new String[2];
        strArr[0] = obj == null ? "null" : obj.toString();
        strArr[1] = str;
        return get(ErrorCode.INVALID_VALUE_2, th, strArr);
    }

    public static DbException getValueTooLongException(String str, String str2, long j) {
        StringBuilder append;
        int length = str2.length();
        int i = j >= 0 ? 22 : 0;
        if (length > 80) {
            append = new StringBuilder(83 + i).append((CharSequence) str2, 0, 80).append("...");
        } else {
            append = new StringBuilder(length + i).append(str2);
        }
        StringBuilder sb = append;
        if (j >= 0) {
            sb.append(" (").append(j).append(')');
        }
        return get(ErrorCode.VALUE_TOO_LONG_2, str, sb.toString());
    }

    public static DbException getFileVersionError(String str) {
        return get(ErrorCode.FILE_VERSION_ERROR_1, "Old database: " + str + " - please convert the database to a SQL script and re-create it.");
    }

    public static RuntimeException getInternalError(String str) {
        RuntimeException runtimeException = new RuntimeException(str);
        traceThrowable(runtimeException);
        return runtimeException;
    }

    public static RuntimeException getInternalError() {
        return getInternalError("Unexpected code path");
    }

    public static SQLException toSQLException(Throwable th) {
        if (th instanceof SQLException) {
            return (SQLException) th;
        }
        return convert(th).getSQLException();
    }

    public static DbException convert(Throwable th) {
        try {
            if (th instanceof DbException) {
                return (DbException) th;
            }
            if (th instanceof SQLException) {
                return new DbException((SQLException) th);
            }
            if (th instanceof InvocationTargetException) {
                return convertInvocation((InvocationTargetException) th, null);
            }
            if (th instanceof IOException) {
                return get(ErrorCode.IO_EXCEPTION_1, th, th.toString());
            }
            if (th instanceof OutOfMemoryError) {
                return get(ErrorCode.OUT_OF_MEMORY, th, new String[0]);
            }
            if ((th instanceof StackOverflowError) || (th instanceof LinkageError)) {
                return get(ErrorCode.GENERAL_ERROR_1, th, th.toString());
            }
            if (th instanceof Error) {
                throw ((Error) th);
            }
            return get(ErrorCode.GENERAL_ERROR_1, th, th.toString());
        } catch (OutOfMemoryError e) {
            return OOME;
        } catch (Throwable th2) {
            try {
                DbException dbException = new DbException(new SQLException("GeneralError", "HY000", ErrorCode.GENERAL_ERROR_1, th));
                dbException.addSuppressed(th2);
                return dbException;
            } catch (OutOfMemoryError e2) {
                return OOME;
            }
        }
    }

    public static DbException convertInvocation(InvocationTargetException invocationTargetException, String str) {
        Throwable targetException = invocationTargetException.getTargetException();
        if ((targetException instanceof SQLException) || (targetException instanceof DbException)) {
            return convert(targetException);
        }
        return get(ErrorCode.EXCEPTION_IN_FUNCTION_1, targetException, str == null ? targetException.getMessage() : str + ": " + targetException.getMessage());
    }

    public static DbException convertIOException(IOException iOException, String str) {
        if (str == null) {
            Throwable cause = iOException.getCause();
            if (cause instanceof DbException) {
                return (DbException) cause;
            }
            return get(ErrorCode.IO_EXCEPTION_1, iOException, iOException.toString());
        }
        return get(ErrorCode.IO_EXCEPTION_2, iOException, iOException.toString(), str);
    }

    public static SQLException getJdbcSQLException(int i) {
        return getJdbcSQLException(i, (Throwable) null, new String[0]);
    }

    public static SQLException getJdbcSQLException(int i, String str) {
        return getJdbcSQLException(i, null, str);
    }

    public static SQLException getJdbcSQLException(int i, Throwable th, String... strArr) {
        String state = ErrorCode.getState(i);
        return getJdbcSQLException(translate(state, strArr), null, state, i, th, null);
    }

    public static SQLException getJdbcSQLException(String str, String str2, String str3, int i, Throwable th, String str4) {
        String filterSQL = filterSQL(str2);
        switch (i / 1000) {
            case 2:
                return new JdbcSQLNonTransientException(str, filterSQL, str3, i, th, str4);
            case 7:
            case 21:
            case 42:
            case 54:
                return new JdbcSQLSyntaxErrorException(str, filterSQL, str3, i, th, str4);
            case 8:
                return new JdbcSQLNonTransientConnectionException(str, filterSQL, str3, i, th, str4);
            case 22:
                return new JdbcSQLDataException(str, filterSQL, str3, i, th, str4);
            case 23:
                return new JdbcSQLIntegrityConstraintViolationException(str, filterSQL, str3, i, th, str4);
            case 28:
                return new JdbcSQLInvalidAuthorizationSpecException(str, filterSQL, str3, i, th, str4);
            case 40:
                return new JdbcSQLTransactionRollbackException(str, filterSQL, str3, i, th, str4);
            default:
                switch (i) {
                    case ErrorCode.GENERAL_ERROR_1 /* 50000 */:
                    case ErrorCode.UNKNOWN_DATA_TYPE_1 /* 50004 */:
                    case ErrorCode.METHOD_NOT_ALLOWED_FOR_QUERY /* 90001 */:
                    case ErrorCode.METHOD_ONLY_ALLOWED_FOR_QUERY /* 90002 */:
                    case ErrorCode.SEQUENCE_EXHAUSTED /* 90006 */:
                    case ErrorCode.OBJECT_CLOSED /* 90007 */:
                    case ErrorCode.CANNOT_DROP_CURRENT_USER /* 90019 */:
                    case ErrorCode.UNSUPPORTED_SETTING_COMBINATION /* 90021 */:
                    case ErrorCode.FILE_RENAME_FAILED_2 /* 90024 */:
                    case ErrorCode.FILE_DELETE_FAILED_1 /* 90025 */:
                    case ErrorCode.IO_EXCEPTION_1 /* 90028 */:
                    case ErrorCode.NOT_ON_UPDATABLE_ROW /* 90029 */:
                    case ErrorCode.IO_EXCEPTION_2 /* 90031 */:
                    case ErrorCode.TRACE_FILE_ERROR_2 /* 90034 */:
                    case ErrorCode.ADMIN_RIGHTS_REQUIRED /* 90040 */:
                    case ErrorCode.ERROR_EXECUTING_TRIGGER_3 /* 90044 */:
                    case ErrorCode.COMMIT_ROLLBACK_NOT_ALLOWED /* 90058 */:
                    case ErrorCode.FILE_CREATION_FAILED_1 /* 90062 */:
                    case ErrorCode.SAVEPOINT_IS_INVALID_1 /* 90063 */:
                    case ErrorCode.SAVEPOINT_IS_UNNAMED /* 90064 */:
                    case ErrorCode.SAVEPOINT_IS_NAMED /* 90065 */:
                    case ErrorCode.NOT_ENOUGH_RIGHTS_FOR_1 /* 90096 */:
                    case ErrorCode.DATABASE_IS_READ_ONLY /* 90097 */:
                    case ErrorCode.WRONG_XID_FORMAT_1 /* 90101 */:
                    case ErrorCode.UNSUPPORTED_COMPRESSION_OPTIONS_1 /* 90102 */:
                    case ErrorCode.UNSUPPORTED_COMPRESSION_ALGORITHM_1 /* 90103 */:
                    case ErrorCode.COMPRESSION_ERROR /* 90104 */:
                    case ErrorCode.EXCEPTION_IN_FUNCTION_1 /* 90105 */:
                    case ErrorCode.ERROR_ACCESSING_LINKED_TABLE_2 /* 90111 */:
                    case ErrorCode.FILE_NOT_FOUND_1 /* 90124 */:
                    case ErrorCode.INVALID_CLASS_2 /* 90125 */:
                    case ErrorCode.DATABASE_IS_NOT_PERSISTENT /* 90126 */:
                    case ErrorCode.RESULT_SET_NOT_UPDATABLE /* 90127 */:
                    case ErrorCode.RESULT_SET_NOT_SCROLLABLE /* 90128 */:
                    case ErrorCode.METHOD_NOT_ALLOWED_FOR_PREPARED_STATEMENT /* 90130 */:
                    case ErrorCode.ACCESS_DENIED_TO_CLASS_1 /* 90134 */:
                    case ErrorCode.RESULT_SET_READONLY /* 90140 */:
                    case ErrorCode.CURRENT_SEQUENCE_VALUE_IS_NOT_DEFINED_IN_SESSION_1 /* 90148 */:
                        return new JdbcSQLNonTransientException(str, filterSQL, str3, i, th, str4);
                    case ErrorCode.FEATURE_NOT_SUPPORTED_1 /* 50100 */:
                        return new JdbcSQLFeatureNotSupportedException(str, filterSQL, str3, i, th, str4);
                    case ErrorCode.LOCK_TIMEOUT_1 /* 50200 */:
                    case ErrorCode.STATEMENT_WAS_CANCELED /* 57014 */:
                    case ErrorCode.LOB_CLOSED_ON_TIMEOUT_1 /* 90039 */:
                        return new JdbcSQLTimeoutException(str, filterSQL, str3, i, th, str4);
                    case ErrorCode.FUNCTION_MUST_RETURN_RESULT_SET_1 /* 90000 */:
                    case ErrorCode.INVALID_TRIGGER_FLAGS_1 /* 90005 */:
                    case ErrorCode.SUM_OR_AVG_ON_WRONG_DATATYPE_1 /* 90015 */:
                    case ErrorCode.MUST_GROUP_BY_COLUMN_1 /* 90016 */:
                    case ErrorCode.SECOND_PRIMARY_KEY /* 90017 */:
                    case ErrorCode.FUNCTION_NOT_FOUND_1 /* 90022 */:
                    case ErrorCode.COLUMN_MUST_NOT_BE_NULLABLE_1 /* 90023 */:
                    case ErrorCode.USER_NOT_FOUND_1 /* 90032 */:
                    case ErrorCode.USER_ALREADY_EXISTS_1 /* 90033 */:
                    case ErrorCode.SEQUENCE_ALREADY_EXISTS_1 /* 90035 */:
                    case ErrorCode.SEQUENCE_NOT_FOUND_1 /* 90036 */:
                    case ErrorCode.VIEW_NOT_FOUND_1 /* 90037 */:
                    case ErrorCode.VIEW_ALREADY_EXISTS_1 /* 90038 */:
                    case ErrorCode.TRIGGER_ALREADY_EXISTS_1 /* 90041 */:
                    case ErrorCode.TRIGGER_NOT_FOUND_1 /* 90042 */:
                    case ErrorCode.ERROR_CREATING_TRIGGER_OBJECT_3 /* 90043 */:
                    case ErrorCode.CONSTRAINT_ALREADY_EXISTS_1 /* 90045 */:
                    case ErrorCode.SUBQUERY_IS_NOT_SINGLE_COLUMN /* 90052 */:
                    case ErrorCode.INVALID_USE_OF_AGGREGATE_FUNCTION_1 /* 90054 */:
                    case ErrorCode.CONSTRAINT_NOT_FOUND_1 /* 90057 */:
                    case ErrorCode.AMBIGUOUS_COLUMN_NAME_1 /* 90059 */:
                    case ErrorCode.ORDER_BY_NOT_IN_RESULT /* 90068 */:
                    case ErrorCode.ROLE_ALREADY_EXISTS_1 /* 90069 */:
                    case ErrorCode.ROLE_NOT_FOUND_1 /* 90070 */:
                    case ErrorCode.USER_OR_ROLE_NOT_FOUND_1 /* 90071 */:
                    case ErrorCode.ROLES_AND_RIGHT_CANNOT_BE_MIXED /* 90072 */:
                    case ErrorCode.METHODS_MUST_HAVE_DIFFERENT_PARAMETER_COUNTS_2 /* 90073 */:
                    case ErrorCode.ROLE_ALREADY_GRANTED_1 /* 90074 */:
                    case ErrorCode.COLUMN_IS_PART_OF_INDEX_1 /* 90075 */:
                    case ErrorCode.FUNCTION_ALIAS_ALREADY_EXISTS_1 /* 90076 */:
                    case ErrorCode.FUNCTION_ALIAS_NOT_FOUND_1 /* 90077 */:
                    case ErrorCode.SCHEMA_ALREADY_EXISTS_1 /* 90078 */:
                    case ErrorCode.SCHEMA_NOT_FOUND_1 /* 90079 */:
                    case ErrorCode.SCHEMA_NAME_MUST_MATCH /* 90080 */:
                    case ErrorCode.COLUMN_CONTAINS_NULL_VALUES_1 /* 90081 */:
                    case ErrorCode.SEQUENCE_BELONGS_TO_A_TABLE_1 /* 90082 */:
                    case ErrorCode.COLUMN_IS_REFERENCED_1 /* 90083 */:
                    case ErrorCode.CANNOT_DROP_LAST_COLUMN /* 90084 */:
                    case ErrorCode.INDEX_BELONGS_TO_CONSTRAINT_2 /* 90085 */:
                    case ErrorCode.CLASS_NOT_FOUND_1 /* 90086 */:
                    case ErrorCode.METHOD_NOT_FOUND_1 /* 90087 */:
                    case ErrorCode.COLLATION_CHANGE_WITH_DATA_TABLE_1 /* 90089 */:
                    case ErrorCode.SCHEMA_CAN_NOT_BE_DROPPED_1 /* 90090 */:
                    case ErrorCode.ROLE_CAN_NOT_BE_DROPPED_1 /* 90091 */:
                    case ErrorCode.CANNOT_TRUNCATE_1 /* 90106 */:
                    case ErrorCode.CANNOT_DROP_2 /* 90107 */:
                    case ErrorCode.VIEW_IS_INVALID_2 /* 90109 */:
                    case ErrorCode.TYPES_ARE_NOT_COMPARABLE_2 /* 90110 */:
                    case ErrorCode.CONSTANT_ALREADY_EXISTS_1 /* 90114 */:
                    case ErrorCode.CONSTANT_NOT_FOUND_1 /* 90115 */:
                    case ErrorCode.LITERALS_ARE_NOT_ALLOWED /* 90116 */:
                    case ErrorCode.CANNOT_DROP_TABLE_1 /* 90118 */:
                    case 90119:
                    case 90120:
                    case ErrorCode.WITH_TIES_WITHOUT_ORDER_BY /* 90122 */:
                    case ErrorCode.CANNOT_MIX_INDEXED_AND_UNINDEXED_PARAMS /* 90123 */:
                    case ErrorCode.TRANSACTION_NOT_FOUND_1 /* 90129 */:
                    case ErrorCode.AGGREGATE_NOT_FOUND_1 /* 90132 */:
                    case ErrorCode.WINDOW_NOT_FOUND_1 /* 90136 */:
                    case ErrorCode.CAN_ONLY_ASSIGN_TO_VARIABLE_1 /* 90137 */:
                    case ErrorCode.PUBLIC_STATIC_JAVA_METHOD_NOT_FOUND_1 /* 90139 */:
                    case ErrorCode.JAVA_OBJECT_SERIALIZER_CHANGE_WITH_DATA_TABLE /* 90141 */:
                    case ErrorCode.FOR_UPDATE_IS_NOT_ALLOWED_IN_DISTINCT_OR_GROUPED_SELECT /* 90145 */:
                    case ErrorCode.INVALID_VALUE_PRECISION /* 90150 */:
                    case ErrorCode.INVALID_VALUE_SCALE /* 90151 */:
                    case ErrorCode.CONSTRAINT_IS_USED_BY_CONSTRAINT_2 /* 90152 */:
                    case ErrorCode.UNCOMPARABLE_REFERENCED_COLUMN_2 /* 90153 */:
                    case ErrorCode.GENERATED_COLUMN_CANNOT_BE_ASSIGNED_1 /* 90154 */:
                    case ErrorCode.GENERATED_COLUMN_CANNOT_BE_UPDATABLE_BY_CONSTRAINT_2 /* 90155 */:
                    case ErrorCode.COLUMN_ALIAS_IS_NOT_SPECIFIED_1 /* 90156 */:
                    case ErrorCode.GROUP_BY_NOT_IN_THE_RESULT /* 90157 */:
                        return new JdbcSQLSyntaxErrorException(str, filterSQL, str3, i, th, str4);
                    case ErrorCode.HEX_STRING_ODD_1 /* 90003 */:
                    case ErrorCode.HEX_STRING_WRONG_1 /* 90004 */:
                    case ErrorCode.INVALID_VALUE_2 /* 90008 */:
                    case ErrorCode.SEQUENCE_ATTRIBUTES_INVALID_7 /* 90009 */:
                    case ErrorCode.INVALID_TO_CHAR_FORMAT /* 90010 */:
                    case ErrorCode.PARAMETER_NOT_SET_1 /* 90012 */:
                    case ErrorCode.PARSE_ERROR_1 /* 90014 */:
                    case ErrorCode.SERIALIZATION_FAILED_1 /* 90026 */:
                    case ErrorCode.DESERIALIZATION_FAILED_1 /* 90027 */:
                    case ErrorCode.SCALAR_SUBQUERY_CONTAINS_MORE_THAN_ONE_ROW /* 90053 */:
                    case ErrorCode.INVALID_TO_DATE_FORMAT /* 90056 */:
                    case ErrorCode.STRING_FORMAT_ERROR_1 /* 90095 */:
                    case ErrorCode.STEP_SIZE_MUST_NOT_BE_ZERO /* 90142 */:
                        return new JdbcSQLDataException(str, filterSQL, str3, i, th, str4);
                    case ErrorCode.URL_RELATIVE_TO_CWD /* 90011 */:
                    case ErrorCode.DATABASE_NOT_FOUND_1 /* 90013 */:
                    case ErrorCode.TRACE_CONNECTION_NOT_CLOSED /* 90018 */:
                    case ErrorCode.DATABASE_ALREADY_OPEN_1 /* 90020 */:
                    case ErrorCode.FILE_CORRUPTED_1 /* 90030 */:
                    case ErrorCode.URL_FORMAT_ERROR_2 /* 90046 */:
                    case ErrorCode.DRIVER_VERSION_ERROR_2 /* 90047 */:
                    case ErrorCode.FILE_VERSION_ERROR_1 /* 90048 */:
                    case ErrorCode.FILE_ENCRYPTION_ERROR_1 /* 90049 */:
                    case ErrorCode.WRONG_PASSWORD_FORMAT /* 90050 */:
                    case ErrorCode.UNSUPPORTED_CIPHER /* 90055 */:
                    case ErrorCode.UNSUPPORTED_LOCK_METHOD_1 /* 90060 */:
                    case ErrorCode.EXCEPTION_OPENING_PORT_2 /* 90061 */:
                    case ErrorCode.DUPLICATE_PROPERTY_1 /* 90066 */:
                    case ErrorCode.CONNECTION_BROKEN_1 /* 90067 */:
                    case ErrorCode.UNKNOWN_MODE_1 /* 90088 */:
                    case ErrorCode.CLUSTER_ERROR_DATABASE_RUNS_ALONE /* 90093 */:
                    case ErrorCode.CLUSTER_ERROR_DATABASE_RUNS_CLUSTERED_1 /* 90094 */:
                    case ErrorCode.DATABASE_IS_CLOSED /* 90098 */:
                    case ErrorCode.ERROR_SETTING_DATABASE_EVENT_LISTENER_2 /* 90099 */:
                    case ErrorCode.OUT_OF_MEMORY /* 90108 */:
                    case ErrorCode.UNSUPPORTED_SETTING_1 /* 90113 */:
                    case ErrorCode.REMOTE_CONNECTION_NOT_ALLOWED /* 90117 */:
                    case ErrorCode.DATABASE_CALLED_AT_SHUTDOWN /* 90121 */:
                    case ErrorCode.CANNOT_CHANGE_SETTING_WHEN_OPEN_1 /* 90133 */:
                    case ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE /* 90135 */:
                    case ErrorCode.INVALID_DATABASE_NAME_1 /* 90138 */:
                    case ErrorCode.AUTHENTICATOR_NOT_AVAILABLE /* 90144 */:
                    case ErrorCode.DATABASE_NOT_FOUND_WITH_IF_EXISTS_1 /* 90146 */:
                    case ErrorCode.METHOD_DISABLED_ON_AUTOCOMMIT_TRUE /* 90147 */:
                    case ErrorCode.REMOTE_DATABASE_NOT_FOUND_1 /* 90149 */:
                        return new JdbcSQLNonTransientConnectionException(str, filterSQL, str3, i, th, str4);
                    case ErrorCode.ROW_NOT_FOUND_WHEN_DELETING_1 /* 90112 */:
                    case ErrorCode.CONCURRENT_UPDATE_1 /* 90131 */:
                    case ErrorCode.ROW_NOT_FOUND_IN_PRIMARY_INDEX /* 90143 */:
                        return new JdbcSQLTransientException(str, filterSQL, str3, i, th, str4);
                    default:
                        return new JdbcSQLException(str, filterSQL, str3, i, th, str4);
                }
        }
    }

    private static String filterSQL(String str) {
        return (str == null || !str.contains(HIDE_SQL)) ? str : "-";
    }

    public static String buildMessageForException(JdbcException jdbcException) {
        String originalMessage = jdbcException.getOriginalMessage();
        StringBuilder sb = new StringBuilder(originalMessage != null ? originalMessage : "- ");
        String sql = jdbcException.getSQL();
        if (sql != null) {
            sb.append("; SQL statement:\n").append(sql);
        }
        sb.append(" [").append(jdbcException.getErrorCode()).append('-').append(224).append(']');
        return sb.toString();
    }

    public static void printNextExceptions(SQLException sQLException, PrintWriter printWriter) {
        int i = 0;
        while (true) {
            SQLException nextException = sQLException.getNextException();
            sQLException = nextException;
            if (nextException != null) {
                int i2 = i;
                i++;
                if (i2 == 100) {
                    printWriter.println("(truncated)");
                    return;
                }
                printWriter.println(sQLException.toString());
            } else {
                return;
            }
        }
    }

    public static void printNextExceptions(SQLException sQLException, PrintStream printStream) {
        int i = 0;
        while (true) {
            SQLException nextException = sQLException.getNextException();
            sQLException = nextException;
            if (nextException != null) {
                int i2 = i;
                i++;
                if (i2 == 100) {
                    printStream.println("(truncated)");
                    return;
                }
                printStream.println(sQLException.toString());
            } else {
                return;
            }
        }
    }

    public Object getSource() {
        return this.source;
    }

    public void setSource(Object obj) {
        this.source = obj;
    }

    public static void traceThrowable(Throwable th) {
        PrintWriter logWriter = DriverManager.getLogWriter();
        if (logWriter != null) {
            th.printStackTrace(logWriter);
        }
    }
}
