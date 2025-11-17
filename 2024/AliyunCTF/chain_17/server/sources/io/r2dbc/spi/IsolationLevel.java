package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/IsolationLevel.class */
public final class IsolationLevel implements TransactionDefinition {
    private static final ConstantPool<IsolationLevel> CONSTANTS = new ConstantPool<IsolationLevel>() { // from class: io.r2dbc.spi.IsolationLevel.1
        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // io.r2dbc.spi.ConstantPool
        public IsolationLevel createConstant(String name, boolean sensitive) {
            return new IsolationLevel(name);
        }
    };
    public static final IsolationLevel READ_COMMITTED = valueOf("READ COMMITTED");
    public static final IsolationLevel READ_UNCOMMITTED = valueOf("READ UNCOMMITTED");
    public static final IsolationLevel REPEATABLE_READ = valueOf("REPEATABLE READ");
    public static final IsolationLevel SERIALIZABLE = valueOf("SERIALIZABLE");
    private final String sql;

    private IsolationLevel(String sql) {
        this.sql = (String) Assert.requireNonNull(sql, "sql must not be null");
    }

    public static IsolationLevel valueOf(String sql) {
        Assert.requireNonNull(sql, "sql must not be null");
        Assert.requireNonEmpty(sql, "sql must not be empty");
        return CONSTANTS.valueOf(sql, false);
    }

    @Override // io.r2dbc.spi.TransactionDefinition
    public <T> T getAttribute(Option<T> option) {
        Assert.requireNonNull(option, "option must not be null");
        if (option.equals(TransactionDefinition.ISOLATION_LEVEL)) {
            return option.cast(this);
        }
        return null;
    }

    public String asSql() {
        return this.sql;
    }

    public String toString() {
        return "IsolationLevel{sql='" + this.sql + "'}";
    }
}
