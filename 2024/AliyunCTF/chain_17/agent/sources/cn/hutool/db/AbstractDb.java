package cn.hutool.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.handler.BeanListHandler;
import cn.hutool.db.handler.EntityHandler;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.handler.HandleHelper;
import cn.hutool.db.handler.NumberHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.handler.StringHandler;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.db.sql.SqlUtil;
import cn.hutool.db.sql.Wrapper;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/AbstractDb.class */
public abstract class AbstractDb implements Serializable {
    private static final long serialVersionUID = 3858951941916349062L;
    protected final DataSource ds;
    protected Boolean isSupportTransaction = null;
    protected boolean caseInsensitive = GlobalDbConfig.caseInsensitive;
    protected SqlConnRunner runner;

    public abstract Connection getConnection() throws SQLException;

    public abstract void closeConnection(Connection connection);

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 1356016999:
                if (implMethodName.equals("lambda$page$f510ff1e$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 7 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/db/handler/RsHandler") && lambda.getFunctionalInterfaceMethodName().equals("handle") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/db/AbstractDb") && lambda.getImplMethodSignature().equals("(Lcn/hutool/db/Page;Ljava/lang/CharSequence;[Ljava/lang/Object;Ljava/lang/Class;Ljava/sql/ResultSet;)Lcn/hutool/db/PageResult;")) {
                    AbstractDb abstractDb = (AbstractDb) lambda.getCapturedArg(0);
                    Page page = (Page) lambda.getCapturedArg(1);
                    CharSequence charSequence = (CharSequence) lambda.getCapturedArg(2);
                    Object[] objArr = (Object[]) lambda.getCapturedArg(3);
                    Class cls = (Class) lambda.getCapturedArg(4);
                    return rs -> {
                        return (PageResult) HandleHelper.handleRsToBeanList(rs, new PageResult(page.getPageNumber(), page.getPageSize(), (int) count(charSequence, objArr)), cls);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public AbstractDb(DataSource ds, Dialect dialect) {
        this.ds = ds;
        this.runner = new SqlConnRunner(dialect);
    }

    public List<Entity> query(String sql, Map<String, Object> params) throws SQLException {
        return (List) query(sql, new EntityListHandler(this.caseInsensitive), params);
    }

    public List<Entity> query(String sql, Object... params) throws SQLException {
        return (List) query(sql, new EntityListHandler(this.caseInsensitive), params);
    }

    public <T> List<T> query(String sql, Class<T> beanClass, Object... params) throws SQLException {
        return (List) query(sql, new BeanListHandler(beanClass), params);
    }

    public Entity queryOne(String sql, Object... params) throws SQLException {
        return (Entity) query(sql, new EntityHandler(this.caseInsensitive), params);
    }

    public Number queryNumber(String sql, Object... params) throws SQLException {
        return (Number) query(sql, new NumberHandler(), params);
    }

    public String queryString(String sql, Object... params) throws SQLException {
        return (String) query(sql, new StringHandler(), params);
    }

    public <T> T query(String str, RsHandler<T> rsHandler, Object... objArr) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) SqlExecutor.query(connection, str, rsHandler, objArr);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public <T> T query(String str, RsHandler<T> rsHandler, Map<String, Object> map) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) SqlExecutor.query(connection, str, rsHandler, map);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public <T> T query(Func1<Connection, PreparedStatement> func1, RsHandler<T> rsHandler) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) SqlExecutor.query(connection, func1, rsHandler);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public int execute(String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int execute = SqlExecutor.execute(conn, sql, params);
            closeConnection(conn);
            return execute;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public Long executeForGeneratedKey(String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            Long executeForGeneratedKey = SqlExecutor.executeForGeneratedKey(conn, sql, params);
            closeConnection(conn);
            return executeForGeneratedKey;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    @Deprecated
    public int[] executeBatch(String sql, Object[]... paramsBatch) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int[] executeBatch = SqlExecutor.executeBatch(conn, sql, paramsBatch);
            closeConnection(conn);
            return executeBatch;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int[] executeBatch(String sql, Iterable<Object[]> paramsBatch) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int[] executeBatch = SqlExecutor.executeBatch(conn, sql, paramsBatch);
            closeConnection(conn);
            return executeBatch;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int[] executeBatch(String... sqls) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int[] executeBatch = SqlExecutor.executeBatch(conn, sqls);
            closeConnection(conn);
            return executeBatch;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int[] executeBatch(Iterable<String> sqls) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int[] executeBatch = SqlExecutor.executeBatch(conn, sqls);
            closeConnection(conn);
            return executeBatch;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int insert(Entity record) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int insert = this.runner.insert(conn, record);
            closeConnection(conn);
            return insert;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int insertOrUpdate(Entity record, String... keys) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int insertOrUpdate = this.runner.insertOrUpdate(conn, record, keys);
            closeConnection(conn);
            return insertOrUpdate;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int upsert(Entity record, String... keys) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int upsert = this.runner.upsert(conn, record, keys);
            closeConnection(conn);
            return upsert;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int[] insert(Collection<Entity> records) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int[] insert = this.runner.insert(conn, records);
            closeConnection(conn);
            return insert;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public List<Object> insertForGeneratedKeys(Entity record) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            List<Object> insertForGeneratedKeys = this.runner.insertForGeneratedKeys(conn, record);
            closeConnection(conn);
            return insertForGeneratedKeys;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public Long insertForGeneratedKey(Entity record) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            Long insertForGeneratedKey = this.runner.insertForGeneratedKey(conn, record);
            closeConnection(conn);
            return insertForGeneratedKey;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int del(String tableName, String field, Object value) throws SQLException {
        return del(Entity.create(tableName).set(field, value));
    }

    public int del(Entity where) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int del = this.runner.del(conn, where);
            closeConnection(conn);
            return del;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public int update(Entity record, Entity where) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            int update = this.runner.update(conn, record, where);
            closeConnection(conn);
            return update;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public <T> Entity get(String tableName, String field, T value) throws SQLException {
        return get(Entity.create(tableName).set(field, (Object) value));
    }

    public Entity get(Entity where) throws SQLException {
        return (Entity) find(where.getFieldNames(), where, new EntityHandler(this.caseInsensitive));
    }

    public <T> T find(Collection<String> collection, Entity entity, RsHandler<T> rsHandler) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) this.runner.find(connection, collection, entity, rsHandler);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public List<Entity> find(Collection<String> fields, Entity where) throws SQLException {
        return (List) find(fields, where, new EntityListHandler(this.caseInsensitive));
    }

    public <T> T find(Query query, RsHandler<T> rsHandler) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) this.runner.find(connection, query, rsHandler);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public <T> T find(Entity entity, RsHandler<T> rsHandler, String... strArr) throws SQLException {
        return (T) find(CollUtil.newArrayList(strArr), entity, rsHandler);
    }

