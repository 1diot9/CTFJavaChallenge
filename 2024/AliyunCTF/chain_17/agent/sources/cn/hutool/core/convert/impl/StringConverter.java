package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.XmlUtil;
import java.io.Closeable;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.TimeZone;
import org.w3c.dom.Node;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/StringConverter.class */
public class StringConverter extends AbstractConverter<String> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public String convertInternal(Object value) {
        if (value instanceof TimeZone) {
            return ((TimeZone) value).getID();
        }
        if (value instanceof Node) {
            return XmlUtil.toStr((Node) value);
        }
        if (value instanceof Clob) {
            return clobToStr((Clob) value);
        }
        if (value instanceof Blob) {
            return blobToStr((Blob) value);
        }
        if (value instanceof Type) {
            return ((Type) value).getTypeName();
        }
        return convertToStr(value);
    }

    private static String clobToStr(Clob clob) {
        Reader reader = null;
        try {
            try {
                reader = clob.getCharacterStream();
                String read = IoUtil.read(reader);
                IoUtil.close((Closeable) reader);
                return read;
            } catch (SQLException e) {
                throw new ConvertException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) reader);
            throw th;
        }
    }

    private static String blobToStr(Blob blob) {
        InputStream in = null;
        try {
            try {
                in = blob.getBinaryStream();
                String read = IoUtil.read(in, CharsetUtil.CHARSET_UTF_8);
                IoUtil.close((Closeable) in);
                return read;
            } catch (SQLException e) {
                throw new ConvertException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }
}
