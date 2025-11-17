package cn.hutool.db.meta;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.BeanFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/meta/MetaUtil.class */
public class MetaUtil {
    public static List<String> getTables(DataSource ds) {
        return getTables(ds, TableType.TABLE);
    }

    public static List<String> getTables(DataSource ds, TableType... types) {
        return getTables(ds, null, null, types);
    }

    public static List<String> getTables(DataSource ds, String schema, TableType... types) {
        return getTables(ds, schema, null, types);
    }

    public static List<String> getTables(DataSource ds, String schema, String tableName, TableType... types) {
        List<String> tables = new ArrayList<>();
        try {
            try {
                Connection conn = ds.getConnection();
                String catalog = getCatalog(conn);
                if (null == schema) {
                    schema = getSchema(conn);
                }
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rs = metaData.getTables(catalog, schema, tableName, Convert.toStrArray(types));
                Throwable th = null;
                if (null != rs) {
                    while (rs.next()) {
                        try {
                            try {
                                String table = rs.getString("TABLE_NAME");
                                if (StrUtil.isNotBlank(table)) {
                                    tables.add(table);
                                }
                            } catch (Throwable th2) {
                                if (rs != null) {
                                    if (th != null) {
                                        try {
                                            rs.close();
                                        } catch (Throwable th3) {
                                            th.addSuppressed(th3);
                                        }
                                    } else {
                                        rs.close();
                                    }
                                }
                                throw th2;
                            }
                        } finally {
                        }
                    }
                }
                if (rs != null) {
                    if (0 != 0) {
                        try {
                            rs.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        rs.close();
                    }
                }
                DbUtil.close(conn);
                return tables;
            } catch (Exception e) {
                throw new DbRuntimeException("Get tables error!", e);
            }
        } catch (Throwable th5) {
            DbUtil.close(null);
            throw th5;
        }
    }

    public static String[] getColumnNames(ResultSet rs) throws DbRuntimeException {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            String[] labelNames = new String[columnCount];
            for (int i = 0; i < labelNames.length; i++) {
                labelNames[i] = rsmd.getColumnLabel(i + 1);
            }
            return labelNames;
        } catch (Exception e) {
            throw new DbRuntimeException("Get colunms error!", e);
        }
    }

