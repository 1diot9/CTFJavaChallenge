package cn.hutool.db.handler;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/RsHandler.class */
public interface RsHandler<T> extends Serializable {
    T handle(ResultSet resultSet) throws SQLException;
}
