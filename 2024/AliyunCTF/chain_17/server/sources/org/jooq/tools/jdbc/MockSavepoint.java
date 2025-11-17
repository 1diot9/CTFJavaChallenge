package org.jooq.tools.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockSavepoint.class */
public class MockSavepoint implements Savepoint {
    private final String name;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MockSavepoint() {
        this(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MockSavepoint(String name) {
        this.name = name;
    }

    @Override // java.sql.Savepoint
    public int getSavepointId() throws SQLException {
        return this.name != null ? this.name.hashCode() : hashCode();
    }

    @Override // java.sql.Savepoint
    public String getSavepointName() throws SQLException {
        return this.name != null ? this.name : toString();
    }
}
