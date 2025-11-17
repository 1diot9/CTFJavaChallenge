package org.jooq.tools.jdbc;

import java.sql.SQLException;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockDataProvider.class */
public interface MockDataProvider {
    MockResult[] execute(MockExecuteContext mockExecuteContext) throws SQLException;
}
