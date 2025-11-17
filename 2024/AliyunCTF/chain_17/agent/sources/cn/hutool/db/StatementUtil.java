package cn.hutool.db;

import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.handler.HandleHelper;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.NamedSql;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlLog;
import cn.hutool.db.sql.SqlUtil;
import java.lang.invoke.SerializedLambda;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/StatementUtil.class */
public class StatementUtil {
    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 658403463:
                if (implMethodName.equals("lambda$getGeneratedKeyOfLong$d32a099d$1")) {
                    z = true;
                    break;
                }
                break;
            case 2055075563:
                if (implMethodName.equals("handleRowToList")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/db/handler/RsHandler") && lambda.getFunctionalInterfaceMethodName().equals("handle") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/db/handler/HandleHelper") && lambda.getImplMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/util/List;")) {
                    return HandleHelper::handleRowToList;
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/db/handler/RsHandler") && lambda.getFunctionalInterfaceMethodName().equals("handle") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/db/StatementUtil") && lambda.getImplMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Long;")) {
                    return rs -> {
                        Long generatedKey = null;
                        if (rs != null && rs.next()) {
                            try {
                                generatedKey = Long.valueOf(rs.getLong(1));
                            } catch (SQLException e) {
                            }
                        }
                        return generatedKey;
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static PreparedStatement fillParams(PreparedStatement ps, Object... params) throws SQLException {
        if (ArrayUtil.isEmpty(params)) {
            return ps;
        }
        return fillParams(ps, new ArrayIter(params));
    }

    public static PreparedStatement fillParams(PreparedStatement ps, Iterable<?> params) throws SQLException {
        return fillParams(ps, params, null);
    }

    public static PreparedStatement fillParams(PreparedStatement ps, Iterable<?> params, Map<Integer, Integer> nullTypeCache) throws SQLException {
        if (null == params) {
            return ps;
        }
        int paramIndex = 1;
        for (Object param : params) {
            int i = paramIndex;
            paramIndex++;
            setParam(ps, i, param, nullTypeCache);
        }
        return ps;
    }

    public static PreparedStatement prepareStatement(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
        return prepareStatement(conn, sqlBuilder.build(), sqlBuilder.getParamValueArray());
    }

    public static PreparedStatement prepareStatement(Connection conn, String sql, Collection<Object> params) throws SQLException {
        return prepareStatement(conn, sql, params.toArray(new Object[0]));
    }

    public static PreparedStatement prepareStatement(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps;
        Assert.notBlank(sql, "Sql String must be not blank!", new Object[0]);
        String sql2 = sql.trim();
        if (ArrayUtil.isNotEmpty(params) && 1 == params.length && (params[0] instanceof Map)) {
            NamedSql namedSql = new NamedSql(sql2, Convert.toMap(String.class, Object.class, params[0]));
            sql2 = namedSql.getSql();
            params = namedSql.getParams();
        }
        SqlLog.INSTANCE.log(sql2, ArrayUtil.isEmpty(params) ? null : params);
        if (GlobalDbConfig.returnGeneratedKey && StrUtil.startWithIgnoreCase(sql2, "insert")) {
            ps = conn.prepareStatement(sql2, 1);
        } else {
            ps = conn.prepareStatement(sql2);
        }
        return fillParams(ps, params);
    }

    public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Object[]... paramsBatch) throws SQLException {
        return prepareStatementForBatch(conn, sql, new ArrayIter((Object[]) paramsBatch));
    }

    public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Iterable<Object[]> paramsBatch) throws SQLException {
        Assert.notBlank(sql, "Sql String must be not blank!", new Object[0]);
        String sql2 = sql.trim();
        SqlLog.INSTANCE.log(sql2, paramsBatch);
        PreparedStatement ps = conn.prepareStatement(sql2);
        Map<Integer, Integer> nullTypeMap = new HashMap<>();
        for (Object[] params : paramsBatch) {
            fillParams(ps, new ArrayIter(params), nullTypeMap);
            ps.addBatch();
        }
        return ps;
    }

    public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Iterable<String> fields, Entity... entities) throws SQLException {
        Assert.notBlank(sql, "Sql String must be not blank!", new Object[0]);
        String sql2 = sql.trim();
        SqlLog.INSTANCE.logForBatch(sql2);
        PreparedStatement ps = conn.prepareStatement(sql2);
        Map<Integer, Integer> nullTypeMap = new HashMap<>();
        for (Entity entity : entities) {
            fillParams(ps, CollUtil.valuesOfKeys(entity, fields), nullTypeMap);
            ps.addBatch();
        }
        return ps;
    }

    public static CallableStatement prepareCall(Connection conn, String sql, Object... params) throws SQLException {
        Assert.notBlank(sql, "Sql String must be not blank!", new Object[0]);
        String sql2 = sql.trim();
        SqlLog.INSTANCE.log(sql2, params);
        CallableStatement call = conn.prepareCall(sql2);
        fillParams(call, params);
        return call;
    }

    public static Long getGeneratedKeyOfLong(Statement ps) throws SQLException {
        return (Long) getGeneratedKeys(ps, rs -> {
            Long generatedKey = null;
            if (rs != null && rs.next()) {
                try {
                    generatedKey = Long.valueOf(rs.getLong(1));
                } catch (SQLException e) {
                }
            }
            return generatedKey;
        });
    }

    public static List<Object> getGeneratedKeys(Statement ps) throws SQLException {
        return (List) getGeneratedKeys(ps, HandleHelper::handleRowToList);
    }

    public static <T> T getGeneratedKeys(Statement statement, RsHandler<T> rsHandler) throws SQLException {
        ResultSet rs = statement.getGeneratedKeys();
        Throwable th = null;
        try {
            try {
                T handle = rsHandler.handle(rs);
                if (rs != null) {
                    if (0 != 0) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        rs.close();
                    }
                }
                return handle;
            } finally {
            }
        } catch (Throwable th3) {
            if (rs != null) {
                if (th != null) {
                    try {
                        rs.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    rs.close();
                }
            }
            throw th3;
        }
    }

    public static int getTypeOfNull(PreparedStatement ps, int paramIndex) {
        int sqlType = 12;
        try {
            ParameterMetaData pmd = ps.getParameterMetaData();
            sqlType = pmd.getParameterType(paramIndex);
        } catch (SQLException e) {
        }
        return sqlType;
    }

    public static void setParam(PreparedStatement ps, int paramIndex, Object param) throws SQLException {
        setParam(ps, paramIndex, param, null);
    }

    private static void setParam(PreparedStatement ps, int paramIndex, Object param, Map<Integer, Integer> nullTypeCache) throws SQLException {
        if (null == param) {
            Integer type = null == nullTypeCache ? null : nullTypeCache.get(Integer.valueOf(paramIndex));
            if (null == type) {
                type = Integer.valueOf(getTypeOfNull(ps, paramIndex));
                if (null != nullTypeCache) {
                    nullTypeCache.put(Integer.valueOf(paramIndex), type);
                }
            }
            ps.setNull(paramIndex, type.intValue());
        }
        if (param instanceof Date) {
            if (param instanceof java.sql.Date) {
                ps.setDate(paramIndex, (java.sql.Date) param);
                return;
            } else if (param instanceof Time) {
                ps.setTime(paramIndex, (Time) param);
                return;
            } else {
                ps.setTimestamp(paramIndex, SqlUtil.toSqlTimestamp((Date) param));
                return;
            }
        }
        if (param instanceof Number) {
            if (param instanceof BigDecimal) {
                ps.setBigDecimal(paramIndex, (BigDecimal) param);
                return;
            } else if (param instanceof BigInteger) {
                ps.setBigDecimal(paramIndex, new BigDecimal((BigInteger) param));
                return;
            }
        }
        ps.setObject(paramIndex, param);
    }
}