    public static String[] getColumnNames(DataSource ds, String tableName) {
        List<String> columnNames = new ArrayList<>();
        try {
            try {
                Connection conn = ds.getConnection();
                String catalog = getCatalog(conn);
                String schema = getSchema(conn);
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rs = metaData.getColumns(catalog, schema, tableName, null);
                Throwable th = null;
                if (null != rs) {
                    while (rs.next()) {
                        try {
                            try {
                                columnNames.add(rs.getString("COLUMN_NAME"));
                            } catch (Throwable th2) {
                                if (rs != null) {
                                    if (th != null) {
                                        try {
                                            rs.close();
                                        } catch (Throwable th3) {
                                            th.addSuppressed(th3);
                                        }
                                    } else {
                                        rs.close();
                                    }
                                }
                                throw th2;
                            }
                        } finally {
                        }
                    }
                }
                if (rs != null) {
                    if (0 != 0) {
                        try {
                            rs.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        rs.close();
                    }
                }
                String[] strArr = (String[]) columnNames.toArray(new String[0]);
                DbUtil.close(conn);
                return strArr;
            } catch (Exception e) {
                throw new DbRuntimeException("Get columns error!", e);
            }
        } catch (Throwable th5) {
            DbUtil.close(null);
            throw th5;
        }
    }

    public static Entity createLimitedEntity(DataSource ds, String tableName) {
        String[] columnNames = getColumnNames(ds, tableName);
        return Entity.create(tableName).setFieldNames(columnNames);
    }

    public static Table getTableMeta(DataSource ds, String tableName) {
        return getTableMeta(ds, null, null, tableName);
    }

    public static Table getTableMeta(DataSource ds, String catalog, String schema, String tableName) {
        Table table = Table.create(tableName);
        try {
            try {
                Connection conn = ds.getConnection();
                if (null == catalog) {
                    catalog = getCatalog(conn);
                }
                table.setCatalog(catalog);
                if (null == schema) {
                    schema = getSchema(conn);
                }
                table.setSchema(schema);
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rs = metaData.getTables(catalog, schema, tableName, new String[]{TableType.TABLE.value()});
                Throwable th = null;
                if (null != rs) {
                    try {
                        try {
                            if (rs.next()) {
                                table.setComment(rs.getString("REMARKS"));
                            }
                        } finally {
                        }
                    } finally {
                    }
                }
                if (rs != null) {
                    if (0 != 0) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        rs.close();
                    }
                }
                rs = metaData.getPrimaryKeys(catalog, schema, tableName);
                Throwable th3 = null;
                if (null != rs) {
                    while (rs.next()) {
                        try {
                            try {
                                table.addPk(rs.getString("COLUMN_NAME"));
                            } finally {
                            }
                        } finally {
                        }
                    }
                }
                if (rs != null) {
                    if (0 != 0) {
                        try {
                            rs.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                    } else {
                        rs.close();
                    }
                }
                rs = metaData.getColumns(catalog, schema, tableName, null);
                Throwable th5 = null;
                if (null != rs) {
                    while (rs.next()) {
                        try {
                            try {
                                table.setColumn(Column.create(table, rs));
                            } finally {
                                if (rs != null) {
                                    if (th5 != null) {
                                        try {
                                            rs.close();
                                        } catch (Throwable th6) {
                                            th5.addSuppressed(th6);
                                        }
                                    } else {
                                        rs.close();
                                    }
                                }
                            }
                        } finally {
                        }
                    }
                }
                if (rs != null) {
                    if (0 != 0) {
                        try {
                            rs.close();
                        } catch (Throwable th7) {
                            th5.addSuppressed(th7);
                        }
                    } else {
                        rs.close();
                    }
                }
                ResultSet rs2 = metaData.getIndexInfo(catalog, schema, tableName, false, false);
                Throwable th8 = null;
                try {
                    try {
                        Map<String, IndexInfo> indexInfoMap = new LinkedHashMap<>();
                        if (null != rs2) {
                            while (rs2.next()) {
                                if (0 != rs2.getShort("TYPE")) {
                                    String indexName = rs2.getString("INDEX_NAME");
                                    String key = StrUtil.join(BeanFactory.FACTORY_BEAN_PREFIX, tableName, indexName);
                                    IndexInfo indexInfo = indexInfoMap.get(key);
                                    if (null == indexInfo) {
                                        indexInfo = new IndexInfo(rs2.getBoolean("NON_UNIQUE"), indexName, tableName, schema, catalog);
                                        indexInfoMap.put(key, indexInfo);
                                    }
                                    indexInfo.getColumnIndexInfoList().add(ColumnIndexInfo.create(rs2));
                                }
                            }
                        }
                        table.setIndexInfoList(ListUtil.toList((Collection) indexInfoMap.values()));
                        if (rs2 != null) {
                            if (0 != 0) {
                                try {
                                    rs2.close();
                                } catch (Throwable th9) {
                                    th8.addSuppressed(th9);
                                }
                            } else {
                                rs2.close();
                            }
                        }
                        DbUtil.close(conn);
                        return table;
                    } finally {
                        if (rs2 != null) {
                            if (th8 != null) {
                                try {
                                    rs2.close();
                                } catch (Throwable th10) {
                                    th8.addSuppressed(th10);
                                }
                            } else {
                                rs2.close();
                            }
                        }
                    }
                } finally {
                }
            } catch (SQLException e) {
                throw new DbRuntimeException("Get columns error!", e);
            }
        } catch (Throwable th11) {
            DbUtil.close(null);
            throw th11;
        }
    }

    @Deprecated
    public static String getCataLog(Connection conn) {
        return getCatalog(conn);
    }

    public static String getCatalog(Connection conn) {
        if (null == conn) {
            return null;
        }
        try {
            return conn.getCatalog();
        } catch (SQLException e) {
            return null;
        }
    }

    public static String getSchema(Connection conn) {
        if (null == conn) {
            return null;
        }
        try {
            return conn.getSchema();
        } catch (SQLException e) {
            return null;
        }
    }
}
