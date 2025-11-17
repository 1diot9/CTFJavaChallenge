package cn.hutool.db.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.db.Entity;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/handler/HandleHelper.class */
public class HandleHelper {
    public static <T> T handleRow(int i, ResultSetMetaData resultSetMetaData, ResultSet resultSet, T t) throws SQLException {
        return (T) handleRow(i, resultSetMetaData, resultSet).toBeanIgnoreCase((Entity) t);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v51, types: [T, java.lang.Object[]] */
    public static <T> T handleRow(int i, ResultSetMetaData resultSetMetaData, ResultSet resultSet, Class<T> cls) throws SQLException {
        Assert.notNull(cls, "Bean Class must be not null !", new Object[0]);
        if (cls.isArray()) {
            Class<?> componentType = cls.getComponentType();
            ?? r0 = (T) ArrayUtil.newArray(componentType, i);
            int i2 = 0;
            int i3 = 1;
            while (i2 < i) {
                r0[i2] = getColumnValue(resultSet, i3, resultSetMetaData.getColumnType(i3), componentType);
                i2++;
                i3++;
            }
            return r0;
        }
        if (Iterable.class.isAssignableFrom(cls)) {
            return (T) Convert.convert((Class) cls, handleRow(i, resultSetMetaData, resultSet, Object[].class));
        }
        if (cls.isAssignableFrom(Entity.class)) {
            return (T) handleRow(i, resultSetMetaData, resultSet);
        }
        if (String.class == cls) {
            return (T) StrUtil.join(", ", (Object[]) handleRow(i, resultSetMetaData, resultSet, Object[].class));
        }
        T t = (T) ReflectUtil.newInstanceIfPossible(cls);
        Map<String, PropDesc> propMap = BeanUtil.getBeanDesc(cls).getPropMap(true);
        for (int i4 = 1; i4 <= i; i4++) {
            String columnLabel = resultSetMetaData.getColumnLabel(i4);
            PropDesc propDesc = propMap.get(columnLabel);
            if (null == propDesc) {
                propDesc = propMap.get(StrUtil.toCamelCase(columnLabel));
            }
            Method setter = null == propDesc ? null : propDesc.getSetter();
            if (null != setter) {
                ReflectUtil.invokeWithCheck(t, setter, getColumnValue(resultSet, i4, resultSetMetaData.getColumnType(i4), TypeUtil.getFirstParamType(setter)));
            }
        }
        return t;
    }

    public static Entity handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs) throws SQLException {
        return handleRow(columnCount, meta, rs, false);
    }

    public static Entity handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs, boolean caseInsensitive) throws SQLException {
        return handleRow(new Entity(null, caseInsensitive), columnCount, meta, rs, true);
    }

    public static <T extends Entity> T handleRow(T row, int columnCount, ResultSetMetaData meta, ResultSet rs, boolean withMetaInfo) throws SQLException {
        for (int i = 1; i <= columnCount; i++) {
            int type = meta.getColumnType(i);
            String columnLabel = meta.getColumnLabel(i);
            if (!"rownum_".equalsIgnoreCase(columnLabel)) {
                row.put(columnLabel, getColumnValue(rs, i, type, null));
            }
        }
        if (withMetaInfo) {
            try {
                row.setTableName(meta.getTableName(1));
            } catch (SQLException e) {
            }
            row.setFieldNames(row.keySet());
        }
        return row;
    }

    public static Entity handleRow(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        return handleRow(columnCount, meta, rs);
    }

    public static List<Object> handleRowToList(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        List<Object> row = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            row.add(getColumnValue(rs, i, meta.getColumnType(i), null));
        }
        return row;
    }

    public static <T extends Collection<Entity>> T handleRs(ResultSet resultSet, T t) throws SQLException {
        return (T) handleRs(resultSet, t, false);
    }

    public static <T extends Collection<Entity>> T handleRs(ResultSet rs, T collection, boolean caseInsensitive) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        while (rs.next()) {
            collection.add(handleRow(columnCount, meta, rs, caseInsensitive));
        }
        return collection;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <E, T extends Collection<E>> T handleRsToBeanList(ResultSet rs, T t, Class<E> elementBeanType) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        while (rs.next()) {
            t.add(handleRow(columnCount, meta, rs, (Class) elementBeanType));
        }
        return t;
    }

    private static Object getColumnValue(ResultSet rs, int columnIndex, int type, Type targetColumnType) throws SQLException {
        Object rawValue = null;
        switch (type) {
            case 92:
                rawValue = rs.getTime(columnIndex);
                break;
            case 93:
                try {
                    rawValue = rs.getTimestamp(columnIndex);
                    break;
                } catch (SQLException e) {
                    break;
                }
            default:
                rawValue = rs.getObject(columnIndex);
                break;
        }
        if (null == targetColumnType || Object.class == targetColumnType) {
            return rawValue;
        }
        return Convert.convert(targetColumnType, rawValue);
    }
}