    public List<Entity> find(Entity where) throws SQLException {
        return (List) find(where.getFieldNames(), where, new EntityListHandler(this.caseInsensitive));
    }

    public <T> List<T> find(Entity where, Class<T> beanClass) throws SQLException {
        return (List) find(where.getFieldNames(), where, BeanListHandler.create(beanClass));
    }

    public List<Entity> findAll(Entity where) throws SQLException {
        return (List) find(where, EntityListHandler.create(), new String[0]);
    }

    public <T> List<T> findAll(Entity where, Class<T> beanClass) throws SQLException {
        return (List) find(where, BeanListHandler.create(beanClass), new String[0]);
    }

    public List<Entity> findAll(String tableName) throws SQLException {
        return findAll(Entity.create(tableName));
    }

    public List<Entity> findBy(String tableName, String field, Object value) throws SQLException {
        return findAll(Entity.create(tableName).set(field, value));
    }

    public List<Entity> findBy(String tableName, Condition... wheres) throws SQLException {
        Query query = new Query(wheres, tableName);
        return (List) find(query, new EntityListHandler(this.caseInsensitive));
    }

    public List<Entity> findLike(String tableName, String field, String value, Condition.LikeType likeType) throws SQLException {
        return findAll(Entity.create(tableName).set(field, (Object) SqlUtil.buildLikeValue(value, likeType, true)));
    }

