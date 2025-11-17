package org.h2.tools;

import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/SimpleRowSource.class */
public interface SimpleRowSource {
    Object[] readRow() throws SQLException;

    void close();

    void reset() throws SQLException;
}
