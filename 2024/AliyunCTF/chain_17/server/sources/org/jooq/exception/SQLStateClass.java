package org.jooq.exception;

import ch.qos.logback.core.pattern.color.ANSIConstants;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/exception/SQLStateClass.class */
public enum SQLStateClass {
    C00_SUCCESSFUL_COMPLETION("00"),
    C01_WARNING("01"),
    C02_NO_DATA("02"),
    C07_DYNAMIC_SQL_ERROR("07"),
    C08_CONNECTION_EXCEPTION("08"),
    C09_TRIGGERED_ACTION_EXCEPTION("09"),
    C0A_FEATURE_NOT_SUPPORTED("0A"),
    C0D_INVALID_TARGET_TYPE_SPECIFICATION("0D"),
    C0E_INVALID_SCHEMA_NAME_LIST_SPECIFICATION("0E"),
    C0F_LOCATOR_EXCEPTION("0F"),
    C0L_INVALID_GRANTOR("0L"),
    C0M_INVALID_SQL_INVOKED_PROCEDURE_REFERENCE("0M"),
    C0P_INVALID_ROLE_SPECIFICATION("0P"),
    C0S_INVALID_TRANSFORM_GROUP_NAME_SPECIFICATION("0S"),
    C0T_TARGET_TABLE_DISAGREES_WITH_CURSOR_SPECIFICATION("0T"),
    C0U_ATTEMPT_TO_ASSIGN_TO_NON_UPDATABLE_COLUMN("0U"),
    C0V_ATTEMPT_TO_ASSIGN_TO_ORDERING_COLUMN("0V"),
    C0W_PROHIBITED_STATEMENT_ENCOUNTERED_DURING_TRIGGER_EXECUTION("0W"),
    C0Z_DIAGNOSTICS_EXCEPTION("0Z"),
    C21_CARDINALITY_VIOLATION("21"),
    C22_DATA_EXCEPTION("22"),
    C23_INTEGRITY_CONSTRAINT_VIOLATION("23"),
    C24_INVALID_CURSOR_STATE("24"),
    C25_INVALID_TRANSACTION_STATE("25"),
    C26_INVALID_SQL_STATEMENT_NAME("26"),
    C27_TRIGGERED_DATA_CHANGE_VIOLATION("27"),
    C28_INVALID_AUTHORIZATION_SPECIFICATION("28"),
    C2B_DEPENDENT_PRIVILEGE_DESCRIPTORS_STILL_EXIST("2B"),
    C2C_INVALID_CHARACTER_SET_NAME("2C"),
    C2D_INVALID_TRANSACTION_TERMINATION("2D"),
    C2E_INVALID_CONNECTION_NAME("2E"),
    C2F_SQL_ROUTINE_EXCEPTION("2F"),
    C2H_INVALID_COLLATION_NAME("2H"),
    C30_INVALID_SQL_STATEMENT_IDENTIFIER(ANSIConstants.BLACK_FG),
    C33_INVALID_SQL_DESCRIPTOR_NAME(ANSIConstants.YELLOW_FG),
    C34_INVALID_CURSOR_NAME(ANSIConstants.BLUE_FG),
    C35_INVALID_CONDITION_NUMBER(ANSIConstants.MAGENTA_FG),
    C36_CURSOR_SENSITIVITY_EXCEPTION(ANSIConstants.CYAN_FG),
    C38_EXTERNAL_ROUTINE_EXCEPTION("38"),
    C39_EXTERNAL_ROUTINE_INVOCATION_EXCEPTION(ANSIConstants.DEFAULT_FG),
    C3B_SAVEPOINT_EXCEPTION("3B"),
    C3C_AMBIGUOUS_CURSOR_NAME("3C"),
    C3D_INVALID_CATALOG_NAME("3D"),
    C3F_INVALID_SCHEMA_NAME("3F"),
    C40_TRANSACTION_ROLLBACK("40"),
    C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION("42"),
    CHZ_REMOTE_DATABASE_ACCESS("HZ"),
    OTHER(""),
    NONE("");

    private static final Map<String, SQLStateClass> lookup = new HashMap();
    private final String className;

    static {
        for (SQLStateClass clazz : values()) {
            lookup.put(clazz.className, clazz);
        }
    }

    SQLStateClass(String className) {
        this.className = className;
    }

    @NotNull
    public String className() {
        return this.className;
    }

    @NotNull
    public static SQLStateClass fromCode(String code) {
        if (code == null || code.length() < 2) {
            return OTHER;
        }
        SQLStateClass result = lookup.get(code.substring(0, 2));
        return result != null ? result : OTHER;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NotNull
    public static SQLStateClass fromSQLiteVendorCode(int errorCode) {
        switch (errorCode & 255) {
            case 0:
                return C00_SUCCESSFUL_COMPLETION;
            case 3:
                return C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION;
            case 18:
                return C22_DATA_EXCEPTION;
            case 19:
                return C23_INTEGRITY_CONSTRAINT_VIOLATION;
            case 20:
                return C22_DATA_EXCEPTION;
            case 27:
                return C01_WARNING;
            case 28:
                return C01_WARNING;
            default:
                return OTHER;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NotNull
    public static SQLStateClass fromTrinoVendorCode(int errorCode) {
        switch (errorCode) {
            case 1:
                return C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION;
            case 9:
            case 19:
                return C22_DATA_EXCEPTION;
            case 16:
                return C23_INTEGRITY_CONSTRAINT_VIOLATION;
            default:
                return OTHER;
        }
    }
}
