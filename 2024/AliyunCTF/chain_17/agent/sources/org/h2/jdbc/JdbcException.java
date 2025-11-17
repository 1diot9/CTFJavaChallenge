package org.h2.jdbc;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcException.class */
public interface JdbcException {
    int getErrorCode();

    String getOriginalMessage();

    String getSQL();

    void setSQL(String str);

    String toString();
}
