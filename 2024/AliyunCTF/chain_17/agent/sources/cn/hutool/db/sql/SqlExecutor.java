package cn.hutool.db.sql;

import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.db.DbUtil;
import cn.hutool.db.StatementUtil;
import cn.hutool.db.handler.RsHandler;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/SqlExecutor.class */
public class SqlExecutor {
    public static int execute(Connection conn, String sql, Map<String, Object> paramMap) throws SQLException {
        NamedSql namedSql = new NamedSql(sql, paramMap);
        return execute(conn, namedSql.getSql(), namedSql.getParams());
    }

    public static int execute(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = StatementUtil.prepareStatement(conn, sql, params);
            int executeUpdate = ps.executeUpdate();
            DbUtil.close(ps);
            return executeUpdate;
        } catch (Throwable th) {
            DbUtil.close(ps);
            throw th;
        }
    }

    public static boolean call(Connection conn, String sql, Object... params) throws SQLException {
        CallableStatement call = null;
        try {
            call = StatementUtil.prepareCall(conn, sql, params);
            boolean execute = call.execute();
            DbUtil.close(call);
            return execute;
        } catch (Throwable th) {
            DbUtil.close(call);
            throw th;
        }
    }

    public static ResultSet callQuery(Connection conn, String sql, Object... params) throws SQLException {
        return StatementUtil.prepareCall(conn, sql, params).executeQuery();
    }

    public static Long executeForGeneratedKey(Connection conn, String sql, Map<String, Object> paramMap) throws SQLException {
        NamedSql namedSql = new NamedSql(sql, paramMap);
        return executeForGeneratedKey(conn, namedSql.getSql(), namedSql.getParams());
    }

    /* JADX WARN: Finally extract failed */
    public static Long executeForGeneratedKey(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = StatementUtil.prepareStatement(conn, sql, params);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                try {
                    Long valueOf = Long.valueOf(rs.getLong(1));
                    DbUtil.close(ps);
                    DbUtil.close(rs);
                    return valueOf;
                } catch (SQLException e) {
                }
            }
            DbUtil.close(ps);
            DbUtil.close(rs);
            return null;
        } catch (Throwable th) {
            DbUtil.close(ps);
            DbUtil.close(rs);
            throw th;
        }
    }

    @Deprecated
    public static int[] executeBatch(Connection conn, String sql, Object[]... paramsBatch) throws SQLException {
        return executeBatch(conn, sql, new ArrayIter((Object[]) paramsBatch));
    }

    public static int[] executeBatch(Connection conn, String sql, Iterable<Object[]> paramsBatch) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = StatementUtil.prepareStatementForBatch(conn, sql, paramsBatch);
            int[] executeBatch = ps.executeBatch();
            DbUtil.close(ps);
            return executeBatch;
        } catch (Throwable th) {
            DbUtil.close(ps);
            throw th;
        }
    }

    public static int[] executeBatch(Connection conn, String... sqls) throws SQLException {
        return executeBatch(conn, new ArrayIter((Object[]) sqls));
    }

    public static int[] executeBatch(Connection conn, Iterable<String> sqls) throws SQLException {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            for (String sql : sqls) {
                statement.addBatch(sql);
            }
            int[] executeBatch = statement.executeBatch();
            DbUtil.close(statement);
            return executeBatch;
        } catch (Throwable th) {
            DbUtil.close(statement);
            throw th;
        }
    }

    public static <T> T query(Connection connection, String str, RsHandler<T> rsHandler, Map<String, Object> map) throws SQLException {
        NamedSql namedSql = new NamedSql(str, map);
        return (T) query(connection, namedSql.getSql(), rsHandler, namedSql.getParams());
    }

    public static <T> T query(Connection connection, SqlBuilder sqlBuilder, RsHandler<T> rsHandler) throws SQLException {
        return (T) query(connection, sqlBuilder.build(), rsHandler, sqlBuilder.getParamValueArray());
    }

    public static <T> T query(Connection connection, String str, RsHandler<T> rsHandler, Object... objArr) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = StatementUtil.prepareStatement(connection, str, objArr);
            T t = (T) executeQuery(preparedStatement, rsHandler);
            DbUtil.close(preparedStatement);
            return t;
        } catch (Throwable th) {
            DbUtil.close(preparedStatement);
            throw th;
        }
    }

    public static <T> T query(Connection connection, Func1<Connection, PreparedStatement> func1, RsHandler<T> rsHandler) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            try {
                preparedStatement = func1.call(connection);
                T t = (T) executeQuery(preparedStatement, rsHandler);
                DbUtil.close(preparedStatement);
                return t;
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw ((SQLException) e);
                }
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtil.close(preparedStatement);
            throw th;
        }
    }

    public static int executeUpdate(PreparedStatement ps, Object... params) throws SQLException {
        StatementUtil.fillParams(ps, params);
        return ps.executeUpdate();
    }

    public static boolean execute(PreparedStatement ps, Object... params) throws SQLException {
        StatementUtil.fillParams(ps, params);
        return ps.execute();
    }

    public static <T> T query(PreparedStatement preparedStatement, RsHandler<T> rsHandler, Object... objArr) throws SQLException {
        StatementUtil.fillParams(preparedStatement, objArr);
        return (T) executeQuery(preparedStatement, rsHandler);
    }

    public static <T> T queryAndClosePs(PreparedStatement preparedStatement, RsHandler<T> rsHandler, Object... objArr) throws SQLException {
        try {
            T t = (T) query(preparedStatement, rsHandler, objArr);
            DbUtil.close(preparedStatement);
            return t;
        } catch (Throwable th) {
            DbUtil.close(preparedStatement);
            throw th;
        }
    }

    private static <T> T executeQuery(PreparedStatement ps, RsHandler<T> rsh) throws SQLException {
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            T handle = rsh.handle(rs);
            DbUtil.close(rs);
            return handle;
        } catch (Throwable th) {
            DbUtil.close(rs);
            throw th;
        }
    }
}
