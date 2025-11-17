package cn.hutool.db.dialect.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Page;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.SqlBuilder;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/impl/OracleDialect.class */
public class OracleDialect extends AnsiSqlDialect {
    private static final long serialVersionUID = 6122761762247483015L;

    public static boolean isNextVal(Object value) {
        return (value instanceof CharSequence) && StrUtil.endWithIgnoreCase(value.toString(), ".nextval");
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect
    protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
        int[] startEnd = page.getStartEnd();
        return find.insertPreFragment("SELECT * FROM ( SELECT row_.*, rownum rownum_ from ( ").append(" ) row_ where rownum <= ").append(Integer.valueOf(startEnd[1])).append(") table_alias_").append(" where table_alias_.rownum_ > ").append(Integer.valueOf(startEnd[0]));
    }

    @Override // cn.hutool.db.dialect.impl.AnsiSqlDialect, cn.hutool.db.dialect.Dialect
    public String dialectName() {
        return DialectName.ORACLE.name();
    }
}
