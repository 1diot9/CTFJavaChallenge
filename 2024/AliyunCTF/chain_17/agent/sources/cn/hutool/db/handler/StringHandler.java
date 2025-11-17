package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/StringHandler.class */
public class StringHandler implements RsHandler<String> {
    private static final long serialVersionUID = -5296733366845720383L;

    public static StringHandler create() {
        return new StringHandler();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.db.handler.RsHandler
    public String handle(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rs.getString(1);
        }
        return null;
    }
}
