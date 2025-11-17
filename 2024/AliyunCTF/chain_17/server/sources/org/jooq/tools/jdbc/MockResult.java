package org.jooq.tools.jdbc;

import java.sql.SQLException;
import org.jooq.Record;
import org.jooq.Result;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockResult.class */
public class MockResult {
    public final int rows;
    public final Result<?> data;
    public final SQLException exception;

    public MockResult() {
        this(-1, null);
    }

    public MockResult(int rows) {
        this(rows, null);
    }

    public MockResult(Record data) {
        this(1, Mock.result(data));
    }

    public MockResult(int rows, Result<?> data) {
        this.rows = rows;
        this.data = data;
        this.exception = null;
    }

    public MockResult(SQLException exception) {
        this.rows = -1;
        this.data = null;
        this.exception = exception;
    }

    public String toString() {
        if (this.exception != null) {
            return "Exception : " + this.exception.getMessage();
        }
        if (this.data != null) {
            return this.data.toString();
        }
        return this.rows;
    }
}
