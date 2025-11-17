package cn.hutool.db.dialect;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/DialectName.class */
public enum DialectName {
    ANSI,
    MYSQL,
    ORACLE,
    POSTGRESQL,
    SQLITE3,
    H2,
    SQLSERVER,
    SQLSERVER2012,
    PHOENIX;

    public boolean match(String dialectName) {
        return StrUtil.equalsIgnoreCase(dialectName, name());
    }
}
