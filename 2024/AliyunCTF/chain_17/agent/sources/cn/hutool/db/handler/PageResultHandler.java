package cn.hutool.db.handler;

import cn.hutool.db.Entity;
import cn.hutool.db.PageResult;
import java.sql.ResultSet;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/PageResultHandler.class */
public class PageResultHandler implements RsHandler<PageResult<Entity>> {
    private static final long serialVersionUID = -1474161855834070108L;
    private final PageResult<Entity> pageResult;
    private final boolean caseInsensitive;

    public static PageResultHandler create(PageResult<Entity> pageResult) {
        return new PageResultHandler(pageResult);
    }

    public PageResultHandler(PageResult<Entity> pageResult) {
        this(pageResult, false);
    }

    public PageResultHandler(PageResult<Entity> pageResult, boolean caseInsensitive) {
        this.pageResult = pageResult;
        this.caseInsensitive = caseInsensitive;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.db.handler.RsHandler
    public PageResult<Entity> handle(ResultSet rs) throws SQLException {
        return (PageResult) HandleHelper.handleRs(rs, this.pageResult, this.caseInsensitive);
    }
}
