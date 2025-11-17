package org.jooq.tools.jdbc;

import java.sql.SQLException;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/Mock.class */
public final class Mock {
    public static final MockDataProvider of(int rows) {
        return of(new MockResult(rows, null));
    }

    public static final MockDataProvider of(Record record) {
        return of(result(record));
    }

    public static final MockDataProvider of(Result<?> result) {
        return of(new MockResult(result.size(), result));
    }

    public static final MockDataProvider of(MockResult... result) {
        return ctx -> {
            return result;
        };
    }

    public static final MockDataProvider of(SQLException exception) {
        return of(new MockResult(exception));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Result<?> result(Record data) {
        Result<Record> result = DSL.using(data.configuration()).newResult(data.fields());
        result.add(data);
        return result;
    }

    private Mock() {
    }
}
