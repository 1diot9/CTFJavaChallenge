package org.h2.engine;

import cn.hutool.core.text.StrPool;
import java.util.HashMap;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/DbSettings.class */
public class DbSettings extends SettingsBase {
    static final int TABLE_SIZE = 64;
    public static final DbSettings DEFAULT = new DbSettings(new HashMap(64));
    public final int analyzeAuto;
    public final int analyzeSample;
    public final int autoCompactFillRate;
    public final boolean databaseToLower;
    public final boolean databaseToUpper;
    public final boolean caseInsensitiveIdentifiers;
    public final boolean defaultConnection;
    public final String defaultEscape;
    public final boolean defragAlways;
    public final boolean dropRestrict;
    public final int estimatedFunctionTableRows;
    public final int lobTimeout;
    public final int maxCompactTime;
    public final int maxQueryTimeout;
    public final boolean optimizeDistinct;
    public final boolean optimizeEvaluatableSubqueries;
    public final boolean optimizeInsertFromSelect;
    public final boolean optimizeInList;
    public final boolean optimizeInSelect;
    public final boolean optimizeOr;
    public final boolean optimizeTwoEquals;
    public final boolean optimizeSimpleSingleRowSubqueries;
    public final int queryCacheSize;
    public final boolean recompileAlways;
    public final boolean reuseSpace;
    public final boolean shareLinkedConnections;
    public final String defaultTableEngine;
    public final boolean mvStore;
    public final boolean compressData;
    public final boolean ignoreCatalogs;
    public final boolean zeroBasedEnums;

    private DbSettings(HashMap<String, String> hashMap) {
        super(hashMap);
        this.analyzeAuto = get("ANALYZE_AUTO", 2000);
        this.analyzeSample = get("ANALYZE_SAMPLE", 10000);
        this.autoCompactFillRate = get("AUTO_COMPACT_FILL_RATE", 90);
        this.caseInsensitiveIdentifiers = get("CASE_INSENSITIVE_IDENTIFIERS", false);
        this.defaultConnection = get("DEFAULT_CONNECTION", false);
        this.defaultEscape = get("DEFAULT_ESCAPE", StrPool.BACKSLASH);
        this.defragAlways = get("DEFRAG_ALWAYS", false);
        this.dropRestrict = get("DROP_RESTRICT", true);
        this.estimatedFunctionTableRows = get("ESTIMATED_FUNCTION_TABLE_ROWS", 1000);
        this.lobTimeout = get("LOB_TIMEOUT", 300000);
        this.maxCompactTime = get("MAX_COMPACT_TIME", 200);
        this.maxQueryTimeout = get("MAX_QUERY_TIMEOUT", 0);
        this.optimizeDistinct = get("OPTIMIZE_DISTINCT", true);
        this.optimizeEvaluatableSubqueries = get("OPTIMIZE_EVALUATABLE_SUBQUERIES", true);
        this.optimizeInsertFromSelect = get("OPTIMIZE_INSERT_FROM_SELECT", true);
        this.optimizeInList = get("OPTIMIZE_IN_LIST", true);
        this.optimizeInSelect = get("OPTIMIZE_IN_SELECT", true);
        this.optimizeOr = get("OPTIMIZE_OR", true);
        this.optimizeTwoEquals = get("OPTIMIZE_TWO_EQUALS", true);
        this.optimizeSimpleSingleRowSubqueries = get("OPTIMIZE_SIMPLE_SINGLE_ROW_SUBQUERIES", true);
        this.queryCacheSize = get("QUERY_CACHE_SIZE", 8);
        this.recompileAlways = get("RECOMPILE_ALWAYS", false);
        this.reuseSpace = get("REUSE_SPACE", true);
        this.shareLinkedConnections = get("SHARE_LINKED_CONNECTIONS", true);
        this.defaultTableEngine = get("DEFAULT_TABLE_ENGINE", (String) null);
        this.mvStore = get("MV_STORE", true);
        this.compressData = get("COMPRESS", false);
        this.ignoreCatalogs = get("IGNORE_CATALOGS", false);
        this.zeroBasedEnums = get("ZERO_BASED_ENUMS", false);
        boolean z = get("DATABASE_TO_LOWER", false);
        boolean containsKey = containsKey("DATABASE_TO_UPPER");
        boolean z2 = get("DATABASE_TO_UPPER", true);
        if (z && z2) {
            if (containsKey) {
                throw DbException.get(ErrorCode.UNSUPPORTED_SETTING_COMBINATION, "DATABASE_TO_LOWER & DATABASE_TO_UPPER");
            }
            z2 = false;
        }
        this.databaseToLower = z;
        this.databaseToUpper = z2;
        HashMap<String, String> settings = getSettings();
        settings.put("DATABASE_TO_LOWER", Boolean.toString(z));
        settings.put("DATABASE_TO_UPPER", Boolean.toString(z2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DbSettings getInstance(HashMap<String, String> hashMap) {
        return new DbSettings(hashMap);
    }
}
