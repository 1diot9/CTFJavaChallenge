package cn.hutool.db.handler;

import cn.hutool.db.Entity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/EntityListHandler.class */
public class EntityListHandler implements RsHandler<List<Entity>> {
    private static final long serialVersionUID = -2846240126316979895L;
    private final boolean caseInsensitive;

    public static EntityListHandler create() {
        return new EntityListHandler();
    }

    public EntityListHandler() {
        this(false);
    }

    public EntityListHandler(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.db.handler.RsHandler
    public List<Entity> handle(ResultSet rs) throws SQLException {
        return (List) HandleHelper.handleRs(rs, new ArrayList(), this.caseInsensitive);
    }
}
