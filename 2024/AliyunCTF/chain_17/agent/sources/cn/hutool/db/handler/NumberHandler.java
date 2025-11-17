package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/NumberHandler.class */
public class NumberHandler implements RsHandler<Number> {
    private static final long serialVersionUID = 4081498054379705596L;

    public static NumberHandler create() {
        return new NumberHandler();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.db.handler.RsHandler
    public Number handle(ResultSet rs) throws SQLException {
        if (null == rs || !rs.next()) {
            return null;
        }
        return rs.getBigDecimal(1);
    }
}
