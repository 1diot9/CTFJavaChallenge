package org.h2.engine;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.spi.ComponentTracker;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.CharsetUtil;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import org.apache.tomcat.jni.SSL;
import org.h2.util.MathUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SysProperties.class */
public class SysProperties {
    public static final String H2_SCRIPT_DIRECTORY = "h2.scriptDirectory";
    public static final String H2_BROWSER = "h2.browser";
    public static final String USER_HOME = Utils.getProperty("user.home", "");
    public static final String ALLOWED_CLASSES = Utils.getProperty("h2.allowedClasses", "*");
    public static final boolean ENABLE_ANONYMOUS_TLS = Utils.getProperty("h2.enableAnonymousTLS", true);
    public static final String BIND_ADDRESS = Utils.getProperty("h2.bindAddress", (String) null);
    public static final boolean CHECK;
    public static final String CLIENT_TRACE_DIRECTORY;
    public static final int COLLATOR_CACHE_SIZE;
    public static final int CONSOLE_MAX_TABLES_LIST_INDEXES;
    public static final int CONSOLE_MAX_TABLES_LIST_COLUMNS;
    public static final int CONSOLE_MAX_PROCEDURES_LIST_COLUMNS;
    public static final boolean CONSOLE_STREAM;
    public static final int CONSOLE_TIMEOUT;
    public static final int DATASOURCE_TRACE_LEVEL;
    public static final int DELAY_WRONG_PASSWORD_MIN;
    public static final int DELAY_WRONG_PASSWORD_MAX;
    public static final boolean JAVA_SYSTEM_COMPILER;
    public static boolean lobCloseBetweenReads;
    public static final int LOB_CLIENT_MAX_SIZE_MEMORY;
    public static final int MAX_FILE_RETRY;
    public static final int MAX_RECONNECT;
    public static final int MAX_MEMORY_ROWS;
    public static final long MAX_TRACE_DATA_LENGTH;
    public static final boolean NIO_LOAD_MAPPED;
    public static final boolean NIO_CLEANER_HACK;
    public static final boolean OBJECT_CACHE;
    public static final int OBJECT_CACHE_MAX_PER_ELEMENT_SIZE;
    public static final int OBJECT_CACHE_SIZE;
    public static final String PG_DEFAULT_CLIENT_ENCODING;
    public static final String PREFIX_TEMP_FILE;
    public static boolean FORCE_AUTOCOMMIT_OFF_ON_COMMIT;
    public static final int SERVER_CACHED_OBJECTS;
    public static final int SERVER_RESULT_SET_FETCH_SIZE;
    public static final int SOCKET_CONNECT_RETRY;
    public static final int SOCKET_CONNECT_TIMEOUT;
    public static final long SPLIT_FILE_SIZE_SHIFT;
    public static final boolean TRACE_IO;
    public static final boolean THREAD_DEADLOCK_DETECTOR;
    public static final String URL_MAP;
    public static final boolean USE_THREAD_CONTEXT_CLASS_LOADER;
    public static final String JAVA_OBJECT_SERIALIZER;
    public static final String AUTH_CONFIG_FILE;
    private static final String H2_BASE_DIR = "h2.baseDir";

