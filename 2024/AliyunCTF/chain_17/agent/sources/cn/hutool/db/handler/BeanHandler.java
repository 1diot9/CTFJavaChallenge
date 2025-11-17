package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/BeanHandler.class */
public class BeanHandler<E> implements RsHandler<E> {
    private static final long serialVersionUID = -5491214744966544475L;
    private final Class<E> elementBeanType;

    public static <E> BeanHandler<E> create(Class<E> beanType) {
        return new BeanHandler<>(beanType);
    }

    public BeanHandler(Class<E> beanType) {
        this.elementBeanType = beanType;
    }

    @Override // cn.hutool.db.handler.RsHandler
    public E handle(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        if (resultSet.next()) {
            return (E) HandleHelper.handleRow(columnCount, metaData, resultSet, (Class) this.elementBeanType);
        }
        return null;
    }
}
