package cn.hutool.db;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectFactory;
import cn.hutool.db.handler.NumberHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.db.sql.SqlUtil;
import cn.hutool.db.sql.Wrapper;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/DialectRunner.class */
public class DialectRunner implements Serializable {
    private static final long serialVersionUID = 1;
    private Dialect dialect;
    protected boolean caseInsensitive;

    public DialectRunner(Dialect dialect) {
        this.caseInsensitive = GlobalDbConfig.caseInsensitive;
        this.dialect = dialect;
    }

    public DialectRunner(String driverClassName) {
        this(DialectFactory.newDialect(driverClassName));
    }

    public int[] insert(Connection conn, Entity... records) throws SQLException {
        checkConn(conn);
        if (ArrayUtil.isEmpty((Object[]) records)) {
            return new int[]{0};
        }
        try {
            if (1 == records.length) {
                PreparedStatement ps = this.dialect.psForInsert(conn, records[0]);
                int[] iArr = {ps.executeUpdate()};
                DbUtil.close(ps);
                return iArr;
            }
            PreparedStatement ps2 = this.dialect.psForInsertBatch(conn, records);
            int[] executeBatch = ps2.executeBatch();
            DbUtil.close(ps2);
            return executeBatch;
        } catch (Throwable th) {
            DbUtil.close(null);
            throw th;
        }
    }

    public int upsert(Connection conn, Entity record, String... keys) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = getDialect().psForUpsert(conn, record, keys);
        } catch (SQLException e) {
        }
        if (null != ps) {
            try {
                int executeUpdate = ps.executeUpdate();
                DbUtil.close(ps);
                return executeUpdate;
            } catch (Throwable th) {
                DbUtil.close(ps);
                throw th;
            }
        }
        return insertOrUpdate(conn, record, keys);
    }

    public int insertOrUpdate(Connection conn, Entity record, String... keys) throws SQLException {
        Entity where = record.filter(keys);
        if (MapUtil.isNotEmpty(where) && count(conn, where) > 0) {
            return update(conn, record, where);
        }
        return insert(conn, record)[0];
    }

    public <T> T insert(Connection connection, Entity entity, RsHandler<T> rsHandler) throws SQLException {
        checkConn(connection);
        if (MapUtil.isEmpty(entity)) {
            throw new SQLException("Empty entity provided!");
        }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.dialect.psForInsert(connection, entity);
            preparedStatement.executeUpdate();
            if (null != rsHandler) {
                T t = (T) StatementUtil.getGeneratedKeys(preparedStatement, rsHandler);
                DbUtil.close(preparedStatement);
                return t;
            }
            DbUtil.close(preparedStatement);
            return null;
        } catch (Throwable th) {
            DbUtil.close(preparedStatement);
            throw th;
        }
    }

    public int del(Connection conn, Entity where) throws SQLException {
        checkConn(conn);
        if (MapUtil.isEmpty(where)) {
            throw new SQLException("Empty entity provided!");
        }
        PreparedStatement ps = null;
        try {
            ps = this.dialect.psForDelete(conn, Query.of(where));
            int executeUpdate = ps.executeUpdate();
            DbUtil.close(ps);
            return executeUpdate;
        } catch (Throwable th) {
            DbUtil.close(ps);
            throw th;
        }
    }

    public int update(Connection conn, Entity record, Entity where) throws SQLException {
        checkConn(conn);
        if (MapUtil.isEmpty(record)) {
            throw new SQLException("Empty entity provided!");
        }
        if (MapUtil.isEmpty(where)) {
            throw new SQLException("Empty where provided!");
        }
        String tableName = record.getTableName();
        if (StrUtil.isBlank(tableName)) {
            tableName = where.getTableName();
            record.setTableName(tableName);
        }
        Query query = new Query(SqlUtil.buildConditions(where), tableName);
        PreparedStatement ps = null;
        try {
            ps = this.dialect.psForUpdate(conn, record, query);
            int executeUpdate = ps.executeUpdate();
            DbUtil.close(ps);
            return executeUpdate;
        } catch (Throwable th) {
            DbUtil.close(ps);
            throw th;
        }
    }

    public <T> T find(Connection connection, Query query, RsHandler<T> rsHandler) throws SQLException {
        checkConn(connection);
        Assert.notNull(query, "[query] is null !", new Object[0]);
        return (T) SqlExecutor.queryAndClosePs(this.dialect.psForFind(connection, query), rsHandler, new Object[0]);
    }

    public long count(Connection conn, Entity where) throws SQLException {
        checkConn(conn);
        return ((Number) SqlExecutor.queryAndClosePs(this.dialect.psForCount(conn, Query.of(where)), new NumberHandler(), new Object[0])).longValue();
    }

    public long count(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
        checkConn(conn);
        String selectSql = sqlBuilder.build();
        int orderByIndex = StrUtil.lastIndexOfIgnoreCase(selectSql, " order by");
        if (orderByIndex > 0) {
            selectSql = StrUtil.subPre(selectSql, orderByIndex);
        }
        return ((Number) SqlExecutor.queryAndClosePs(this.dialect.psForCount(conn, SqlBuilder.of(selectSql).addParams(sqlBuilder.getParamValueArray())), new NumberHandler(), new Object[0])).longValue();
    }

    public <T> T page(Connection connection, Query query, RsHandler<T> rsHandler) throws SQLException {
        checkConn(connection);
        if (null == query.getPage()) {
            return (T) find(connection, query, rsHandler);
        }
        return (T) SqlExecutor.queryAndClosePs(this.dialect.psForPage(connection, query), rsHandler, new Object[0]);
    }

    public <T> T page(Connection connection, SqlBuilder sqlBuilder, Page page, RsHandler<T> rsHandler) throws SQLException {
        checkConn(connection);
        if (null == page) {
            return (T) SqlExecutor.query(connection, sqlBuilder, rsHandler);
        }
        return (T) SqlExecutor.queryAndClosePs(this.dialect.psForPage(connection, sqlBuilder, page), rsHandler, new Object[0]);
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public Dialect getDialect() {
        return this.dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setWrapper(Character wrapperChar) {
        setWrapper(new Wrapper(wrapperChar));
    }

    public void setWrapper(Wrapper wrapper) {
        this.dialect.setWrapper(wrapper);
    }

    private void checkConn(Connection conn) {
        Assert.notNull(conn, "Connection object must be not null!", new Object[0]);
    }
}
