package org.h2.engine;

import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/IsolationLevel.class */
public enum IsolationLevel {
    READ_UNCOMMITTED(1, 0),
    READ_COMMITTED(2, 3),
    REPEATABLE_READ(4, 1),
    SNAPSHOT(6, 1),
    SERIALIZABLE(8, 1);

    private final String sql = name().replace('_', ' ').intern();
    private final int jdbc;
    private final int lockMode;

    public static IsolationLevel fromJdbc(int i) {
        switch (i) {
            case 1:
                return READ_UNCOMMITTED;
            case 2:
                return READ_COMMITTED;
            case 3:
            case 5:
            case 7:
            default:
                throw DbException.getInvalidValueException("isolation level", Integer.valueOf(i));
            case 4:
                return REPEATABLE_READ;
            case 6:
                return SNAPSHOT;
            case 8:
                return SERIALIZABLE;
        }
    }

    public static IsolationLevel fromLockMode(int i) {
        switch (i) {
            case 0:
                return READ_UNCOMMITTED;
            case 1:
            case 2:
                return SERIALIZABLE;
            case 3:
            default:
                return READ_COMMITTED;
        }
    }

    public static IsolationLevel fromSql(String str) {
        boolean z = -1;
        switch (str.hashCode()) {
            case -1116651265:
                if (str.equals("SERIALIZABLE")) {
                    z = 4;
                    break;
                }
                break;
            case -730039967:
                if (str.equals("REPEATABLE READ")) {
                    z = 2;
                    break;
                }
                break;
            case -107284142:
                if (str.equals("READ COMMITTED")) {
                    z = true;
                    break;
                }
                break;
            case 1067500996:
                if (str.equals("SNAPSHOT")) {
                    z = 3;
                    break;
                }
                break;
            case 1320559961:
                if (str.equals("READ UNCOMMITTED")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return READ_UNCOMMITTED;
            case true:
                return READ_COMMITTED;
            case true:
                return REPEATABLE_READ;
            case true:
                return SNAPSHOT;
            case true:
                return SERIALIZABLE;
            default:
                throw DbException.getInvalidValueException("isolation level", str);
        }
    }

    IsolationLevel(int i, int i2) {
        this.jdbc = i;
        this.lockMode = i2;
    }

    public String getSQL() {
        return this.sql;
    }

    public int getJdbc() {
        return this.jdbc;
    }

    public int getLockMode() {
        return this.lockMode;
    }

    public boolean allowNonRepeatableRead() {
        return ordinal() < REPEATABLE_READ.ordinal();
    }
}
