package cn.hutool.db.handler;

import cn.hutool.db.Entity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/EntitySetHandler.class */
public class EntitySetHandler implements RsHandler<LinkedHashSet<Entity>> {
    private static final long serialVersionUID = 8191723216703506736L;
    private final boolean caseInsensitive;

    public static EntitySetHandler create() {
        return new EntitySetHandler();
    }

    public EntitySetHandler() {
        this(false);
    }

    public EntitySetHandler(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.db.handler.RsHandler
    public LinkedHashSet<Entity> handle(ResultSet rs) throws SQLException {
        return (LinkedHashSet) HandleHelper.handleRs(rs, new LinkedHashSet(), this.caseInsensitive);
    }
}
