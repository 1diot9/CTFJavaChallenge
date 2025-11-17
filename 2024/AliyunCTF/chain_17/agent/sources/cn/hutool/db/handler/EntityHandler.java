package cn.hutool.db.handler;

import cn.hutool.db.Entity;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/EntityHandler.class */
public class EntityHandler implements RsHandler<Entity> {
    private static final long serialVersionUID = -8742432871908355992L;
    private final boolean caseInsensitive;

    public static EntityHandler create() {
        return new EntityHandler();
    }

    public EntityHandler() {
        this(false);
    }

    public EntityHandler(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.db.handler.RsHandler
    public Entity handle(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        if (rs.next()) {
            return HandleHelper.handleRow(columnCount, meta, rs, this.caseInsensitive);
        }
        return null;
    }
}
