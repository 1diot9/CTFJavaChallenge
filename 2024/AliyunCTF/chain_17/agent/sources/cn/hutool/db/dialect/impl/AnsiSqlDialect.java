package cn.hutool.db.dialect.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.StatementUtil;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/impl/AnsiSqlDialect.class */
public class AnsiSqlDialect implements Dialect {
    private static final long serialVersionUID = 2088101129774974580L;
    protected Wrapper wrapper = new Wrapper();

    @Override // cn.hutool.db.dialect.Dialect
    public Wrapper getWrapper() {
        return this.wrapper;
    }

    @Override // cn.hutool.db.dialect.Dialect
    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForInsert(Connection conn, Entity entity) throws SQLException {
        SqlBuilder insert = SqlBuilder.create(this.wrapper).insert(entity, dialectName());
        return StatementUtil.prepareStatement(conn, insert);
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForInsertBatch(Connection conn, Entity... entities) throws SQLException {
        if (ArrayUtil.isEmpty((Object[]) entities)) {
            throw new DbRuntimeException("Entities for batch insert is empty !");
        }
        SqlBuilder insert = SqlBuilder.create(this.wrapper).insert(entities[0], dialectName());
        Set<String> fields = (Set) CollUtil.filter(entities[0].keySet(), (v0) -> {
            return CharSequenceUtil.isNotBlank(v0);
        });
        return StatementUtil.prepareStatementForBatch(conn, insert.build(), fields, entities);
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForDelete(Connection conn, Query query) throws SQLException {
        Assert.notNull(query, "query must be not null !", new Object[0]);
        Condition[] where = query.getWhere();
        if (ArrayUtil.isEmpty((Object[]) where)) {
            throw new SQLException("No 'WHERE' condition, we can't prepared statement for delete everything.");
        }
        SqlBuilder delete = SqlBuilder.create(this.wrapper).delete(query.getFirstTableName()).where(where);
        return StatementUtil.prepareStatement(conn, delete);
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForUpdate(Connection conn, Entity entity, Query query) throws SQLException {
        Assert.notNull(query, "query must be not null !", new Object[0]);
        Condition[] where = query.getWhere();
        if (ArrayUtil.isEmpty((Object[]) where)) {
            throw new SQLException("No 'WHERE' condition, we can't prepare statement for update everything.");
        }
        SqlBuilder update = SqlBuilder.create(this.wrapper).update(entity).where(where);
        return StatementUtil.prepareStatement(conn, update);
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForFind(Connection conn, Query query) throws SQLException {
        return psForPage(conn, query);
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForPage(Connection conn, Query query) throws SQLException {
        Assert.notNull(query, "query must be not null !", new Object[0]);
        if (StrUtil.hasBlank(query.getTableNames())) {
            throw new DbRuntimeException("Table name must be not empty !");
        }
        SqlBuilder find = SqlBuilder.create(this.wrapper).query(query);
        return psForPage(conn, find, query.getPage());
    }

    @Override // cn.hutool.db.dialect.Dialect
    public PreparedStatement psForPage(Connection conn, SqlBuilder sqlBuilder, Page page) throws SQLException {
        if (null != page) {
            sqlBuilder = wrapPageSql(sqlBuilder.orderBy(page.getOrders()), page);
        }
        return StatementUtil.prepareStatement(conn, sqlBuilder);
    }

    protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
        return find.append(" limit ").append(Integer.valueOf(page.getPageSize())).append(" offset ").append(Integer.valueOf(page.getStartPosition()));
    }

    @Override // cn.hutool.db.dialect.Dialect
    public String dialectName() {
        return DialectName.ANSI.name();
    }
}
