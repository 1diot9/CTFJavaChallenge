package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/ValueListHandler.class */
public class ValueListHandler implements RsHandler<List<List<Object>>> {
    private static final long serialVersionUID = 1;

    public static ValueListHandler create() {
        return new ValueListHandler();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.db.handler.RsHandler
    public List<List<Object>> handle(ResultSet rs) throws SQLException {
        ArrayList<List<Object>> result = new ArrayList<>();
        while (rs.next()) {
            result.add(HandleHelper.handleRowToList(rs));
        }
        return result;
    }
}
