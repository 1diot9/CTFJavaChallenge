package cn.hutool.db.sql;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/LogicalOperator.class */
public enum LogicalOperator {
    AND,
    OR;

    public boolean isSame(String logicalOperatorStr) {
        if (StrUtil.isBlank(logicalOperatorStr)) {
            return false;
        }
        return name().equalsIgnoreCase(logicalOperatorStr.trim());
    }
}
