package org.h2.command.dml;

import cn.hutool.core.text.StrPool;
import java.util.ArrayList;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/SetTypes.class */
public class SetTypes {
    public static final int IGNORECASE = 0;
    public static final int MAX_LOG_SIZE = 1;
    public static final int MODE = 2;
    public static final int READONLY = 3;
    public static final int LOCK_TIMEOUT = 4;
    public static final int DEFAULT_LOCK_TIMEOUT = 5;
    public static final int DEFAULT_TABLE_TYPE = 6;
    public static final int CACHE_SIZE = 7;
    public static final int TRACE_LEVEL_SYSTEM_OUT = 8;
    public static final int TRACE_LEVEL_FILE = 9;
    public static final int TRACE_MAX_FILE_SIZE = 10;
    public static final int COLLATION = 11;
    public static final int CLUSTER = 12;
    public static final int WRITE_DELAY = 13;
    public static final int DATABASE_EVENT_LISTENER = 14;
    public static final int MAX_MEMORY_ROWS = 15;
    public static final int LOCK_MODE = 16;
    public static final int DB_CLOSE_DELAY = 17;
    public static final int THROTTLE = 18;
    public static final int MAX_MEMORY_UNDO = 19;
    public static final int MAX_LENGTH_INPLACE_LOB = 20;
    public static final int ALLOW_LITERALS = 21;
    public static final int SCHEMA = 22;
    public static final int OPTIMIZE_REUSE_RESULTS = 23;
    public static final int SCHEMA_SEARCH_PATH = 24;
    public static final int REFERENTIAL_INTEGRITY = 25;
    public static final int MAX_OPERATION_MEMORY = 26;
    public static final int EXCLUSIVE = 27;
    public static final int CREATE_BUILD = 28;
    public static final int VARIABLE = 29;
    public static final int QUERY_TIMEOUT = 30;
    public static final int REDO_LOG_BINARY = 31;
    public static final int JAVA_OBJECT_SERIALIZER = 32;
    public static final int RETENTION_TIME = 33;
    public static final int QUERY_STATISTICS = 34;
    public static final int QUERY_STATISTICS_MAX_ENTRIES = 35;
    public static final int LAZY_QUERY_EXECUTION = 36;
    public static final int BUILTIN_ALIAS_OVERRIDE = 37;
    public static final int AUTHENTICATOR = 38;
    public static final int IGNORE_CATALOGS = 39;
    public static final int CATALOG = 40;
    public static final int NON_KEYWORDS = 41;
    public static final int TIME_ZONE = 42;
    public static final int VARIABLE_BINARY = 43;
    public static final int DEFAULT_NULL_ORDERING = 44;
    public static final int TRUNCATE_LARGE_LENGTH = 45;
    private static final int COUNT = 46;
    private static final ArrayList<String> TYPES;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !SetTypes.class.desiredAssertionStatus();
        ArrayList<String> arrayList = new ArrayList<>(46);
        arrayList.add("IGNORECASE");
        arrayList.add("MAX_LOG_SIZE");
        arrayList.add("MODE");
        arrayList.add("READONLY");
        arrayList.add("LOCK_TIMEOUT");
        arrayList.add("DEFAULT_LOCK_TIMEOUT");
        arrayList.add("DEFAULT_TABLE_TYPE");
        arrayList.add("CACHE_SIZE");
        arrayList.add("TRACE_LEVEL_SYSTEM_OUT");
        arrayList.add("TRACE_LEVEL_FILE");
        arrayList.add("TRACE_MAX_FILE_SIZE");
        arrayList.add("COLLATION");
        arrayList.add("CLUSTER");
        arrayList.add("WRITE_DELAY");
        arrayList.add("DATABASE_EVENT_LISTENER");
        arrayList.add("MAX_MEMORY_ROWS");
        arrayList.add("LOCK_MODE");
        arrayList.add("DB_CLOSE_DELAY");
        arrayList.add("THROTTLE");
        arrayList.add("MAX_MEMORY_UNDO");
        arrayList.add("MAX_LENGTH_INPLACE_LOB");
        arrayList.add("ALLOW_LITERALS");
        arrayList.add("SCHEMA");
        arrayList.add("OPTIMIZE_REUSE_RESULTS");
        arrayList.add("SCHEMA_SEARCH_PATH");
        arrayList.add("REFERENTIAL_INTEGRITY");
        arrayList.add("MAX_OPERATION_MEMORY");
        arrayList.add("EXCLUSIVE");
        arrayList.add("CREATE_BUILD");
        arrayList.add(StrPool.AT);
        arrayList.add("QUERY_TIMEOUT");
        arrayList.add("REDO_LOG_BINARY");
        arrayList.add("JAVA_OBJECT_SERIALIZER");
        arrayList.add("RETENTION_TIME");
        arrayList.add("QUERY_STATISTICS");
        arrayList.add("QUERY_STATISTICS_MAX_ENTRIES");
        arrayList.add("LAZY_QUERY_EXECUTION");
        arrayList.add("BUILTIN_ALIAS_OVERRIDE");
        arrayList.add("AUTHENTICATOR");
        arrayList.add("IGNORE_CATALOGS");
        arrayList.add("CATALOG");
        arrayList.add("NON_KEYWORDS");
        arrayList.add("TIME ZONE");
        arrayList.add("VARIABLE_BINARY");
        arrayList.add("DEFAULT_NULL_ORDERING");
        arrayList.add("TRUNCATE_LARGE_LENGTH");
        TYPES = arrayList;
        if (!$assertionsDisabled && arrayList.size() != 46) {
            throw new AssertionError();
        }
    }

    private SetTypes() {
    }

    public static int getType(String str) {
        return TYPES.indexOf(str);
    }

    public static ArrayList<String> getTypes() {
        return TYPES;
    }

    public static String getTypeName(int i) {
        return TYPES.get(i);
    }
}
