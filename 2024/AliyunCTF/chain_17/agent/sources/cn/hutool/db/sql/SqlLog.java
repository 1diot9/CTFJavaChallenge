package cn.hutool.db.sql;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.level.Level;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/SqlLog.class */
public enum SqlLog {
    INSTANCE;

    public static final String KEY_SHOW_SQL = "showSql";
    public static final String KEY_FORMAT_SQL = "formatSql";
    public static final String KEY_SHOW_PARAMS = "showParams";
    public static final String KEY_SQL_LEVEL = "sqlLevel";
    private static final Log log = LogFactory.get();
    private boolean showSql;
    private boolean formatSql;
    private boolean showParams;
    private Level level = Level.DEBUG;

    SqlLog() {
    }

    public void init(boolean isShowSql, boolean isFormatSql, boolean isShowParams, Level level) {
        this.showSql = isShowSql;
        this.formatSql = isFormatSql;
        this.showParams = isShowParams;
        this.level = level;
    }

    public void log(String sql) {
        log(sql, null);
    }

    public void logForBatch(String sql) {
        if (this.showSql) {
            Log log2 = log;
            Level level = this.level;
            Object[] objArr = new Object[1];
            objArr[0] = this.formatSql ? SqlFormatter.format(sql) : sql;
            log2.log(level, "\n[Batch SQL] -> {}", objArr);
        }
    }

    public void log(String sql, Object paramValues) {
        if (this.showSql) {
            if (null != paramValues && this.showParams) {
                Log log2 = log;
                Level level = this.level;
                Object[] objArr = new Object[2];
                objArr[0] = this.formatSql ? SqlFormatter.format(sql) : sql;
                objArr[1] = paramValues;
                log2.log(level, "\n[SQL] -> {}\nParams -> {}", objArr);
                return;
            }
            Log log3 = log;
            Level level2 = this.level;
            Object[] objArr2 = new Object[1];
            objArr2[0] = this.formatSql ? SqlFormatter.format(sql) : sql;
            log3.log(level2, "\n[SQL] -> {}", objArr2);
        }
    }
}
