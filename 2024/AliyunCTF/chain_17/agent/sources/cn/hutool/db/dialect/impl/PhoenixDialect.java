package cn.hutool.db.dialect.impl;

import cn.hutool.db.Entity;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/impl/PhoenixDialect.class */
public class PhoenixDialect extends AnsiSqlDialect {
    private static final long serialVersionUID = 1;

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect, cn.hutool.db.dialect.Dialect
    public PreparedStatement psForUpdate(Connection conn, Entity entity, Query query) throws SQLException {
        return super.psForInsert(conn, entity);
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect, cn.hutool.db.dialect.Dialect
    public String dialectName() {
        return DialectName.PHOENIX.name();
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
        return psForInsert(conn, entity);
    }
}
