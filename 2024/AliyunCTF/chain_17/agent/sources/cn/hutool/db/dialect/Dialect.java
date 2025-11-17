package cn.hutool.db.dialect;

import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/Dialect.class */
public interface Dialect extends Serializable {
    Wrapper getWrapper();

    void setWrapper(Wrapper wrapper);

    PreparedStatement psForInsert(Connection connection, Entity entity) throws SQLException;

    PreparedStatement psForInsertBatch(Connection connection, Entity... entityArr) throws SQLException;

    PreparedStatement psForDelete(Connection connection, Query query) throws SQLException;

    PreparedStatement psForUpdate(Connection connection, Entity entity, Query query) throws SQLException;

    PreparedStatement psForFind(Connection connection, Query query) throws SQLException;

    PreparedStatement psForPage(Connection connection, Query query) throws SQLException;

    PreparedStatement psForPage(Connection connection, SqlBuilder sqlBuilder, Page page) throws SQLException;

    String dialectName();

    default PreparedStatement psForCount(Connection conn, Query query) throws SQLException {
        return psForCount(conn, SqlBuilder.create().query(query));
    }

    default PreparedStatement psForCount(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
        return psForPage(conn, sqlBuilder.insertPreFragment("SELECT count(1) from(").append(") hutool_alias_count_"), null);
    }

    default PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
        throw new SQLException("Unsupported upsert operation of " + dialectName());
    }
}
