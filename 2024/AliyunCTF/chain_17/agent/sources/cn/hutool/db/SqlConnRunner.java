package cn.hutool.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectFactory;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.handler.HandleHelper;
import cn.hutool.db.handler.PageResultHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlUtil;
import java.lang.invoke.SerializedLambda;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/SqlConnRunner.class */
public class SqlConnRunner extends DialectRunner {
    private static final long serialVersionUID = 1;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 1895216862:
                if (implMethodName.equals("lambda$insertForGeneratedKey$2dfcceed$1")) {
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
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/db/handler/RsHandler") && lambda.getFunctionalInterfaceMethodName().equals("handle") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/db/SqlConnRunner") && lambda.getImplMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Long;")) {
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

    public static SqlConnRunner create(Dialect dialect) {
        return new SqlConnRunner(dialect);
    }

    public static SqlConnRunner create(DataSource ds) {
        return new SqlConnRunner(DialectFactory.getDialect(ds));
    }

    public static SqlConnRunner create(String driverClassName) {
        return new SqlConnRunner(driverClassName);
    }

    public SqlConnRunner(Dialect dialect) {
        super(dialect);
    }

    public SqlConnRunner(String driverClassName) {
        super(driverClassName);
    }

    public int[] insert(Connection conn, Collection<Entity> records) throws SQLException {
        return insert(conn, (Entity[]) records.toArray(new Entity[0]));
    }

    public int insert(Connection conn, Entity record) throws SQLException {
        return insert(conn, record)[0];
    }

    public List<Object> insertForGeneratedKeys(Connection conn, Entity record) throws SQLException {
        return (List) insert(conn, record, HandleHelper::handleRowToList);
    }

    public Long insertForGeneratedKey(Connection conn, Entity record) throws SQLException {
        return (Long) insert(conn, record, rs -> {
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

    public <T> T find(Connection connection, Collection<String> collection, Entity entity, RsHandler<T> rsHandler) throws SQLException {
        return (T) find(connection, Query.of(entity).setFields(collection), rsHandler);
    }

    public <T> T find(Connection connection, Entity entity, RsHandler<T> rsHandler, String... strArr) throws SQLException {
        return (T) find(connection, CollUtil.newArrayList(strArr), entity, rsHandler);
    }

    public List<Entity> find(Connection conn, Entity where) throws SQLException {
        return (List) find(conn, where.getFieldNames(), where, new EntityListHandler(this.caseInsensitive));
    }

    public List<Entity> findAll(Connection conn, Entity where) throws SQLException {
        return (List) find(conn, where, new EntityListHandler(this.caseInsensitive), new String[0]);
    }

    public List<Entity> findAll(Connection conn, String tableName) throws SQLException {
        return findAll(conn, Entity.create(tableName));
    }

    public List<Entity> findBy(Connection conn, String tableName, String field, Object value) throws SQLException {
        return findAll(conn, Entity.create(tableName).set(field, value));
    }

    public List<Entity> findLike(Connection conn, String tableName, String field, String value, Condition.LikeType likeType) throws SQLException {
        return findAll(conn, Entity.create(tableName).set(field, (Object) SqlUtil.buildLikeValue(value, likeType, true)));
    }

    public List<Entity> findIn(Connection conn, String tableName, String field, Object... values) throws SQLException {
        return findAll(conn, Entity.create(tableName).set(field, (Object) values));
    }

    public long count(Connection conn, CharSequence selectSql, Object... params) throws SQLException {
        return count(conn, SqlBuilder.of(selectSql).addParams(params));
    }

    public <T> T page(Connection connection, Collection<String> collection, Entity entity, int i, int i2, RsHandler<T> rsHandler) throws SQLException {
        return (T) page(connection, Query.of(entity).setFields(collection).setPage(new Page(i, i2)), rsHandler);
    }

    public PageResult<Entity> page(Connection conn, SqlBuilder sqlBuilder, Page page) throws SQLException {
        PageResultHandler pageResultHandler = new PageResultHandler(new PageResult(page.getPageNumber(), page.getPageSize(), (int) count(conn, sqlBuilder)), this.caseInsensitive);
        return (PageResult) page(conn, sqlBuilder, page, pageResultHandler);
    }

    public PageResult<Entity> page(Connection conn, Collection<String> fields, Entity where, int page, int numPerPage) throws SQLException {
        return page(conn, fields, where, new Page(page, numPerPage));
    }

    public PageResult<Entity> page(Connection conn, Entity where, Page page) throws SQLException {
        return page(conn, (Collection<String>) null, where, page);
    }

    public PageResult<Entity> page(Connection conn, Collection<String> fields, Entity where, Page page) throws SQLException {
        PageResultHandler pageResultHandler = new PageResultHandler(new PageResult(page.getPageNumber(), page.getPageSize(), (int) count(conn, where)), this.caseInsensitive);
        return (PageResult) page(conn, fields, where, page, pageResultHandler);
    }

    public <T> T page(Connection connection, Collection<String> collection, Entity entity, Page page, RsHandler<T> rsHandler) throws SQLException {
        return (T) page(connection, Query.of(entity).setFields(collection).setPage(page), rsHandler);
    }
}
