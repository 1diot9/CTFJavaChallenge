package cn.hutool.db.sql;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/Direction.class */
public enum Direction {
    ASC,
    DESC;

    public static Direction fromString(String value) throws IllegalArgumentException {
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        if (1 == value.length()) {
            if ("A".equalsIgnoreCase(value)) {
                return ASC;
            }
            if ("D".equalsIgnoreCase(value)) {
                return DESC;
            }
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(StrUtil.format("Invalid value [{}] for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), e);
        }
    }
}