    public long count(Entity where) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            long count = this.runner.count(conn, where);
            closeConnection(conn);
            return count;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public long count(SqlBuilder sql) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            long count = this.runner.count(conn, sql);
            closeConnection(conn);
            return count;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public long count(CharSequence selectSql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            long count = this.runner.count(conn, selectSql, params);
            closeConnection(conn);
            return count;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public <T> T page(Collection<String> collection, Entity entity, int i, int i2, RsHandler<T> rsHandler) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) this.runner.page(connection, collection, entity, i, i2, rsHandler);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public <T> T page(Entity entity, int i, int i2, RsHandler<T> rsHandler) throws SQLException {
        return (T) page(entity, new Page(i, i2), rsHandler);
    }

    public List<Entity> pageForEntityList(Entity where, int page, int numPerPage) throws SQLException {
        return pageForEntityList(where, new Page(page, numPerPage));
    }

    public List<Entity> pageForEntityList(Entity where, Page page) throws SQLException {
        return (List) page(where, page, new EntityListHandler(this.caseInsensitive));
    }

    public <T> T page(Entity entity, Page page, RsHandler<T> rsHandler) throws SQLException {
        return (T) page(entity.getFieldNames(), entity, page, rsHandler);
    }

    public <T> T page(Collection<String> collection, Entity entity, Page page, RsHandler<T> rsHandler) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) this.runner.page(connection, collection, entity, page, rsHandler);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public <T> T page(CharSequence charSequence, Page page, RsHandler<T> rsHandler, Object... objArr) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) this.runner.page(connection, SqlBuilder.of(charSequence).addParams(objArr), page, rsHandler);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public <T> PageResult<T> page(CharSequence sql, Page page, Class<T> elementBeanType, Object... params) throws SQLException {
        return (PageResult) page(sql, page, rs -> {
            return (PageResult) HandleHelper.handleRsToBeanList(rs, new PageResult(page.getPageNumber(), page.getPageSize(), (int) count(sql, params)), elementBeanType);
        }, params);
    }

    public <T> T page(SqlBuilder sqlBuilder, Page page, RsHandler<T> rsHandler) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            T t = (T) this.runner.page(connection, sqlBuilder, page, rsHandler);
            closeConnection(connection);
            return t;
        } catch (Throwable th) {
            closeConnection(connection);
            throw th;
        }
    }

    public PageResult<Entity> page(CharSequence sql, Page page, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            PageResult<Entity> page2 = this.runner.page(conn, SqlBuilder.of(sql).addParams(params), page);
            closeConnection(conn);
            return page2;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public PageResult<Entity> page(Collection<String> fields, Entity where, int pageNumber, int pageSize) throws SQLException {
        return page(fields, where, new Page(pageNumber, pageSize));
    }

    public PageResult<Entity> page(Collection<String> fields, Entity where, Page page) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            PageResult<Entity> page2 = this.runner.page(conn, fields, where, page);
            closeConnection(conn);
            return page2;
        } catch (Throwable th) {
            closeConnection(conn);
            throw th;
        }
    }

    public PageResult<Entity> page(Entity where, int page, int numPerPage) throws SQLException {
        return page(where, new Page(page, numPerPage));
    }

    public PageResult<Entity> page(Entity where, Page page) throws SQLException {
        return page(where.getFieldNames(), where, page);
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public SqlConnRunner getRunner() {
        return this.runner;
    }

    public void setRunner(SqlConnRunner runner) {
        this.runner = runner;
    }

    public AbstractDb setWrapper(Character wrapperChar) {
        return setWrapper(new Wrapper(wrapperChar));
    }

    public AbstractDb setWrapper(Wrapper wrapper) {
        this.runner.setWrapper(wrapper);
        return this;
    }

    public AbstractDb disableWrapper() {
        return setWrapper((Wrapper) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkTransactionSupported(Connection conn) throws SQLException, DbRuntimeException {
        if (null == this.isSupportTransaction) {
            this.isSupportTransaction = Boolean.valueOf(conn.getMetaData().supportsTransactions());
        }
        if (false == this.isSupportTransaction.booleanValue()) {
            throw new DbRuntimeException("Transaction not supported for current database!");
        }
    }
}
