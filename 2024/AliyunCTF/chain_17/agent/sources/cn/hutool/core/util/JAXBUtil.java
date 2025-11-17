package cn.hutool.core.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/JAXBUtil.class */
public class JAXBUtil {
    public static String beanToXml(Object bean) {
        return beanToXml(bean, CharsetUtil.CHARSET_UTF_8, true);
    }

    public static String beanToXml(Object bean, Charset charset, boolean format) {
        try {
            JAXBContext context = JAXBContext.newInstance(new Class[]{bean.getClass()});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(format));
            marshaller.setProperty("jaxb.encoding", charset.name());
            StringWriter writer = new StringWriter();
            marshaller.marshal(bean, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new UtilException("convertToXml 错误：" + e.getMessage(), e);
        }
    }

    public static <T> T xmlToBean(String str, Class<T> cls) {
        return (T) xmlToBean(StrUtil.getReader(str), cls);
    }

    public static <T> T xmlToBean(File file, Charset charset, Class<T> cls) {
        return (T) xmlToBean(FileUtil.getReader(file, charset), cls);
    }

    public static <T> T xmlToBean(Reader reader, Class<T> cls) {
        try {
            try {
                T t = (T) JAXBContext.newInstance(new Class[]{cls}).createUnmarshaller().unmarshal(reader);
                IoUtil.close((Closeable) reader);
                return t;
            } catch (Exception e) {
                throw new RuntimeException("convertToJava2 错误：" + e.getMessage(), e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) reader);
            throw th;
        }
    }
}
