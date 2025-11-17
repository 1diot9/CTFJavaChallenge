package org.h2.api;

import java.sql.Connection;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/AggregateFunction.class */
public interface AggregateFunction {
    int getType(int[] iArr) throws SQLException;

    void add(Object obj) throws SQLException;

    Object getResult() throws SQLException;

    default void init(Connection connection) throws SQLException {
    }
}
