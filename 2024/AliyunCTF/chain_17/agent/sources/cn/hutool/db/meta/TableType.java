package cn.hutool.db.meta;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/meta/TableType.class */
public enum TableType {
    TABLE("TABLE"),
    VIEW("VIEW"),
    SYSTEM_TABLE("SYSTEM TABLE"),
    GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
    LOCAL_TEMPORARY("LOCAL TEMPORARY"),
    ALIAS("ALIAS"),
    SYNONYM("SYNONYM");

    private final String value;

    TableType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    @Override // java.lang.Enum
    public String toString() {
        return value();
    }
}
