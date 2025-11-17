package cn.hutool.db.dialect.impl;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.StatementUtil;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/impl/PostgresqlDialect.class */
public class PostgresqlDialect extends AnsiSqlDialect {
    private static final long serialVersionUID = 3889210427543389642L;

    public PostgresqlDialect() {
        this.wrapper = new Wrapper('\"');
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect, cn.hutool.db.dialect.Dialect
    public String dialectName() {
        return DialectName.POSTGRESQL.name();
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
        Assert.notEmpty(keys, "Keys must be not empty for Postgres.", new Object[0]);
        SqlBuilder.validateEntity(entity);
        SqlBuilder builder = SqlBuilder.create(this.wrapper);
        StringBuilder fieldsPart = new StringBuilder();
        StringBuilder placeHolder = new StringBuilder();
        StringBuilder updateHolder = new StringBuilder();
        entity.forEach((field, value) -> {
            if (StrUtil.isNotBlank(field)) {
                if (fieldsPart.length() > 0) {
                    fieldsPart.append(", ");
                    placeHolder.append(", ");
                    updateHolder.append(", ");
                }
                String wrapedField = null != this.wrapper ? this.wrapper.wrap(field) : field;
                fieldsPart.append(wrapedField);
                updateHolder.append(wrapedField).append("=EXCLUDED.").append(field);
                placeHolder.append(CoreConstants.NA);
                builder.addParams(value);
            }
        });
        String tableName = entity.getTableName();
        if (null != this.wrapper) {
            tableName = this.wrapper.wrap(tableName);
        }
        builder.append("INSERT INTO ").append(tableName).append(" (").append(fieldsPart).append(") VALUES (").append(placeHolder).append(") ON CONFLICT (").append(ArrayUtil.join((Object[]) keys, (CharSequence) ", ")).append(") DO UPDATE SET ").append(updateHolder);
        return StatementUtil.prepareStatement(conn, builder);
    }
}