    static {
        CHECK = Utils.getProperty("h2.check", !"0.9".equals(Utils.getProperty("java.specification.version", (String) null)));
        CLIENT_TRACE_DIRECTORY = Utils.getProperty("h2.clientTraceDirectory", "trace.db/");
        COLLATOR_CACHE_SIZE = Utils.getProperty("h2.collatorCacheSize", 32000);
        CONSOLE_MAX_TABLES_LIST_INDEXES = Utils.getProperty("h2.consoleTableIndexes", 100);
        CONSOLE_MAX_TABLES_LIST_COLUMNS = Utils.getProperty("h2.consoleTableColumns", 500);
        CONSOLE_MAX_PROCEDURES_LIST_COLUMNS = Utils.getProperty("h2.consoleProcedureColumns", 300);
        CONSOLE_STREAM = Utils.getProperty("h2.consoleStream", true);
        CONSOLE_TIMEOUT = Utils.getProperty("h2.consoleTimeout", ComponentTracker.DEFAULT_TIMEOUT);
        DATASOURCE_TRACE_LEVEL = Utils.getProperty("h2.dataSourceTraceLevel", 1);
        DELAY_WRONG_PASSWORD_MIN = Utils.getProperty("h2.delayWrongPasswordMin", 250);
        DELAY_WRONG_PASSWORD_MAX = Utils.getProperty("h2.delayWrongPasswordMax", SerializerCache.DEFAULT_MAX_CACHED);
        JAVA_SYSTEM_COMPILER = Utils.getProperty("h2.javaSystemCompiler", true);
        lobCloseBetweenReads = Utils.getProperty("h2.lobCloseBetweenReads", false);
        LOB_CLIENT_MAX_SIZE_MEMORY = Utils.getProperty("h2.lobClientMaxSizeMemory", SSL.SSL_OP_SINGLE_DH_USE);
        MAX_FILE_RETRY = Math.max(1, Utils.getProperty("h2.maxFileRetry", 16));
        MAX_RECONNECT = Utils.getProperty("h2.maxReconnect", 3);
        MAX_MEMORY_ROWS = getAutoScaledForMemoryProperty("h2.maxMemoryRows", Level.ERROR_INT);
        MAX_TRACE_DATA_LENGTH = Utils.getProperty("h2.maxTraceDataLength", NetUtil.PORT_RANGE_MAX);
        NIO_LOAD_MAPPED = Utils.getProperty("h2.nioLoadMapped", false);
        NIO_CLEANER_HACK = Utils.getProperty("h2.nioCleanerHack", false);
        OBJECT_CACHE = Utils.getProperty("h2.objectCache", true);
        OBJECT_CACHE_MAX_PER_ELEMENT_SIZE = Utils.getProperty("h2.objectCacheMaxPerElementSize", 4096);
        try {
            OBJECT_CACHE_SIZE = MathUtils.nextPowerOf2(Utils.getProperty("h2.objectCacheSize", 1024));
            PG_DEFAULT_CLIENT_ENCODING = Utils.getProperty("h2.pgClientEncoding", CharsetUtil.UTF_8);
            PREFIX_TEMP_FILE = Utils.getProperty("h2.prefixTempFile", "h2.temp");
            FORCE_AUTOCOMMIT_OFF_ON_COMMIT = Utils.getProperty("h2.forceAutoCommitOffOnCommit", false);
            SERVER_CACHED_OBJECTS = Utils.getProperty("h2.serverCachedObjects", 64);
            SERVER_RESULT_SET_FETCH_SIZE = Utils.getProperty("h2.serverResultSetFetchSize", 100);
            SOCKET_CONNECT_RETRY = Utils.getProperty("h2.socketConnectRetry", 16);
            SOCKET_CONNECT_TIMEOUT = Utils.getProperty("h2.socketConnectTimeout", 2000);
            SPLIT_FILE_SIZE_SHIFT = Utils.getProperty("h2.splitFileSizeShift", 30);
            TRACE_IO = Utils.getProperty("h2.traceIO", false);
            THREAD_DEADLOCK_DETECTOR = Utils.getProperty("h2.threadDeadlockDetector", false);
            URL_MAP = Utils.getProperty("h2.urlMap", (String) null);
            USE_THREAD_CONTEXT_CLASS_LOADER = Utils.getProperty("h2.useThreadContextClassLoader", false);
            JAVA_OBJECT_SERIALIZER = Utils.getProperty("h2.javaObjectSerializer", (String) null);
            AUTH_CONFIG_FILE = Utils.getProperty("h2.authConfigFile", (String) null);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid h2.objectCacheSize", e);
        }
    }

    private SysProperties() {
    }

    public static void setBaseDir(String str) {
        if (!str.endsWith("/")) {
            str = str + "/";
        }
        System.setProperty(H2_BASE_DIR, str);
    }

    public static String getBaseDir() {
        return Utils.getProperty(H2_BASE_DIR, (String) null);
    }

    public static String getScriptDirectory() {
        return Utils.getProperty(H2_SCRIPT_DIRECTORY, "");
    }

    private static int getAutoScaledForMemoryProperty(String str, int i) {
        String property = Utils.getProperty(str, (String) null);
        if (property != null) {
            try {
                return Integer.decode(property).intValue();
            } catch (NumberFormatException e) {
            }
        }
        return Utils.scaleForAvailableMemory(i);
    }
}
