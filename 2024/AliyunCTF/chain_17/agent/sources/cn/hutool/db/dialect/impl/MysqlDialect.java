package cn.hutool.db.dialect.impl;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.StatementUtil;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/impl/MysqlDialect.class */
public class MysqlDialect extends AnsiSqlDialect {
    private static final long serialVersionUID = -3734718212043823636L;

    public MysqlDialect() {
        this.wrapper = new Wrapper('`');
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect
    protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
        return find.append(" LIMIT ").append(Integer.valueOf(page.getStartPosition())).append(", ").append(Integer.valueOf(page.getPageSize()));
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect, cn.hutool.db.dialect.Dialect
    public String dialectName() {
        return DialectName.MYSQL.toString();
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
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
                String field = null != this.wrapper ? this.wrapper.wrap(field) : field;
                fieldsPart.append(field);
                updateHolder.append(field).append("=values(").append(field).append(")");
                placeHolder.append(CoreConstants.NA);
                builder.addParams(value);
            }
        });
        String tableName = entity.getTableName();
        if (null != this.wrapper) {
            tableName = this.wrapper.wrap(tableName);
        }
        builder.append("INSERT INTO ").append(tableName).append(" (").append(fieldsPart).append(") VALUES (").append(placeHolder).append(") ON DUPLICATE KEY UPDATE ").append(updateHolder);
        return StatementUtil.prepareStatement(conn, builder);
    }
}
