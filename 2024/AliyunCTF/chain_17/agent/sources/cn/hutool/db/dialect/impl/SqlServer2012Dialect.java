package cn.hutool.db.dialect.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Page;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/impl/SqlServer2012Dialect.class */
public class SqlServer2012Dialect extends AnsiSqlDialect {
    private static final long serialVersionUID = -37598166015777797L;

    public SqlServer2012Dialect() {
        this.wrapper = new Wrapper('\"');
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect
    protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
        if (false == StrUtil.containsIgnoreCase(find.toString(), "order by")) {
            find.append(" order by current_timestamp");
        }
        return find.append(" offset ").append(Integer.valueOf(page.getStartPosition())).append(" row fetch next ").append(Integer.valueOf(page.getPageSize())).append(" row only");
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect, cn.hutool.db.dialect.Dialect
    public String dialectName() {
        return DialectName.SQLSERVER2012.name();
    }
}
