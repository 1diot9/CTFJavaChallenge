package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/BeanListHandler.class */
public class BeanListHandler<E> implements RsHandler<List<E>> {
    private static final long serialVersionUID = 4510569754766197707L;
    private final Class<E> elementBeanType;

    public static <E> BeanListHandler<E> create(Class<E> beanType) {
        return new BeanListHandler<>(beanType);
    }

    public BeanListHandler(Class<E> beanType) {
        this.elementBeanType = beanType;
    }

    @Override // cn.hutool.db.handler.RsHandler
    public List<E> handle(ResultSet rs) throws SQLException {
        return (List) HandleHelper.handleRsToBeanList(rs, new ArrayList(), this.elementBeanType);
    }
}
