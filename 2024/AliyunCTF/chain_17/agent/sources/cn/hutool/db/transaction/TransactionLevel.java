package cn.hutool.db.transaction;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/transaction/TransactionLevel.class */
public enum TransactionLevel {
    NONE(0),
    READ_UNCOMMITTED(1),
    READ_COMMITTED(2),
    REPEATABLE_READ(4),
    SERIALIZABLE(8);

    private final int level;

    TransactionLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}
