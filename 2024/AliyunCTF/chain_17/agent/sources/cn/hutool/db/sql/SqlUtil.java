package cn.hutool.db.sql;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.Entity;
import cn.hutool.db.sql.Condition;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/SqlUtil.class */
public class SqlUtil {
    public static String buildEqualsWhere(Entity entity, List<Object> paramValues) {
        if (null == entity || entity.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" WHERE ");
        boolean isNotFirst = false;
        for (Map.Entry<String, Object> entry : entity.entrySet()) {
            if (isNotFirst) {
                sb.append(" and ");
            } else {
                isNotFirst = true;
            }
            sb.append("`").append(entry.getKey()).append("`").append(" = ?");
            paramValues.add(entry.getValue());
        }
        return sb.toString();
    }

    public static Condition[] buildConditions(Entity entity) {
        if (null == entity || entity.isEmpty()) {
            return null;
        }
        Condition[] conditions = new Condition[entity.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : entity.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Condition) {
                int i2 = i;
                i++;
                conditions[i2] = (Condition) value;
            } else {
                int i3 = i;
                i++;
                conditions[i3] = new Condition(entry.getKey(), value);
            }
        }
        return conditions;
    }

    public static String buildLikeValue(String value, Condition.LikeType likeType, boolean withLikeKeyword) {
        if (null == value) {
            return null;
        }
        CharSequence[] charSequenceArr = new CharSequence[1];
        charSequenceArr[0] = withLikeKeyword ? "LIKE " : "";
        StringBuilder likeValue = StrUtil.builder(charSequenceArr);
        switch (likeType) {
            case StartWith:
                likeValue.append(value).append('%');
                break;
            case EndWith:
                likeValue.append('%').append(value);
                break;
            case Contains:
                likeValue.append('%').append(value).append('%');
                break;
        }
        return likeValue.toString();
    }

    public static String formatSql(String sql) {
        return SqlFormatter.format(sql);
    }

    public static String rowIdToString(RowId rowId) {
        return StrUtil.str(rowId.getBytes(), CharsetUtil.CHARSET_ISO_8859_1);
    }

    public static String clobToStr(Clob clob) {
        Reader reader = null;
        try {
            try {
                reader = clob.getCharacterStream();
                String read = IoUtil.read(reader);
                IoUtil.close((Closeable) reader);
                return read;
            } catch (SQLException e) {
                throw new DbRuntimeException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) reader);
            throw th;
        }
    }

    public static String blobToStr(Blob blob, Charset charset) {
        InputStream in = null;
        try {
            try {
                in = blob.getBinaryStream();
                String read = IoUtil.read(in, charset);
                IoUtil.close((Closeable) in);
                return read;
            } catch (SQLException e) {
                throw new DbRuntimeException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public static Blob createBlob(Connection conn, InputStream dataStream, boolean closeAfterUse) {
        OutputStream out = null;
        try {
            try {
                Blob blob = conn.createBlob();
                out = blob.setBinaryStream(1L);
                IoUtil.copy(dataStream, out);
                IoUtil.close((Closeable) out);
                if (closeAfterUse) {
                    IoUtil.close((Closeable) dataStream);
                }
                return blob;
            } catch (SQLException e) {
                throw new DbRuntimeException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) out);
            if (closeAfterUse) {
                IoUtil.close((Closeable) dataStream);
            }
            throw th;
        }
    }

    public static Blob createBlob(Connection conn, byte[] data) {
        try {
            Blob blob = conn.createBlob();
            blob.setBytes(0L, data);
            return blob;
        } catch (SQLException e) {
            throw new DbRuntimeException(e);
        }
    }

    public static Date toSqlDate(java.util.Date date) {
        return new Date(date.getTime());
    }

    public static Timestamp toSqlTimestamp(java.util.Date date) {
        return new Timestamp(date.getTime());
    }
}
