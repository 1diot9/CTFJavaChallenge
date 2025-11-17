package cn.hutool.db.dialect.impl;

import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.Wrapper;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/impl/Sqlite3Dialect.class */
public class Sqlite3Dialect extends AnsiSqlDialect {
    private static final long serialVersionUID = -3527642408849291634L;

    public Sqlite3Dialect() {
        this.wrapper = new Wrapper('[', ']');
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect, cn.hutool.db.dialect.Dialect
    public String dialectName() {
        return DialectName.SQLITE3.name();
    }
}
